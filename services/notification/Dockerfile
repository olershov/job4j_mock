# Этап сборки проекта в jar-файл
FROM maven:3.6.3-openjdk-17 as maven
RUN mkdir job4j_notification
WORKDIR job4j_notification
COPY . .
RUN mvn package -Dmaven.test.skip=true

# Этап запуска проекта
FROM openjdk:17.0.2-jdk
WORKDIR job4j_notification
COPY --from=maven /job4j_notification/target/notification-1.0.0.jar notification.jar
CMD ["java", "-jar", "notification.jar"]