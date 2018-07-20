package com.roy;

import com.roy.springdata.TestRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Test {

    private ApplicationContext applicationContext = null;

    {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    @org.junit.Test
    public void test() throws SQLException {

        DataSource dataSource = applicationContext.getBean(DataSource.class);
        System.out.println(dataSource.getConnection());


    }

    @org.junit.Test
    public void testGet(){

        TestRepository testRepository = applicationContext.getBean(TestRepository.class);
        com.roy.springdata.Test test = testRepository.getById(1);
        System.out.println(test);
    }
}
