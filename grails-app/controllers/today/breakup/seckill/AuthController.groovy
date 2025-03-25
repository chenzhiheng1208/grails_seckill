package today.breakup.seckill

import grails.converters.JSON

class AuthController {
    JwtService jwtService
    AuthService authService

    def login() {
        def requestJson = request.JSON
        String username = requestJson.username
        String password = requestJson.password

        if (!username || !password) {
            render(status: 400, text: "Username and password are required")
            return
        }

        User user = User.findByUsername(username)
        if (!user) {
            render(status: 401, text: "Invalid credentials")
            return
        }

        if (authService.checkPassword(password, user.password)) {
            String token = jwtService.generateToken(user.username, user.roleName)
            render([token: token, role: user.roleName] as JSON)
        } else {
            render(status: 401, text: "Invalid credentials")
        }
    }
}