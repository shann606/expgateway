package com.exp.gateway.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	@Bean
	WebClient getWebClient() {

		HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(5)) // Response Timeout
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // Connect Timeout
				.option(ChannelOption.SO_KEEPALIVE, true); // Keep-Alive

		return WebClient.builder().baseUrl("http://localhost:8080")
				.clientConnector(new ReactorClientHttpConnector(httpClient))

				.build();

	}

	@Bean
	ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService uds, PasswordEncoder encoder) {

		UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(
				uds);

		manager.setPasswordEncoder(encoder);

		return manager;
	}

}
