FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw clean package -DskipTests -B

EXPOSE 8080

ENTRYPOINT ["sh","-c","java -jar target/*.jar"]