package com.chsbk.pioner_pixel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PionerPixelApplication {
	public static void main(String[] args) {
		SpringApplication.run(PionerPixelApplication.class, args);
	}
}
