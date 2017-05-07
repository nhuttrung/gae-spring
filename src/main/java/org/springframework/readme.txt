
*** Android Studio requires JDK 8.
However, when runnung Spring Boot on DevAppServer we got following errors:

1. org.springframework.core.DefaultParameterNameDiscoverer
java.lang.NoClassDefFoundError: java.lang.reflect.Parameter is a restricted class.

==> Tweak by not using StandardReflectionParameterNameDiscoverer

2. org.springframework.format.support.DefaultFormattingConversionService
java.lang.NoClassDefFoundError: java.time.format.FormatStyle is a restricted class

==> Tweak by not using DateTimeFormatterRegistrar

*** Spring Social on AppEngine
https://github.com/vtserman/spring-social-appengine

3. org.springframework.social.facebook.api.UserOperations
org.springframework.social.UncategorizedApiException: (#12) bio field is deprecated for versions v2.8 and higher

https://github.com/spring-projects/spring-social-facebook/pull/218
