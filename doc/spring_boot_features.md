Spring Boot特性
===============
### SpringApplication
SpringApplication类提供了一种从main()方法启动Spring应用的便捷方式。在很多情况下，你只需委托给SpringApplication.run这个静态方法：
```java
public static void main(String[] args){
    SpringApplication.run(MySpringConfiguration.class, args);
}
```
* 自定义Banner
  
通过在classpath下添加一个banner.txt或设置banner.location来指定相应的文件可以改变启动过程中打印的banner。如果这个文件有特殊的编码，你可以使用banner.encoding设置它（默认为UTF-8）。

在banner.txt中可以使用如下的变量：

| 变量        | 描述     | 
| ----------- | :--------|
|${application.version}|MANIFEST.MF中声明的应用版本号，例如1.0|
|${application.formatted-version}|MANIFEST.MF中声明的被格式化后的应用版本号（被括号包裹且以v作为前缀），用于显示，例如(v1.0)|
|${spring-boot.version}|正在使用的Spring Boot版本号，例如1.2.2.BUILD-SNAPSHOT|
|${spring-boot.formatted-version}|正在使用的Spring Boot被格式化后的版本号（被括号包裹且以v作为前缀）,  用于显示，例如(v1.2.2.BUILD-SNAPSHOT)|

**注**：如果想以编程的方式产生一个banner，可以使用SpringBootApplication.setBanner(…)方法。使用org.springframework.boot.Banner接口，实现你自己的printBanner()方法。

* 自定义SpringApplication

如果默认的SpringApplication不符合你的口味，你可以创建一个本地的实例并自定义它。例如，关闭banner你可以这样写：
```java
public static void main(String[] args){
    SpringApplication app = new SpringApplication(MySpringConfiguration.class);
    app.setShowBanner(false);
    app.run(args);
}
```
**注**：传递给SpringApplication的构造器参数是spring beans的配置源。在大多数情况下，这些将是@Configuration类的引用，但它们也可能是XML配置或要扫描包的引用。

