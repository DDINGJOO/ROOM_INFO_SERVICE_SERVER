# Makefile for Room Info Service

.PHONY: help build test test-unit test-integration clean docker-up docker-down

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-20s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build the project
	./gradlew clean build -x test

test: ## Run all tests
	./gradlew clean test

test-unit: ## Run unit tests only
	./gradlew clean test -PexcludeIntegrationTests

test-integration: docker-up ## Run integration tests with TestContainers
	./gradlew clean test --tests "*IntegrationTest*" --tests "*MaxOccupancy*"
	$(MAKE) docker-down

test-with-docker: ## Run tests using docker-compose
	docker-compose -f docker-compose.test.yml up -d
	@echo "Waiting for services to start..."
	@sleep 10
	./gradlew clean test
	docker-compose -f docker-compose.test.yml down

docker-up: ## Start test containers
	docker-compose -f docker-compose.test.yml up -d
	@echo "Waiting for services to be healthy..."
	@sleep 10

docker-down: ## Stop test containers
	docker-compose -f docker-compose.test.yml down -v

docker-logs: ## Show test container logs
	docker-compose -f docker-compose.test.yml logs -f

clean: ## Clean build artifacts and test containers
	./gradlew clean
	docker-compose -f docker-compose.test.yml down -v
	rm -rf build/

run: ## Run the application
	./gradlew bootRun --args='--spring.profiles.active=dev'

run-prod: ## Run the application in production mode
	./gradlew bootRun --args='--spring.profiles.active=prod'