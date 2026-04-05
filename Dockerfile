# ---------- frontend build ----------
FROM node:20-alpine AS frontend-build
WORKDIR /frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./

# Railway のサービス変数を build 時に使うため ARG として宣言
# 未指定時は本番用 build を実行
ARG FRONTEND_BUILD_SCRIPT=build
RUN npm run ${FRONTEND_BUILD_SCRIPT}

# ---------- backend build ----------
FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# フロント build 成果物を Spring Boot の static 配下へ取り込む
COPY --from=frontend-build /frontend/dist/ ./src/main/resources/static/

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# ---------- runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=backend-build /app/build/libs/*.jar /tmp/
RUN sh -c 'cp $(find /tmp -maxdepth 1 -name "*.jar" ! -name "*-plain.jar" | head -n 1) /app/app.jar'

ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]