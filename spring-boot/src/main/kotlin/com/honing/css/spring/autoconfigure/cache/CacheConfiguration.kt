package com.honing.css.spring.autoconfigure.cache

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
open class CacheConfiguration {


  private val logger = LoggerFactory.getLogger(CacheConfiguration::class.java)

  @Bean
  @ConditionalOnProperty(value = ["spring.cache.type"], havingValue = "redis")
  open fun redisCacheConfiguration(
    jackson2ObjectMapperBuilder: ObjectProvider<Jackson2ObjectMapperBuilder>,
    cacheProperties: CacheProperties
  ): RedisCacheConfiguration {
    val available = jackson2ObjectMapperBuilder.getIfAvailable { Jackson2ObjectMapperBuilder.json() }
    return createRedisCacheConfiguration(cacheProperties, available)
  }


  fun createRedisCacheConfiguration(
    cacheProperties: CacheProperties,
    jackson2ObjectMapperBuilder: Jackson2ObjectMapperBuilder
  ): RedisCacheConfiguration {
    logger.info("Using customized {}", RedisCacheConfiguration::class.java)
    val redisProperties = cacheProperties.redis
    var config = RedisCacheConfiguration.defaultCacheConfig()
    //		cacheConfiguration.entryTtl(Duration.ofSeconds(10));
    config = config.serializeValuesWith(
      RedisSerializationContext.SerializationPair.fromSerializer(
        Jackson2JsonRedisSerializer(
          jackson2ObjectMapperBuilder.build(),
          Any::class.java
        )
      )
    )
    if (redisProperties.timeToLive != null) {
      config = config.entryTtl(redisProperties.timeToLive)
    }
    if (redisProperties.keyPrefix != null) {
      config = config.prefixCacheNameWith(redisProperties.keyPrefix)
    }
    if (!redisProperties.isCacheNullValues) {
      config = config.disableCachingNullValues()
    }
    if (!redisProperties.isUseKeyPrefix) {
      config = config.disableKeyPrefix()
    }
    return config
  }
}
