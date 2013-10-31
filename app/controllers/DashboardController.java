package controllers;

import models.WebApp;

import java.util.List;

public class DashboardController extends MainController {

    public static void index() {
        List<WebApp> webApps = WebApp.allOrdered();
        render(webApps);
    }

}