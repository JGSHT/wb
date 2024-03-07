package org.example.repository;

import org.example.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Integer> {

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @Query("update user set nickName = :#{#user.nickName}, age = :#{#user.age} where id = :#{#user.id}")
    Mono<User> updateUser(@Param("user") User user);


    /**
     * 更新用户信息(带条件判断)
     *
     * @param user
     * @return
     */
    @Query("UPDATE User SET " +
            "name = CASE WHEN :#{#user.nickName} IS NULL THEN nickName ELSE :#{#user.nickName} END, " +
            "age = CASE WHEN :#{#user.age} IS NULL THEN age ELSE :#{#user.age} END " +
            "WHERE id = :#{#user.id}")
    Mono<User> updateUserQuery(@Param("user") User user);

}
