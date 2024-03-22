FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

COPY . .

RUN ./gradlew assemble

FROM openjdk:17-oracle

WORKDIR /app

COPY --from=builder /app/build/libs/postech-hackaton-apigateway-1.0.0.jar .

EXPOSE 8080

CMD ["java", "-jar", "postech-hackaton-apigateway-1.0.0.jar"]