package org.example.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "user_role")
public class UserRole {

    @Id
    private Integer id;

    private Integer userId;

    private Integer roleId;
}
