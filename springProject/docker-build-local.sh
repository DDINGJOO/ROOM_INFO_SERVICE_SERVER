#!/bin/bash

# Local Docker Build Script (single architecture for testing)

set -e

# Configuration
IMAGE_NAME="ddingsh9/roominfoserver"
TAG="local"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}[INFO]${NC} Building Docker image locally: ${IMAGE_NAME}:${TAG}"

# Clean and build the application
echo -e "${GREEN}[INFO]${NC} Cleaning and building Spring Boot application..."
./gradlew clean build -x test

# Build Docker image for local architecture only
echo -e "${GREEN}[INFO]${NC} Building Docker image for local architecture..."
docker build -t ${IMAGE_NAME}:${TAG} .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}[SUCCESS]${NC} Successfully built ${IMAGE_NAME}:${TAG}"
    echo -e "${GREEN}[INFO]${NC} To run the container:"
    echo "  docker run -p 8080:8080 ${IMAGE_NAME}:${TAG}"
else
    echo -e "${RED}[ERROR]${NC} Failed to build Docker image"
    exit 1
fi