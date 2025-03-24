package today.breakup.seckill

import grails.converters.JSON
import org.springframework.data.redis.core.RedisTemplate

class RedisTestController {

    RedisTemplate<String, String> redisTemplate // 注入通用 RedisTemplate

    def testConnection() {
        try {
            redisTemplate.opsForValue().set("test_key", "Hello Redis")
            def value = redisTemplate.opsForValue().get("test_key")
            render([message: "Redis 连接成功", value: value] as JSON)
        } catch (Exception e) {
            render(status: 500, text: "Redis 连接失败: ${e.message}")
        }
    }
}
