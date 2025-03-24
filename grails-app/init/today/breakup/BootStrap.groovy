package today.breakup
import today.breakup.seckill.User
import today.breakup.seckill.Product

class BootStrap {

    def init = { servletContext ->
        if (User.count() == 0) {
            new User(
                    username: 'admin',
                    password: 'admin123',
                    email: 'admin@example.com',
                    roleName: 'ADMIN'
            ).save(failOnError: true)

            new User(
                    username: 'user',
                    password: 'user123',
                    email: 'user@example.com',
                    roleName: 'USER'
            ).save(failOnError: true)
            println "默认用户创建完成"
        }

        if (Product.count() == 0) {
            new Product(
                    name: '商品A',
                    description: '这是商品A的描述',
                    price: 10.00,
                    inventory: 5,
                    status: 'listed'
            ).save(failOnError: true)

            new Product(
                    name: '商品B',
                    description: '这是商品B的描述',
                    price: 99.99,
                    inventory: 3,
                    status: 'unlisted'
            ).save(failOnError: true)
            println "默认商品创建完成"
        }
    }
    def destroy = {
    }
}
