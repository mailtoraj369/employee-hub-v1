FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/employee-hub-0.0.1-SNAPSHOT.jar employee-hub.jar
COPY data/myAcmeEmployee.sqlite ./data/myAcmeEmployee.sqlite

ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "employee-hub.jar"]
