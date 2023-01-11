FROM openjdk:17-jre
COPY build/libs/shop-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]