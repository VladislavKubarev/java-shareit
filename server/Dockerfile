FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar server_shareit.jar
ENTRYPOINT ["java","-jar","/server_shareit.jar"]