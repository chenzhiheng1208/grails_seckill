package today.breakup.seckill

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
class SeckillNotificationService {

    private final StringRedisTemplate redisTemplate

    @Autowired
    SeckillNotificationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate
    }

    static final String ORDER_NOTIFICATION_KEY = "seckill:order:notifications"

    @Scheduled(fixedRate = 10000L)
    void sendNotifications() {
        println "计时中"
        long currentTime = System.currentTimeMillis()

        Set<String> messages = redisTemplate.opsForZSet().rangeByScore(ORDER_NOTIFICATION_KEY, 0, currentTime)

        if (messages) {
            messages.each { message ->
                sendMessage(message)

                redisTemplate.opsForZSet().remove(ORDER_NOTIFICATION_KEY, message)
            }
        }
    }

    private static void sendMessage(String message) {
        println "消息: ${message}"
    }
}
