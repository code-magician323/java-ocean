## install

```shell
# method 01: it is slow
sudo curl -L https://get.daocloud.io/docker/compose/releases/download/1.25.1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# method 02: it can be install by pip
pip install --upgrade pip
pip install --upgrade pip
pip uninstall docker-compose
```

## **Config**

- Dockerfile

  - The config which contains the Commands to build docker image

- DockerCompose
  - Define a process to build the docker services, and the config of these docker services

## **Component**

- Docker Image

  - The template of docker container, it is the docker container's set of file

- Docker Container

  - The instance of docker image, which can be run or stop

- Docker Repository
  - The repository of docker images, such as docker hub, we can upload, download images from docker repository

## **Common Command**

- docker images: show the list of local images
- docker build: build docker image
- docker tag: tag the docker images, help to manage images
- docker rmi <imageId/imageName>: delete the dock image
- docker ps: show the list of running docker containers, use 'docker ps -a' to show the list of all the docer contains
- docker start/stop/restart <containerId/containerName>: start/stop/restart container
- docker rm <containerId/containerName>: delete docker container
- docker-compose up <service name>: build and start the docker services
- docker-compose down <service name>: stop and remove the docker services
- docker-compose start <service name>: start the docker services
- docker-compose stop <service name>: stop the docker services

---

## reference

1. [docker-compose](https://docs.docker.com/compose/install/)
2. [version](https://docs.docker.com/compose/compose-file/compose-versioning/)
