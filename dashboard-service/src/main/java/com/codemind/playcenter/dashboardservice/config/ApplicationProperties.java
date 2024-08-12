package com.codemind.playcenter.dashboardservice.config;

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

	@Value("${auth.secret.key}")
	private String authSecretKey;

	public String getApiGatewayUrl() {
		return apiGatewayUrl;
	}

	public void setApiGatewayUrl(String apiGatewayUrl) {
		this.apiGatewayUrl = apiGatewayUrl;
	}

	public String getAuthSecretKey() {
		return authSecretKey;
	}

	public void setAuthSecretKey(String authSecretKey) {
		this.authSecretKey = authSecretKey;
	}

}
