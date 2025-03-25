package today.breakup.seckill

import grails.gorm.transactions.Transactional

@Transactional
class UserService {
    AuthService authService
    def register(String username, String password, String email, String roleName) {
        if (User.findByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在")
        }
        if (User.findByEmail(email)) {
            throw new IllegalArgumentException("邮箱已被使用")
        }
        String encryptedPassword = authService.encodePassword(password)
        new User(username: username, password: encryptedPassword, email: email, roleName: roleName).save(flush: true)
    }

    def authenticate(String username, String password) {
        String encryptedPassword = authService.encodePassword(password)
        return User.findByUsernameAndPassword(username, encryptedPassword)
    }
}
