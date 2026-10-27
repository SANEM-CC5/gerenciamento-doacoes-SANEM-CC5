FROM eclipse-temurin:17-jdk

WORKDIR /app

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/javalovers-backend-1.0.0.jar"]
