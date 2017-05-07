package vn.khtt.gae.spring.social;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.SocialConfiguration;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;

import java.util.Random;

import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An implementation of {@link SocialConfigurer} for AppEngine, which use Cookie to store remember-me token
 * @see SocialConfiguration#usersConnectionRepository
 */
public class CookieAppEngineSocialConfigurer extends AppEngineSocialConfigurer {
    private static final String COOKIE_NAME = "SPRING_SOCIAL_REMEMBER_ME";

    private static class CookieUserIdSource implements UserIdSource {
        @Override
        public String getUserId() {
            HttpServletRequest request = Utils.getCurrentRequest();

            Cookie[] cookies = request.getCookies();
            if (cookies == null){
                return null;
            }

            for (Cookie cookie : cookies){
                if (COOKIE_NAME.equals(cookie.getName())){
                    String token = cookie.getValue();
                    return extractUserIdFromToken(token);
                }
            }

            return null;
        }

        private String extractUserIdFromToken(String token){
            return token;   // TODO
        }
    }

    @Entity
    private static class AllocId {
        static {
            ObjectifyService.register(AllocId.class);
        }

        @Id
        private Long id;

        public Long getId(){
            return id;
        }
    }
    private static class CookieConnectionSignUp implements ConnectionSignUp {
        @Override
        public String execute(Connection<?> connection) {
            // 1.
            // Key<UserConnection> key = ofy().factory().allocateId(UserConnection.class);
            // String userId = "" + key.getId();

            // 2.
            Random random = new Random();
            String userId = "" + Math.abs(random.nextLong());

            // 3.
            // AllocId allocId = new AllocId();
            // ofy().save().entity(allocId).now();
            // ofy().delete().entity(allocId);
            // String userId = "" + allocId.getId();

            return userId;
        }
    }

    private static class CookieConnectionInterceptor<S> extends ConnectionInterceptorAdapter<S>{
        public void afterCreate(String userId, Connection<S> connection) {
            addCookie(userId, connection);
        }

        public void afterUpdate(String userId, Connection<S> connection) {
            addCookie(userId, connection);
        }

        private void addCookie(String userId, Connection<S> connection){
            HttpServletResponse response = Utils.getCurrentResponse();
            Cookie cookie = new Cookie(COOKIE_NAME, userId);
            cookie.setPath("/");
            int maxAge = 2 * 3600;
            ConnectionData data = connection.createData();
            if (data.getExpireTime() != null){
                maxAge = (int)((data.getExpireTime() - System.currentTimeMillis())/1000);
            }
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);
        }
    }

    private CookieUserIdSource userIdSource = new CookieUserIdSource();
    private CookieConnectionSignUp connectionSignUp = new CookieConnectionSignUp();

    public UserIdSource getUserIdSource() {
        return userIdSource;
    }

    public AppEngineUsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        AppEngineUsersConnectionRepository repository = super.getUsersConnectionRepository(connectionFactoryLocator);

        repository.setConnectionSignUp(connectionSignUp);
        repository.addInterceptor(new CookieConnectionInterceptor());

        return repository;
    }
}
