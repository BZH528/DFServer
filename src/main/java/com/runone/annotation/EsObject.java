package com.runone.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * elasticsearch的自定义对象注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EsObject {

    //es的索引库名称
    String indexName();

    //es 的文档类型
    String type();

    int shards() default 5;

    int replicas() default 1;
}