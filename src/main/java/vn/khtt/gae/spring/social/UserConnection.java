package vn.khtt.gae.spring.social;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;

import java.io.Serializable;

/**
 * Persistent for ConnectionData
 */
@Entity
public class UserConnection implements Serializable {
  @Id
  private Long id;

  @Index
  private String userId;
  
  @Index
  private String providerId;
  
  @Index
  private String providerUserId;
  
  private String displayName;
  
  private String profileUrl;
  
  private String imageUrl;
  
  private String accessToken;
  
  private String secret;
  
  private String refreshToken;

  @Index
  private Long expireTime;

  private UserConnection() {
  }

  public UserConnection(String userId, Connection connection) {
    this.userId = userId;
    setConnectionData(connection.createData());
  }

  public void setConnectionData(ConnectionData data) {
    this.providerId = data.getProviderId();
    this.providerUserId = data.getProviderUserId();
    this.displayName = data.getDisplayName();
    this.profileUrl = data.getProfileUrl();
    this.imageUrl = data.getImageUrl();
    this.accessToken = data.getAccessToken();
    this.secret = data.getSecret();
    this.refreshToken = data.getRefreshToken();
    this.expireTime = data.getExpireTime();
  }

  public ConnectionData getConnectionData(){
    return new ConnectionData(providerId, providerUserId, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime);
  }

  public String getUserId() {
    return userId;
  }

}
