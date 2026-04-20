package com.hisabnikash.erp.cachesupport;

import java.util.function.Supplier;

public record CachedLookupResult<T>(boolean found, T value) {

    public static <T> CachedLookupResult<T> found(T value) {
        return new CachedLookupResult<>(true, value);
    }

    public static <T> CachedLookupResult<T> notFound() {
        return new CachedLookupResult<>(false, null);
    }

    public T getOrThrow(Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!found) {
            throw exceptionSupplier.get();
        }
        return value;
    }
}
