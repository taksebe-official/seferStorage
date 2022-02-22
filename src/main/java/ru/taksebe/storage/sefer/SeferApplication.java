package ru.taksebe.storage.sefer;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ru.taksebe.storage.sefer")
public class SeferApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeferApplication.class, args);
	}
}