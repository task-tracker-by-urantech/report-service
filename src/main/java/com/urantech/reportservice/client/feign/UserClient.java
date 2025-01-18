package com.urantech.reportservice.client.feign;

import com.urantech.reportservice.model.rest.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/admin/users")
    List<UserResponse> getAllUsersWithUnfinishedTasks();
}
