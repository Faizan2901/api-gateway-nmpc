package com.codemind.playcenter.authenticationservice.authuserservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.codemind.playcenter.authenticationservice.proxy.UserProxy;
import com.codemind.playcenter.authenticationservice.webuser.Role;
import com.codemind.playcenter.authenticationservice.webuser.WebUser;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserProxy proxy;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to load user: " + username);
        WebUser user = proxy.getUser(username);
        if (user == null) {
            System.out.println("User not found: " + username);
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        System.out.println("User found: " + user);

        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }

    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role tempRole : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getRoleDescription());
            authorities.add(tempAuthority);
        }
        System.out.println("Roles mapped to authorities: " + authorities);
        return authorities;
    }
}
