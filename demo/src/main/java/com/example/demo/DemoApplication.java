package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

	private static final String ESC = String.valueOf((char) 27);
	private static final String ANSI_RESET = ESC + "[0m";
	private static final String ANSI_BOLD_CYAN = ESC + "[1;36m";
	private static final String ANSI_GREEN = ESC + "[1;32m";

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		// Banner stampato direttamente su stdout (senza prefissi di log)
		// così il link è ben visibile e cliccabile nel terminale.
		System.out.println();
		System.out.println("=======================================================");
		System.out.println("  " + ANSI_GREEN + "APPLICAZIONE AVVIATA CON SUCCESSO!" + ANSI_RESET);
		System.out.println("  Apri questo link nel browser:");
		System.out.println("  " + ANSI_BOLD_CYAN + "http://localhost:8080/api/meteo/medie" + ANSI_RESET);
		System.out.println("=======================================================");
		System.out.println();
	}
}
