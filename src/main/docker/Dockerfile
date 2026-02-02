FROM quay.io/quarkus/ubi9-quarkus-graalvmce-builder-image:jdk-21
USER root

ARG MAVEN_VERSION
WORKDIR /project

RUN  curl -L -o mvn.tar.gz https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    mkdir mvn && tar -xvzf mvn.tar.gz -C mvn --strip-components 1

COPY --chown=quarkus:quarkus pom.xml .
COPY --chown=quarkus:quarkus src ./src

ENV PATH="/musl/bin:/project/mvn/bin:${PATH}"
RUN mvn package -Dnative -DskipTests
