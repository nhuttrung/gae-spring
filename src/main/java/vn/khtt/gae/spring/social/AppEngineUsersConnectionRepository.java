package vn.khtt.gae.spring.social;

import com.googlecode.objectify.Key;
import static com.googlecode.objectify.ObjectifyService.ofy;
import com.googlecode.objectify.cmd.QueryKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;

public class AppEngineUsersConnectionRepository implements UsersConnectionRepository {
  private final ConnectionFactoryLocator connectionFactoryLocator;
  private ConnectionSignUp connectionSignUp;
  private UserIdUpdater userIdUpdater;
  
  public AppEngineUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
    this.connectionFactoryLocator = connectionFactoryLocator;
  }

  public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
    this.connectionSignUp = connectionSignUp;
  }

  public void setUserIdUpdater(UserIdUpdater userIdUpdater) {
    this.userIdUpdater = userIdUpdater;
  }

  @Override
  public List<String> findUserIdsWithConnection(Connection<?> connection) {
    List<String> result = new ArrayList<String>();
    
    ConnectionKey key = connection.getKey();
    QueryKeys<UserConnection> keys = ofy().load().type(UserConnection.class)
      .filter("providerId", key.getProviderId())
      .filter("providerUserId", key.getProviderUserId()).keys();
    for (Key k : keys){
      result.add(k.getName());
    }
    
    if (result.size() == 0 && connectionSignUp != null){
      String newUserId = connectionSignUp.execute(connection);
      if (newUserId != null){
        createConnectionRepository(newUserId).addConnection(connection);
        return Arrays.asList(newUserId);
      }
    }
    
    return result;
  }

  @Override
  public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
    Set<String> result = new HashSet<String>();
    
    QueryKeys<UserConnection> keys = ofy().load().type(UserConnection.class)
      .filter("providerId", providerId)
      .filter("providerUserId in ", providerUserIds).keys();
    for (Key k : keys){
      result.add(k.getName());
    }
    
    return result;
  }

  @Override
  public ConnectionRepository createConnectionRepository(String userId) {
    AppEngineConnectionRepository repository = new AppEngineConnectionRepository(connectionFactoryLocator, userId);
    repository.setUserIdUpdater(userIdUpdater);
    return repository;
  }
}
