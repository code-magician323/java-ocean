FROM elasticsearch:7.6.2

LABEL zack=zzhang_xz@163.com
ARG VERSION=7.6.2
ENV CUSTOM_DIC_NAME=custom.dic

WORKDIR /usr/share/elasticsearch

ADD ./bin/docker-entrypoint.sh /usr/share/elasticsearch/bin/elasticsearch

# install wget for download ik
RUN set -x \
    && mkdir -p ./plugins/analysis-ik \
    && cd ./plugins/analysis-ik  \
    # && yum update -y \
    && yum -y install wget unzip \
    && wget -c https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v${VERSION}/elasticsearch-analysis-ik-${VERSION}.zip  \
    && unzip elasticsearch*.zip  \
    && touch ./config/$CUSTOM_DIC_NAME \
    && sed -i -e 's|<entry key="ext_dict"></entry>|<entry key="ext_dict">$CUSTOM_DIC_NAME</entry>|g' config/IKAnalyzer.cfg.xml \
    && rm elasticsearch*.zip \
    && yum autoremove -y wget \
    && yum autoremove -y unzip \
    && yum clean all \
    && cd /usr/share/elasticsearch

# RUN chmod +x /usr/share/elasticsearch/bin/docker-dic.sh
# ENTRYPOINT [ "/usr/share/elasticsearch/bin/docker-dic.sh"]

RUN chmod +777 /usr/share/elasticsearch/bin/elasticsearch
CMD ["/usr/share/elasticsearch/bin/elasticsearch"]
