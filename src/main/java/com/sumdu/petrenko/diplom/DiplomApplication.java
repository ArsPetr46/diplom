package com.sumdu.petrenko.diplom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Головний клас програми.
 * <p>
 * Цей клас є точкою входу в програму та ініціалізує Spring Boot додаток.
 * </p>
 */
@SpringBootApplication
public class DiplomApplication {

	/**
	 * Головний метод програми.
	 *
	 * @param args аргументи командного рядка
	 */
	public static void main(String[] args) {
		SpringApplication.run(DiplomApplication.class, args);
	}

}
