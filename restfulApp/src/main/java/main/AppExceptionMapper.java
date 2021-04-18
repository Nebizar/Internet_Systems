package main;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class AppExceptionMapper implements ExceptionMapper<Throwable> {

    public Response toResponse(Throwable ex) {
        ex.printStackTrace();
        return Response.status(500)
                .entity(ex.toString())
                .type(MediaType.TEXT_PLAIN).
                        build();
    }
}