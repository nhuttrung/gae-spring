* Notes:

1. AppEngineSocialConfigurer --(create)--> AppEngineUsersConnectionRepository --(create)--> AppEngineConnectionRepository

@see org.springframework.social.config.annotation.SocialConfiguration#usersConnectionRepository

In a @Configuration class (e.g your SocialConfig class), create a new @Bean of AppEngineSocialConfigurer.
Example:
@Configuration
public class SocialConfig {
  @Bean
  public SocialConfigurer socialConfigurerAdapter() {
    return new SessionAppEngineSocialConfigurer();
    // or
    return new CookieAppEngineSocialConfigurer();
  }
}

2. Spring Social has two main controllers:
a) org.springframework.social.connect.web.ConnectController
b) org.springframework.social.connect.web.ProviderSignInController
I prefer to use ProviderSignInController in my apps


* Good articles
https://juplo.de/develop-a-facebook-app-with-spring-social-part-05-refactor-the-redirect-logic/
