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
