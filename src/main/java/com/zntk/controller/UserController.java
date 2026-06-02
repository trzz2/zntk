package com.zntk.controller;

import com.zntk.entity.User;
import com.zntk.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public List<User> listUsers() {
        return userMapper.selectList(null);
    }
}