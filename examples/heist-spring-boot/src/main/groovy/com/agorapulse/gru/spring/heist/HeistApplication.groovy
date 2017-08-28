package com.agorapulse.gru.spring.heist

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
class HeistApplication {

    // for testing only
    private static ConfigurableApplicationContext context

	static void main(String[] args) {
		context = SpringApplication.run HeistApplication, args
	}
}
