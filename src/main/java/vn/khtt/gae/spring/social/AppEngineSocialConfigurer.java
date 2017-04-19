/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vn.khtt.gae.spring.social;

import com.googlecode.objectify.ObjectifyService;

import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.SocialConfiguration;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * Default implementation of {@link SocialConfigurer} for AppEngine
 * @see SocialConfiguration#usersConnectionRepository
 */
public class AppEngineSocialConfigurer extends SocialConfigurerAdapter {
  public AppEngineSocialConfigurer() {
    ObjectifyService.register(UserConnection.class);
    ObjectifyService.register(UserProfile.class);
  }
  
  public UserIdSource getUserIdSource() {
    return new SessionUserIdHandler();
  }

  @Override
  public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
    AppEngineUsersConnectionRepository repository = new AppEngineUsersConnectionRepository(connectionFactoryLocator);
    repository.setConnectionSignUp(new ProviderUserIdConnectionSignUp());
    repository.setUserIdUpdater(new SessionUserIdHandler());
    return repository;
  }
}
