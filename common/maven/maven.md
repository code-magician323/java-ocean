## Maven

1. 项目依懒: `依赖传播, 依赖优先原则, 可选依赖, 排除依赖, 依赖范围`

   - 依赖传播: 直接依赖/间接依赖
   - 依赖优先原则
     - 最短路径优先
     - 相同路径下配置在前的优先
   - 可选依赖: 除非使用者显示引用, 否则不会自动传递
     ```xml
     <optional>true</optional>
     ```
   - 排除依赖

     ```xml
     <exclusions>
        <exclusion>
           <groupId>org.springframework</groupId>
           <artifactId>spring-web</artifactId>
        </exclusion>
     </exclusions>
     ```

   - 依赖范围 `<scope>`

     - compile(默认): 编译范围, 编译和打包都会依赖
     - provided: 提供范围, 编译时依赖, 但不会打包进去[servlet-api.jar 因为 Tomcat 自带了]
     - runtime: 运行时范围, 打包时依赖, 编译不会[写代码不需要 mysql-connector.jar]
     - test: 测试范围, 编译运行测试用例依赖, 不会打包进去[junit.jar]
     - system: 表示由系统中 CLASSPATH 指定, 编译时依赖, 不会打包进去

       ```xml
       <!-- jre 下的 tools: 需要在运行环境上将 tools.jar 包含到 CLASSPATH  -->
       <dependency>
          <groupId>com.sun</groupId>
          <artifactId>tools</artifactId>
          <version>${java.version}</version>
          <scope>system</scope>
          <optional>true</optional>
          <systemPath>${java.home}/../lib/tools.jar</systemPath>
       </dependency>
       ```

       ```xml
       <!-- 项目下的 jar -->
       <dependency>
          <groupId>jsr</groupId>
          <artifactId>jsr</artifactId>
          <version>3.5</version>
          <scope>system</scope>
          <optional>true</optional>
          <systemPath>${basedir}/lib/jsr305.jar</systemPath>
       </dependency>

       <!-- 将项目路径下的 jar 打进最终的包 -->
       <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-dependency-plugin</artifactId>
          <version>2.10</version>
          <executions>
             <execution>
                   <id>copy-dependencies</id>
                   <phase>compile</phase>
                   <goals>
                      <goal>copy-dependencies</goal>
                   </goals>
                   <configuration>
                      <outputDirectory>${project.build.directory}/${project.build.finalName}/WEB-INF/lib</outputDirectory>
                      <includeScope>system</includeScope>
                      <excludeGroupIds>com.sun</excludeGroupIds>
                   </configuration>
             </execution>
          </executions>
       </plugin>
       ```

2. maven 项目属性

   - \${basedir} 项目根目录
   - \${version}表示项目版本;
   - ${project.basedir}同${basedir};
   - ${project.version}表示项目版本, 与${version}相同;
   - \${project.build.directory} 构建目录，缺省为 target
   - \${project.build.sourceEncoding}表示主源码的编码格式;
   - \${project.build.sourceDirectory}表示主源码路径;
   - \${project.build.finalName}表示输出文件名称;
   - \${project.build.outputDirectory} 构建过程输出目录，缺省为 target/classes

3. 项目构建 build

   - defaultGoal: 执行构建时默认的 goal 或 phase, 如 jar:jar 或者 package 等
   - directory: 构建的结果所在的路径, 默认为\${basedir}/target 目录
   - finalName: 构建的最终结果的名字, 该名字可能在其他 plugin 中被改变

   ```xml
   <defaultGoal>package</defaultGoal>
   <directory>${basedir}/target2</directory>
   <finalName>${artifactId}-${version}</finalName>
   ```

   - targetPath: 资源文件的目标路径
   - directory: 资源文件的路径, 默认位于 `${basedir}/src/main/resources/` 目录下
   - includes: 一组文件名的匹配模式, 被匹配的资源文件将被构建过程处理
   - excludes: 一组文件名的匹配模式, 被匹配的资源文件将被构建过程忽略. 同时被 includes 和 excludes 匹配的资源文件将被忽略
   - filtering: 默认 false, true 表示通过参数对资源文件中的 `${key}` 在编译时进行动态变更;
     - 替换源可用 `-Dkey`
     - pom 中的 `<properties>` 值: properties 文件中的值可以定义在 pom 文件的 <properties> 中
     - <filters> 中指定的 properties 文件

   ```xml
   <resources>
      <resource>
         <directory>src/main/java</directory>
         <includes>
            <include>**/*.MF</include>
            <include>**/*.XML</include>
         </includes>
         <filtering>true</filtering>
      </resource>
      <resource>
         <directory>src/main/resources</directory>
         <includes>
            <include>**/*</include>
            <include>*</include>
         </includes>
         <filtering>true</filtering>
      </resource>
   </resources>
   ```

---

## issue

1. pom package tag

   - pom
   - jar
   - war

2. pom relative tag

   - default value: ../pom.xml

3. plugins tag

4. dependencyManager usage:

   - version control
   - and donot import maven

5. maven 打包没有 jar, no main manifest attribute, in xx.jar 或者 SpringApplication ClassNotFound

   - parent

     ```xml
     <build>
         <plugins>
             <plugin>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-maven-plugin</artifactId>
                 <version>2.1.7.RELEASE</version>
                 <executions>
                     <execution>
                         <goals>
                             <goal>repackage</goal>
                         </goals>
                     </execution>
                 </executions>
             </plugin>
         </plugins>
     </build>
     ```

   - common: **`两个注释的地方2选1吧`**

     ```xml
     <!-- Failed to execute goal repackage failed: Unable to find main class -> [Help 1] -->
     <properties>
         <spring-boot.repackage.skip>true</spring-boot.repackage.skip>
     </properties>

     <build>
         <plugins>
             <plugin>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-maven-plugin</artifactId>
                 <!-- 可运行的 jar -->
                 <configuration>
                     <classifier>exec</classifier>
                 </configuration>
             </plugin>
         </plugins>
     </build>
     ```

   - module: 正常使用

## reference

1. https://github.com/Alice52/java-ocean/issues/102
2. [maven build](https://blog.csdn.net/DamonREN/article/details/85091900)
