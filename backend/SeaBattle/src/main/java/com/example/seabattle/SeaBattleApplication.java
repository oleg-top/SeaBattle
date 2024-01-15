package com.example.seabattle;

import com.example.seabattle.utils.FileStorageUtils;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class SeaBattleApplication implements CommandLineRunner {

	@Resource
	FileStorageUtils fileStorageUtils;

	public static void main(String[] args) {
		SpringApplication.run(SeaBattleApplication.class, args);
	}

	@Override
	public void run(String... arg) throws Exception {
		fileStorageUtils.init();
	}
}
