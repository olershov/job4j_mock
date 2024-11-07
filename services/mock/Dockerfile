# Этап сборки проекта в jar-файл
FROM maven:3.6.3-openjdk-17 as maven
RUN mkdir job4j_mock
WORKDIR job4j_mock
COPY . .
RUN mvn package -Dmaven.test.skip=true

# Этап запуска проекта
FROM openjdk:17.0.2-jdk
WORKDIR job4j_mock
COPY --from=maven /job4j_mock/target/mock-1.0.0.jar mock.jar
CMD ["java", "-jar", "mock.jar"]