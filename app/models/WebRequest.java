package models;

import play.db.jpa.Model;

import javax.persistence.*;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class WebRequest extends Model {

    @ManyToOne
    @JoinColumn(name = "webapp_id")
    public WebApp webApp;

    public Date date;

    //TODO : Créer un objet embedded Response

    public Integer responseCode;

    @Enumerated(EnumType.STRING)
    public Response.Status responseStatus;

    @Enumerated(EnumType.STRING)
    public Response.Status.Family responseStatusFamily;

    public Long responseTime;

    @Enumerated(EnumType.STRING)
    public Error error;

    public WebRequest(WebApp webApp, Integer responseCode, Long responseTime) {
        this.webApp = webApp;
        this.date = new Date();
        this.responseCode = responseCode;
        this.responseStatus = getStatus(responseCode);
        this.responseStatusFamily = getStatusFamily(responseCode);
        this.responseTime = responseTime;
    }

    public WebRequest(WebApp webApp, Error error) {
        this.webApp = webApp;
        this.date = new Date();
        this.error = error;
    }

    private Response.Status getStatus(Integer code) {
        return code != null ? Response.Status.fromStatusCode(code) : null;
    }

    private Response.Status.Family getStatusFamily(Integer code) {
        Response.Status status = getStatus(code);
        return status != null ? status.getFamily() : null;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean hasSuccessfulStatus() {
        return Response.Status.Family.SUCCESSFUL.equals(this.responseStatusFamily);
    }

    public boolean hasFailedStatus() {
        return Response.Status.Family.CLIENT_ERROR.equals(this.responseStatusFamily) ||
                Response.Status.Family.SERVER_ERROR.equals(this.responseStatusFamily);
    }

    public boolean hasWarningStatus() {
        return Response.Status.Family.REDIRECTION.equals(this.responseStatusFamily) ||
                Response.Status.Family.OTHER.equals(this.responseStatusFamily) ||
                Response.Status.Family.INFORMATIONAL.equals(this.responseStatusFamily);
    }

    public static WebRequest findLastWebRequestForWebApp(Long id) {
        return WebRequest.find("webApp.id = ? ORDER BY date DESC", id).first();
    }

    public static void deleteOldWebRequests(int day) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -day);
        WebRequest.delete("date < ?", c.getTime());
    }

    public enum Error {
        MALFORMED_URL, UNKNOWN_HOST, TIMEOUT, UNKNOWN_ERROR
    }
}
