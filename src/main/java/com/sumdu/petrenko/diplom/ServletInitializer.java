package com.sumdu.petrenko.diplom;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Клас для ініціалізації Spring Boot додатку в середовищі сервлету.
 * <p>
 * Цей клас дозволяє запускати Spring Boot додаток в контейнері сервлетів, наприклад, Tomcat.
 * </p>
 */
public class ServletInitializer extends SpringBootServletInitializer {
	/**
	 * Налаштування Spring Boot додатку.
	 *
	 * @param application SpringApplicationBuilder
	 * @return налаштований SpringApplicationBuilder
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DiplomApplication.class);
	}

}
