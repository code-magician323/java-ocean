FROM redis:5.0
ARG password=admin

COPY ["./conf/redis.conf", "/usr/local/etc/redis/redis.conf"]

RUN mkdir /logs && touch /logs/redis.log && chown redis:redis /logs && chown redis:redis /logs/redis.log
# RUN chmod -R 777 /logs
# VOLUME /logs

# set password
RUN echo "requirepass " $password >> /usr/local/etc/redis/redis.conf

CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]
