FROM openjdk:8-jre

ARG JVM_OPTS=-Xmx1024M
ARG REV=1.8

RUN apt-get update && apt-get install -y git

RUN mkdir /build/ && \
    cd build && \
    wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar && \
    java $JVM_OPTS -jar BuildTools.jar --rev $REV && \
    mkdir /minecraft && \
    cp Spigot/Spigot-Server/target/spigot-*.jar /minecraft/spigot.jar && \
    cd /minecraft

COPY minecraft /minecraft
WORKDIR /minecraft

RUN mkdir /minecraft/plugins

VOLUME /minecraft/plugins
VOLUME /minecraft/world
VOLUME /minecraft/world_nether
VOLUME /minecraft/world_the_end

EXPOSE 25565

CMD java -Xmx2048M -jar spigot.jar
