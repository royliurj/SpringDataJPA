package com.roy.springdata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "JPA_Test2")
public class Test2 {

    @Id
    @GeneratedValue
    private Integer id;
    private String testName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    @Override
    public String toString() {
        return "Test2{" +
                "id=" + id +
                ", testName='" + testName + '\'' +
                '}';
    }
}
