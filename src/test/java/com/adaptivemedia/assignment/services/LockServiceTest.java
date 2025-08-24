package com.adaptivemedia.assignment.services;

import com.adaptivemedia.assignment.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;


class LockServiceTest extends BaseIntegrationTest {


    @Autowired
    private LockService lockService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void acquireLockOrThrow() {
        String lockName = "test-lock";

        assertThatNoException().isThrownBy(() -> lockService.acquireLockOrThrow(lockName));
    }

    @Test
    void acquireLockOrThrowThrowsWhenLockAlreadyHeld() throws InterruptedException {
        String lockName = "cron-lock";
        CountDownLatch lockAcquired = new CountDownLatch(1);
        CountDownLatch testComplete = new CountDownLatch(1);
        AtomicBoolean exceptionThrown = new AtomicBoolean(false);

        Thread cronJob1 = new Thread(() -> {
            TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
            txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            txTemplate.execute(status -> {
                lockService.acquireLockOrThrow(lockName);
                lockAcquired.countDown();

                try {
                    testComplete.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                lockService.releaseLock(lockName);
                return null;
            });
        });

        Thread cronJob2 = new Thread(() -> {
            try {
                lockAcquired.await();
                TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
                txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

                txTemplate.execute(status -> {
                    lockService.acquireLockOrThrow(lockName);
                    return null;
                });
            } catch (Exception e) {
                exceptionThrown.set(true);
            }
        });

        cronJob1.start();
        cronJob2.start();

        Thread.sleep(500);

        assertTrue(exceptionThrown.get(), "Second cron job should have thrown exception");

        testComplete.countDown();

        cronJob1.join();
        cronJob2.join();
    }

    @Test
    void releaseLock() {
        String lockName = "release-lock";
        lockService.acquireLockOrThrow(lockName);
        assertThatNoException().isThrownBy(() -> lockService.releaseLock(lockName));
    }

    @Test
    void releaseLockHandlesNonExistentLock() {
        String lockName = "nonexistent-lock";

        assertThatNoException().isThrownBy(() -> lockService.releaseLock(lockName));
    }
}