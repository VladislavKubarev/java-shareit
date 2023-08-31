FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar gateway_shareit.jar
ENTRYPOINT ["java","-jar","/gateway_shareit.jar"]