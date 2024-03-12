package org.example.repository;

import org.example.dto.MenuRolesDto;
import org.example.entity.Role;
import org.example.entity.RoleMenu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RoleMenuRepository extends R2dbcRepository<RoleMenu, Integer> {

    @Query("SELECT m.path, GROUP_CONCAT(r.role_code) as role_codes FROM menu m JOIN role_menu rm join role r on rm.role_id = r.id and m.id = rm.menu_id\n" +
            "GROUP BY m.id;")
    Flux<MenuRolesDto> getAllRoleMenus();

    @Query("SELECT * FROM role r JOIN role_menu rm on r.id = rm.role_id join menu m on m.id = rm.menu_id where m.path = :#{#path} ;")
    Flux<Role> findMenuRoles(@Param("path") String path);
}
