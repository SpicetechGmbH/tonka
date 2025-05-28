# Stage 1: Build the frontend
FROM node:22 AS frontend-build
WORKDIR /frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm install
COPY frontend ./
RUN npm run build

# Stage 2: Build and run the backend
FROM maven:3.9.9-eclipse-temurin-21 AS backend-runtime
WORKDIR /backend

# Install PostgreSQL client tools for `pg_isready`
RUN apt-get update && apt-get install -y postgresql-client

# Copy the frontend build output
COPY --from=frontend-build /frontend/dist ./src/main/resources/static

# Copy the backend source code and Maven configuration
COPY backend/pom.xml .
COPY backend/src ./src
COPY backend/entrypoint.sh ./entrypoint.sh
RUN chmod +x entrypoint.sh

# Pass database connection details to Maven
ENV POSTGRES_URL=jdbc:postgresql://dts-os-db:5432/postgres
ENV POSTGRES_USER=user
ENV POSTGRES_PASSWORD=pass
ENV POSTGRES_HOST=dts-os-db
ENV POSTGRES_DB=postgres

EXPOSE 8080
ENTRYPOINT ["./entrypoint.sh"]
