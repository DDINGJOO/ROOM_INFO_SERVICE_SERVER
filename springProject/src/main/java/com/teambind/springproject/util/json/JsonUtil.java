package com.teambind.springproject.util.json;

public interface JsonUtil {
	String toJson(Object object);
	
	<T> T fromJson(String json, Class<T> clazz);
}
