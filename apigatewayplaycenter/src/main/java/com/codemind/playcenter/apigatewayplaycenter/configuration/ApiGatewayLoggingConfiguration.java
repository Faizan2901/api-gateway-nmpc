package com.codemind.playcenter.apigatewayplaycenter.configuration;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class ApiGatewayLoggingConfiguration {

	@Bean
	public GlobalFilter customGlobalFilter() {
		return (exchange, chain) -> {
			System.out.println("Global Pre Filter executed");

			// Log Request Headers
			exchange.getRequest().getHeaders().forEach((name, values) -> {
				List<String> headerValues = values;
				headerValues.forEach(value -> {
					System.out.println(name + ": " + value);
				});
			});

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				System.out.println("Global Post Filter executed");
			}));
		};
	}
}
