package models;

import org.apache.commons.lang.StringUtils;
import play.data.validation.*;
import play.db.jpa.Model;
import services.Scheduler;
import services.SchedulerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity
public class WebApp extends Model {


    private static final Long DELAY = 1L;

    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    @OneToMany(mappedBy = "webApp", cascade = CascadeType.ALL)
    public List<WebRequest> webRequests;

    @Required
    @MaxSize(50)
    @Unique
    public String name;

    @Required
    @URL
    public String url;

    @Required
    @Min(1)
    @Max(1440)
    public Long requestDelay;

    public String watchers;

    @Transient
    private WebRequest lastWebRequest;

    public boolean isWatched() {
        boolean watched = false;
        Scheduler scheduler = SchedulerFactory.getScheduler(this);
        if (scheduler != null) {
            watched = scheduler.isRunning();
        }
        return watched;
    }

    public static List<WebApp> allOrdered(){
        return WebApp.find("ORDER BY name ASC").fetch();
    }

    public List<String> getWatchersList() {
        List<String> watchersList = new ArrayList<String>();
        if (watchers != null) {
            String[] emails = watchers.split(",");
            for (String email : emails) {
                email = email.trim();
                if(StringUtils.isNotBlank(email)){
                    watchersList.add(email);
                }
            }
        }
        return watchersList;
    }

    private void formatWatchers() {
        List<String> watchers = this.getWatchersList();
        StringBuilder watchersBuilder = new StringBuilder();
        int i = 0;
        for (String watcher : watchers) {
            i++;
            watchersBuilder.append(watcher);
            if (i < watchers.size()) {
                watchersBuilder.append(" , ");
            }
        }
        this.watchers = watchersBuilder.toString();
    }

    public void startWatchdog() {
        Scheduler scheduler = SchedulerFactory.getScheduler(this);
        if (scheduler == null) {
            scheduler = SchedulerFactory.createScheduler(this, requestDelay, TIME_UNIT);
        }
        if (scheduler != null && !scheduler.isRunning()) {
            scheduler.start();
        }

    }

    public void stopWatchdog() {
        Scheduler scheduler = SchedulerFactory.getScheduler(this);
        if (scheduler != null && scheduler.isRunning()) {
            scheduler.stop();
        }
    }

    public WebRequest getLastWebRequest() {
        if (lastWebRequest == null) {
            lastWebRequest = WebRequest.findLastWebRequestForWebApp(id);
        }
        return lastWebRequest;
    }

    public List<WebRequest> getAccomplishedWebRequest(){
        return WebRequest.find("webApp.id = ? AND error IS NULL ORDER BY date ASC", id).fetch();
    }

    @Override
    public WebApp save() {
        formatWatchers();
        SchedulerFactory.updateScheduler(this);
        return super.save();
    }

    @Override
    public WebApp delete() {
        SchedulerFactory.deleteScheduler(this);
        return super.delete();
    }
}
