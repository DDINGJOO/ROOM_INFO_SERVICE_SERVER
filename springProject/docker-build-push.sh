#!/bin/bash

# Docker Multi-Architecture Build and Push Script
# Supports: linux/amd64, linux/arm64

set -e

# Configuration
IMAGE_NAME="ddingsh9/roominfoserver"
TAG="latest"
PLATFORMS="linux/amd64,linux/arm64"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Functions
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

print_info "Starting multi-architecture Docker build for ${IMAGE_NAME}:${TAG}"

# Check if user is logged in to Docker Hub
if ! docker info 2>/dev/null | grep -q "Username"; then
    print_warning "You are not logged in to Docker Hub."
    print_info "Attempting to login to Docker Hub..."
    docker login
    if [ $? -ne 0 ]; then
        print_error "Failed to login to Docker Hub."
        exit 1
    fi
fi

# Check if buildx is available
if ! docker buildx version &> /dev/null; then
    print_error "Docker buildx is not available. Please update Docker to use buildx."
    exit 1
fi

# Create or use existing buildx builder
BUILDER_NAME="multiarch-builder"
if ! docker buildx ls | grep -q ${BUILDER_NAME}; then
    print_info "Creating new buildx builder: ${BUILDER_NAME}"
    docker buildx create --name ${BUILDER_NAME} --driver docker-container --use
    docker buildx inspect --bootstrap
else
    print_info "Using existing buildx builder: ${BUILDER_NAME}"
    docker buildx use ${BUILDER_NAME}
fi

# Clean build artifacts
print_info "Cleaning previous build artifacts..."
./gradlew clean

# Build the application
print_info "Building Spring Boot application..."
./gradlew build -x test

# Build and push multi-architecture Docker image
print_info "Building Docker image for platforms: ${PLATFORMS}"
print_info "Image: ${IMAGE_NAME}:${TAG}"

docker buildx build \
    --platform ${PLATFORMS} \
    --tag ${IMAGE_NAME}:${TAG} \
    --push \
    --progress=plain \
    .

if [ $? -eq 0 ]; then
    print_info "✅ Successfully built and pushed ${IMAGE_NAME}:${TAG}"
    print_info "Supported architectures: ${PLATFORMS}"

    # Display image information
    print_info "Inspecting pushed image..."
    docker buildx imagetools inspect ${IMAGE_NAME}:${TAG}
else
    print_error "❌ Failed to build and push Docker image"
    exit 1
fi

# Optional: Build and push with version tag
if [ -n "$1" ]; then
    VERSION_TAG="$1"
    print_info "Building with version tag: ${VERSION_TAG}"

    docker buildx build \
        --platform ${PLATFORMS} \
        --tag ${IMAGE_NAME}:${VERSION_TAG} \
        --push \
        --progress=plain \
        .

    if [ $? -eq 0 ]; then
        print_info "✅ Successfully built and pushed ${IMAGE_NAME}:${VERSION_TAG}"
    else
        print_error "❌ Failed to build version tagged image"
    fi
fi

print_info "Build and push completed!"
print_info "You can pull the image with:"
print_info "  docker pull ${IMAGE_NAME}:${TAG}"