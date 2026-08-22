package com.huaji.galgamebyhuaji.myUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtil {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public static ObjectMapper getObjectMapper () {
		return OBJECT_MAPPER;
	}
}