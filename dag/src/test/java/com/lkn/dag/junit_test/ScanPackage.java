package com.lkn.dag.junit_test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;

/**
 * @author xijiu
 * @since 2022/3/30 下午8:45
 */
@SpringBootApplication(scanBasePackages = {"com.lkn.dag"})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
        PersistenceExceptionTranslationAutoConfiguration.class, RedisAutoConfiguration.class,
        GsonAutoConfiguration.class, ValidationAutoConfiguration.class})
public class ScanPackage {
}
