package com.cloudshare.server.auth;


import com.cloudshare.server.share.model.Share;

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
        return ShareContextThreadHolder.threadLocal.get().getId();
    }

    public static void clear() {
        ShareContextThreadHolder.threadLocal.remove();
    }
}
