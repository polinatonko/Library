FROM openjdk:21-jdk
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/library-0.0.1-SNAPSHOT.jar library.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar library.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar library.jar