你也可以使用application.properties文件来配置SpringApplication。具体参考[Externalized 配置](#Externalized 配置)。查看配置选项的完整列表，可参考[SpringApplication Javadoc](http://docs.spring.io/spring-boot/docs/1.2.2.BUILD-SNAPSHOT/api/org/springframework/boot/SpringApplication.html).
    
* 流畅的构建API

如果你需要创建一个分层的ApplicationContext（多个具有父子关系的上下文），或你只是喜欢使用流畅的构建API，你可以使用SpringApplicationBuilder。SpringApplicationBuilder允许你以链式方式调用多个方法，包括可以创建层次结构的parent和child方法。
```java
new SpringApplicationBuilder()
    .showBanner(false)
    .sources(Parent.class)
    .child(Application.class)
    .run(args);
```
**注**：创建ApplicationContext层次时有些限制，比如，Web组件(components)必须包含在子上下文(child context)中，且相同的Environment即用于父上下文也用于子上下文中。具体参考[SpringApplicationBuilder javadoc](http://docs.spring.io/spring-boot/docs/1.2.2.BUILD-SNAPSHOT/api/org/springframework/boot/builder/SpringApplicationBuilder.html)

* Application事件和监听器

除了常见的Spring框架事件，比如[ContextRefreshedEvent](http://docs.spring.io/spring/docs/4.1.4.RELEASE/javadoc-api/org/springframework/context/event/ContextRefreshedEvent.html)，一个SpringApplication也发送一些额外的应用事件。一些事件实际上是在ApplicationContext被创建前触发的。

你可以使用多种方式注册事件监听器，最普通的是使用SpringApplication.addListeners(…)方法。在你的应用运行时，应用事件会以下面的次序发送：

1. 在运行开始，但除了监听器注册和初始化以外的任何处理之前，会发送一个ApplicationStartedEvent。
2. 在Environment将被用于已知的上下文，但在上下文被创建前，会发送一个ApplicationEnvironmentPreparedEvent。
3. 在refresh开始前，但在bean定义已被加载后，会发送一个ApplicationPreparedEvent。
4. 启动过程中如果出现异常，会发送一个ApplicationFailedEvent。

**注**：你通常不需要使用应用程序事件，但知道它们的存在会很方便（在某些场合可能会使用到）。在Spring内部，Spring Boot使用事件处理各种各样的任务。

* Web环境

一个SpringApplication将尝试为你创建正确类型的ApplicationContext。在默认情况下，使用AnnotationConfigApplicationContext或AnnotationConfigEmbeddedWebApplicationContext取决于你正在开发的是否是web应用。

用于确定一个web环境的算法相当简单（基于是否存在某些类）。如果需要覆盖默认行为，你可以使用setWebEnvironment(boolean webEnvironment)。通过调用setApplicationContextClass(…)，你可以完全控制ApplicationContext的类型。

**注**：当JUnit测试里使用SpringApplication时，调用setWebEnvironment(false)是可取的。

* 命令行启动器

如果你想获取原始的命令行参数，或一旦SpringApplication启动，你需要运行一些特定的代码，你可以实现CommandLineRunner接口。在所有实现该接口的Spring beans上将调用run(String… args)方法。
```java
import org.springframework.boot.*
import org.springframework.stereotype.*

@Component
public class MyBean implements CommandLineRunner {
    public void run(String... args) {
        // Do something...
    }
}
```
如果一些CommandLineRunner beans被定义必须以特定的次序调用，你可以额外实现org.springframework.core.Ordered接口或使用org.springframework.core.annotation.Order注解。

* Application退出

每个SpringApplication在退出时为了确保ApplicationContext被优雅的关闭，将会注册一个JVM的shutdown钩子。所有标准的Spring生命周期回调（比如，DisposableBean接口或@PreDestroy注解）都能使用。

此外，如果beans想在应用结束时返回一个特定的退出码（exit code），可以实现org.springframework.boot.ExitCodeGenerator接口。

### 外化配置

Spring Boot允许外化（externalize）你的配置，这样你能够在不同的环境下使用相同的代码。你可以使用properties文件，YAML文件，环境变量和命令行参数来外化配置。使用@Value注解，可以直接将属性值注入到你的beans中，并通过Spring的Environment抽象或绑定到结构化对象来访问。

Spring Boot使用一个非常特别的PropertySource次序来允许对值进行合理的覆盖，需要以下面的次序考虑属性：

1. 命令行参数
2. 来自于java:comp/env的JNDI属性
3. Java系统属性（System.getProperties()）
4. 操作系统环境变量
5. 只有在random.*里包含的属性会产生一个RandomValuePropertySource
6. 在打包的jar外的应用程序配置文件（application.properties，包含YAML和profile变量）
7. 在打包的jar内的应用程序配置文件（application.properties，包含YAML和profile变量）
8. 在@Configuration类上的@PropertySource注解
9. 默认属性（使用SpringApplication.setDefaultProperties指定）

下面是一个具体的示例（假设你开发一个使用name属性的@Component）：
```java
import org.springframework.stereotype.*
import org.springframework.beans.factory.annotation.*

@Component
public class MyBean {
    @Value("${name}")
    private String name;
    // ...
}
```
你可以将一个application.properties文件捆绑到jar内，用来提供一个合理的默认name属性值。当运行在生产环境时，可以在jar外提供一个application.properties文件来覆盖name属性。对于一次性的测试，你可以使用特定的命令行开关启动（比如，java -jar app.jar --name="Spring"）。

RandomValuePropertySource在注入随机值（比如，密钥或测试用例）时很有用。它能产生整数，longs或字符串，比如：
```java
my.secret=${random.value}
my.number=${random.int}
my.bignumber=${random.long}
my.number.less.than.ten=${random.int(10)}
my.number.in.range=${random.int[1024,65536]}
```
random.int*语法是OPEN value (,max) CLOSE，此处OPEN，CLOSE可以是任何字符，并且value，max是整数。如果提供max，那么value是最小的值，max是最大的值（不包含在内）。

* 访问命令行属性
* Application属性文件
* 特定的Profile属性
* 属性占位符
* 使用YAML代替Properties
* 加载YAML
* 在Spring环境中使用YAML暴露属性
* Multi-profile YAML文档
* YAML缺点
* 类型安全的配置属性
* 第3方配置
* 相关绑定
* @ConfigurationProperties校验 

### Profiles
* 添加激活的配置(profiles)
* 以编程方式设置profiles
* Profile特定配置文件

### 日志
* 日志格式
* 控制台输出
* 文件输出
* 日志级别
* 自定义日志配置

### 开发Web应用
* Spring Web MVC框架
* Spring MVC自动配置
* HttpMessageConverters
* MessageCodesResolver
* 静态内容
* 模板引擎
* 错误处理
* JAX-RS和Jersey
* 内嵌servlet容器支持
  1. Servlets和Filters
  2. EmbeddedWebApplicationContext
  3. 自定义内嵌servlet容器
  4. JSP的限制

### 安全

### 使用SQL数据库
* 配置DataSource
* 使用JdbcTemplate
* JPA和Spring Data
  1. 实体类
  2. Spring Data JPA仓库
  3. 创建和删除JPA数据库
  
### 使用NoSQL技术
* Redis
  1. 连接Redis
* MongoDB
  1. 连接MongoDB数据库
  2. MongoDBTemplate
  3. Spring Data MongoDB仓库
* Gemfire
* Solr
  1. 连接Solr
  2. Spring Data Solr仓库 
* Elasticsearch
  1. 连接Elasticsearch
  2. Spring Data Elasticseach仓库
  
### 消息
* JMS
  1. HornetQ支持
  2. ActiveQ支持
  3. 使用JNDI ConnectionFactory
  4. 发送消息
  5. 接收消息

### 发送邮件

### 使用JTA处理分布式事务
* 使用一个Atomikos事务管理器
* 使用一个Bitronix事务管理器
* 使用一个J2EE管理的事务管理器
* 混合XA和non-XA的JMS连接
* 支持可替代的内嵌事务管理器

### Spring集成

### 基于JMX的监控和管理

### 测试
* 测试作用域依赖
* 测试Spring应用
* 测试Spring Boot应用
  1. 使用Spock测试Spring Boot应用
* 测试工具
  1. ConfigFileApplicationContextInitializer
  2. EnvironmentTestUtils
  3. OutputCapture
  4. TestRestTemplate

### 开发自动配置和使用条件
* 理解auto-configured beans
* 定位auto-configuration候选者
* Condition注解
  1. Class条件
  2. Bean条件
  3. Property条件
  4. Resource条件
  5. Web Application条件
  6. SpEL表达式条件

### WebSockets









