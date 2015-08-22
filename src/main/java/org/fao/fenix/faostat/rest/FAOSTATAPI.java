package org.fao.fenix.faostat.rest;

import com.sun.jersey.api.core.InjectParam;
import org.fao.fenix.faostat.core.DatasourcePool;
import org.fao.fenix.faostat.core.FAOSTATAPICore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
@Component
@Path("/v1.0")
public class FAOSTATAPI {

    @InjectParam
    DatasourcePool datasourcePool;

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sayHello(@PathParam("name") String name) {

        /* Load core library. */
        FAOSTATAPICore c = new FAOSTATAPICore();

        /* Stream result */
        return Response.status(200).entity(datasourcePool.getDatasource(name.toUpperCase()).getUrl()).build();

    }

}