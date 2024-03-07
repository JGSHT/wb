package org.example.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    private Integer id;

    private String username;

    private String password;

    private String nickName;

    private Integer age;
}
