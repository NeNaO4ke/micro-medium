FROM eclipse-temurin:11 as Builder
WORKDIR source
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} gateway-0.0.1-SNAPSHOT.jar
EXPOSE 8080
RUN java -Djarmode=layertools -jar gateway-0.0.1-SNAPSHOT.jar extract

FROM eclipse-temurin:11
WORKDIR app
COPY --from=builder source/dependencies/ ./
COPY --from=builder source/spring-boot-loader/ ./
COPY --from=builder source/snapshot-dependencies/ ./
COPY --from=builder source/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
