FROM java:alpine
MAINTAINER zack "zzhang_xz@163.com"

# set environment
ENV SENTINEL_BASE_DIR="/home/sentinel" \
    JAVA_OPTS="-server -Xms512m -Xmx512m -XX:+AggressiveOpts -XX:MaxDirectMemorySize=512m" \
    # TODO: make this configable
    # JVM_XMS="2g" \
    # JVM_XMX="2g" \
    # JVM_XMN="1g" \
    # JVM_MS="128m" \
    # JVM_MMS="320m" \
    TIME_ZONE="Asia/Shanghai" \
    SENTINEL_VERSION="1.7.0"

ARG SENTINEL_DASHBOARD_VERSION=$SENTINEL_VERSION

WORKDIR /$SENTINEL_BASE_DIR

RUN set -x \
    && apk --no-cache add ca-certificates wget \
    && update-ca-certificates \
    && wget https://github.com/alibaba/Sentinel/releases/download/${SENTINEL_DASHBOARD_VERSION}/sentinel-dashboard-${SENTINEL_DASHBOARD_VERSION}.jar -P $SENTINEL_BASE_DIR \
    && ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo '$TIME_ZONE' > /etc/timezone

ADD bin/docker-sentinel-entrypoint.sh bin/docker-sentinel-entrypoint.sh

# set startup log dir
RUN mkdir -p logs/sentinel \
	&& cd logs/sentinel \
	&& touch start.out \
	&& ln -sf /dev/stdout start.out \
	&& ln -sf /dev/stderr start.out
RUN chmod +x bin/docker-sentinel-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["bin/docker-sentinel-entrypoint.sh"]
