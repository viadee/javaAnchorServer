FROM maven:3.6-jdk-8-alpine as build
WORKDIR /usr/javaAnchorServer
COPY . .
RUN mvn -B -e -T 1C verify -DskipTests

FROM openjdk:8
COPY --from=build /usr/javaAnchorServer/application/target/*SNAPSHOT.jar ./
CMD ["java","jar","/usr/javaAnchorServer/application/target/*SNAPSHOT.jar"]
