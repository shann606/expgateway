package com.exp.gateway.service;

import java.time.Duration;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.exp.gateway.customuser.CustomUser;
import com.exp.gateway.dto.UserDTO;
import com.exp.gateway.exception.ServerException;

import reactor.core.publisher.Mono;

@Service
public class CustomUserServiceImpl implements ReactiveUserDetailsService {

	private WebClient webClient;

	public CustomUserServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {

		return webClient.get().uri("/api/v1/users/{username}", username).retrieve()

				.bodyToMono(UserDTO.class)
				.onErrorResume(WebClientResponseException.class,
						ex -> ex.getStatusCode().is4xxClientError() ? Mono.empty()
								: Mono.error(new UsernameNotFoundException("user not found exception")))
				.onErrorResume(WebClientResponseException.class,
						ex -> ex.getStatusCode().is5xxServerError() ? Mono.empty()
								: Mono.error(new ServerException("downstream is down")))
				.timeout(Duration.ofSeconds(5))

				.map(u ->

				new CustomUser(u.id(), u.username(), u.password(),
						u.roles().stream().map(SimpleGrantedAuthority::new).toList())

				)
				;

	}

}
