FROM eclipse-temurin:11 as Builder
WORKDIR source
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} article-0.0.1-SNAPSHOT.jar
RUN java -Djarmode=layertools -jar article-0.0.1-SNAPSHOT.jar extract

FROM eclipse-temurin:11
WORKDIR app
COPY --from=builder source/dependencies/ ./
COPY --from=builder source/spring-boot-loader/ ./
COPY --from=builder source/snapshot-dependencies/ ./
COPY --from=builder source/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
