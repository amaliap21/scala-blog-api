# Stage 1: Build aplikasi
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.8.0_2.13.10 AS builder
WORKDIR /app
COPY . .
RUN sbt stage

# Stage 2: Run aplikasi
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/universal/stage .
EXPOSE 8080
CMD ["./bin/scala-blog-api"]