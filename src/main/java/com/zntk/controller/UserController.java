package com.zntk.controller;

import com.zntk.common.RequireAdmin;
import com.zntk.common.Result;
import com.zntk.entity.User;
import com.zntk.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户管理", description = "管理员查询用户列表")
@RestController
public class UserController {

    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @RequireAdmin
    @Operation(summary = "查询用户列表", description = "管理员查询所有用户，返回前会隐藏密码字段")
    @GetMapping("/users")
    public Result<List<User>> listUsers() {
        List<User> users = userMapper.selectList(null);
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }
}
