package vn.khtt.gae.spring.social;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.social.UserIdSource;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A UserIdSource which use HttpSession to store and retrieve userId
 */
public class SessionUserIdHandler implements UserIdSource, UserIdUpdater, SignInAdapter {
  private static final String USER_ID = "SPRING_SOCIAL_USER_ID";

  public SessionUserIdHandler() {
  }

  @Override
  public String getUserId() {
    HttpServletRequest request = Utils.getCurrentRequest();
    HttpSession session = request.getSession(true);
    String userId = (String)session.getAttribute(USER_ID);
    
    return userId;
  }
  
  @Override
  public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
    HttpServletRequest req = Utils.getCurrentRequest();
    HttpSession session = req.getSession(true);
    
    session.setAttribute(USER_ID, userId);
    
    return null;
  }

  @Override
  public void updateUserId(Connection<?> connection) {
    HttpServletRequest req = Utils.getCurrentRequest();
    HttpSession session = req.getSession(true);

    String userId = Utils.getUserId(connection);
    session.setAttribute(USER_ID, userId);
  }
}
