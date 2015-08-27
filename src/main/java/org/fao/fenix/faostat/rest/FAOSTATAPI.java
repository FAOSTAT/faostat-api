package org.fao.fenix.faostat.rest;

import com.sun.jersey.api.core.InjectParam;
import org.fao.fenix.faostat.beans.DefaultOptionsBean;
import org.fao.fenix.faostat.core.FAOSTATAPICore;
import org.fao.fenix.faostat.jdbc.JDBCIterable;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
@Component
@Path("/v1.0")
public class FAOSTATAPI {

    @InjectParam
    DefaultOptionsBean o;

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
                              @QueryParam("client_key") String client_key,
                              @QueryParam("output_type") String output_type) {


        /* Store user preferences. */
        this.storeUserOptions(datasource, api_key, client_key, output_type);

        /* Query the DB and return the results. */
        try {

            /* Fetch the iterable for the required query. */
            final JDBCIterable i = this.getFaostatapiCore().getJDBCIterable("groups", this.getO().getDatasource(), lang);

            /* Initiate the output stream. */
            StreamingOutput stream = new StreamingOutput() {

                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {

                    /* Initiate the buffer writer. */
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                    /* Write the content. */
                    writer.write("[");
                    while (i.hasNext()) {
                        writer.write(i.nextJSON());
                        if (i.hasNext())
                            writer.write(",");
                    }
                    writer.write("]");

                    /* Flush the writer. */
                    writer.flush();

                }

            };

            /* Stream result */
            return Response.status(200).entity(stream).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    private void storeUserOptions(String datasource, String apiKey, String clientKey, String outputType) {
        this.getO().setDatasource(datasource);
        this.getO().setApiKey(apiKey);
        this.getO().setClientKey(clientKey);
        this.getO().setOutputType(outputType);
    }

    public FAOSTATAPICore getFaostatapiCore() {
        return faostatapiCore;
    }

    public void setFaostatapiCore(FAOSTATAPICore faostatapiCore) {
        this.faostatapiCore = faostatapiCore;
    }

    public DefaultOptionsBean getO() {
        return o;
    }

    public void setO(DefaultOptionsBean o) {
        this.o = o;
    }

}