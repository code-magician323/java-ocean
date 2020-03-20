FROM redis:5.0

COPY ["./redis.conf", "/usr/local/etc/redis/redis.conf"]

# change ip bind
# RUN sed -i -e 's@bind 127.0.0.1@bind 0.0.0.0@g' /usr/local/etc/redis/redis.conf

# disable daemonize
RUN sed -i -e 's@daemonize yes@daemonize no@g' /usr/local/etc/redis/redis.conf

# close protect mode
RUN sed -i -e 's@protected-mode yes@protected-mode no@g' /usr/local/etc/redis/redis.conf

# set password
RUN echo "requirepass Yu1252068782?" >> /usr/local/etc/redis/redis.conf

RUN mkdir /logs && chown redis:redis /logs

ENTRYPOINT [ "redis-server","/usr/local/etc/redis/redis.conf"]
CMD []

# build in cmd and in /redis
# docker build -f redis.dockerfile -t dev_redis:5.0 .
