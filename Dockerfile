FROM maven:3.9.16-eclipse-temurin-17-alpine AS builder

WORKDIR /healthconnect-cloud-platform

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests 

FROM eclipse-temurin:17-jre-alpine

WORKDIR /healthconnect-cloud-platform

COPY --from=builder /healthconnect-cloud-platform/target/*.jar healthconnect-cloud-platform.jar

EXPOSE 8081

CMD ["java", "-jar", "healthconnect-cloud-platform.jar"]