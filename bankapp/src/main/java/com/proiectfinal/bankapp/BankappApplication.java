package com.proiectfinal.bankapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BankappApplication {

	//@Autowired
	//private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(BankappApplication.class, args);
	}

}
