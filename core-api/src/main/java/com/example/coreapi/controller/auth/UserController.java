package com.example.coreapi.controller.auth;

import com.example.common.mappers.responses.UserView;
import com.example.common.payload.request.UserRequest;
import com.example.common.util.Pagination;
import com.example.common.util.RestApiResponse;
import com.example.coreapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public RestApiResponse<UserView> save(@RequestBody UserRequest request) {
        return new RestApiResponse<>("200", "Success", userService.save(request));
    }

    @GetMapping("/{id}")
    public RestApiResponse<UserView> findById(@PathVariable Long id) {
        return new RestApiResponse<>("200", "Success", userService.findById(id));
    }

    @GetMapping
    public RestApiResponse<List<UserView>> findAll(Pagination pagination) {
        return new RestApiResponse<>("200", "Success", userService.findAll(pagination), pagination);
    }

    @PutMapping("/{id}")
    public RestApiResponse<UserView> update(@PathVariable Long id, @RequestBody UserRequest request) {
        return new RestApiResponse<>("200", "Success", userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public RestApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return new RestApiResponse<>("200", "Success", null);
    }

}