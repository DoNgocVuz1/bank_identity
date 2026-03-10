package com.example.bank_identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class   BankIdentityApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankIdentityApplication.class, args);
	}

}
