package today.breakup.seckill

import grails.converters.JSON
import grails.gorm.transactions.Transactional

class ProductController {

    ProductService productService

    static allowedMethods = [list: "GET", add: "POST",editProduct: "PUT",deleteProduct: "DELETE",listProduct:"PUT"]
// 编辑指定id的商品信息
    @Transactional
    def editProduct() {
//        def productId = params.id
        def updatedProductData = request.JSON  // 获取请求体中的JSON数据
//        productId = updatedProductData.id as int
        def product = Product.get(updatedProductData.id as int)  // 获取指定id的商品
        if (!product) {
            render status: 404, text: "商品未找到"
            return
        }

        def price = new BigDecimal(updatedProductData.price as String)  // 显式转换
        def inventory = updatedProductData.inventory as int
        // 更新商品信息
        product.name = updatedProductData.name ?: product.name
        product.description = updatedProductData.description ?: product.description
        product.price = price ?: product.price
        product.inventory = inventory ?: product.inventory

        // 保存更新后的商品信息
        if (product.save(flush: true)) {
            render status: 200, text: "商品信息更新成功"
        } else {
            render status: 400, text: "更新商品信息失败"
        }
    }

    // 修改商品状态为"listed"（已上架）
    @Transactional
    def listProduct() {
        def requestBody = request.JSON
        def productId = requestBody.id
        println("productId: $productId")
        def product = Product.get(productId)
        if (!product) {
            render status: 404, text: "商品未找到"
            return
        }

        product.status = 'listed'  // 修改商品状态为已上架
        if (product.save(flush: true)) {
            render status: 200, text: "商品已成功上架"
        } else {
            render status: 400, text: "商品上架失败"
        }
    }
    @Transactional
    def deleteProduct() {
        def requestBody = request.JSON
        def productId = requestBody.id
        def product = Product.get(productId)  // 获取指定id的商品
        if (!product) {
            render status: 404, text: "商品未找到"
            return
        }

        try {
            product.delete() // 不使用 flush: true，避免同步问题
            render status: 200, text: "商品已成功删除"
        } catch (Exception e) {
            render status: 500, text: "删除商品失败: ${e.message}"
        }
    }
    def list() {
        render(productService.listAll() as JSON)
    }

    def add() {
        def requestBody = request.JSON
        try {
            def price = new BigDecimal(requestBody.price as String)  // 显式转换
            def inventory = requestBody.inventory as int
            productService.createProduct(requestBody.name, requestBody.description, price, inventory)
            render([message: "商品添加成功"] as JSON)
        } catch (Exception e) {
            render(status: 400, text: e.message)
        }
    }
    def buyProductPage() {
        def products = Product.list()
        render(view: "buyProduct", model: [products: products])
    }

    def inventoryManagePage() {
        def products = Product.list()
        render(view: "inventoryManage", model: [products: products])
    }
}
