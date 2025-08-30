package com.huaji.galgamebyhuaji;

import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GalGameByHuaJiApplicationTests {
	@Autowired
	PasswordEncryptionUtil passwordEncryptionUtil;

	@Test
	void contextLoads() {
	}

}