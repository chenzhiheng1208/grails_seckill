package today.breakup.seckill

import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
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

    def initializeStock(Long productId, Integer stock) {
        String stockKey = STOCK_KEY_PREFIX + productId
        redisTemplate.opsForValue().set(stockKey, stock.toString(), 1, TimeUnit.DAYS)
    }

    def placeOrder(Long userId, Long productId) {
        String stockKey = STOCK_KEY_PREFIX + productId
        ValueOperations<String, String> ops = redisTemplate.opsForValue()

        Integer stock = ops.get(stockKey)?.toInteger()
        if (stock == null || stock <= 0) {
            throw new IllegalArgumentException("商品库存不足")
        }

        // 预扣减库存，防止超卖
        long remaining = ops.decrement(stockKey)
        if (remaining < 0) {
            // 若库存扣减后为负，回滚扣减
            ops.increment(stockKey)
            throw new IllegalArgumentException("商品已售罄")
        }

        // 获取用户和商品信息
        def user = User.get(userId)
        def product = Product.get(productId)
        if (!user) {
            // 回滚库存
            ops.increment(stockKey)
            throw new IllegalArgumentException("用户不存在")
        }
        if (!product) {
            ops.increment(stockKey)
            throw new IllegalArgumentException("商品不存在")
        }

        // 创建秒杀订单，并进行错误检查
        def order = new SeckillOrder(user: user, product: product)
        if (!order.save(flush: true)) {
            // 输出错误信息，回滚库存
            ops.increment(stockKey)
            throw new IllegalStateException("订单创建失败: " + order.errors)
        }

        // 将 Redis 中剩余库存同步回数据库
        product.inventory = remaining
        if (!product.save(flush: true)) {
            throw new IllegalStateException("商品库存更新失败: " + product.errors)
        }

        return "秒杀成功"
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def createOrder(Long userId, Long productId) {
        def user = User.get(userId)
        def product = Product.get(productId)
        if (!user || !product) {
            throw new IllegalArgumentException("用户或商品不存在")
        }
        new SeckillOrder(user: user, product: product).save(flush: true)
    }
}
