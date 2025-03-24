package today.breakup.seckill

import grails.gorm.transactions.Transactional

@Transactional
class ProductService {

    def listAll() {
        return Product.list()
    }

    def createProduct(String name, String description, BigDecimal price, Integer inventory) {
        new Product(name: name, description: description, price: price, inventory: inventory).save(flush: true)
    }

    def updateProduct(Long id, String name, String description, BigDecimal price, Integer inventory, String status) {
        def product = Product.get(id)
        if (!product) {
            throw new IllegalArgumentException("商品不存在")
        }
        product.name = name
        product.description = description
        product.price = price
        product.inventory = inventory
        product.status = status
        product.save(flush: true)
    }

    def deleteProduct(Long id) {
        def product = Product.get(id)
        if (product) {
            product.delete(flush: true)
        } else {
            throw new IllegalArgumentException("商品不存在")
        }
    }
}
