FROM maven:3.6-jdk-8-alpine as build
WORKDIR /usr/javaAnchorServer
COPY . .
RUN mvn -B -e -T 1C verify

FROM java:8-jre-alpine
WORKDIR /app
COPY --from=build /usr/javaAnchorServer/application/target/*SNAPSHOT.jar ./
CMD ["java","jar","./*SNAPSHOT.jar"]
