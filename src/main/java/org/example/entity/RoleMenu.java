package org.example.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Table(name = "role_menu")
public class RoleMenu {

    @Id
    private Integer id;

    private Integer roleId;

    private Integer menuId;
}
