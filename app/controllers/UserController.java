package controllers;


import models.User;
import play.data.validation.Valid;
import play.data.validation.Validation;

import java.util.List;

@Check("ADMIN")
public class UserController extends MainController {


    public static void add() {
        render("UserController/form.html");
    }

    public static void edit(Long id) {
        notFoundIfNull(id);
        User user = User.findById(id);
        notFoundIfNull(user);
        render("UserController/form.html", user);
    }

    public static void delete(Long id) {
        notFoundIfNull(id);
        User user = User.findById(id);
        notFoundIfNull(user);
        User connectedUser = SecurityController.connectedUser();
        if (user.id.equals(connectedUser.id)) {
            flash.error("You can't delete yourself !");
        } else {
            user.delete();
        }
        list();
    }

    public static void list() {
        List<User> users = User.allOrdered();
        render(users);
    }

    public static void save(@Valid User user) {
        notFoundIfNull(user);

        if (user.id == null) {
            validation.required(user.newPassword).key("user.newPassword");
        }

        if (!Validation.hasErrors()) {
            user.save();
            UserController.list();
        } else {
            Validation.keep();
            params.flash();
            if (user.id != null) {
                edit(user.id);
            } else {
                add();
            }
        }
    }

}
