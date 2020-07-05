## DockerFile: Based On CentOS7

### 1. introduce

1. function: series script command to build Docker image
2. folw: DockerFile -- docker build -- docker run
3. commands are executed from top to bottom orderly

### 2. keyword

1. `FROM` - based image[scratch]
2. `MAINTAINER` - mirror maintainer
3. `RUN` - command to run when the container is built

   ```shell
   RUN yum -y install vim
   RUN yum -y install net-tools
   ```

4. `EXPOSE` - expose port
5. `WORKDIR` - enter container default working directory
6. `ENV` - set enviromrnt variables

   - 通过 ENV 定义的环境变量不能被 `CMD` 指令使用, 也不能被 `docker run` 的命令参数引用
   - 在 docker run 命令中通过 -e 标记来传递环境变量, 这样容器运行时就可以使用该变量

   ```shell
   ENV MY_PATH /usr/mytest
   ```

7. `ADD` - copy host directory into container, and automatically process URL and decompress tar archive
8. `COPY` - function as `ADD`, copy files from build context to target mirror of the new layer

   ```shell
   COPY src dest
   COPY ["src", "dest"]
   ```

9. `VOLUME` - `volume container` for data preservation and persistence
10. `CMD` - specify a command to run when the container starts, and only last `CMD is worked`, **`do overwrite last command`**

    ```shell
    # 1. shell
    CMD SPECIFY_COMMAND
    # 2. exec
    CMD RUNNABLE_FILE ARGS1 ARGS1 ...

    # 3. test
    CMD echo "test CMD"/$VAR
    ```

11. `ENTRYPOINT` - function as `COPY`, **`no overwrite last command`**
12. `ONBUILD` - run this command when building an inherited Dockerfile **`[FROM PARENT_IMAGE]`** and the parent image's onbuild is triggered

    ```shell
    # 1. this image must be exist
    FROM FATHER_IMAGE
    # 2. this command will triger FATHER_IMAGE build
    ONBUILD RUN echo "build father image"
    ```

13. conlusion
    ![avatar](/static/image/container/docker-file.png)

14. diff between `CMD` and `ENTRYPOINT`

    ```shell
    ## CDM
    # 1. customize ip DockerFile
    FROM centos
    RUN yum install -y curl
    CMD ["curl", "-s", "http://ip.cn"]
    # 2. build
    docker build -f DOCKERFILE_LOCATION -t NEW_CONTAINER_ID:VTAG .
    # 3. run
    docker run -it CONTAINER_CUSTOM_IP # ok
    docker run -it CONTAINER_CUSTOM_IP -i # error, [-i] will overwrite CMD ["curl", "-s", "http://ip.cn"]

    ## ENTRYPOINT
    # 1. optimization
    FROM centos
    RUN yum install -y curl
    ENTRYPOINT ["curl", "-s", "http://ip.cn"]
    # 2. build
    docker build -f DOCKERFILE_LOCATION -t NEW_CONTAINER_ID:VTAG .
    # 3. run
    docker run -it CONTAINER_CUSTOM_IP # ok
    docker run -it CONTAINER_CUSTOM_IP -i # ok, do not overwrite, will be appended
    ```

### 3. build folw

1. docker runs a container from the base image
2. execute an command and make changes to the container
3. execute docker commit to submit a new mirror layer
4. docker runs a new container based on the image just submitted
5. each command will create a new image layer and submit the image

6. syntax

   ```shell
   # 1. image build
   docker build -f DOCKERFILE_LOCATION -t NEW_CONTAINER_ID:VTAG . # . means current directory
   # 2. check history
   docker hsitory CONTAINER_ID
   ```

7. custom centos

   ```shell
   # 1. customize DockerFile content
   FROM centos
   MAINTAINER zack<zzhang_xz@163.com>

   ENV WORKPATH /usr/local
   WORKDIR $WORKPATH

   RUN yum -y install net-tools

   EXPOSE 80
   CMD echo $WORKPATH
   CMD /bin/bash

   # 2. build
   docker build -f DOCKERFILE_LOCATION -t NEW_CONTAINER_ID:VTAG .
   ```

### 4. conlusion

- diagram
  ![avatar](/static/image/container/docker-build.png)

---

## sample

