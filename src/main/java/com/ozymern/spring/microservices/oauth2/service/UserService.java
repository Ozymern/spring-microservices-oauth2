package com.ozymern.spring.microservices.oauth2.service;

import com.ozymern.spring.microservices.commons.models.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService extends UserDetailsService {

    User finByUsername(String username);
    User update( User user,  Long id);
}
