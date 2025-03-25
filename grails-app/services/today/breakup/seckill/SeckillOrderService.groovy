package today.breakup.seckill

import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.transaction.annotation.Propagation

import java.util.concurrent.TimeUnit

@Transactional
class SeckillOrderService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public SeckillOrderService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    static final String STOCK_KEY_PREFIX = "seckill:stock:"
    static final String ORDER_KEY_PREFIX = "seckill:order:"

    // Lua脚本实现原子性库存检查和扣减
    private static final String STOCK_DEDUCTION_SCRIPT =
            """
        local stockKey = KEYS[1]
        local orderKey = KEYS[2]
        local userId = ARGV[1]
        
        -- 检查库存
        local stock = tonumber(redis.call('GET', stockKey))
        if not stock or stock <= 0 then
            return 0
        end
        
        -- 检查是否已经购买过
        if redis.call('SISMEMBER', orderKey, userId) == 1 then
            return -1
        end
        
        -- 扣减库存
        redis.call('DECR', stockKey)
        -- 记录购买用户
        redis.call('SADD', orderKey, userId)
        return 1
        """

    private final RedisScript<Long> stockDeductionScript = new DefaultRedisScript<>(STOCK_DEDUCTION_SCRIPT, Long.class)

    /**
     * 初始化库存和订单集合
     */
    def initializeStock(Long productId, Integer stock) {
        String stockKey = STOCK_KEY_PREFIX + productId
        String orderKey = ORDER_KEY_PREFIX + productId

        redisTemplate.opsForValue().set(stockKey, stock.toString(), 1, TimeUnit.DAYS)
        // 删除可能存在的旧订单集合
        redisTemplate.delete(orderKey)
    }

    /**
     * 秒杀下单主方法
     */
    def placeOrder(Long userId, Long productId) {
        String stockKey = STOCK_KEY_PREFIX + productId
        String orderKey = ORDER_KEY_PREFIX + productId

        // 使用Lua脚本原子性执行库存检查和扣减
        Long result = redisTemplate.execute(
                stockDeductionScript,
                Arrays.asList(stockKey, orderKey),
                userId.toString()
        )

        // 处理Lua脚本返回结果
        println(result)
        switch(result) {
            case 0:
                throw new IllegalArgumentException("商品库存不足")
            case -1:
                throw new IllegalArgumentException("您已经参加过此商品的秒杀")
            case 1:
                // 继续处理
                break
            default:
                throw new IllegalStateException("系统繁忙，请稍后再试")
        }

        // 异步创建订单（使用独立事务）
        createOrderAsync(userId, productId)

        return "秒杀成功，订单处理中"
    }

    /**
     * 异步创建订单（使用独立事务）
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def createOrderAsync(Long userId, Long productId) {
        try {
            def user = User.get(userId)
            def product = Product.get(productId)

            if (!user || !product) {
                log.error("用户或商品不存在: userId=${userId}, productId=${productId}")
                return
            }

            // 创建订单
            def order = new SeckillOrder(user: user, product: product)
            if (!order.save(flush: true)) {
                log.error("订单创建失败: ${order.errors}")
                return
            }

            // 异步更新数据库库存（可以放在定时任务中批量处理）
            // 这里为了演示直接更新
            updateProductStock(productId)

        } catch (Exception e) {
            log.error("创建订单异常: ${e.message}", e)
        }
    }

    /**
     * 更新商品库存（从Redis同步到数据库）
     */
    @Transactional
    def updateProductStock(Long productId) {
        String stockKey = STOCK_KEY_PREFIX + productId
        def stockStr = redisTemplate.opsForValue().get(stockKey)

        if (stockStr) {
            def product = Product.get(productId)
            if (product) {
                product.inventory = stockStr.toInteger()
                if (!product.save(flush: true)) {
                    log.error("商品库存更新失败: ${product.errors}")
                }
            }
        }
    }
}