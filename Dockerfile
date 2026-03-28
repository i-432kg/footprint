# ---------- frontend build ----------
FROM node:20-alpine AS frontend-build
WORKDIR /frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# ---------- backend build ----------
FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# フロント build 成果物を Spring Boot の static 配下へ取り込む
COPY --from=frontend-build /frontend/dist/ ./src/main/resources/static/

RUN echo "=== frontend files copied into backend static ===" \
 && find ./src/main/resources/static -maxdepth 4 | sort \
 && echo "=== manifest file ===" \
 && ls -la ./src/main/resources/static/.vite \
 && cat ./src/main/resources/static/.vite/manifest.json

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test \
 && echo "=== built jars ===" \
 && ls -lh ./build/libs \
 && echo "=== inspect boot jar contents ===" \
 && mkdir -p /tmp/jar-check \
 && cd /tmp/jar-check \
 && cp /app/build/libs/*.jar ./app.jar \
 && java -Djarmode=tools -jar app.jar extract --destination extracted \
 && find extracted/BOOT-INF/classes/static -maxdepth 4 | sort

# ---------- runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=backend-build /app/build/libs/*.jar /tmp/
RUN sh -c 'cp $(find /tmp -maxdepth 1 -name "*.jar" ! -name "*-plain.jar" | head -n 1) /app/app.jar'

ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]