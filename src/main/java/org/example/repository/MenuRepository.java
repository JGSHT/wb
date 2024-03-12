package org.example.repository;

import org.example.entity.Menu;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends R2dbcRepository<Menu, Integer> {

}
