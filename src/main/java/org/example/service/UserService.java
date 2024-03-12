package org.example.service;


import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.example.entity.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.security.model.UserDetail;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    /**
     * 查询全部用户信息
     *
     * @return
     */
    public Flux<User> getAllUserInfo(){
        return userRepository.findAll();
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id
     * @return
     */
    public Mono<User> getUserById(Integer id){
        return userRepository.findById(id);
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    public Mono<User> saveUser(User user){
        return userRepository.save(user);
    }

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    public Mono<User> updateUser(User user){
        return userRepository.updateUser(user);
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    public Mono<Void> deleteUser(Integer id){
        return userRepository.deleteById(id);
    }

    private Map<String, User> data;

    @PostConstruct
    public void init() {
        data = new HashMap<>();
        data.put("user", new User(1,"user", "123456", "张三", 18, 1));
        data.put("admin", new User(2,"admin", "123456", "李管", 35, 1));
    }

    public Mono<User> findUserByName(String username){
        return Mono.justOrEmpty(data.get(username));
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsernameEquals(username)
                .flatMap(user -> {
                    return roleRepository.getUserRoles(user.getId()).collectList().flatMap(
                            roles -> {
                                return Mono.justOrEmpty(new UserDetail(
                                        user.getUsername(), user.getPassword(), user.getEnable().equals(1),
                                        roles));
                            }
                    );
                });

    }

    public Mono<UserDetails> findByPhone(String phone) {
        return userRepository.findByUsernameEquals(phone)
                .flatMap(user -> roleRepository.getUserRoles(user.getId()).collectList().flatMap(
                        roles -> Mono.justOrEmpty(new UserDetail(
                                user.getUsername(), user.getPassword(), user.getEnable().equals(1),
                                roles))
                ));

    }
}
