# Этап сборки проекта в jar-файл
FROM maven:3.6.3-openjdk-17 as maven
RUN mkdir job4j_desc
WORKDIR job4j_desc
COPY . .
RUN mvn package -Dmaven.test.skip=true

# Этап запуска проекта
FROM openjdk:17.0.2-jdk
WORKDIR job4j_desc
COPY --from=maven /job4j_desc/target/desc-1.0.0.jar desc.jar
CMD ["java", "-jar", "desc.jar"]