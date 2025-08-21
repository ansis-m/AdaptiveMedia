package com.adaptivemedia.assignment.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LockService {

    private final DSLContext dsl;

    public void acquireLockOrThrow(String lockName) {
        int lockId = lockName.hashCode();
        Boolean acquired = dsl.select(DSL.field("pg_try_advisory_lock({0})", Boolean.class, lockId))
                              .fetchOneInto(Boolean.class);

        if (acquired == null || !acquired) {
            throw new RuntimeException("Failed to acquire lock: " + lockName);
        }
    }

    public boolean releaseLock(String lockName) {
        try {
            int lockId = lockName.hashCode();
            Boolean released = dsl.select(DSL.field("pg_advisory_unlock({0})", Boolean.class, lockId))
                                  .fetchOneInto(Boolean.class);

            log.debug("Lock '{}' release: {}", lockName, released);
            return released != null && released;
        } catch (Exception e) {
            log.error("Error releasing lock: {}", lockName, e);
            return false;
        }
    }
}
