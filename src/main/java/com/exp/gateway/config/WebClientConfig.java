package com.exp.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	WebClient getWebClient() {

		return WebClient.builder().baseUrl("http://localhost:8080").build();

	}

	@Bean
	ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService uds, PasswordEncoder encoder) {

		UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(
				uds);

		manager.setPasswordEncoder(encoder);

		return manager;
	}

}
