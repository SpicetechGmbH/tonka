package de.spicetech.dts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DtsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DtsApplication.class, args);

		String dtsAscii = ""
		+ " ____ _____ _____  \n"
		+ "|  _ \\_   _/ ___/ \n"
		+ "| | | || | \\___ \\ \n"
		+ "| |_| || |  ___) |\n"
		+ "|____/ |_| /____/ started successfully!";		
		System.out.println(dtsAscii);
	}

}
