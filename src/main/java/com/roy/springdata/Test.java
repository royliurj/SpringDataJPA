package com.roy.springdata;

import javax.persistence.*;

@Entity
@Table(name = "JPA_Test")
public class Test {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private Integer test2Id;
    private String test2TestName;

    @JoinColumn(name = "Test2_ID")
    @ManyToOne
    private Test2 test2;

    public Test2 getTest2() {
        return test2;
    }

    public void setTest2(Test2 test2) {
        this.test2 = test2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTest2Id() {
        return test2Id;
    }

    public void setTest2Id(Integer test2Id) {
        this.test2Id = test2Id;
    }

    public String getTest2TestName() {
        return test2TestName;
    }

    public void setTest2TestName(String test2TestName) {
        this.test2TestName = test2TestName;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

