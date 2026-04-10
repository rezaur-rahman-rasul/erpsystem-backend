package com.hisabnikash.erp.enterprisestructure.audit.aop;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {

    String action();
}
