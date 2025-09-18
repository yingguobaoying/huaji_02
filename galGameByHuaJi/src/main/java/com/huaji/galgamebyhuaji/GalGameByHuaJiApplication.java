package com.huaji.galgamebyhuaji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class GalGameByHuaJiApplication {

	public static void main(String[] args) {
		System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
		System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
		SpringApplication.run(GalGameByHuaJiApplication.class, args);
	}

}
