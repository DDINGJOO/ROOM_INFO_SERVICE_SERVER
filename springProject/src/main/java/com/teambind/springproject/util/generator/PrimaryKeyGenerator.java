package com.teambind.springproject.util.generator;

import org.springframework.stereotype.Component;

@Component
public interface PrimaryKeyGenerator {
	String generateKey();
	
	Long generateLongKey();
}
