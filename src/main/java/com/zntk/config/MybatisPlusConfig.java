package com.zntk.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类。
 *
 * 这个类专门放 MyBatis-Plus 的一些增强配置。
 * 这次我们在里面配置“分页插件”。
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器。
     *
     * 拦截器可以理解成：
     * 在 SQL 真正执行前，MyBatis-Plus 可以先拦一下，
     * 然后对 SQL 做一些增强处理。
     *
     * 这里添加的是分页插件。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建 MyBatis-Plus 总拦截器对象
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页内部拦截器
        // 它的作用是：
        // 当你使用 Page 对象查询时，自动给 SQL 拼接分页语句
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());

        // 把配置好的拦截器交给 Spring 管理
        return interceptor;
    }
}