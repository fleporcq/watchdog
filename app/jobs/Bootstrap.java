package jobs;

import models.Role;
import models.User;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.io.IOException;

@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() throws IOException {
        if(User.findByUsername("admin") == null){
            User user = new User();
            user.username = "admin";
            user.newPassword = "admin";
            user.role = Role.ADMIN;
            user.save();
        }
    }

}