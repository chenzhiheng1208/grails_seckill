package today.breakup

import today.breakup.seckill.AuthService
import today.breakup.seckill.User
import today.breakup.seckill.Product

class BootStrap {
    def authService = new AuthService()
    def init = { servletContext ->
        // 用 withTransaction 包裹用户创建操作
        User.withTransaction { status ->
            if (User.count() == 0) {
                new User(
                        username: 'admin',
                        password: authService.encodePassword('admin123'),
                        email: 'admin@example.com',
                        roleName: 'ADMIN'
                ).save(failOnError: true)

                new User(
                        username: 'user',
                        password: authService.encodePassword('user123'),
                        email: 'user@example.com',
                        roleName: 'USER'
                ).save(failOnError: true)
                println "默认用户创建完成"
            }
        }

        // 用 withTransaction 包裹商品创建操作
        Product.withTransaction { status ->
            if (Product.count() == 0) {
                new Product(
                        name: '商品A',
                        description: '这是商品A的描述',
                        price: 100.00G,
                        inventory: 50,
                        status: 'listed'
                ).save(failOnError: true)

                new Product(
                        name: '商品B',
                        description: '这是商品B的描述',
                        price: 200.00G,
                        inventory: 30,
                        status: 'unlisted'
                ).save(failOnError: true)
                println "默认商品创建完成"
            }
        }
    }
    def destroy = {
    }
}
