FROM java:openjdk-8-jdk
LABEL MAINTAINER="Mickael BARON"

ENV MAVEN_VERSION 3.3.9
RUN curl -fsSLk https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
	&& mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
	&& ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven

ADD pom.xml /work/pom.xml
WORKDIR /work
RUN ["mvn", "dependency:go-offline"]

ADD ["src", "/work/src"]
RUN ["mvn", "package"]

ENTRYPOINT ["java", "-cp", "target/classes:target/dependency/*", "fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice"]
CMD [localhost]

