# Stage 1: build the jar using Maven + JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first so Docker can cache this layer between builds
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: run the jar on a lightweight JRE-only image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/expense-splitter-1.0.0.jar app.jar

# Render sets $PORT automatically; make Spring Boot listen on it
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]