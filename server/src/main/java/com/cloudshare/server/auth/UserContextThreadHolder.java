package com.cloudshare.server.auth;


/**
 * @author novo
 * @since 2022-07-13
 */
public class UserContextThreadHolder {
    private static final ThreadLocal<UserContext> threadLocal = new ThreadLocal<>();

    public static void setUserContext(UserContext userContext) {
        UserContextThreadHolder.threadLocal.set(userContext);
    }

    public static UserContext getUserContext() {
        return UserContextThreadHolder.threadLocal.get();
    }

    public static Integer getUserScope() {
        return UserContextThreadHolder.threadLocal.get().scope();
    }

    public static Long getUserId() {
        return UserContextThreadHolder.threadLocal.get().id();
    }

    public static void clear() {
        UserContextThreadHolder.threadLocal.remove();
    }
}
