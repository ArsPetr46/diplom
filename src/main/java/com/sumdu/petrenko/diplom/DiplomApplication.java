package com.sumdu.petrenko.diplom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	 * Логер для головного класу програми.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DiplomApplication.class);

	/**
	 * Головний метод програми.
	 *
	 * @param args аргументи командного рядка
	 */
	public static void main(String[] args) {
		logger.info("Запуск додатка");
		SpringApplication.run(DiplomApplication.class, args);
		logger.info("Додаток успішно запущено");
	}

}
