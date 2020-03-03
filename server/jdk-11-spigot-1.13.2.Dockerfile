FROM openjdk:11-jre

ARG JVM_OPTS="-Xmx1024M"

RUN apt-get update && apt-get install -y git

RUN mkdir /build/ && \
    cd build && \
    wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar && \
    java $JVM_OPTS -jar BuildTools.jar --rev 1.13.2 && \
    mkdir /minecraft && \
    cp Spigot/Spigot-Server/target/spigot-*.jar /minecraft/spigot.jar && \
    cd /minecraft

COPY minecraft /minecraft
WORKDIR /minecraft
RUN chmod +x start.sh

RUN mkdir /minecraft/plugins

VOLUME "/minecraft/plugins"

EXPOSE 25565

CMD ["java", "-Xmx2048M", "-jar", "spigot.jar"]
