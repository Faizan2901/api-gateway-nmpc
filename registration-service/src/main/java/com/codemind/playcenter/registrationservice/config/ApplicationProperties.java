package com.codemind.playcenter.registrationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
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
