package com.ruoyi.common.utils.ip;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class IpRegionConfig {

    @Bean
    public Searcher ipSearcher() throws Exception {
        // 1. 从 resource 读取文件流（解决 Jar 包内无法直接访问 File 的问题）
        ClassPathResource resource = new ClassPathResource("data/ip2region_v4.xdb");
        InputStream inputStream = resource.getInputStream();
        
        // 2. 读取到内存数组中
        byte[] cBuff = FileCopyUtils.copyToByteArray(inputStream);
        
        // 3. 使用 Content 模式初始化 Searcher
        return Searcher.newWithBuffer(cBuff);
    }
}