package org.example.repository;

import org.example.entity.Role;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RoleRepository extends R2dbcRepository<Role, Integer> {

    @Query("SELECT * FROM role r JOIN user_role ur on r.id = ur.role_id where ur.user_id = :#{#userId} ;")
    Flux<Role> getUserRoles(@Param("userId")Integer userId);
}
