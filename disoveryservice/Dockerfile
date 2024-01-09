FROM openjdk:17-ea-jdk-slim
VOLUME /tmp
COPY target/discoveryservice-1.0.jar DiscoveryService.jar
ENTRYPOINT ["java", "-jar", "DiscoveryService.jar"]