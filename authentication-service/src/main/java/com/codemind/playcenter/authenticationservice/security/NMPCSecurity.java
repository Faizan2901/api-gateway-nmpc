package com.codemind.playcenter.authenticationservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.codemind.playcenter.authenticationservice.authuserservice.UserService;
import com.codemind.playcenter.authenticationservice.config.ApplicationProperties;

@Configuration
public class NMPCSecurity {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(bCryptPasswordEncoder());
        return auth;
    }

    @SuppressWarnings("removal")
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(configurer -> {
                configurer
                    .requestMatchers("/authentication-service/**", "/user-service/**", "/dashboard-service/**", "/users/**", "/play-center/**", "/css/**", "/fonts/**", "/images/**", "/js/**", "/vendor/**")
                    .permitAll()
                    .anyRequest().authenticated();
                try {
					configurer.and().httpBasic();
				} catch (Exception e) {
					e.printStackTrace();
				}
            })
            .formLogin(form -> 
                form
                    .loginPage(applicationProperties.getApiGatewayUrl() + "/authentication-service/play-center/")
                    .loginProcessingUrl("/authenticateUser")
                    .successHandler(customAuthenticationSuccessHandler)
                    .permitAll()
            )
            .logout(logout -> 
                logout.logoutUrl("/authentication-service/play-center/logout").permitAll()
            )
            .csrf().disable(); // Disable CSRF if you suspect it is causing issues

        return httpSecurity.build();
    }


}
