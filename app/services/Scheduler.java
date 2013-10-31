package services;

import models.WebApp;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private ScheduledExecutorService executorService;

    private Future<?> future;

    private WebApp webApp;

    private Long delay;

    private TimeUnit unit;

    public Scheduler(ScheduledExecutorService executorService, WebApp webApp, Long delay, TimeUnit unit) {
        this.executorService = executorService;
        this.webApp = webApp;
        this.delay = delay;
        this.unit = unit;
    }

    public void start() {
        future = executorService.scheduleWithFixedDelay(new Watchdog(webApp), 0, delay, unit);
    }

    public void updateWebApp(WebApp webApp){
        this.webApp = webApp;
    }

    public void stop() {
        if (!future.isCancelled()) {
            future.cancel(true);
        }
    }

    public boolean isRunning() {
        return future != null && !future.isCancelled() && !future.isDone();
    }


}
