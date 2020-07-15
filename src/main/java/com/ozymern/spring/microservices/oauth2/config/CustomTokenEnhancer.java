package com.ozymern.spring.microservices.oauth2.config;


import com.ozymern.spring.microservices.commons.models.entity.User;
import com.ozymern.spring.microservices.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private UserService userService;


    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        User user=userService.finByUsername(authentication.getName());


        Map<String,Object> info=new HashMap<>();

        info.put("name",user.getName());
        info.put("last_name",user.getLastName());
        info.put("email",user.getEmail());


        //agrego las claims personalizadas al token
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);

        return accessToken;
    }
}
