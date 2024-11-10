# Этап сборки проекта в jar-файл
FROM maven:3.6.3-openjdk-17 as maven
RUN mkdir job4j_site
WORKDIR job4j_site
COPY . .
RUN mvn package -Dmaven.test.skip=true

# Этап запуска проекта
FROM openjdk:17.0.2-jdk
WORKDIR job4j_site
COPY --from=maven /job4j_site/target/site-1.0.0.jar site.jar
CMD ["java", "-jar", "site.jar"]