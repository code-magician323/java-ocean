#!/bin/sh
#startup Server
RUN_CMD="java"

# 应用参数
RUN_CMD="$RUN_CMD -Dserver.port:8080"
RUN_CMD="$RUN_CMD -Dcsp.sentinel.dashboard.server=localhost:8080"
RUN_CMD="$RUN_CMD -Dproject.name=sentinel-dashboard"

RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD -jar"
RUN_CMD="$RUN_CMD sentinel-dashboard-\"$SENTINEL_VERSION\".jar"

echo $RUN_CMD
eval $RUN_CMD
