package today.breakup.seckill

import grails.gorm.transactions.Transactional
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.Date

@Transactional
class JwtService {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private static final long EXPIRATION_TIME = 86400000 // 1 天

    String generateToken(String username, String roleName) {
        Date now = new Date()
        Date expiryDate = new Date(now.time + EXPIRATION_TIME)

        return Jwts.builder()
                .setSubject(username)
                .claim("role", roleName)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact()
    }

    String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
    }

    boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token)
            return true
        } catch (Exception e) {
            return false
        }
    }
}