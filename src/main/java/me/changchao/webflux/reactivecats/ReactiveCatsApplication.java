package me.changchao.webflux.reactivecats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import zipkin2.Span;
import zipkin2.reporter.Reporter;

@SpringBootApplication
public class ReactiveCatsApplication {
	@Bean
	WebClient webClient() {
		// https://stackoverflow.com/questions/49095366/right-way-to-use-spring-webclient-in-multi-thread-environment
		return WebClient.create("https://api.thecatapi.com/v1/images/search");

	}

	@Bean
	public Reporter<Span> spanReporter() {
		return Reporter.CONSOLE;
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactiveCatsApplication.class, args);
	}

}
