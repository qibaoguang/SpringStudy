Spring Java-based容器配置
==========

多年以来，Spring大量的XML配置及复杂的依赖管理饱受非议。为了实现免XML的开发体验，Spring添加了新的配置注解以支持Java Config开发模式，其中最重要的注解就是@Configuration和@Bean。

### 基本概念：@Bean和@Configuration
在Spring新的Java-configuration支持中，最核心的部分就是使用@Configuration注解的类和使用@Bean注解的类。

@Bean注解用于指示一个方法实例化，配置，初始化一个新的被Spring容器管理的对象。对于熟悉Spring `<beans/>` XML配置的人来说，@Bean注解跟`<bean/>`元素作用相同。你可以在任何Spring @Component中使用@Bean注解的方法，不过，它们通常和@Configuration注解的beans一块使用。

使用@Configuration注解一个类意味着它的主要目的是作为bean定义的来源。此外，@Configuration类允许bean之间（inter-bean）的依赖，你只需简单地调用该类中其他的@Bean方法。
示例：
```java
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }

}
```
上面的AppConfig类等价于下面的Spring `<bean/>` XML：
```xml
<beans>
    <bean id="myService" class="com.acme.services.MyServiceImpl"/>
</beans>
```
* Full @Configuration VS 'lite' @Beans模式
当@Bean方法声明在没有被@Conguration注解的类里，这就是所谓的以'精简'模式处理。例如，在一个@Component中，甚至在一个普通的类中声明的bean方法都会以'精简'处理。
跟完整@Configuration不同的是，精简@Bean方法难以声明bean之间的依赖。通常，在精简模式中操作时，不应该在一个@Bean方法中调用另一个@Bean方法。
一种推荐的方式是只在@Configuration类中使用@Bean方法，这样可以确保总是使用'完整'模式，避免@Bean方法意外地被调用多次，减少那些在精简模式下产生的很难跟踪的微妙bugs。

### 使用AnnotationConfigApplicationContext实例化Spring容器
AnnotationConfigApplicationContext是在Spring 3.0中新增的。这个多功能的ApplicationContext实现即可接收@Configuration类作为输入，也可接收普通的@Component类，及使用JSR-330元数据注解的类。
当将@Configuration类作为输入时，@Configuration类本身被注册为一个bean定义，并且该类中所有声明的@Bean方法也被注册为bean定义。
当将@Component和JSR-330类作为输入时，它们被注册为bean定义，并且在需要的地方使用DI元数据，比如@Autowired或@Inject。

* 构造器实例化
跟实例化一个ClassPathXmlApplicationContext时将Spring XML文件用作输入类似，在实例化一个AnnotationConfigApplicationContext时可以使用@Configuration类作为输入。这就允许Spring容器完全零XML配置：
```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```
如上所述，AnnotationConfigApplicationContext不局限于只使用@Configuration类。任何@Component或JSR-330注解的类都可以作为AnnotationConfigApplicationContext构造器的输入。例如：
```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(MyServiceImpl.class, Dependency1.class, Dependency2.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```
上面假设MyServiceImpl，Dependency1和Dependency2使用Spring依赖注入注解，比如@Autowired。

* register(Class<?>…​)实例化
可以使用无参的构造器实例化AnnotationConfigApplicationContext，然后使用`register()`方法对容器进行配置。这种方式在以编程方式构造一个AnnotationConfigApplicationContext时非常有用。
```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(AppConfig.class, OtherConfig.class);
    ctx.register(AdditionalConfig.class);
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```

* 启用scan(String…​)的组件扫描
想要启用组件扫描，只需按如下方式注解你的@Configuration类：
```java
@Configuration
@ComponentScan(basePackages = "com.acme")
public class AppConfig  {
    ...
}
```
**注：** 有经验的Spring用户会比较熟悉来自Spring `context:`命名空间的等效XML声明：
```xml
<beans>
    <context:component-scan base-package="com.acme"/>
</beans>
```
在上面的示例中，将会扫描`com.acme`包，查找任何被@Component注解的类，并且这些类将被注册为容器里的Spring bean定义。AnnotationConfigApplicationContext暴露`scan(String…​)`方法来实现相同的容器扫描功能：
```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.scan("com.acme");
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
}
```
**注：** 记着@Configuration类是被@Component元注解的，所以它们也是组件扫描的候选者！在上面的示例中，假设AppConfig定义在`com.acme`包（或任何下层包）中，在调用`scan()`期间它也会被扫描，当`refresh()`时它的所有@Bean方法将被处理，并注册为容器里的bean定义。

* 使用AnnotationConfigWebApplicationContext支持web应用
AnnotationConfigWebApplicationContext是AnnotationConfigApplicationContext的WebApplicationContext变种，当配置Spring ContextLoaderListener servlet监听器，Spring MVC DispatcherServlet等会用到。下面的web.xml片段配置了一个典型的Spring MVC web应用。注意contextClass的context-param和init-param的使用：
```xml
<web-app>
    <!-- 配置ContextLoaderListener使用 AnnotationConfigWebApplicationContext代替默认的XmlWebApplicationContext -->
    <context-param>
        <param-name>contextClass</param-name>
        <param-value>
            org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </context-param>

    <!-- Configuration位置必须包含一个或多个逗号或空格分隔的全限定 @Configuration类.组件扫描中要指定该全限定包.-->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>com.acme.AppConfig</param-value>
    </context-param>

    <!-- 像平常那样使用ContextLoaderListener启动根应用上下文 -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!--像平常那样声明一个Spring MVC DispatcherServlet -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- 配置DispatcherServlet使用AnnotationConfigWebApplicationContext代替默认的XmlWebApplicationContext -->
        <init-param>
            <param-name>contextClass</param-name>
            <param-value>
                org.springframework.web.context.support.AnnotationConfigWebApplicationContext
            </param-value>
        </init-param>
        <!-- 再次，配置路径必须包含一个或多个逗号，或空格分隔的全限定@Configuration类 -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>com.acme.web.MvcConfig</param-value>
        </init-param>
    </servlet>

    <!-- 将/app/*的所有请求映射到该dispatcher servlet -->
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>
</web-app>
```


























