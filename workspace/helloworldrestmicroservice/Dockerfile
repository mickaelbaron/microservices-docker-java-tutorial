FROM openjdk:11-slim
LABEL MAINTAINER="Mickael BARON"

ENV MAVEN_VERSION 3.8.5
RUN apt-get update -y && apt-get install -y curl && curl -fsSLk https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
	&& mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
	&& ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven

ADD pom.xml /work/pom.xml
WORKDIR /work
RUN ["mvn", "dependency:go-offline"]

ADD ["src", "/work/src"]
RUN ["mvn", "package"]

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "target/classes:target/dependency/*", "com.kumuluz.ee.EeApplication"]
