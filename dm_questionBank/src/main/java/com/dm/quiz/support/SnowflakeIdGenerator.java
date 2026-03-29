package com.dm.quiz.support;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 题目 / 试卷 / 内容表等业务主键（BIGINT 非自增）由应用生成。
 * <p>多实例部署时为每个实例配置不同的 {@code quiz.snowflake.worker-id}（0～31）。</p>
 */
@Component
public class SnowflakeIdGenerator {

    private final Snowflake snowflake;

    public SnowflakeIdGenerator(
            @Value("${quiz.snowflake.worker-id:1}") long workerId,
            @Value("${quiz.snowflake.datacenter-id:1}") long datacenterId) {
        this.snowflake = IdUtil.getSnowflake(workerId, datacenterId);
    }

    public long nextId() {
        return snowflake.nextId();
    }
}
