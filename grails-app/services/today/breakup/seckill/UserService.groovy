package today.breakup.seckill

import grails.gorm.transactions.Transactional

@Transactional
class UserService {

    def register(String username, String password, String email, String roleName) {
        if (User.findByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在")
        }
        if (User.findByEmail(email)) {
            throw new IllegalArgumentException("邮箱已被使用")
        }
        new User(username: username, password: password, email: email, roleName: roleName).save(flush: true)
    }

    def authenticate(String username, String password) {
        return User.findByUsernameAndPassword(username, password)
    }
}
