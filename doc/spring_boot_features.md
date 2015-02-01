Spring Boot特性
===============
### SpringApplication
* 自定义Banner
* 自定义SpringApplication
* 流畅的构建API
* Application事件和监听器
* Web环境
* 命令行启动器
* Application退出
* 
### Externalized 配置
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









