FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/rokue-like-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# we may uncomment if a port would be necessary
# EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]