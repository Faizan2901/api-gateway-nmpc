package com.codemind.playcenter.registrationservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.codemind.playcenter.registrationservice.user.WebUser;

import jakarta.servlet.http.HttpServletRequest;


@FeignClient(name = "user-service")
public interface WebUserProxy {

	@GetMapping("/users/webuser")
	public WebUser getWebUserObject();

	@GetMapping("/users/user")
	public WebUser getExististingUser(@RequestParam("name") String name);

	@PostMapping("/users/user")
    public void save(@RequestBody WebUser webUser);


}
