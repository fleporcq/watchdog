package controllers;

import models.CachedSession;
import models.User;

import java.util.Arrays;

public class SecurityController extends Secure.Security {

    public static boolean authenticate(String login, String password) {
        User user = User.findByUsername(login);
        if (user != null && user.checkPassword(password)) {
            return true;
        }
        return false;
    }

    public static boolean check(String sRoles) {
        User user = connectedUser();
        if (sRoles != null && user != null) {
            String[] roles = sRoles.split(",");
            if (Arrays.asList(roles).contains(user.role.name())) {
                return true;
            }
        }
        return false;
    }

    public static User connectedUser() {
        User user = null;
        if (session != null) {
            CachedSession cachedSession = CachedSession.current();
            if (cachedSession != null) {
                user = cachedSession.getUser();
            }
            if (user == null) {

                user = User.findByUsername(connected());
                cachedSession.setUser(user);
            }
        }

        return user;
    }

    static void onDisconnect() {
        CachedSession.destroy();
    }
}
