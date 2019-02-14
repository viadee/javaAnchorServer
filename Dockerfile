FROM maven:3.6-jdk-8-alpine as build
WORKDIR /usr/javaAnchorServer
COPY . .
RUN mvn -B -e -T 1C verify -DskipTests

FROM openjdk:8-jre-alpine
RUN apk add --no-cache curl bash
WORKDIR /app
COPY --from=build /usr/javaAnchorServer/application/target/javaAnchorServer.jar .

ENV SERVER_PORT 5000
ENV SPARK_LIB_FOLDER "/spark"
ENV SPARK_MASTER_URL "spark://localhost:7077"

EXPOSE 5000

CMD ["java","-jar","javaAnchorServer.jar"]
