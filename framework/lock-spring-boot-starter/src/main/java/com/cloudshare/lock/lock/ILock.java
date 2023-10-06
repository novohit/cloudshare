package com.cloudshare.lock.lock;

/**
 * @author novo
 * @since 2023/10/6
 */
public interface ILock {

    boolean tryLock(String key, long timeout);

    boolean unlock(String key);
}
