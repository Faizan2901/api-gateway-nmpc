package com.codemind.playcenter.dashboardservice.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.codemind.playcenter.dashboardservice.webuser.WebUser;


@FeignClient(name = "user-service")
public interface UserProxy {

	@GetMapping("/users/user/{name}")
	public WebUser getUser(@PathVariable("name") String name);
	
	@GetMapping("/users/user-list")
	public List<WebUser> getUserForManagement(@RequestParam("id") int id);
	
	

}
