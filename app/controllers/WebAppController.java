package controllers;

import models.WebApp;
import models.WebRequest;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Util;

import java.util.List;

public class WebAppController extends MainController {

    public static void add() {
        render("WebAppController/form.html");
    }

    public static void edit(Long id) {
        notFoundIfNull(id);
        WebApp webApp = WebApp.findById(id);
        notFoundIfNull(webApp);
        render("WebAppController/form.html", webApp);
    }

    public static void delete(Long id) {
        notFoundIfNull(id);
        WebApp webApp = WebApp.findById(id);
        notFoundIfNull(webApp);
        webApp.delete();
        flash.success("%s WebApp successfully deleted !", webApp.name);
        DashboardController.index();
    }

    public static void save(@Valid WebApp webApp) {
        notFoundIfNull(webApp);

        validateWatchers(webApp);

        if (!Validation.hasErrors()) {
            if (webApp.id != null) {
                flash.success("%s WebApp successfully updated !", webApp.name);
            } else {
                flash.success("%s WebApp successfully created !", webApp.name);
            }

            webApp.save();
            DashboardController.index();
        } else {
            Validation.keep();
            params.flash();
            if (webApp.id != null) {
                edit(webApp.id);
            } else {
                add();
            }
        }
    }

    @Util
    private static void validateWatchers(WebApp webApp) {
        List<String> watchers = webApp.getWatchersList();
        for (String email : watchers) {
            if (!validation.email(email).key("webApp.watchers").ok) {
                break;
            }
        }
    }

    public static void start(Long id) {
        notFoundIfNull(id);
        WebApp webApp = WebApp.findById(id);
        notFoundIfNull(webApp);
        if (!webApp.isWatched()) {
            webApp.startWatchdog();
        }

        DashboardController.index();
    }

    public static void stop(Long id) {
        notFoundIfNull(id);
        WebApp webApp = WebApp.findById(id);
        notFoundIfNull(webApp);
        if (webApp.isWatched()) {
            webApp.stopWatchdog();
        }
        DashboardController.index();
    }

    public static void startAll() {
        List<WebApp> webApps = WebApp.findAll();
        for (WebApp webApp : webApps) {
            if (!webApp.isWatched()) {
                webApp.startWatchdog();
            }
        }
        DashboardController.index();
    }

    public static void stopAll() {
        List<WebApp> webApps = WebApp.findAll();
        for (WebApp webApp : webApps) {
            if (webApp.isWatched()) {
                webApp.stopWatchdog();
            }
        }
        DashboardController.index();
    }

    public static void details(Long id) {
        notFoundIfNull(id);
        WebApp webApp = WebApp.findById(id);
        notFoundIfNull(webApp);
        render(webApp);
    }
}
