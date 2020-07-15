package com.ozymern.spring.microservices.oauth2.config;


import com.ozymern.spring.microservices.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;
@RefreshScope
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig
    extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private CustomTokenEnhancer customTokenEnhancer;

    @Autowired
    private Environment env;


    @Autowired
    private UserService userDetailsService;

    //permiso que tendran los endpoint del servidor de autenticacion
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

        //tokenKeyAccess es la ruta para generar el token oauth/token
        security.tokenKeyAccess("permitAll()")
            //validar el token via http basic : client id y client secret
            .checkTokenAccess("isAuthenticated()");
    }

    //registrar clientes
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //clientId : identificador de nuestra aplicacion
        //secret: contraseña encriptada
        clients.inMemory().
            //credenciales de la aplicacion cliente
                withClient(env.getProperty("config.security.oaut.client.id"))
            .secret(bCryptPasswordEncoder.encode(env.getProperty("config.security.oaut.client.secret")))
            //alcance de la aplicacion, permiso de la aplicacion cliente
            .scopes("read", "write")
            //credenciales del usuario
            //authorizedGrantTypes: como vamos a optener el token, con password de credenciales, optener un nuevo token justo antes de que expire el token
            .authorizedGrantTypes("password", "refresh_token")
            //tiempo de validez del token antes de que caduque
            .accessTokenValiditySeconds(3600)
            //tiempo de validez del refresh_token antes de que caduque
            .refreshTokenValiditySeconds(3800);
        //otro cliente
//        .and()
//        //credenciales de la aplicacion cliente
//        .withClient("angular2")
//            .secret(bCryptPasswordEncoder.encode("admin1234"))
//            //alcance de la aplicacion, permiso de la aplicacion cliente
//            .scopes("read", "write")
//            //credenciales del usuario
//            //authorizedGrantTypes: como vamos a optener el token, con password de credenciales, optener un nuevo token justo antes de que expire el token
//            .authorizedGrantTypes("password", "refresh_token")
//            //tiempo de validez del token antes de que caduque
//            .accessTokenValiditySeconds(3600)
//            //tiempo de validez del refresh_token antes de que caduque
//            .refreshTokenValiditySeconds(3600)

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //cadena para unir los datos del token
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(customTokenEnhancer, accessTokenConverter()));
        //registrar en authenticationManager
        endpoints.authenticationManager(authenticationManager)
            //Para convertir tokens de acceso a diferentes formatos, como JWT
            .accessTokenConverter(accessTokenConverter())
            //componente que se encarga de guardar el token
            .tokenStore(tokenStore())
            .userDetailsService(userDetailsService)
            //agregar a la configuracion del endpoint el TokenEnhancerChain
            .tokenEnhancer(tokenEnhancerChain);

    }

    @Bean
    public JwtTokenStore tokenStore() {
        //  recibe el accessTokenConverter  es el componente que se encarga de convertir el token en jwt
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {

        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey(env.getProperty("config.security.oaut.jwt.key"));

        return accessTokenConverter;

    }
}
