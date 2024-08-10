package com.codemind.playcenter.apigatewayplaycenter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
	
	@Value("${api.gateway.url}")
	private String apiGatewayUrl;

	public String getApiGatewayUrl() {
		return apiGatewayUrl;
	}

	public void setApiGatewayUrl(String apiGatewayUrl) {
		this.apiGatewayUrl = apiGatewayUrl;
	}
	
	

}
