package org.example.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@Table(name = "role")
public class Role {

    @Id
    private Integer id;

    private String roleName;

    private String roleCode;
}
