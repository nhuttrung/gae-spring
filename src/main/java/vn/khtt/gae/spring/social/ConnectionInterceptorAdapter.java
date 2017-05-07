package vn.khtt.gae.spring.social;

import org.springframework.social.connect.Connection;

import java.util.Collection;

public class ConnectionInterceptorAdapter<S> implements ConnectionInterceptor<S> {
    @Override
    public void beforeCreate(String userId, Connection<S> connection) {
    }

    @Override
    public void afterCreate(String userId, Connection<S> connection) {
    }

    @Override
    public void beforeUpdate(String userId, Connection<S> connection) {
    }

    @Override
    public void afterUpdate(String userId, Connection<S> connection) {
    }

    @Override
    public void beforeRemove(String userId, Collection<? extends Connection<S>> connections) {
    }

    @Override
    public void afterRemove(String userId, Collection<? extends Connection<S>> connections) {
    }
}
