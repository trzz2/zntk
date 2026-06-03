package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.common.UserContext;
import com.zntk.dto.LoginRequest;
import com.zntk.dto.LoginResponse;
import com.zntk.dto.RegisterRequest;
import com.zntk.entity.User;
import com.zntk.mapper.UserMapper;
import com.zntk.service.AuthService;
import com.zntk.util.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(
            UserMapper userMapper,
            JwtUtil jwtUtil,
            StringRedisTemplate stringRedisTemplate
    ) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Long register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());

        if (userMapper.selectOne(wrapper) != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole(0);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);

        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());

        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("Username or password error");
        }

        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new RuntimeException("Account disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Username or password error");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        return response;
    }

    @Override
    public Boolean logout(String token) {
        Map<String, Object> payload = jwtUtil.parseToken(token);
        String tokenId = payload.get("jti").toString();
        long remainingSeconds = jwtUtil.getRemainingSeconds(payload);

        if (remainingSeconds > 0) {
            stringRedisTemplate.opsForValue().set(
                    BLACKLIST_KEY_PREFIX + tokenId,
                    "1",
                    remainingSeconds,
                    TimeUnit.SECONDS
            );
        }

        return true;
    }

    @Override
    public User currentUser() {
        User user = userMapper.selectById(UserContext.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setPassword(null);
        return user;
    }
}
