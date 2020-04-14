FROM centos:7.5.1804
MAINTAINER pader "zzhang_xz@163.com"

# set environment
ENV MODE="cluster" \
    PREFER_HOST_MODE="ip"\
    BASE_DIR="/home/nacos" \
    CLASSPATH=".:/home/nacos/conf:$CLASSPATH" \
    CLUSTER_CONF="/home/nacos/conf/cluster.conf" \
    FUNCTION_MODE="all" \
    JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk" \
    NACOS_USER="nacos" \
    JAVA="/usr/lib/jvm/java-1.8.0-openjdk/bin/java" \
    JVM_XMS="2g" \
    JVM_XMX="2g" \
    JVM_XMN="1g" \
    JVM_MS="128m" \
    JVM_MMS="320m" \
    NACOS_DEBUG="n" \
    TOMCAT_ACCESSLOG_ENABLED="false" \
    TIME_ZONE="Asia/Shanghai" \
    MYSQL_ADRESS="101.132.45.28" \
    MYSQL_PORT="3306" \
    MYSQL_USERNAME="root" \
    MYSQL_PASSWORD="Yu1252068782?" \
    MYSQL_DATABASE="nacos_dev"

ARG NACOS_VERSION=1.1.4

WORKDIR /$BASE_DIR

RUN set -x \
    && yum update -y \
    && yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel wget iputils nc  vim libcurl\
    && wget  https://github.com/alibaba/nacos/releases/download/${NACOS_VERSION}/nacos-server-${NACOS_VERSION}.tar.gz -P /home \
    && tar -xzvf /home/nacos-server-${NACOS_VERSION}.tar.gz -C /home \
    && rm -rf /home/nacos-server-${NACOS_VERSION}.tar.gz /home/nacos/bin/* /home/nacos/conf/*.properties /home/nacos/conf/*.example /home/nacos/conf/nacos-mysql.sql \
    && yum autoremove -y wget \
    && ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo '$TIME_ZONE' > /etc/timezone \
    && yum clean all

ADD bin/docker-startup.sh bin/docker-startup.sh
ADD conf/application.properties conf/application.properties
ADD init.d/custom.properties init.d/custom.properties


# db.num=1
# db.url.0=jdbc:mysql://101.132.45.28:3306/nacos_dev?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
# db.user=root
# db.password=Yu1252068782?
# update database info
RUN sed -i -e 's|101.132.45.28:3306|$MYSQL_ADRESS:$MYSQL_PORT|g' -e 's|nacos_dev|$MYSQL_DATABASE|g' -e 's|db.user=root|db.user=$MYSQL_USERNAME|g' -e 's|db.password=Yu1252068782?|db.password=$MYSQL_PASSWORD|g' init.d/custom.properties

# set startup log dir
RUN mkdir -p logs \
	&& cd logs \
	&& touch start.out \
	&& ln -sf /dev/stdout start.out \
	&& ln -sf /dev/stderr start.out
RUN chmod +x bin/docker-startup.sh

EXPOSE 8848
ENTRYPOINT ["bin/docker-startup.sh"]
