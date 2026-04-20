package com.hisabnikash.erp.cachesupport;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class JitteredTtlFunctionTest {

    @Test
    void addsConfiguredJitterToBaseTtl() {
        JitteredTtlFunction ttlFunction = new JitteredTtlFunction(
                Duration.ofMinutes(10),
                Duration.ofMinutes(2),
                () -> Duration.ofSeconds(45).toMillis()
        );

        Duration ttl = ttlFunction.getTimeToLive("key", "value");

        assertThat(ttl).isEqualTo(Duration.ofMinutes(10).plusSeconds(45));
    }

    @Test
    void clampsJitterAboveConfiguredMaximum() {
        JitteredTtlFunction ttlFunction = new JitteredTtlFunction(
                Duration.ofMinutes(10),
                Duration.ofSeconds(30),
                () -> Duration.ofMinutes(5).toMillis()
        );

        Duration ttl = ttlFunction.getTimeToLive("key", "value");

        assertThat(ttl).isEqualTo(Duration.ofMinutes(10).plusSeconds(30));
    }
}
