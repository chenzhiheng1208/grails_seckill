package today.breakup.seckill

import grails.gorm.annotation.Entity

@Entity
class SeckillOrder {
    User user
    Product product
    Integer quantity = 1
    String orderStatus = "pending" // pending, completed

    Date dateCreated

    static constraints = {
        quantity nullable: false, min: 1
        orderStatus inList: ["pending", "completed"]
    }
}
