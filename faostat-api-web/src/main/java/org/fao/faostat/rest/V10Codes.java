package org.fao.faostat.rest;

import com.sun.jersey.api.core.InjectParam;
import org.fao.faostat.beans.DatasourceBean;
import org.fao.faostat.beans.DefaultOptionsBean;
import org.fao.faostat.core.FAOSTATAPICore;
import org.fao.faostat.core.StreamBuilder;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
@Component
@Path("/v1.0/{lang}/codes/{id}/{domain_code}")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class V10Codes {

    @InjectParam
    FAOSTATAPICore faostatapiCore;

    private DefaultOptionsBean o;

    public V10Codes() {
        this.setO(new DefaultOptionsBean());
    }

    @GET
    public Response getDimensions(@PathParam("lang") String lang,
                                  @PathParam("id") String id,
                                  @PathParam("domain_code") String domain_code,
                                  @QueryParam("datasource") String datasource,
                                  @QueryParam("api_key") String api_key,
                                  @QueryParam("client_key") String client_key,
                                  @QueryParam("output_type") String output_type,
                                  @QueryParam("show_lists") boolean show_lists,
                                  @QueryParam("show_full_metadata") boolean show_full_metadata,
                                  @QueryParam("subdimensions") String subdimensions,
                                  @QueryParam("whitelist") String whitelist,
                                  @QueryParam("blacklist") String blacklist) {


        /* Store user preferences. */
        this.getO().storeUserOptions(datasource, api_key, client_key, output_type);

        /* Store procedure parameters. */
        this.getO().addParameter("lang", this.faostatapiCore.iso2faostat(lang));
        this.getO().addParameter("domain_code", domain_code);
        this.getO().addParameter("id", id);
        this.getO().addParameter("show_lists", String.valueOf(show_lists));
        this.getO().addParameter("show_full_metadata", String.valueOf(show_full_metadata));
        this.getO().addParameter("subdimensions", subdimensions);
        this.getO().addParameter("whitelist", whitelist);
        this.getO().addParameter("blacklist", blacklist);

        /* Query the DB and return the results. */
        try {

            /* Stream builder. */
            StreamBuilder sb = new StreamBuilder();

            /* Datasource bean. */
            DatasourceBean datasourceBean = this.faostatapiCore.getDatasourcePool().getDatasource(this.getO().getDatasource().toUpperCase());

            /* Query the DB and create an output stream. */
            StreamingOutput stream = sb.createCodesOutputStream(datasourceBean, this.getO());

            /* Stream result */
            return Response.status(200).entity(stream).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    public void setO(DefaultOptionsBean o) {
        this.o = o;
    }

    public DefaultOptionsBean getO() {
        return o;
    }

}