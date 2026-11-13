package rs.ac.uns.ftn.informatika.cache.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class RedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // Shared JSON serializer to store objects cleanly as JSON in Redis
        RedisSerializationContext.SerializationPair<Object> jsonSerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

        // Default cache configuration (15 seconds TTL)
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(30))
                .disableCachingNullValues()
                .serializeValuesWith(jsonSerializer);

        // Setup individual cache configurations via a Map (compatible with Spring Data Redis 2.1.x)
        Map<String, RedisCacheConfiguration> initialCacheConfigs = new HashMap<>();
        initialCacheConfigs.put("product", defaultCacheConfig.entryTtl(Duration.ofSeconds(45)));
        initialCacheConfigs.put("productAll", defaultCacheConfig.entryTtl(Duration.ofSeconds(45)));
        initialCacheConfigs.put("productByName", defaultCacheConfig.entryTtl(Duration.ofSeconds(45)));
        initialCacheConfigs.put("productById", defaultCacheConfig.entryTtl(Duration.ofSeconds(45 )));
        initialCacheConfigs.put("byNameMatch", defaultCacheConfig.entryTtl(Duration.ofSeconds(45 )));
        initialCacheConfigs.put("byParamMatch", defaultCacheConfig.entryTtl(Duration.ofSeconds(45 )));

        // Build the RedisCacheManager
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(initialCacheConfigs)
                .build();
    }
}