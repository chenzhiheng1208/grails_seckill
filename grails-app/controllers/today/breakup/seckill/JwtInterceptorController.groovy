package today.breakup.seckill

import grails.web.api.ServletAttributes
import io.jsonwebtoken.Claims

class JwtInterceptor implements ServletAttributes {
    JwtService jwtService

    JwtInterceptor() {
        matchAll()
                .excludes(controller: "auth")
                .excludes(controller: "user")
                .excludes(controller: "product")
        match(uri: "/")
    }

    boolean before() {
        if (request.forwardURI == "/" || request.forwardURI.startsWith("/user/loginPage")) {
            return true
        }

        String authHeader = request.getHeader("Authorization")
        if (!authHeader?.startsWith("Bearer ")) {
            render(status: 401, text: "Missing or invalid token")
            return false
        }

        String token = authHeader.substring(7)
        if (!jwtService.validateToken(token)) {
            render(status: 401, text: "Invalid token")
            return false
        }

        Claims claims = jwtService.getUsernameFromToken(token)
        request.setAttribute("user", claims.subject)
        request.setAttribute("role", claims.get("role"))
        return true
    }
}
