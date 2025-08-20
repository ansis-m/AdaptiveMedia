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

    public boolean tryAcquireLock(String lockName) {
        try {
            int lockId = lockName.hashCode();
            Boolean acquired = dsl.select(DSL.field("pg_try_advisory_lock({0})", Boolean.class, lockId))
                                  .fetchOneInto(Boolean.class);

            log.debug("Lock '{}' acquisition: {}", lockName, acquired);
            return acquired != null && acquired;
        } catch (Exception e) {
            log.error("Error acquiring lock: {}", lockName, e);
            return false;
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
