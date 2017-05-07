package vn.khtt.gae.spring.social;

import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.SocialConfiguration;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.web.SessionUserIdSource;

/**
 * An implementation of {@link SocialConfigurer} for AppEngine, which use {@link SessionUserIdSource}
 * @see SocialConfiguration#usersConnectionRepository
 */
public class SessionAppEngineSocialConfigurer extends AppEngineSocialConfigurer {
    private SessionUserIdSource userIdSource = new SessionUserIdSource();

    private class SesssionConnectionSignUp implements ConnectionSignUp {
        @Override
        public String execute(Connection<?> connection) {
            return userIdSource.getUserId();
        }
    }

    public UserIdSource getUserIdSource() {
        return userIdSource;
    }

    public AppEngineUsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        AppEngineUsersConnectionRepository repository = super.getUsersConnectionRepository(connectionFactoryLocator);

        repository.setConnectionSignUp(new SesssionConnectionSignUp());

        return repository;
    }
}
