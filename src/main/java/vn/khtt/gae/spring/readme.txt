
1. AppEngineResourceBundleMessageSource:
- Fixed issue: java.util.ResourceBundle$Control is a restricted class. Please see the Google App Engine developer's guide for more details.
- How to use:
In your @Configuration:
@Configuration
public class WebConfig {
  @Bean
  public MessageSource messageSource(){
    AbstractMessageSource messageSource = new AppEngineResourceBundleMessageSource();
    return messageSource;
  }
}

2. ApplicationContextHolder:
- Util class for getting the Spring's ApplicationContext
- How to use:
In your @Configuration:
@Configuration
public class WebConfig {
  @Bean
  public ApplicationContextHolder applicationContextHolder(){
    return new ApplicationContextHolder();
  }
}

In your app: 
ApplicationContext ctx = ApplicationContextHolder.getApplicationContext();
