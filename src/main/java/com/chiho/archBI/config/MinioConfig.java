package com.chiho.archBI.config;

/*
从配置源（如 application.yml、application.properties 或环境变量）中读取属性值，并注入到 Bean 的字段或方法参数中。
 */

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MinioConfig {
    // 配置项的参数，用于读取yml当中的配置
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.readPath}")
    private String readPath;

    /**
     * MinioClient是用来与Minio交互的java客户端
     * @return 返回MinioClient实例，有了这个实例才能使用Minio SDK当中的方法
     */
    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey,secretKey)
                .build();
    }
}
