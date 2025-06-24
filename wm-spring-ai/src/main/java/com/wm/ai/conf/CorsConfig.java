package com.wm.ai.conf;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许指定域名进行跨域调用
        config.addAllowedOriginPattern("*"); // 注意：生产环境建议明确指定域名，而不是使用通配符
        // 允许任何请求头
        config.addAllowedHeader("*");
        // 允许任何方法（POST、GET等）
        config.addAllowedMethod("*");
        // 允许携带凭证（如Cookie）
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有接口都有效
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
