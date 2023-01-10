FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} shop.jar
ENTRYPOINT ["java","-jar","/shop.jar"]