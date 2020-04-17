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
RUN echo 'logfile "/logs/redis.log"' >> /usr/local/etc/redis/redis.conf

RUN mkdir /logs && touch /logs/redis.log && chown redis:redis /logs && chown redis:redis /logs/redis.log
# RUN chmod -R 777 /logs
# VOLUME /logs

CMD ["redis-server", "/usr/local/etc/redis/redis.conf", "--appendonly yes"]



## VERSION 2
# FROM debian:buster-slim

# # add our user and group first to make sure their IDs get assigned consistently, regardless of whatever dependencies get added
# RUN groupadd -r -g 999 redis && useradd -r -g redis -u 999 redis

# # grab gosu for easy step-down from root
# # https://github.com/tianon/gosu/releases
# ENV GOSU_VERSION 1.11
# RUN set -eux; \
# # save list of currently installed packages for later so we can clean up
#     savedAptMark="$(apt-mark showmanual)"; \
#     apt-get update; \
#     apt-get install -y --no-install-recommends \
#         ca-certificates \
#         dirmngr \
#         gnupg \
#         wget \
#     ; \
#     rm -rf /var/lib/apt/lists/*; \
#     \
#     dpkgArch="$(dpkg --print-architecture | awk -F- '{ print $NF }')"; \
#     wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch"; \
#     wget -O /usr/local/bin/gosu.asc "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch.asc"; \
#     \
# # verify the signature
#     export GNUPGHOME="$(mktemp -d)"; \
#     gpg --batch --keyserver hkps://keys.openpgp.org --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4; \
#     gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu; \
#     gpgconf --kill all; \
#     rm -rf "$GNUPGHOME" /usr/local/bin/gosu.asc; \
#     \
# # clean up fetch dependencies
#     apt-mark auto '.*' > /dev/null; \
#     [ -z "$savedAptMark" ] || apt-mark manual $savedAptMark > /dev/null; \
#     apt-get purge -y --auto-remove -o APT::AutoRemove::RecommendsImportant=false; \
#     \
#     chmod +x /usr/local/bin/gosu; \
# # verify that the binary works
#     gosu --version; \
#     gosu nobody true

# ENV REDIS_VERSION 5.0.8
# ENV REDIS_DOWNLOAD_URL http://download.redis.io/releases/redis-5.0.8.tar.gz
# ENV REDIS_DOWNLOAD_SHA f3c7eac42f433326a8d981b50dba0169fdfaf46abb23fcda2f933a7552ee4ed7

# RUN set -eux; \
#     \
#     savedAptMark="$(apt-mark showmanual)"; \
#     apt-get update; \
#     apt-get install -y --no-install-recommends \
#         ca-certificates \
#         wget \
#         \
#         gcc \
#         libc6-dev \
#         make \
#     ; \
#     rm -rf /var/lib/apt/lists/*; \
#     \
#     wget -O redis.tar.gz "$REDIS_DOWNLOAD_URL"; \
#     echo "$REDIS_DOWNLOAD_SHA *redis.tar.gz" | sha256sum -c -; \
#     mkdir -p /usr/src/redis; \
#     tar -xzf redis.tar.gz -C /usr/src/redis --strip-components=1; \
#     rm redis.tar.gz; \
#     \
# # disable Redis protected mode [1] as it is unnecessary in context of Docker
# # (ports are not automatically exposed when running inside Docker, but rather explicitly by specifying -p / -P)
# # [1]: https://github.com/antirez/redis/commit/edd4d555df57dc84265fdfb4ef59a4678832f6da
#     grep -q '^#define CONFIG_DEFAULT_PROTECTED_MODE 1$' /usr/src/redis/src/server.h; \
#     sed -ri 's!^(#define CONFIG_DEFAULT_PROTECTED_MODE) 1$!\1 0!' /usr/src/redis/src/server.h; \
#     grep -q '^#define CONFIG_DEFAULT_PROTECTED_MODE 0$' /usr/src/redis/src/server.h; \
# # for future reference, we modify this directly in the source instead of just supplying a default configuration flag because apparently "if you specify any argument to redis-server, [it assumes] you are going to specify everything"
# # see also https://github.com/docker-library/redis/issues/4#issuecomment-50780840
# # (more exactly, this makes sure the default behavior of "save on SIGTERM" stays functional by default)
#     \
#     make -C /usr/src/redis -j "$(nproc)" all; \
#     make -C /usr/src/redis install; \
#     \
# # TODO https://github.com/antirez/redis/pull/3494 (deduplicate "redis-server" copies)
#     serverMd5="$(md5sum /usr/local/bin/redis-server | cut -d' ' -f1)"; export serverMd5; \
#     find /usr/local/bin/redis* -maxdepth 0 \
#         -type f -not -name redis-server \
#         -exec sh -eux -c ' \
#             md5="$(md5sum "$1" | cut -d" " -f1)"; \
#             test "$md5" = "$serverMd5"; \
#         ' -- '{}' ';' \
#         -exec ln -svfT 'redis-server' '{}' ';' \
#     ; \
#     \
#     rm -r /usr/src/redis; \
#     \
#     apt-mark auto '.*' > /dev/null; \
#     [ -z "$savedAptMark" ] || apt-mark manual $savedAptMark > /dev/null; \
#     find /usr/local -type f -executable -exec ldd '{}' ';' \
#         | awk '/=>/ { print $(NF-1) }' \
#         | sort -u \
#         | xargs -r dpkg-query --search \
#         | cut -d: -f1 \
#         | sort -u \
#         | xargs -r apt-mark manual \
#     ; \
#     apt-get purge -y --auto-remove -o APT::AutoRemove::RecommendsImportant=false; \
#     \
#     redis-cli --version; \
#     redis-server --version

# RUN mkdir /data && chown redis:redis /data
# VOLUME /data
# WORKDIR /data

# COPY ./bin/docker-entrypoint.sh /usr/local/bin/
# ENTRYPOINT ["docker-entrypoint.sh"]

# EXPOSE 6379

# # custom config file
# COPY ["./redis.conf", "/usr/local/etc/redis/redis.conf"]

# # disable daemonize
# RUN sed -i -e 's@daemonize yes@daemonize no@g' /usr/local/etc/redis/redis.conf

# # close protect mode
# RUN sed -i -e 's@protected-mode yes@protected-mode no@g' /usr/local/etc/redis/redis.conf

# # set password
# RUN echo "requirepass Yu1252068782?" >> /usr/local/etc/redis/redis.conf

# CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]