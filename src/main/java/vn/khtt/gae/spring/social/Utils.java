package vn.khtt.gae.spring.social;

import javax.servlet.http.HttpServletRequest;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Utils {
  /**
   * The userId is combined of providerId and providerUserId, 
   * e.g userId = providerId + "-" + providerUserId
   * @param connection
   * @return
   */
  public static String getUserId(Connection<?> connection){
    ConnectionKey key = connection.getKey();
    String userId = key.getProviderId() + "-" + key.getProviderUserId();
    
    return userId;
  }

  public static HttpServletRequest getCurrentRequest(){
    ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = sra.getRequest();

    return request;
  }

  public static void notImplemented(){
    RuntimeException e = new RuntimeException("Not implemented!");
    e.printStackTrace();
    throw e;
  }
}
