package com.codemind.playcenter.dashboardservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.codemind.playcenter.dashboardservice.webuser.WebUser;

@FeignClient(name = "user-service")
public interface UserProxy {

	@GetMapping("/users/user/{name}")
	public WebUser getUser(@PathVariable("name") String name);
	
	

}
