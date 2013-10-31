package jobs;

import models.WebRequest;
import play.Play;
import play.jobs.Every;
import play.jobs.Job;

@Every("24h")
public class DeleteOldWebRequestsJob extends Job{
    public void doJob(){
        int keepWebRequestsMaxDays = Integer.valueOf(Play.configuration.getProperty("watchdog.keepWebRequestsMaxDays","7"));
        WebRequest.deleteOldWebRequests(keepWebRequestsMaxDays);
    }

}
