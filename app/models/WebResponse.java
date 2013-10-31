package models;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.ws.rs.core.Response;

@Embeddable
public class WebResponse {
    public Integer code;

    @Enumerated(EnumType.STRING)
    public Response.Status status;

    @Enumerated(EnumType.STRING)
    public Response.Status.Family family;

    public Long time;


    public WebResponse(Integer code, Long time) {
        this.code = code;
        this.status = getStatus(code);
        this.family = getStatusFamily(code);
        this.time = time;
    }

    private Response.Status getStatus(Integer code) {
        return code != null ? Response.Status.fromStatusCode(code) : null;
    }

    private Response.Status.Family getStatusFamily(Integer code) {
        Response.Status status = getStatus(code);
        return status != null ? status.getFamily() : null;
    }

    public boolean hasSuccessfulStatus() {
        return Response.Status.Family.SUCCESSFUL.equals(this.family);
    }

    public boolean hasFailedStatus() {
        return Response.Status.Family.CLIENT_ERROR.equals(this.family) ||
                Response.Status.Family.SERVER_ERROR.equals(this.family);
    }

    public boolean hasWarningStatus() {
        return Response.Status.Family.REDIRECTION.equals(this.family) ||
                Response.Status.Family.OTHER.equals(this.family) ||
                Response.Status.Family.INFORMATIONAL.equals(this.family);
    }
}
