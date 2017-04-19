# gae-spring: Spring on AppEngine

## Install
```
- git clone https://github.com/nhuttrung/gae-spring.git
- gradle install
```

## Usage
### ResourceBundle
When using Spring's ResourceBundle on AppEngine, an exeption throws:
java.util.ResourceBundle$Control is a restricted class. Please see the Google App Engine developer's guide for more details.

To fix this issue, in your @Configuration class, setup AppEngineResourceBundleMessageSource bean:
```Java
@Configuration
public class WebConfig {
  @Bean
  public MessageSource messageSource(){
    return new AppEngineResourceBundleMessageSource();
  }
}
```


### Spring Social
In your @Configuration class, setup AppEngineSocialConfigurer bean:
```Java
@Configuration
public class SocialConfig {
  @Bean
  public SocialConfigurer socialConfigurerAdapter() {
    return new AppEngineSocialConfigurer();
  }
}
```
If you are using org.springframework.social.connect.web.ConnectController, no additional configuration required
If you are using org.springframework.social.connect.web.ProviderSignInController, you need to configure a SignInAdapter bean:
```Java
@Configuration
public class SocialConfig {
  ...
  
  public SignInAdapter signInAdapter(){
    return new SignInAdapter(){
      @Override
      public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
        return null;  // Or a url to tell Spring Social forward to once user signed in your app
      }
    };
  }
}
```
