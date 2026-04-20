package com.hisabnikash.erp.cachesupport;

import java.time.Duration;

public interface CacheEntryLockManager {

    boolean tryLock(String lockKey, String token, Duration ttl);

    void unlock(String lockKey, String token);
}
