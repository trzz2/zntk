package com.zntk.common;

public class UserContext {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser loginUser) {
        USER_HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        LoginUser loginUser = USER_HOLDER.get();
        if (loginUser == null) {
            throw new UnauthorizedException("Please login first");
        }
        return loginUser;
    }

    public static Long getUserId() {
        return get().getUserId();
    }

    public static Integer getRole() {
        return get().getRole();
    }

    public static boolean isAdmin() {
        return Integer.valueOf(1).equals(getRole());
    }

    public static void clear() {
        USER_HOLDER.remove();
    }

    public static class LoginUser {

        private final Long userId;
        private final String username;
        private final Integer role;

        public LoginUser(Long userId, String username, Integer role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public Integer getRole() {
            return role;
        }
    }
}
