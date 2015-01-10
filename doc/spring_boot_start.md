Spring Boot初级教程
=======================

### Spring Boot安装
环境要求：Java 8，Maven 3.2或Gradle 1.12
1. 添加Spring Boot的依赖
<pre>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd"&gt;

    &lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
    &lt;groupId&gt;com.example&lt;/groupId&gt;
    &lt;artifactId&gt;myproject&lt;/artifactId&gt;
    &lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;

    &lt;!-- Inherit defaults from Spring Boot --&gt;
    &lt;parent&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-starter-parent&lt;/artifactId&gt;
        &lt;version&gt;1.2.1.RELEASE&lt;/version&gt;
    &lt;/parent&gt;

    &lt;!-- Add typical dependencies for a web application --&gt;
    &lt;dependencies&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
            &lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
        &lt;/dependency&gt;
    &lt;/dependencies&gt;

    &lt;!-- Package as an executable jar --&gt;
    &lt;build&gt;
        &lt;plugins&gt;
            &lt;plugin&gt;
                &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
                &lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;
            &lt;/plugin&gt;
        &lt;/plugins&gt;
    &lt;/build&gt;
&lt;/project&gt;
</pre>
注：Spring Boot依赖的groupId是org.springframework.boot，一般Maven pom需要继承spring-boot-starter-parent，然后声明相应[Starter POMs](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-starter-poms).





