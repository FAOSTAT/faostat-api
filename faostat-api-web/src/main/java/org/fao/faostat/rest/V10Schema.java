package org.fao.faostat.rest;

import com.sun.jersey.api.core.InjectParam;
import org.fao.faostat.core.FAOSTATAPICore;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
@Component
@Path("/v1.0")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class V10Schema {

    @InjectParam
    FAOSTATAPICore faostatapiCore;

    @GET
    public Response getSchema() {

        /* Stream result */
        return Response.status(200).entity(faostatapiCore.getSchema()).build();

    }

}