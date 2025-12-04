# Build env
FROM maven:3.9.11-eclipse-temurin-11 AS build-java-stage
LABEL MAINTAINER="Mickael BARON"

COPY pom.xml /work/pom.xml
WORKDIR /work
RUN ["mvn", "dependency:go-offline"]

COPY src /work/src
RUN ["mvn", "package"]

# Run env
FROM eclipse-temurin:25-jre-alpine
COPY --from=build-java-stage /work/target/classes /classes/
COPY --from=build-java-stage /work/target/dependency/*.jar /dependency/

ENTRYPOINT ["java", "-cp", "classes:dependency/*", "fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice"]
CMD ["localhost"]