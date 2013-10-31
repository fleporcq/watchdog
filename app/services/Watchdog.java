package services;

import models.WebApp;
import models.WebRequest;
import notifiers.MailNotifier;
import play.Logger;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.*;
import java.util.List;

public class Watchdog implements Runnable {

    private final static int CONNECT_TIMEOUT = 10000;//10 seconds
    private final static int READ_TIMEOUT = 10000;//10 seconds

    private WebApp webApp;

    public Watchdog(WebApp webApp) {
        this.webApp = webApp;
    }

    public void run() {
        WebRequest webRequest = null;

        try {
            webRequest = request();
        } catch (MalformedURLException e) {
            webRequest = new WebRequest(webApp, WebRequest.Error.MALFORMED_URL);
            Logger.error("Malformed url : " + webApp.url);
        } catch (UnknownHostException e) {
            webRequest = new WebRequest(webApp, WebRequest.Error.UNKNOWN_HOST);
            Logger.error("Unknown host : " + webApp.url);
        } catch (SocketTimeoutException e) {
            webRequest = new WebRequest(webApp, WebRequest.Error.TIMEOUT);
            Logger.error("Read timeout : " + webApp.url);
        } catch (IOException e) {
            webRequest = new WebRequest(webApp, WebRequest.Error.UNKNOWN_ERROR);
            e.printStackTrace();
        }

        if (webRequest != null) {
            if (webApp.getWatchersList().size() > 0) {
                notifyWatchersIfWebAppStateHasChanged(webRequest);
            }
            saveWebRequest(webRequest);
        }
    }

    private void notifyWatchersIfWebAppStateHasChanged(WebRequest webRequest) {

        WebRequest lastWebRequest = getLastWebRequest();

        if (webRequest.hasError()) {
            if (lastWebRequest == null || !webRequest.error.equals(lastWebRequest.error)) {
                MailNotifier.webRequestError(webApp, webRequest);
            }
        } else {
            if (lastWebRequest == null || !webRequest.responseStatus.equals(lastWebRequest.responseStatus)) {
                if (webRequest.hasFailedStatus()) {
                    MailNotifier.webAppLost(webApp, webRequest);
                }
                if (webRequest.hasWarningStatus()) {
                    MailNotifier.webAppWarn(webApp, webRequest);
                }
            }
            if (lastWebRequest != null && !webRequest.responseStatus.equals(lastWebRequest.responseStatus)) {
                if (webRequest.hasSuccessfulStatus()) {
                    MailNotifier.webAppRetrieved(webApp, webRequest);
                }
            }
        }
    }


    private WebRequest request() throws IOException {
        long startTime = System.currentTimeMillis();

        URL url = new URL(webApp.url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        int responseCode = connection.getResponseCode();
        long endTime = System.currentTimeMillis();

        connection.disconnect();

        return new WebRequest(webApp, responseCode, endTime - startTime);
    }

    private void saveWebRequest(WebRequest webRequest) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            EntityManagerHelper.beginTransaction();
            em.persist(webRequest);
            EntityManagerHelper.commit();
        } catch (Exception e) {
            Logger.error("Error persisting webRequest for webApp (" + webApp.name + ") : " + e.getMessage());
            EntityManagerHelper.rollback();
        } finally {
            EntityManagerHelper.closeEntityManager();
        }

    }

    private WebRequest getLastWebRequest() {
        WebRequest lastWebRequest = null;
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            EntityManagerHelper.beginTransaction();
            List<WebRequest> webRequests = em.createQuery("SELECT wr FROM WebRequest wr WHERE wr.webApp.id = :id ORDER BY wr.date DESC", WebRequest.class).setParameter("id", webApp.id).setMaxResults(1).getResultList();
            lastWebRequest = webRequests.size() > 0 ? webRequests.get(0) : null;
            EntityManagerHelper.commit();
        } catch (Exception e) {
            Logger.error("Error selecting last webRequest for webApp (" + webApp.name + ") : " + e.getMessage());
            EntityManagerHelper.rollback();
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
        return lastWebRequest;
    }
}