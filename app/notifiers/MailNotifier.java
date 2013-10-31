package notifiers;

import models.WebApp;
import models.WebRequest;
import play.Logger;
import play.Play;
import play.mvc.Mailer;

public class MailNotifier extends Mailer {

    private static void setFrom() {
        setFrom(Play.configuration.getProperty("mail.from", "watchdog@wyniwyg.com"));
    }

    private static String getSubjectPrefix(WebApp webApp) {
        return Play.configuration.getProperty("application.name", "Watchdog") + " - " + webApp.name + " WebApp - ";
    }

    public static void webAppLost(WebApp webApp, WebRequest lastWebRequest) {
        Logger.info("Sending email webApp lost : " + webApp.name + " - " + lastWebRequest.responseStatus);

        setSubject(getSubjectPrefix(webApp) + "Signal lost");
        for (String watcher : webApp.getWatchersList()) {
            addRecipient(watcher);
        }
        setFrom();
        send(webApp, lastWebRequest);
    }


    public static void webAppWarn(WebApp webApp, WebRequest lastWebRequest) {
        Logger.info("Sending email webApp warning : " + webApp.name + " - " + lastWebRequest.responseStatus);

        setSubject(getSubjectPrefix(webApp) + "Signal warning");
        for (String watcher : webApp.getWatchersList()) {
            addRecipient(watcher);
        }
        setFrom();
        send(webApp, lastWebRequest);
    }

    public static void webAppRetrieved(WebApp webApp, WebRequest lastWebRequest) {
        Logger.info("Sending email webApp retrieved : " + webApp.name + " - " + lastWebRequest.responseStatus);

        setSubject(getSubjectPrefix(webApp) + "Signal retrieved");
        for (String watcher : webApp.getWatchersList()) {
            addRecipient(watcher);
        }
        setFrom();
        send(webApp, lastWebRequest);
    }

    public static void webRequestError(WebApp webApp, WebRequest lastWebRequest) {
        Logger.info("Sending email webRequest error : " + webApp.name + " - " + lastWebRequest.error);

        setSubject(getSubjectPrefix(webApp) + "Request error");
        for (String watcher : webApp.getWatchersList()) {
            addRecipient(watcher);
        }
        setFrom();
        send(webApp, lastWebRequest);
    }


}
