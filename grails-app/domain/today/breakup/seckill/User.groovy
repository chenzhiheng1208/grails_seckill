package today.breakup.seckill

import grails.gorm.annotation.Entity

@Entity
class User {
    String username
    String password
    String email
    String roleName // ADMIN, USER

    Date dateCreated
    Date lastUpdated

    static constraints = {
        username unique: true, nullable: false, blank: false
        password nullable: false, blank: false
        email unique: true, nullable: false, email: true
        roleName nullable: false, inList: ["ADMIN", "USER"]
    }
}
