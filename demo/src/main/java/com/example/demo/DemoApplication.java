package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		log.info("=======================================================");
		log.info("APPLICAZIONE AVVIATA CON SUCCESSO");
		log.info("Endpoint disponibile: http://localhost:8080/api/meteo/medie");
		log.info("=======================================================");
	}
}
