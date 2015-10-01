package org.fao.faostat.rest;

import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
@Component
@Path("/v1.0/{lang}/domains/{group_code}")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class V10Domains extends V10 {

    public V10Domains() {
        super();
    }

    @GET
    public Response getDomains(@PathParam("lang") String lang,
                               @PathParam("group_code") String group_code,
                               @QueryParam("blacklist") String blacklist,
                               @QueryParam("whitelist") String whitelist,
                               @QueryParam("datasource") String datasource,
                               @QueryParam("api_key") String api_key,
                               @QueryParam("client_key") String client_key,
                               @QueryParam("output_type") String output_type) {


        /* Store user preferences. */
        this.storeUserOptions(datasource, api_key, client_key, output_type);

        /* Store procedure parameters. */
        this.getO().addParameter("lang", this.faostatapiCore.iso2faostat(lang));
        this.getO().addParameter("group_code", group_code);
        this.getO().addParameter("blacklist", blacklist);
        this.getO().addParameter("whitelist", whitelist);

        /* Query the DB and return the results. */
        try {

            /* Query the DB and create an output stream. */
            StreamingOutput stream = this.getFaostatapiCore().createDomainsOutputStream("domains", this.getO());

            /* Stream result */
            return Response.status(200).entity(stream).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

}