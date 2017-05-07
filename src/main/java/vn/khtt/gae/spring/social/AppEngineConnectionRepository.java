package vn.khtt.gae.spring.social;

import static com.googlecode.objectify.ObjectifyService.ofy;
import com.googlecode.objectify.cmd.QueryKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import java.util.Set;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AppEngineConnectionRepository implements ConnectionRepository {
  private final ConnectionFactoryLocator connectionFactoryLocator;
  private String userId;
  private final MultiValueMap<Class<?>, ConnectionInterceptor<?>> interceptors = new LinkedMultiValueMap<Class<?>, ConnectionInterceptor<?>>();
  
  public AppEngineConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator, String userId) {
    this.connectionFactoryLocator = connectionFactoryLocator;
    this.userId = userId;
  }

  /**
   * Configure the list of interceptors that should receive callbacks during the connection CRUD operations.
   * @param interceptors the connect interceptors to add
   */
  public void setInterceptors(List<ConnectionInterceptor<?>> interceptors) {
    for (ConnectionInterceptor<?> interceptor : interceptors) {
      addInterceptor(interceptor);
    }
  }

  /**
   * Adds a ConnectionInterceptor to receive callbacks during the connection CRUD operations.
   * @param interceptor the connection interceptor to add
   */
  public void addInterceptor(ConnectionInterceptor<?> interceptor) {
    Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(), ConnectionInterceptor.class);
    interceptors.add(serviceApiType, interceptor);
  }

  @Override
  public MultiValueMap<String, Connection<?>> findAllConnections() {
    List<UserConnection> userConnections = ofy().load().type(UserConnection.class)
      .filter("userId", userId).list();
    
    LinkedMultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
    Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
    for (String registeredProviderId : registeredProviderIds) {
      connections.put(registeredProviderId, Collections.<Connection<?>>emptyList());
    }

    for (UserConnection userConnection : userConnections){
      Connection<?> connection = this.toConnection(userConnection);
      String providerId = connection.getKey().getProviderId();
      if (connections.get(providerId).size() == 0) {
        connections.put(providerId, new LinkedList<Connection<?>>());
      }
      connections.add(providerId, connection);
    }
    
    return connections;
  }

  @Override
  public List<Connection<?>> findConnections(String providerId) {
    List<UserConnection> userConnections = ofy().load().type(UserConnection.class)
      .filter("userId", userId).filter("providerId", providerId).list();
    
    List<Connection<?>> connections = new ArrayList<Connection<?>>();
    for (UserConnection userConnection : userConnections){
      connections.add(this.toConnection(userConnection));
    }
    
    return connections;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <A> List<Connection<A>> findConnections(Class<A> apiType) {
    String providerId = getProviderId(apiType);
    List<?> providerConnections = findConnections(providerId);
    return (List<Connection<A>>)providerConnections;
  }

  @Override
  public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
    Utils.notImplemented();
    // TODO Implement this method
    
    MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();
    
    return connectionsForUsers;
  }

  @Override
  public Connection<?> getConnection(ConnectionKey connectionKey) {
    List<UserConnection> userConnections = ofy().load().type(UserConnection.class)
      .filter("userId", userId)
      .filter("providerId", connectionKey.getProviderId())
      .filter("providerUserId", connectionKey.getProviderUserId()).list();
    
    if (userConnections.size() > 0){
      return toConnection(userConnections.get(0));
    } else{
      throw new NoSuchConnectionException(connectionKey);
    }
  }

  @Override
  @SuppressWarnings({ "cast", "unchecked" })
  public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
    String providerId = getProviderId(apiType);
    return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
  }

  @Override
  @SuppressWarnings({"unchecked", "cast"})
  public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
    String providerId = getProviderId(apiType);
    Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
    if (connection == null) {
      throw new NotConnectedException(providerId);
    }
    return connection;
  }

  @Override
  @SuppressWarnings({"unchecked", "cast"})
  public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
    String providerId = getProviderId(apiType);
    Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
    
    return connection;
  }

  @Override
  public void addConnection(Connection<?> connection) {
    for (ConnectionInterceptor interceptor : interceptingConnectionsTo(connection)) {
      interceptor.beforeCreate(userId, connection);
    }

    saveConnection(connection);

    for (ConnectionInterceptor interceptor : interceptingConnectionsTo(connection)) {
      interceptor.afterCreate(userId, connection);
    }
  }

  @Override
  public void updateConnection(Connection<?> connection) {
    for (ConnectionInterceptor interceptor : interceptingConnectionsTo(connection)) {
      interceptor.beforeUpdate(userId, connection);
    }

    saveConnection(connection);

    for (ConnectionInterceptor interceptor : interceptingConnectionsTo(connection)) {
      interceptor.afterUpdate(userId, connection);
    }
  }

  @Override
  public void removeConnections(String providerId) {
    QueryKeys<UserConnection> keys = ofy().load().type(UserConnection.class)
      .filter("providerId", providerId)
      .keys();
    ofy().delete().keys(keys);
  }

  @Override
  public void removeConnection(ConnectionKey connectionKey) {
    QueryKeys<UserConnection> keys = ofy().load().type(UserConnection.class)
      .filter("providerId", connectionKey.getProviderId())
      .filter("providerUserId", connectionKey.getProviderUserId()).keys();
    ofy().delete().keys(keys);
  }

  private <A> String getProviderId(Class<A> apiType) {
    return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
  }
  
  private Connection<?> findPrimaryConnection(String providerId) {
    List<UserConnection> userConnections = ofy().load().type(UserConnection.class)
      .filter("userId", userId).filter("providerId", providerId).list();
    
    if (userConnections.size() > 0){
      Connection<?> connection = toConnection(userConnections.get(0));
      return connection;
    }

    return null;
  }
  
  private void saveConnection(Connection<?> connection) {
    if (userId == null){
      System.out.println("WARN: userId == null");
      return;
    }

    ConnectionData connectionData = connection.createData();
    List<UserConnection> userConnections = ofy().load().type(UserConnection.class)
            .filter("userId", userId)
            .filter("providerId", connectionData.getProviderId())
            .filter("providerUserId", connectionData.getProviderUserId()).list();
    if (userConnections.size() == 0){
      UserConnection userConnection = new UserConnection(userId, connection);
      ofy().save().entity(userConnection).now();
    }else {
      for (UserConnection userConnection : userConnections){
        userConnection.setConnectionData(connectionData);
        ofy().save().entities(userConnections).now();
      }
    }
  }

  private Connection toConnection(UserConnection userConnection){
    ConnectionData connectionData = userConnection.getConnectionData();
    ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
    return connectionFactory.createConnection(connectionData);
  }

  private List<ConnectionInterceptor<?>> interceptingConnectionsTo(Connection<?> connection) {
    Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connection.getClass(), Connection.class);
    List<ConnectionInterceptor<?>> typedInterceptors = interceptors.get(serviceType);
    if (typedInterceptors == null) {
      typedInterceptors = Collections.emptyList();
    }
    return typedInterceptors;
  }
}
