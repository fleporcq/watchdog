package models;

import play.Play;
import play.cache.Cache;
import play.mvc.Scope;

import java.io.Serializable;

public final class CachedSession implements Serializable {

    private User user;


    private CachedSession() {
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public static CachedSession current() {
        CachedSession cachedSession = null;
        if (Scope.Session.current() != null) {
            String id = Scope.Session.current().getId();
            cachedSession = (CachedSession) Cache.get(id);
            if (cachedSession == null) {
                cachedSession = new CachedSession();
                String cachedSessionExpiration = Play.configuration.getProperty("cachedSession.expiration", "1h");
                Cache.set(id, cachedSession, cachedSessionExpiration);
            }
        }
        return cachedSession;
    }

    public static void destroy() {
        String id = Scope.Session.current().getId();
        Cache.delete(id);
    }

}
