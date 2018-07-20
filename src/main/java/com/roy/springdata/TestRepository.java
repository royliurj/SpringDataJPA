package com.roy.springdata;


import org.springframework.data.repository.Repository;

public interface TestRepository extends Repository<Test,Integer> {
    Test getById(Integer id);
}
