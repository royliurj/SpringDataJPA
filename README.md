SpringData：是Spring的子项目，用于将数据库访问，支持NoSQL和关心型数据库。主要目的是是数据库的访问变的方便快捷。  

SpringData JPA:致力于减少数据访问层（DAO）的开发量，开发者只需要声明持久层的接口。具体实现交给SpringData JPA完成。

# HelloWorld
1. 配置Spring整合JPA
```
    <!--1，配置外部数据源-->
    <context:property-placeholder location="classpath:db.properties"/>

    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="user" value="${jdbc.user}"></property>
        <property name="password" value="${jdbc.password}"></property>
        <property name="jdbcUrl" value="${jdbc.jdbcUrl}"></property>
        <property name="driverClass" value="${jdbc.driverClass}"></property>
        <property name="maxPoolSize" value="${jdbc.maxPoolSize}"></property>
        <property name="initialPoolSize" value="${jdbc.initPoolSize}"></property>
    </bean>

    <!--2，配置JPA的EntityManagerFactory-->
    <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <property name="dataSource" ref="dataSource"></property>
        <!--配置JPA提供者的适配器,可以通过内部bean的方式来配置-->
        <property name="jpaVendorAdapter">
            <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter"></bean>
        </property>
        <!--配置实体类所在的包-->
        <property name="packagesToScan" value="com.roy.entities"></property>
        <!--配置JPA的基本属性，例如JPA的实现产品等-->
        <property name="jpaProperties">
            <props>
                <prop key="hibernate.format_sql">true</prop>
                <prop key="hibernate.show_sql">true</prop>
                <prop key="hibernate.hbm2ddl.auto">update</prop>
                <prop key="hibernate.dialect">org.hibernate.dialect.MySQL5Dialect</prop>
            </props>
        </property>
    </bean>

    <!--3，配置事务管理器-->
    <bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"></property>
    </bean>

    <!--4，配置支持注解的事务-->
    <tx:annotation-driven transaction-manager="transactionManager"/>
```
2. 在Spring配置文件中配置SpringData
```
<!--5，配置SpringData-->
    <!--加入JPA的命名空间-->
    <!--basepackage: 扫描repository bean 所在的包-->
    <jpa:repositories base-package="com.roy.springdata" entity-manager-factory-ref="entityManagerFactory"/>
```
3. 声明持久层接口，接口集成Repository，并声明需要的方法
```
import org.springframework.data.repository.Repository;

public interface TestRepository extends Repository<Test,Integer> {
    Test getById(Integer id);
}
```
4. 测试调用
```
    private ApplicationContext applicationContext = null;

    {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    @org.junit.Test
    public void testGet(){

        TestRepository testRepository = applicationContext.getBean(TestRepository.class);
        com.roy.springdata.Test test = testRepository.getById(1);
        System.out.println(test);
    }
```

# Repository
### 简要介绍
1. 是一个空接口，即是一个标记接口。  
2. 若继承了这个接口Repository，则该接口会被IOC容器识别为一个RepositoryBean纳入到IOC容器中，进而可以在该接口中定义满足一定规则的方法。  
3. 可以通过注解@RepositoryDefinition的方法来替代继承接口
```
@RepositoryDefinition(domainClass = Test.class,idClass = Integer.class)
public interface TestRepository{
    Test getById(Integer id);
}
```
4. 子接口
- CrudRepository 实现了一组CRUD的相关方法
- PagingAndSortingRepository 实现了分页排序的相关方法
- JpaRepository 实现了一组JPA规范的相关方法
- JpaSpecificationExecutor 实现一组JPA Criteria查询相关的方法

