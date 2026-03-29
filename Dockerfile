#Fase de contrsuccion
FROM maven:3.8.6-eclipse-temurin-17 as builder 

#Copiar codigo fuente y manifiesto pom.xml
WORKDIR /code
COPY src ./src
COPY pom.xml .

# Construir paquete
RUN mvn package -DskipTests

FROM eclipse-temurin:17
USER root
RUN ln -sf /usr/share/zoneinfo/America/Mexico_City /etc/localtime

ARG PORT_SERVER
ENV PORT_SERVER ${PORT_SERVER:-8090}

#BASE DE DATOS*********************************************************************
ARG DB_AIT_JDBC
ENV DB_AIT_JDBC ${DB_AIT_JDBC:-jdbc:postgresql://localhost:5432/bd}

ARG DB_AIT_USR
ENV DB_AIT_USR ${DB_AIT_USR:-dbteuser}

ARG DB_AIT_PASS
ENV DB_AIT_PASS ${DB_AIT_PASS:-dbtepass}

#VOLUME
ARG STORAGE_PATH
ENV STORAGE_PATH ${STORAGE_PATH:-/app}
RUN mkdir -p $STORAGE_PATH
VOLUME $STORAGE_PATH

#ENV JAR_FILE ${NAME_APLICATION}.jar
ENV JAR_FILE ait-deliveri.jar

COPY --from=builder /code/target/$JAR_FILE .

ENTRYPOINT [ "sh", "-c"]
CMD [ "java -jar $JAR_FILE" ]