# Docker Build and Deployment Guide

## Overview

This guide explains how to build and deploy the Room Info Service using Docker with support for multiple architectures (
AMD64 and ARM64).

## Files

- `Dockerfile` - Multi-stage Dockerfile for Spring Boot application
- `docker-build-push.sh` - Multi-architecture build and push script
- `docker-build-local.sh` - Local single-architecture build script
- `docker-compose.yml` - Complete stack for local development
- `.dockerignore` - Files to exclude from Docker build context

## Prerequisites

### Required

- Docker 20.10+ with buildx support
- Docker Hub account (for pushing images)
- Java 17+ and Gradle (for local builds)

### Check Docker buildx support

```bash
docker buildx version
```

If not available, update Docker or enable experimental features.

## Usage

### 1. Multi-Architecture Build and Push (Production)

Build for both AMD64 and ARM64 architectures and push to Docker Hub:

```bash
# Login to Docker Hub first
docker login

# Build and push with latest tag
./docker-build-push.sh

# Build and push with specific version tag
./docker-build-push.sh v1.0.0
```

This will:

- Build the Spring Boot application
- Create Docker images for linux/amd64 and linux/arm64
- Push to Docker Hub as `ddingsh9/roominfoserver:latest`

### 2. Local Build (Development)

For quick local testing with single architecture:

```bash
# Build local image
./docker-build-local.sh

# Run the container
docker run -p 8080:8080 ddingsh9/roominfoserver:local
```

### 3. Docker Compose Stack (Local Development)

Run the complete stack with PostgreSQL and Kafka:

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f room-info-service

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

Services available:

- Room Info Service: http://localhost:8080
- Kafka UI: http://localhost:8090
- PostgreSQL: localhost:5432
- Kafka: localhost:9092

## Architecture Support

The multi-architecture build creates images that work on:

- **linux/amd64**: Intel/AMD processors (most cloud servers, older Macs)
- **linux/arm64**: ARM processors (Apple Silicon Macs, AWS Graviton)

## Image Details

### Image Name

```
ddingsh9/roominfoserver:latest
```

### Pulling the Image

The correct architecture will be automatically selected:

```bash
# On Intel/AMD systems
docker pull ddingsh9/roominfoserver:latest  # pulls amd64

# On Apple Silicon/ARM systems
docker pull ddingsh9/roominfoserver:latest  # pulls arm64
```

### Verify Multi-Architecture Support

```bash
docker buildx imagetools inspect ddingsh9/roominfoserver:latest
```

## Environment Variables

Configure the application using environment variables:

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  ddingsh9/roominfoserver:latest
```

## Health Check

The container includes a health check endpoint:

```bash
curl http://localhost:8080/actuator/health
```

## Troubleshooting

### Buildx not found

```bash
# Enable buildx
docker buildx create --use

# Or install buildx plugin
docker run --privileged --rm tonistiigi/binfmt --install all
```

### Permission denied on scripts

```bash
chmod +x docker-build-push.sh
chmod +x docker-build-local.sh
```

### Out of memory during build

Increase Docker memory allocation in Docker Desktop settings or use:

```bash
docker buildx create --use --driver-opt env.BUILDKIT_STEP_LOG_MAX_SIZE=50000000
```

### Platform not supported

Ensure your Docker version supports multi-platform builds:

```bash
docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
```

## CI/CD Integration

### GitHub Actions Example

```yaml
- name: Build and Push Docker Image
  run: |
    echo "${{ secrets.DOCKER_PASSWORD }}" | docker login -u "${{ secrets.DOCKER_USERNAME }}" --password-stdin
    ./docker-build-push.sh ${{ github.ref_name }}
```

### GitLab CI Example

```yaml
docker-build:
  script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
    - ./docker-build-push.sh $CI_COMMIT_TAG
```

## Security Notes

1. The container runs as non-root user `spring` for security
2. Health checks are configured for container orchestration
3. JVM is configured for container memory limits
4. Use secrets management for sensitive environment variables

## Performance Tuning

JVM options are optimized for containers:

- `-XX:MaxRAMPercentage=75.0` - Use 75% of container memory
- `-XX:+UseContainerSupport` - Enable container-aware JVM settings

Adjust via JAVA_OPTS environment variable as needed.

## Support

For issues or questions:

1. Check container logs: `docker logs <container-id>`
2. Verify health: `docker exec <container-id> curl localhost:8080/actuator/health`
3. Review build output for errors
