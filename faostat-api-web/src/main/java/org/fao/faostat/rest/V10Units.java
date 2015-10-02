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
@Path("/v1.0/{lang}/units/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class V10Units {

    @InjectParam
    FAOSTATAPICore faostatapiCore;

    private DefaultOptionsBean o;

    public V10Units() {
        this.setO(new DefaultOptionsBean());
    }

    @GET
    public Response getUnits(@PathParam("lang") String lang,
                             @QueryParam("datasource") String datasource,
                             @QueryParam("api_key") String api_key,
                             @QueryParam("client_key") String client_key,
                             @QueryParam("output_type") String output_type) {


        /* Store user preferences. */
        this.getO().storeUserOptions(datasource, api_key, client_key, output_type);

        /* Store procedure parameters. */
        this.getO().addParameter("lang", this.faostatapiCore.iso2faostat(lang));

        /* Query the DB and return the results. */
        try {

            /* Stream builder. */
            StreamBuilder sb = new StreamBuilder();

            /* Datasource bean. */
            DatasourceBean datasourceBean = this.faostatapiCore.getDatasourcePool().getDatasource(this.getO().getDatasource().toUpperCase());

            /* Query the DB and create an output stream. */
            StreamingOutput stream = sb.createOutputStream("units", datasourceBean, this.getO());

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