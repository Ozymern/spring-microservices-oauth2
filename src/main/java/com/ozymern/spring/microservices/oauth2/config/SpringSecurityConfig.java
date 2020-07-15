package com.ozymern.spring.microservices.oauth2.config;

import com.ozymern.spring.microservices.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserService userDetailsService;

    @Autowired
    private AuthenticationEventPublisher authenticationEventPublisher;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // Se define la clase que recupera los usuarios y el algoritmo para procesar las passwords
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
            //agregar los mensaje de exito y fallos en la autenticacion
            .and().authenticationEventPublisher(authenticationEventPublisher);
        super.configure(auth);
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
