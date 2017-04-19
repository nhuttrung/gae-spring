package vn.khtt.gae.spring.social;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;

/**
 * Persistent for ConnectionData
 */
@Entity
public class UserConnection {
  @Id
  private String id;
  
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
  
  private Long expireTime;

  private UserConnection() {
  }
  public UserConnection(Connection connection) {
    this(Utils.getUserId(connection), connection.createData());
  }
  private UserConnection(String userId, ConnectionData data) {
    this(userId, data.getProviderId(), data.getProviderUserId(), data.getDisplayName(), 
         data.getProfileUrl(), data.getImageUrl(), data.getAccessToken(), 
         data.getSecret(), data.getRefreshToken(), data.getExpireTime());
  }
  private UserConnection(String userId, String providerId, String providerUserId, String displayName, String profileUrl, String imageUrl, String accessToken, String secret, String refreshToken, Long expireTime) {
//    StringBuilder sb = new StringBuilder(providerId).append("-").append(providerUserId);
//    this.id = sb.toString();
    this.id = providerId + "-" + providerUserId;
    this.userId = userId;

    this.providerId = providerId;
    this.providerUserId = providerUserId;
    this.displayName = displayName;
    this.profileUrl = profileUrl;
    this.imageUrl = imageUrl;
    this.accessToken = accessToken;
    this.secret = secret;
    this.refreshToken = refreshToken;
    this.expireTime = expireTime;
  }
  
  public ConnectionData getConnectionData(){
    return new ConnectionData(providerId, providerUserId, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime);
  }
}
