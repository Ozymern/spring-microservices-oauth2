package com.ozymern.spring.microservices.oauth2.remoto;


import com.ozymern.spring.microservices.commons.models.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user")
public interface UserFeignClient {

    @GetMapping("/users/search/user-username")
     User findByUsername(@RequestParam String username);

    @PutMapping("/users/{id}")
     User update(@RequestBody User user, @PathVariable Long id);
}

//localhost:8096/api/v1/user/users/search/user-username?username=ozymern
