package com.roy.springdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Transactional
    public void uptateTest(Integer id, String name){
        testRepository.updateTestName(id,name);
    }

    @Transactional
    public void saveTests(List<Test> tests){
        testRepository.save(tests);
    }
}
