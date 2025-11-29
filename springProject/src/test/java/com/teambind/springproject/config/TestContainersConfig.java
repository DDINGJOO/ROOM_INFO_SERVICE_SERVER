package com.teambind.springproject.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

/**
 * TestContainers configuration for integration tests
 * The actual container setup is in IntegrationTestBase
 */
@TestConfiguration
@Profile("integration-test")
public class TestContainersConfig {
    // Configuration is handled by IntegrationTestBase with @DynamicPropertySource
}