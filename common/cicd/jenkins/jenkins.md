## Jenkins

### 1. install

1. docker-compose

```yml
version: '3.1'
services:
  jenkins:
    restart: always
    image: jenkinsci/jenkins
    container_name: jenkins
    ports:
      - 9090:8080
      # JNLP-based Jenkins agent communicates with Jenkins master over TCP port 50000
      - 50000:50000
    environment:
      TZ: Asia/Shanghai
    volumes:
      - /root/jenkins:/var/jenkins_home
```

2. run follow command

   ```shell
   mkdir /root/jenkins
   sudo chown -R 1000:1000 /root/jenkins
   docker-compose up -d jenkins

   # stop jenkins and replace source
   cp /root/jenkins/updates/default.json /root/jenkins/updates/default.json.bak
   sed -i 's/http:\/\/updates.jenkins-ci.org\/download/https:\/\/mirrors.tuna.tsinghua.edu.cn\/jenkins/g' default.json && sed -i 's/http:\/\/www.google.com/https:\/\/www.baidu.com/g' default.json

   # open link and install plugins
   ```

3. replace source

   - 更换源 -> 系统管理 -> 管理插件 -> 高级 -> 升级站点
   - 把 `http://updates.jenkins-ci.org/update-center.json` 换成 `http://mirror.esuni.jp/jenkins/updates/update-center.json`
   - 镜像源查询 `http://mirrors.jenkins-ci.org/status.html`

4. common plugins

|     plugin name     |  description   |
| :-----------------: | :------------: |
|  Maven Integration  | maven 管理插件 |
| Deploy to container |  容器部署插件  |
|      Pipeline       |  管道集成插件  |
|   Email Extension   |  邮件通知插件  |
|         SSH         | 用于 ssh 通信  |

5. maven 项目

   - 新建 maven job
   - 配置 checkout 源码
   - 编写 maven 构建 命令
   - 自动部署至 Tomcat 配置, 且必须保留 manager 项目

     ```java
     <role rolename="admin-gui"/>
     <role rolename="manager-gui"/>
     <role rolename="manager-script"/>
     <user username="manager" password="manager" roles="manager-gui,manager-script"/>
     <user username="admin" password="admin" roles="admin-gui,manager-gui"/>
     ```

   - 存档配置: 构建后操作添加 Deploy war/ear to a container 项目

     ```xml
     <!-- maven setting: upload to nexus -->
     <server>
         <id>nexusReleases</id>
         <username>deployment</username>
         <password>deployment123</password>
     </server>
     ```

6. 参数化构建
7. workflow

   ![avatar](/static/image/common/cicd/jinekins-maven.png)

### pipeline

1. syntax

   - agent: 表示 Jenkins 应该为 Pipeline 的这一部分分配一个执行者和工作区
   - stage: 描述了这条 Pipeline 的一个阶段
   - steps: 描述了要在其中运行的步骤 stage
   - sh: 执行给定的 shell 命令
   - junit: 是由 JUnit 插件提供的 用于聚合测试报告的 Pipeline 步骤

2. sample: it can be maintain in git repo and config in jenkins

   ```js
   pipeline {
       agent any
       stages {
           stage('checkout') {
               steps {
                   echo 'checkout'
                   checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'gogs_luban', url: 'http://git.jiagouedu.com/java-vip/tuling-api-gateway']]])
               }
           }
           stage('build'){
               steps {
                   echo 'build'
                   sh 'mvn clean install'
               }
           }
           stage('save') {
               steps {
               echo 'save'
               archiveArtifacts 'target/*.war'
               }
           }
       }
   }
   ```
