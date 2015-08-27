package org.fao.fenix.faostat.rest;

import com.sun.jersey.api.core.InjectParam;
import org.fao.fenix.faostat.core.FAOSTATAPICore;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
@Component
@Path("/v1.0")
public class FAOSTATAPI {

    @InjectParam
    FAOSTATAPICore faostatapiCore;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchema(@PathParam("name") String name) {

        /* Stream result */
        return Response.status(200).entity(faostatapiCore.getSchema()).build();

    }

    @GET
    @Path("/{lang}/groups/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups(@PathParam("lang") String lang,
                              @QueryParam("datasource") String datasource,
                              @QueryParam("api_key") String api_key,
                              @QueryParam("client_key") String client_key) {

        try {

            /* Fetch query from configuration file. */
            String query = faostatapiCore.getGroups(lang);

            /* Stream result */
            return Response.status(200).entity(query).build();

        } catch (IOException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

}