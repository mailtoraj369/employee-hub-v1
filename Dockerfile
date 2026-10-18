FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --chown=10001:10001 target/employee-hub-0.0.1-SNAPSHOT.jar  employee-hub-service.jar
COPY --chown=10001:10001 data/myAcmeEmployee.sqlite data/myAcmeEmployee.sqlite

ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 9090

USER 10001:10001

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "employee-hub-service.jar"]