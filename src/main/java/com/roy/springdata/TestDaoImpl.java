package com.roy.springdata;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TestDaoImpl implements TestDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void test() {
        Test test = entityManager.find(Test.class,1);
        System.out.println(test);
    }
}
