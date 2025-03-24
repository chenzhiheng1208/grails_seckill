package today.breakup.seckill

import grails.gorm.annotation.Entity

@Entity
class Product {
    String name
    String description
    BigDecimal price
    Integer inventory
    String status = "unlisted" // 默认未上架

    Date dateCreated
    Date lastUpdated

    static constraints = {
        name nullable: false, blank: false
        price nullable: false, min: 0.01G
        inventory nullable: false, min: 0
        status inList: ["unlisted", "listed"]
    }
}
