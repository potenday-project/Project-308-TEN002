package bside.com.project308.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${jwt.token-time}") long tokenTime;
    @Bean
    public CaffeineCache matchCaffeineConfig() {

        return new CaffeineCache("matchPartner",Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                                                        .build());
    }

    @Bean
    public CaffeineCache authCaffeineConfig() {

        return new CaffeineCache("expiredToken",Caffeine.newBuilder()
                                                        .maximumSize(1000)
                                                        .expireAfterWrite(tokenTime, TimeUnit.MINUTES)
                                                        .build());
    }


}