1. tomcat

   ```shell
   # 1. Dockfile content
   FROM         centos
   MAINTAINER    zack<zzhang_xz@163.com>
   # copy host c.txt to container and rename to cincontainer.txt
   COPY c.txt /usr/local/cincontainer.txt
   # copy host file to container and decompress
   ADD jdk-8u171-linux-x64.tar.gz /usr/local/
   ADD apache-tomcat-9.0.8.tar.gz /usr/local/
   # install vim
   RUN yum -y install vim
   # set WORKDIR
   ENV MYPATH /usr/local
   WORKDIR $MYPATH
   # set PATH
   ENV JAVA_HOME /usr/local/jdk1.8.0_171
   ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
   ENV CATALINA_HOME /usr/local/apache-tomcat-9.0.8
   ENV CATALINA_BASE /usr/local/apache-tomcat-9.0.8
   ENV PATH $PATH:$JAVA_HOME/bin:$CATALINA_HOME/lib:$CATALINA_HOME/bin
   # expose port
   EXPOSE  8080
   # start tomcat
   # ENTRYPOINT ["/usr/local/apache-tomcat-9.0.8/bin/startup.sh" ]
   # CMD ["/usr/local/apache-tomcat-9.0.8/bin/catalina.sh","run"]
   CMD /usr/local/apache-tomcat-9.0.8/bin/startup.sh && tail -F /usr/local/apache-tomcat-9.0.8/bin/logs/catalina.out

   # 2. docker build
   docker build -f DOCKERFILE_LOCATION -t custom-tomcat-image .

   # 3. run
   docker run -d -p 9080:8080 --name custom-tomcat -v /root/tomcat9/test:/usr/local/apache-tomcat-9.0.8/webapps/test -v /root/tomcat9/tomcat9logs/:/usr/local/apache-tomcat-9.0.8/logs --privileged=true custom-tomcat-image
   ```

2. wso2is

   ```shell
   FROM velo/base-jdk8

   # set Docker image build arguments
   # build arguments for user/group configurations
   ARG USER=wso2carbon
   ARG USER_ID=802
   ARG USER_GROUP=wso2
   ARG USER_GROUP_ID=802
   ARG USER_HOME=/home/${USER}
   # build arguments for WSO2 product installation
   ARG WSO2_SERVER_NAME=wso2is
   ARG WSO2_SERVER_VERSION=5.8.0
   ARG WSO2_SERVER=${WSO2_SERVER_NAME}-${WSO2_SERVER_VERSION}
   ARG WSO2_SERVER_HOME=${USER_HOME}/${WSO2_SERVER}
   ARG CAMP_BUNDLE_URL=http://localhost

   ARG WSO2_SERVER_DIST_URL=${CAMP_BUNDLE_URL}/${WSO2_SERVER}.zip

   # create the non-root user and group and set MOTD login message
   RUN \
       groupadd --system -g ${USER_GROUP_ID} ${USER_GROUP} \
       && useradd --system --create-home --home-dir ${USER_HOME} --no-log-init -g ${USER_GROUP_ID} -u ${USER_ID} ${USER}

   # create Java prefs dir
   # this is to avoid warning logs printed by FileSystemPreferences class
   RUN \
       apt-get update \
       && apt-get install sudo \
       && mkdir -p ${USER_HOME}/.java/.systemPrefs \
       && mkdir -p ${USER_HOME}/.java/.userPrefs \
       && chmod -R 755 ${USER_HOME}/.java \
       && sudo chown -R ${USER}:${USER_GROUP} ${USER_HOME}/.java

   # copy init script to user home
   # COPY --chown=wso2carbon:wso2 bin/docker-entrypoint.sh ${USER_HOME}/
   COPY bin/docker-entrypoint.sh /home/wso2carbon
   RUN chown -R wso2carbon:wso2 /home/wso2carbon

   # add the WSO2 product distribution to user's home directory
   RUN \
       wget --no-check-certificate -O ${WSO2_SERVER}.zip "${WSO2_SERVER_DIST_URL}" \
       && unzip -d ${USER_HOME} ${WSO2_SERVER}.zip \
       && chown wso2carbon:wso2 -R ${WSO2_SERVER_HOME} \
       && rm -rf ${WSO2_SERVER_HOME}/jdk \
       && rm -f ${WSO2_SERVER}.zip

   # set the user and work directory
   USER ${USER_ID}
   WORKDIR ${USER_HOME}

   # set environment variables
   ENV JAVA_OPTS="-Djava.util.prefs.systemRoot=${USER_HOME}/.java -Djava.util.prefs.userRoot=${USER_HOME}/.java/.userPrefs" \
       WORKING_DIRECTORY=${USER_HOME} \
       WSO2_SERVER_HOME=${WSO2_SERVER_HOME}

   # expose ports
   EXPOSE 4000 9763 9443

   # initiate container and start WSO2 Carbon server
   ENTRYPOINT ["/home/wso2carbon/docker-entrypoint.sh"]
   ```

## reference

1. [dockerfile how to use `docker -e` arg](https://segmentfault.com/a/1190000007271728)
2. [dockerfile env](https://www.cnblogs.com/Json1208/p/8974978.html)
3. [dockerfile network](https://blog.csdn.net/qq_41822345/article/details/107123141)
4. [docker jmap](https://www.cnblogs.com/qmfsun/p/10858473.html)
