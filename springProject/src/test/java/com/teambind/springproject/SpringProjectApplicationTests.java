package com.teambind.springproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Application context loading test with TestContainers
 */
class SpringProjectApplicationTests extends IntegrationTestBase {

	@Test
	void contextLoads() {
		// Application context loads successfully with real PostgreSQL and Kafka
	}

}
