package org.fao.fenix.faostat.rest;

import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
@Component
@Path("/v1.0/{lang}/groups/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class V10Groups extends V10 {

    public V10Groups() {
        super();
    }

    @GET
    public Response getGroups(@PathParam("lang") String lang,
                              @QueryParam("datasource") String datasource,
                              @QueryParam("api_key") String api_key,
                              @QueryParam("client_key") String client_key,
                              @QueryParam("output_type") String output_type) {


        /* Store user preferences. */
        this.storeUserOptions(datasource, lang, api_key, client_key, output_type);

        /* Query the DB and return the results. */
        try {

            /* Query the DB and create an output stream. */
            StreamingOutput stream = this.getFaostatapiCore().createOutputStream("groups", this.getO());

            /* Stream result */
            return Response.status(200).entity(stream).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

}