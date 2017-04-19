package vn.khtt.gae.spring.social;

import org.springframework.social.connect.Connection;

/**
 * Interface used for updating userId when a social Connection is established
 */
public interface UserIdUpdater {
  void updateUserId(Connection<?> connection);
}
