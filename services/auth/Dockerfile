# Этап сборки проекта в jar-файл
FROM maven:3.6.3-openjdk-17 as maven
RUN mkdir job4j_auth
WORKDIR job4j_auth
COPY . .
RUN mvn package -Dmaven.test.skip=true

# Этап запуска проекта
FROM openjdk:17.0.2-jdk
WORKDIR job4j_auth
COPY --from=maven /job4j_auth/target/auth-1.0.0.jar auth.jar
CMD ["java", "-jar", "auth.jar"]