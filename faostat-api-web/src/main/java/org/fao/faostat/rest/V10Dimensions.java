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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
@Component
@Path("/v1.0/{lang}/dimensions/{domain_code}")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class V10Dimensions {

    @InjectParam
    FAOSTATAPICore faostatapiCore;

    private DefaultOptionsBean o;

    public V10Dimensions() {
        this.setO(new DefaultOptionsBean());
    }

    @GET
    public Response getDimensions(@PathParam("lang") String lang,
                                  @PathParam("domain_code") String domain_code,
                                  @QueryParam("datasource") String datasource,
                                  @QueryParam("api_key") String api_key,
                                  @QueryParam("client_key") String client_key,
                                  @QueryParam("output_type") String output_type) {


        /* Store user preferences. */
        this.getO().storeUserOptions(datasource, api_key, client_key, output_type);

        /* Store procedure parameters. */
        this.getO().addParameter("lang", this.faostatapiCore.iso2faostat(lang));
        this.getO().addParameter("domain_code", domain_code);

        /* Query the DB and return the results. */
        try {

            /* Stream builder. */
            StreamBuilder sb = new StreamBuilder();

            /* Datasource bean. */
            DatasourceBean datasourceBean = this.faostatapiCore.getDatasourcePool().getDatasource(this.getO().getDatasource().toUpperCase());

            /* Query the DB and create an output stream. */
            StreamingOutput stream = sb.createDimensionOutputStream("dimensions", datasourceBean, this.getO());

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