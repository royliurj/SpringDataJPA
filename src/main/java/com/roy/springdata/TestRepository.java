package com.roy.springdata;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import java.util.List;

//@RepositoryDefinition(domainClass = Test.class,idClass = Integer.class)
//public interface TestRepository{
//    Test getByIdeger id);
//}

public interface TestRepository extends JpaRepository<Test,Integer>, JpaSpecificationExecutor<Test> ,TestDao {
    Test getById(Integer id);

    /**
     * 获取 name like 'name%' and id < 'id' 的数据集合
     * 获取name以什么开始，并且id<多少的数据
     * @param name
     * @param id
     * @return
     */
    List<Test> getByNameStartingWithAndIdLessThan(String name, Integer id);


    /**
     * Test与Test2是多对一的关系，多个test对应一个Test2
     * Test2TestName 表示Test2表中的testName列
     * @param id
     * @return
     */
    List<Test> getByTest2_IdGreaterThan(Integer id);
    List<Test> getByTest2_TestNameStartingWith(String name);

    @Query("select t from Test t where t.id = (select max(t2.id) from Test t2)")
    Test getMaxIdTest();


    @Query("select t from Test t where t.id = ?1 And t.name = ?2")
    List<Test> getTestList(Integer id, String name);

    @Query("select t from Test t where t.id = :id And t.name = :name")
    List<Test> getTestList2(@Param("name") String name2, @Param("id") Integer id2);

    //使用%
    @Query("select t from Test t where t.name LIKE %?1%")
    List<Test> testQuery();

    //nativeQuery = true,可以使用原生的SQL查询
    @Query(value = "select count(id) from jpa_test", nativeQuery = true)
    long getTotalCount();

    //可以通过自定义的JPQL完成update和delete的操作，注意JPQL不支持Insert
    //必须使用Modifying注解进行修饰，以通知SpringData，这是一个update或者delete操作
    //Update、Delete操作需要使用事务（需要定义Service层，在Service层的方法上添加事务）
    //默认情况下，SpringData的每个方法上有事务，但都是一个只读事务，不能完成修改操作
    @Modifying
    @Query("update Test t set t.name = :name where t.id = :id")
    void updateTestName(@Param("id") Integer id , @Param("name") String name);


}