### 基本使用
1. 查询  
查询方法以find、read、get方法开头  
查询条件使用关键字连接，属性首字母大写  
关键字：
![image](https://note.youdao.com/yws/public/resource/a5ac814a8685730fc2a92360e1f2c032/xmlnote/EEABC16302A54852A3BC6B85A3085CCC/29467)
```
    /**
     * 获取 name like 'name%' and id < 'id' 的数据集合
     * 获取name以什么开始，并且id<多少的数据
     * @param name
     * @param id
     * @return
     */
    List<Test> getByNameStartingWithAndIdLessThan(String name, Integer id);
```
支持级联查询
```
    //test中的关联属性
    @JoinColumn(name = "Test2_ID")
    @ManyToOne
    private Test2 test2;
    
    /**
     * Test与Test2是多对一的关系，多个test对应一个Test2
     * Test2TestName 表示Test2表中的testName列
     * @param id
     * @return
     */
    List<Test> getByTest2IdGreaterThan(Integer id);
    List<Test> getByTest2TestNameStartingWith(String name);
```
若当前类有符合条件的属性，则优先使用，而不实用级联属性，若需要使用级联属性，则属性之间使用_进行连接
```
    //test表中有列test2Id和test2TestName， 同test2表中的列相同，在执行上面的查询，只能查询test表中的数据，没有进行级联查询
    private Integer test2Id;
    private String test2TestName;

    @JoinColumn(name = "Test2_ID")
    @ManyToOne
    private Test2 test2;
    
    //接口中的方法定义成下面，就会进行就能查询
    List<Test> getByTest2_IdGreaterThan(Integer id);
    List<Test> getByTest2_TestNameStartingWith(String name);
```
2. Query注解
```
    //使用Query注解，可以自定义JPQL语句
    @Query("select t from Test t where t.id = (select max(t2.id) from Test t2)")
    Test getMaxIdTest();
    
    //传递参数，使用占位符，顺序必须一致
    @Query("select t from Test t where t.id = ?1 And t.name = ?2")
    List<Test> getTestList(Integer id, String name);
    
    //使用命名参数传递
    @Query("select t from Test t where t.id = :id And t.name = :name")
    List<Test> getTestList2(@Param("name") String name2, @Param("id") Integer id2);
    
    //占位符上使用%
    @Query("select t from Test t where t.name LIKE %?1%")
    List<Test> testQuery(String name);
    
    //nativeQuery = true,可以使用原生的SQL查询
    @Query(value = "select count(id) from jpa_test", nativeQuery = true)
    long getTotalCount();
```
3. Modifying注解
```
    //可以通过自定义的JPQL完成update和delete的操作，注意JPQL不支持Insert
    //必须使用Modifying注解进行修饰，以通知SpringData，这是一个update或者delete操作
    //Update、Delete操作需要使用事务（需要定义Service层，在Service层的方法上添加事务）
    //默认情况下，SpringData的每个方法上有事务，但都是一个只读事务，不能完成修改操作
    @Modifying
    @Query("update Test t set t.name = :name where t.id = :id")
    void updateTestName(@Param("id") Integer id , @Param("name") String name);
    
    @Service
    public class TestService {
    
        @Autowired
        private TestRepository testRepository;
    
        @Transactional
        public void uptateTest(Integer id, String name){
            testRepository.updateTestName(id,name);
        }
    }
```
4. CrudRepository接口  
主要是实现了增删改查的操作
```
    @Transactional
    public void saveTests(List<Test> tests){
        testRepository.save(tests);
    }
    
    @org.junit.Test
    public void testCrudRepository(){

        List<com.roy.springdata.Test> tests = new ArrayList<com.roy.springdata.Test>();

        for (int i = 0; i < 10; i++) {
            com.roy.springdata.Test test = new com.roy.springdata.Test();
            test.setId(i+1);
            String name = "name " + (i+1);
            test.setName(name);
            tests.add(test);
        }
        testService.saveTests(tests);
    }
```
5. PagingAndSortingRepository接口

```
    @org.junit.Test
    public void testPagingAndSortingRepository(){

        Sort.Order order1 = new Sort.Order(Sort.Direction.DESC,"name");
        Sort.Order order2 = new Sort.Order(Sort.Direction.ASC,"id");
        Sort sort = new Sort(order1,order2);
        //PageNumber从0开始,每页3条
        Pageable pageable = new PageRequest(0,3,sort);

        Page<com.roy.springdata.Test> list = testRepository.findAll(pageable);

        System.out.println("总记录数"+list.getTotalElements());
        System.out.println("当前页码"+list.getNumber());
        System.out.println("总页数" + list.getTotalPages());
        System.out.println("当前页码的List" + list.getContent());
        System.out.println("当前页面有多少条记录" + list.getNumberOfElements());
    }
```
6. JpaRepository接口
```
    //saveAndFlush， 有id更新，没有id插入
    @org.junit.Test
    public void testJpaRepository(){
        com.roy.springdata.Test test = new com.roy.springdata.Test();
        test.setId(1);
        test.setName("111");
        com.roy.springdata.Test result = testRepository.saveAndFlush(test);
    }
```
7. JpaSepcificationExecutor接口
```
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
```
### 自定义Repository方法
1. 定义一个接口：声明要添加的方法
```
public interface TestDao {
    void test();
}
```
2. 提供该接口的实现类：类名需要在声明的Repository后面添加Impl，并实现方法
```
public class TestDaoImpl implements TestDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void test() {
        Test test = entityManager.find(Test.class,1);
        System.out.println(test);
    }
}
```
3. 声明Repository接口，并继承1)声明的接口
```
    public interface TestRepository extends JpaRepository<Test,Integer>, JpaSpecificationExecutor<Test> ,TestDao {
    ...
    }
```
4. 注意：默认情况下，SpringData会在base-package中查"接口名"作为实现类，也可以通过repository-impl-postfix声明后缀