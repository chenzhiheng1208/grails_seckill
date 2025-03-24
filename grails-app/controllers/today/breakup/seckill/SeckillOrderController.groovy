package today.breakup.seckill

import grails.converters.JSON

class SeckillOrderController {

    SeckillOrderService seckillOrderService

    static allowedMethods = [
            initializeStock   : "POST",
            initializeAllStocks: "POST",
            placeOrder         : "POST"
    ]

    def initializeStock() {
        def requestBody = request.JSON
        seckillOrderService.initializeStock(requestBody.productId, requestBody.stock)
        render([message: "库存已初始化"] as JSON)
    }

    def initializeAllStocks() {
        println "初始化所有商品库存"
        def products = Product.list()
        products.each { product ->
            seckillOrderService.initializeStock(product.id, product.inventory)
        }
        render([message: "所有商品库存已初始化"] as JSON)
    }

    def placeOrder() {
        def requestBody = request.JSON
        try {
            def message = seckillOrderService.placeOrder(requestBody.userId, requestBody.productId)
            render([message: message] as JSON)
        } catch (Exception e) {
            render(status: 400, text: e.message)
        }
    }
}
