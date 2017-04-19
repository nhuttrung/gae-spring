package vn.khtt.gae.spring.social;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

public class ProviderUserIdConnectionSignUp implements ConnectionSignUp {
  public ProviderUserIdConnectionSignUp() {
  }

  @Override
  public String execute(Connection<?> connection) {
    return Utils.getUserId(connection);
  }
}
