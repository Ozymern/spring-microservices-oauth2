package com.ozymern.spring.microservices.oauth2.service;

import brave.Tracer;
import com.ozymern.spring.microservices.commons.models.entity.User;
import com.ozymern.spring.microservices.oauth2.remoto.UserFeignClient;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private Tracer tracer;


    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {

            User user = userFeignClient.findByUsername(username);

            LOGGER.info("username: {}", username);


            return new UserDetails() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {

                    List<GrantedAuthority> authorities = user.getRoles().stream().map(x -> new SimpleGrantedAuthority(x.getName()))
                        .peek(role -> LOGGER.info("ROLE: {}", role.getAuthority()))
                        .collect(Collectors.toList());

                    return authorities;
                }

                @Override
                public String getPassword() {
                    return user.getPassword();
                }

                @Override
                public String getUsername() {
                    return user.getUsername();
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }

                @Override
                public boolean isEnabled() {
                    return user.getEnabled();
                }
            };
        } catch (FeignException f) {
            LOGGER.error("Error: no existe usuario en la BD error {}",f.getMessage());
            //add nuevo tag para visualizar la traza en zipkin
            tracer.currentSpan().tag("error.getUser","no existe usuario en la BD "+f.getMessage());
            throw new UsernameNotFoundException("Error: no existe usuario en la BD");
        }
    }

    @Override
    public User finByUsername(String username) {

        LOGGER.info("[finByUsername]: {}", username);
        return userFeignClient.findByUsername(username);

    }

    @Override
    public User update(User user, Long id) {
        return userFeignClient.update(user, id);
    }
}
