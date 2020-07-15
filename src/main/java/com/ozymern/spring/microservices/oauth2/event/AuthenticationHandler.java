package com.ozymern.spring.microservices.oauth2.event;


import com.ozymern.spring.microservices.commons.models.entity.User;
import com.ozymern.spring.microservices.oauth2.service.UserService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHandler implements AuthenticationEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationHandler.class);

    @Autowired
    private UserService userService;

    //para manejar exito en la autenticacion
    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        LOGGER.info("[publishAuthenticationSuccess] login success {}", userDetails.getUsername());

        User user = userService.finByUsername(authentication.getName());

        if (user.getAttempts()!=null && user.getAttempts()>0){
            user.setAttempts(0);
            userService.update(user,user.getId());
        }
    }


    //para manejar error en la autenticacion
    @Override
    public void publishAuthenticationFailure(AuthenticationException e, Authentication authentication) {

        LOGGER.error("[publishAuthenticationFailure] error login {}", e.getMessage());

        //implementar el numeros de intentos en la autenticacion
        try {
            User user = userService.finByUsername(authentication.getName());

            if (user.getAttempts() == null) {
                //asignamos valor inicial
                user.setAttempts(0);
            }
            LOGGER.info("[publishAuthenticationFailure]  Numero de intentos  actuales es de {}", user.getAttempts());

            user.setAttempts(user.getAttempts() + 1);
            LOGGER.info("[publishAuthenticationFailure]  Numero de intentos  despues es de {}", user.getAttempts());
            if (user.getAttempts() >= 3) {
                LOGGER.info("[publishAuthenticationFailure]  usuario {} desabilitado por {} intentos", user.getName(), user.getAttempts());
                user.setEnabled(false);
            }

            userService.update(user, user.getId());

        } catch (FeignException f) {
            LOGGER.error("[publishAuthenticationFailure] error login  al optener el usuario {}", f.getMessage());

        }

    }
}
