FROM openjdk:8-jre

ARG JVM_OPTS=-Xmx1024M

RUN apt-get update && apt-get install -y git

RUN mkdir /build/ && \
    cd build && \
    mkdir /minecraft && \
    wget https://papermc.io/api/v1/paper/1.8.8/443/download -O /minecraft/paper-mc.jar && \
    cd /minecraft

COPY minecraft /minecraft
WORKDIR /minecraft

RUN mkdir /minecraft/plugins

VOLUME /minecraft/plugins
VOLUME /minecraft/world
VOLUME /minecraft/world_nether
VOLUME /minecraft/world_the_end

EXPOSE 25565

CMD java -Xmx2048M -jar paper-mc.jar
