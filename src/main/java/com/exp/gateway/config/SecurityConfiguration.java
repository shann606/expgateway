package com.exp.gateway.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.InMemoryReactiveSessionRegistry;
import org.springframework.security.core.session.ReactiveSessionRegistry;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerFormLoginAuthenticationConverter;
import org.springframework.security.web.server.authentication.SessionLimit;

import com.exp.gateway.exception.ServerException;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager manager,
			ReactiveSessionRegistry registry) {

		AuthenticationWebFilter filter = new AuthenticationWebFilter(manager);

		filter.setServerAuthenticationConverter(new ServerFormLoginAuthenticationConverter());

		return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.authorizeExchange(
						r -> r.pathMatchers("/login", "/register", "/api/v1/users/**", "/api/**", "/css/**", "/js/**")
								.permitAll().pathMatchers("/**").authenticated())

				.httpBasic(Customizer.withDefaults()).formLogin(login -> login.loginPage("/login")

						.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/dashboard"))

						.authenticationFailureHandler(customAuthenticationFailureHandler()))

				.sessionManagement(sessionMgt -> {

					sessionMgt.concurrentSessions(session -> {
						session.maximumSessions(SessionLimit.of(1));
						
						session.sessionRegistry(registry);
						

					});

				})

				.logout(x -> x.logoutUrl("/signout"))

				.build();

	}

	private ServerAuthenticationFailureHandler customAuthenticationFailureHandler() {
		return (WebFilterExchange exchange, AuthenticationException ex) -> {
			var response = exchange.getExchange().getResponse();
			response.setStatusCode(HttpStatus.FOUND);

			if (ex.getCause() != null && ex.getCause() instanceof ServerException) {
				response.getHeaders().setLocation(URI.create("/login?down"));

			} else if (ex.getCause() != null && ex.getCause() instanceof UsernameNotFoundException) {
				response.getHeaders().setLocation(URI.create("/login?error"));

			}

			else {
				response.getHeaders().setLocation(URI.create("/login?error"));
			}

			// FIX: Add the "return" keyword here to return the Mono<Void>
			return response.setComplete();
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	ReactiveSessionRegistry reactiveSessionRegistry() {

		return new InMemoryReactiveSessionRegistry();
	}

}
