package controllers;

import models.CachedSession;
import models.User;
import play.data.validation.Valid;

public class AccountController extends MainController {

    public static void edit() {
        User user = SecurityController.connectedUser();
        notFoundIfNull(user);
        render("AccountController/form.html", user);
    }

    public static void save(@Valid User user) {
        notFoundIfNull(user);
        notFoundIfNull(user.id);

        User connectedUser = SecurityController.connectedUser();

        if (!user.id.equals(connectedUser.id)) {
            notFound();
        }

        if (!validation.hasErrors()) {
            user.save();
            flash.success("Account successfully updated !");
            DashboardController.index();
        }
        else {
            params.flash();
            validation.keep();
            edit();
        }

    }
}
