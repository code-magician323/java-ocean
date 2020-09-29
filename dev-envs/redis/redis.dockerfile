FROM redis:5.0
ARG password=admin

COPY ["./conf/redis.conf", "/usr/local/etc/redis/redis.conf"]
COPY ["./conf/cluster.conf", "/usr/local/etc/redis/cluster.conf"]

RUN sed -i -e 's@cluster-enabled yes@# cluster-enabled yes@g' /usr/local/etc/redis/redis.conf
RUN sed -i -e 's@cluster-config-file nodes-6379.conf@# cluster-config-file nodes-6379.conf@g' /usr/local/etc/redis/redis.conf
RUN sed -i -e 's@cluster-node-timeout 5000@# cluster-node-timeout 5000@g' /usr/local/etc/redis/redis.conf
RUN sed -i -e "s@# masterauth <master-password>@masterauth $password@g" /usr/local/etc/redis/redis.conf

# RUN mkdir /logs && touch /logs/redis.log && chown redis:redis /logs && chown redis:redis /logs/redis.log
# RUN chmod -R 777 /logs
# VOLUME /logs

# set password
RUN echo "requirepass " $password >>/usr/local/etc/redis/redis.conf

CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]
