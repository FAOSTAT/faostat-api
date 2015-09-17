package org.fao.fenix.faostat.rest;

import com.sun.jersey.api.core.InjectParam;
import org.fao.fenix.faostat.beans.DefaultOptionsBean;
import org.fao.fenix.faostat.core.FAOSTATAPICore;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getSchema(@PathParam("name") String name) {

        /* Stream result */
        return Response.status(200).entity(faostatapiCore.getSchema()).build();

    }

    @GET
    @Path("/{lang}/groups/")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
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

    @GET
    @Path("/{lang}/groupsanddomains/")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getGroupsAndDomains(@PathParam("lang") String lang,
                                        @QueryParam("datasource") String datasource,
                                        @QueryParam("api_key") String api_key,
                                        @QueryParam("client_key") String client_key,
                                        @QueryParam("output_type") String output_type) {


        /* Store user preferences. */
        this.storeUserOptions(datasource, lang, api_key, client_key, output_type);

        /* Query the DB and return the results. */
        try {

            /* Query the DB and create an output stream. */
            StreamingOutput stream = this.getFaostatapiCore().createOutputStream("groupsanddomains", this.getO());

            /* Stream result */
            return Response.status(200).entity(stream).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    private void storeUserOptions(String datasource, String lang, String apiKey, String clientKey, String outputType) {
        this.getO().setDatasource(datasource != null ? datasource : this.getO().getDatasource());
        this.getO().setLang(lang != null ? lang : this.getO().getLang());
        this.getO().setApiKey(apiKey != null ? apiKey : this.getO().getApiKey());
        this.getO().setClientKey(clientKey != null ? clientKey : this.getO().getClientKey());
        this.getO().setOutputType(outputType != null ? outputType : this.getO().getOutputType());
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