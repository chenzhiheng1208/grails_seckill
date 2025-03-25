package today.breakup.seckill

import grails.gorm.transactions.Transactional
import org.mindrot.jbcrypt.BCrypt
@Transactional
class AuthService {


    // 加密密码
    String encodePassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    // 校验密码是否匹配
    boolean checkPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash)
    }
}
