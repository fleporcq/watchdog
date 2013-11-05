package jobs;

import models.WebRequest;
import play.Logger;
import play.Play;
import play.jobs.Every;
import play.jobs.Job;

@Every("24h")
public class DeleteOldWebRequests extends Job{
    public void doJob(){
        int webRequestMaxAge = Integer.valueOf(Play.configuration.getProperty("watchdog.webRequestMaxAge","7"));
        WebRequest.deleteOldWebRequests(webRequestMaxAge);
        Logger.info("Requests aged more than %s days have been successfully deleted!", webRequestMaxAge);
    }

}
