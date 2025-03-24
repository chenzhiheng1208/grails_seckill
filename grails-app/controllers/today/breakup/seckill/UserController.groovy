package today.breakup.seckill

import grails.converters.JSON

class UserController {

    UserService userService

    static allowedMethods = [register: "POST", login: "POST"]

    def register() {
        def requestBody = request.JSON
        try {
            userService.register(requestBody.username, requestBody.password, requestBody.email, requestBody.roleName)
            render([message: "注册成功"] as JSON)
        } catch (Exception e) {
            render(status: 400, text: e.message)
        }
    }

    def login() {
        def requestBody = request.JSON
        def user = userService.authenticate(requestBody.username, requestBody.password)
        if (user) {
            session.user = user // 存入 session
            // 暂时直接返回用户 ID，后续可优化为 JWT Token
            render([message: "登录成功", userId: user.id,userrole:user.roleName] as JSON)
        } else {
            render(status: 401, text: "用户名或密码错误")
        }
    }
    def loginPage() {
        render(view: "login")
    }
//    private String getRedirectUrl(String roleName) {
//        if (roleName == "ADMIN") {
//            return "/product/inventoryManagePage"
//        } else {
//            return "/product/buyProductPage"
//        }
//    }
}
