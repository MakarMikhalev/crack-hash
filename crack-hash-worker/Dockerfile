FROM maven:3.8.1-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/crack-hash-worker-0.1.0.jar ./crack-hash-worker.jar

ENTRYPOINT ["java", "-jar", "/app/crack-hash-worker.jar"]
