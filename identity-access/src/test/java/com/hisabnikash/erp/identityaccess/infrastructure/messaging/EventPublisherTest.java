package com.hisabnikash.erp.identityaccess.infrastructure.messaging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class EventPublisherTest {

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test
    void publishRunsImmediatelyWhenNoTransactionIsActive() {
        AtomicInteger sendCount = new AtomicInteger();
        KafkaOperations<String, Object> kafkaTemplate = kafkaOperations(sendCount);
        RecordingTaskExecutor taskExecutor = new RecordingTaskExecutor(true);

        EventPublisher publisher = new EventPublisher(kafkaTemplate, taskExecutor);

        publisher.publish("identity.user.created", "UserCreated", "USER", UUID.randomUUID(), "payload");

        assertThat(taskExecutor.executionCount()).isEqualTo(1);
        assertThat(sendCount).hasValue(1);
    }

    @Test
    void publishDefersDispatchUntilAfterCommitWhenTransactionIsActive() {
        AtomicInteger sendCount = new AtomicInteger();
        KafkaOperations<String, Object> kafkaTemplate = kafkaOperations(sendCount);
        RecordingTaskExecutor taskExecutor = new RecordingTaskExecutor(false);
        EventPublisher publisher = new EventPublisher(kafkaTemplate, taskExecutor);

        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);

        publisher.publish("identity.user.created", "UserCreated", "USER", UUID.randomUUID(), "payload");

        assertThat(taskExecutor.executionCount()).isZero();
        assertThat(TransactionSynchronizationManager.getSynchronizations()).hasSize(1);

        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }

        assertThat(taskExecutor.executionCount()).isEqualTo(1);
        assertThat(sendCount).hasValue(0);

        taskExecutor.runAll();

        assertThat(sendCount).hasValue(1);
    }

    @SuppressWarnings("unchecked")
    private KafkaOperations<String, Object> kafkaOperations(AtomicInteger sendCount) {
        return (KafkaOperations<String, Object>) Proxy.newProxyInstance(
                KafkaOperations.class.getClassLoader(),
                new Class<?>[]{KafkaOperations.class},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "toString" -> "KafkaOperationsProxy";
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == args[0];
                            default -> null;
                        };
                    }
                    if ("send".equals(method.getName())) {
                        sendCount.incrementAndGet();
                        return CompletableFuture.completedFuture(null);
                    }
                    throw new UnsupportedOperationException("Unexpected method: " + method.getName());
                }
        );
    }

    private static final class RecordingTaskExecutor implements TaskExecutor {
        private final boolean runImmediately;
        private final List<Runnable> tasks = new ArrayList<>();
        private int executionCount;

        private RecordingTaskExecutor(boolean runImmediately) {
            this.runImmediately = runImmediately;
        }

        @Override
        public void execute(Runnable task) {
            executionCount++;
            if (runImmediately) {
                task.run();
                return;
            }
            tasks.add(task);
        }

        int executionCount() {
            return executionCount;
        }

        void runAll() {
            List<Runnable> pending = new ArrayList<>(tasks);
            tasks.clear();
            pending.forEach(Runnable::run);
        }
    }
}
