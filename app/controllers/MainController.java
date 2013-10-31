package controllers;


import models.User;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class MainController extends Controller{

    @Before
    public static void before() {
        User connectedUser = SecurityController.connectedUser();
        if (connectedUser != null) {
            renderArgs.put("connectedUser", connectedUser);
        }
        renderArgs.put("applicationName", Play.configuration.getProperty("application.name"));
        renderArgs.put("applicationVersion", Play.configuration.getProperty("application.version"));
    }

}
