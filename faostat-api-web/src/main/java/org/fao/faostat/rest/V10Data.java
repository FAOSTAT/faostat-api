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
@Path("/v1.0/{lang}/data/{domain_code}")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class V10Data {

    @InjectParam
    FAOSTATAPICore faostatapiCore;

    private DefaultOptionsBean o;

    public V10Data() {
        this.setO(new DefaultOptionsBean());
    }

    @GET
    public Response getData(@PathParam("lang") String lang,
                            @PathParam("domain_code") String domain_code,
                            @QueryParam("datasource") String datasource,
                            @QueryParam("api_key") String api_key,
                            @QueryParam("client_key") String client_key,
                            @QueryParam("output_type") String output_type,
                            @QueryParam("list_1_codes") String list_1_codes,
                            @QueryParam("list_2_codes") String list_2_codes,
                            @QueryParam("list_3_codes") String list_3_codes,
                            @QueryParam("list_4_codes") String list_4_codes,
                            @QueryParam("list_5_codes") String list_5_codes,
                            @QueryParam("list_6_codes") String list_6_codes,
                            @QueryParam("list_7_codes") String list_7_codes,
                            @QueryParam("null_values") boolean null_values,
                            @QueryParam("thousand_separator") String thousand_separator,
                            @QueryParam("decimal_separator") String decimal_separator,
                            @QueryParam("decimal_places") int decimal_places,
                            @QueryParam("limit") int limit) {


        /* Store user preferences. */
        this.getO().storeUserOptions(datasource, api_key, client_key, output_type);

        /* Store procedure parameters. */
        this.getO().addParameter("lang", this.faostatapiCore.iso2faostat(lang));
        this.getO().addParameter("domain_code", domain_code);
        this.getO().addParameter("list_1_codes", list_1_codes);
        this.getO().addParameter("list_2_codes", list_2_codes);
        this.getO().addParameter("list_3_codes", list_3_codes);
        this.getO().addParameter("list_4_codes", list_4_codes);
        this.getO().addParameter("list_5_codes", list_5_codes);
        this.getO().addParameter("list_6_codes", list_6_codes);
        this.getO().addParameter("list_7_codes", list_7_codes);
        this.getO().addParameter("null_values", String.valueOf(null_values));
        this.getO().addParameter("thousand_separator", thousand_separator);
        this.getO().addParameter("decimal_separator", decimal_separator);
        this.getO().addParameter("decimal_places", String.valueOf(decimal_places));
        this.getO().addParameter("limit", String.valueOf(limit));

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