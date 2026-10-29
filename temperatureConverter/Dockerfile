FROM maven:3.9-eclipse-temurin-21

RUN apt-get update && apt-get install -y --no-install-recommends \
    libgtk-3-0 libxtst6 libxrender1 libxi6 libgl1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

RUN mkdir -p /opt/sqlite-tmp && chmod 1777 /opt/sqlite-tmp
ENV JAVA_TOOL_OPTIONS="-Dorg.sqlite.tmpdir=/opt/sqlite-tmp"

COPY pom.xml .
COPY . .

RUN mvn -B -ntp clean test dependency:go-offline

CMD ["mvn", "-o", "javafx:run"]
