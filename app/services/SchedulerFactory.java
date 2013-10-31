package services;

import models.WebApp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SchedulerFactory {

    private static final int THREAD_POOL_SIZE = 3;

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);

    private Map<WebApp, Scheduler> schedulers = new HashMap<WebApp, Scheduler>();

    private static SchedulerFactory INSTANCE = new SchedulerFactory();

    private SchedulerFactory() {
    }

    private static SchedulerFactory getInstance() {
        return INSTANCE;
    }

    public static Scheduler createScheduler(WebApp webApp, Long delay, TimeUnit unit) {
        SchedulerFactory factory = SchedulerFactory.getInstance();
        Scheduler scheduler = new Scheduler(EXECUTOR_SERVICE, webApp, delay, unit);
        factory.schedulers.put(webApp, scheduler);
        return scheduler;
    }

    public static Scheduler deleteScheduler(WebApp webApp) {
        SchedulerFactory factory = SchedulerFactory.getInstance();
        Scheduler scheduler = factory.schedulers.remove(webApp);
        if (scheduler != null) {
            scheduler.stop();
        }
        return scheduler;
    }

    public static Scheduler updateScheduler(WebApp webApp) {
        Scheduler scheduler = SchedulerFactory.getScheduler(webApp);
        if (scheduler != null) {
            scheduler.updateWebApp(webApp);
            if (scheduler.isRunning()) {
                scheduler.stop();
                scheduler.start();
            }
        }
        return scheduler;
    }


    public static Scheduler getScheduler(WebApp webApp) {
        SchedulerFactory factory = SchedulerFactory.getInstance();
        return factory.schedulers.get(webApp);
    }

    public static Map<WebApp, Scheduler> getSchedulers() {
        SchedulerFactory factory = SchedulerFactory.getInstance();
        return factory.schedulers;
    }
}
