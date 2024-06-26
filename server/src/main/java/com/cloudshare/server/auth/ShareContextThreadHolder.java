package com.cloudshare.server.auth;


import com.cloudshare.server.model.Share;

/**
 * @author novo
 * @since 2022-07-13
 */
public class ShareContextThreadHolder {
    private static final ThreadLocal<Share> threadLocal = new ThreadLocal<>();

    public static void setShare(Share share) {
        ShareContextThreadHolder.threadLocal.set(share);
    }

    public static Share getShare() {
        return ShareContextThreadHolder.threadLocal.get();
    }

    public static Long getShareId() {
        return ShareContextThreadHolder.threadLocal.get().getShareId();
    }

    public static Long getShareUserId() {
        return ShareContextThreadHolder.threadLocal.get().getUserId();
    }

    public static void clear() {
        ShareContextThreadHolder.threadLocal.remove();
    }
}
