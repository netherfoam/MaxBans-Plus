# Server Samples

## Purpose
This provides Dockerised Bukkit containers to test the plugin against. These are manually run, and are not part of any
automated integration test suite.

## Running
* Install Docker and Docker-Compose
* Run `mvn clean package`
* Copy `target/maxbans-plus-*.jar` to `server/plugins/`
* Run the docker-compose.yml files with `docker-compose -f spigot-1.8.yml up --build`
* Connect to `localhost:25565` in Minecraft
