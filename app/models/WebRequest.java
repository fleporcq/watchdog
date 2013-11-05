package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
public class WebRequest extends Model {

    @ManyToOne
    @JoinColumn(name = "webapp_id")
    public WebApp webApp;

    public Date date;

    @Embedded
    public WebResponse webResponse;

    @Enumerated(EnumType.STRING)
    public Error error;

    public boolean flagged;

    public WebRequest(WebApp webApp, WebResponse webResponse) {
        this.webApp = webApp;
        this.date = new Date();
        this.webResponse = webResponse;
    }

    public WebRequest(WebApp webApp, Error error) {
        this.webApp = webApp;
        this.date = new Date();
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean flag() {
        return flagged = true;
    }

    public static WebRequest findLastWebRequestForWebApp(Long id) {
        return WebRequest.find("webApp.id = ? ORDER BY date DESC", id).first();
    }

    public static void deleteOldWebRequests(int maxAge) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -maxAge);
        WebRequest.delete("date < ? AND flagged = ?", c.getTime(), false);
    }

    public enum Error {
        MALFORMED_URL, UNKNOWN_HOST, TIMEOUT, UNKNOWN_ERROR
    }

    public boolean hasNewError(WebRequest lastWebRequest) {
        return this.hasError() && (lastWebRequest == null || !this.error.equals(lastWebRequest.error));
    }

    public boolean hasNewResponseStatus(WebRequest lastWebRequest) {
        return !this.hasError() && (lastWebRequest == null || lastWebRequest.hasError() || !this.webResponse.status.equals(lastWebRequest.webResponse.status));
    }

}
