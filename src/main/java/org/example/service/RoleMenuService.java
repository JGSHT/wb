package org.example.service;


import lombok.AllArgsConstructor;
import org.example.dto.MenuRolesDto;
import org.example.repository.RoleMenuRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;

    /**
     * 查询系统全部权限信息
     *
     * @return
     *
     */
    public Flux<MenuRolesDto> findAll(){
        System.out.println("查询权限");
        return roleMenuRepository.getAllRoleMenus();
    }

//    public Flux<Role> findMenuRoles(String path){
//        return
//    }

}
