package org.example.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@Table(name = "menu")
public class Menu {

    @Id
    private Integer id;

    private String menuCode;

    private String path;
}
