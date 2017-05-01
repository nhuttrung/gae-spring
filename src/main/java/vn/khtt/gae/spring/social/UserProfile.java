package vn.khtt.gae.spring.social;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;

@Entity
public class UserProfile {
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

  private String name;

  private String firstName;

  private String lastName;

  private String email;

  private String username;

  private UserProfile(){
  }
  
  public UserProfile(Connection connection) {
    ConnectionData data = connection.createData();
    org.springframework.social.connect.UserProfile profile = connection.fetchUserProfile();
    
    this.id = data.getProviderId() + "-" + data.getProviderUserId();
    this.userId = Utils.getUserId(connection);
    this.providerId = data.getProviderId();
    this.providerUserId = data.getProviderUserId();
    this.displayName = data.getDisplayName();
    this.profileUrl = data.getProfileUrl();
    this.imageUrl = data.getImageUrl();
    
    this.name = profile.getName();
    this.firstName = profile.getFirstName();
    this.lastName = profile.getLastName();
    this.email = profile.getEmail();
    this.username = profile.getUsername();
  }
  
  public String getDisplayName(){
    return displayName;
  }

  public String getName(){
    return name;
  }
}
