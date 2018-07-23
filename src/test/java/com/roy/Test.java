package com.roy;

import com.roy.springdata.TestRepository;
import com.roy.springdata.TestService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Test {

    private ApplicationContext applicationContext = null;
    private TestRepository testRepository = null;
    private TestService testService = null;

    {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        testRepository = applicationContext.getBean(TestRepository.class);

        testService = applicationContext.getBean(TestService.class);
    }

    @org.junit.Test
    public void test() throws SQLException {

        DataSource dataSource = applicationContext.getBean(DataSource.class);
        System.out.println(dataSource.getConnection());


    }

    @org.junit.Test
    public void testGet(){

        TestRepository testRepository = applicationContext.getBean(TestRepository.class);

        System.out.println(testRepository.getClass().getName());

        List<com.roy.springdata.Test> test = testRepository.getByNameStartingWithAndIdLessThan("2",5);
        System.out.println(test);
    }

    @org.junit.Test
    public void testKeyWord2(){

        TestRepository testRepository = applicationContext.getBean(TestRepository.class);
        testRepository.getByTest2_IdGreaterThan(1);

        testRepository.getByTest2_TestNameStartingWith("123");
    }

    @org.junit.Test
    public void testQuery(){
        com.roy.springdata.Test test  = testRepository.getMaxIdTest();
        System.out.println(test);

        testRepository.getTestList(1,"aa");

        testRepository.getTestList2("aa",3);

        long a = testRepository.getTotalCount();
        System.out.println(a);
    }

    @org.junit.Test
    public void testModiy(){

        testService.uptateTest(1,"222");
    }

    @org.junit.Test
    public void testCrudRepository(){

        List<com.roy.springdata.Test> tests = new ArrayList<com.roy.springdata.Test>();

        for (int i = 0; i < 10; i++) {
            com.roy.springdata.Test test = new com.roy.springdata.Test();
            String name = "name " + (i+1);
            test.setName(name);
            tests.add(test);
        }
        testService.saveTests(tests);
    }

    @org.junit.Test
    public void testCrueRepositoryCount(){
        System.out.println(testRepository.count());
    }

    @org.junit.Test
    public void testPagingAndSortingRepository(){

        Sort.Order order1 = new Sort.Order(Sort.Direction.DESC,"name");
        Sort.Order order2 = new Sort.Order(Sort.Direction.ASC,"id");
        Sort sort = new Sort(order1,order2);
        //PageNumber从0开始
        Pageable pageable = new PageRequest(0,3,sort);

        Page<com.roy.springdata.Test> list = testRepository.findAll(pageable);

        System.out.println("总记录数"+list.getTotalElements());
        System.out.println("当前页码"+list.getNumber());
        System.out.println("总页数" + list.getTotalPages());
        System.out.println("当前页码的List" + list.getContent());
        System.out.println("当前页面有多少条记录" + list.getNumberOfElements());
    }

    @org.junit.Test
    public void testJpaRepository(){
        com.roy.springdata.Test test = new com.roy.springdata.Test();
        test.setId(1);
        test.setName("111");
        com.roy.springdata.Test result = testRepository.saveAndFlush(test);
    }

    //实现带查询条件的分页，查询id>5的数据
    //调用 JpaSepcificationExecutor的findAll(Specification<T> spec, Pageable pageable)方法
    //Specification封装了JPA Criteria的查询条件
    @org.junit.Test
    public void testJpaSepcificationExecutor(){

        PageRequest pageRequest = new PageRequest(0,3);

        //通常使用匿名内部类
        Specification<com.roy.springdata.Test> specification = new Specification<com.roy.springdata.Test>() {
            /**
             * @param *root 代表查询的实体类
             * @param criteriaQuery 可以从中得到root对象，即告知JPA Criteria查询哪一个实体类、可以添加查询条件、结合EntityManager对象得到最终查询的TypedQuery对象
             * @param *criteriaBuilder 用于创建Criteria相关对象的工厂，当然可以从中获取到Predicate对象
             * @return *代表一个查询条件
             */
            public Predicate toPredicate(Root<com.roy.springdata.Test> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path path = root.get("id");
                Predicate predicate = criteriaBuilder.gt(path,5);
                return predicate;
            }
        };

        Page<com.roy.springdata.Test> page = testRepository.findAll(specification,pageRequest);
        System.out.println("总记录数"+page.getTotalElements());
        System.out.println("当前页码"+page.getNumber());
        System.out.println("总页数" + page.getTotalPages());
        System.out.println("当前页码的List" + page.getContent());
        System.out.println("当前页面有多少条记录" + page.getNumberOfElements());
    }

    @org.junit.Test
    public void testCustomRepository(){
        testRepository.test();
    }
}
