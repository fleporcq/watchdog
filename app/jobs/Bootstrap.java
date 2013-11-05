package jobs;

import models.Role;
import models.User;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.io.IOException;

@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() throws IOException {
        if(User.getAdminCount() == 0){
            User user = new User();
            user.username = "admin";
            user.newPassword = "admin";
            user.role = Role.ADMIN;
            user.save();
            Logger.info("'%s' user (with password '%s') has been successfully created!", user.username, user.newPassword);
        }
    }

}