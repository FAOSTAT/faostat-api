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
@Path("/v1.0/{lang}/codes/{dimension_code}/{domain_codes}")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class V10Codes extends V10 {

    public V10Codes() {
        super();
    }

    @GET
    public Response getDimensions(@PathParam("lang") String lang,
                                  @PathParam("dimension_code") String dimension_code,
                                  @PathParam("domain_codes") String domain_codes,
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
        this.storeUserOptions(datasource, api_key, client_key, output_type);

        /* Store procedure parameters. */
        this.getO().addParameter("lang", this.faostatapiCore.iso2faostat(lang));
        this.getO().addParameter("dimension_code", dimension_code);
        this.getO().addParameter("domain_codes", domain_codes);
        this.getO().addParameter("show_lists", String.valueOf(show_lists));
        this.getO().addParameter("show_full_metadata", String.valueOf(show_full_metadata));
        this.getO().addParameter("subdimensions", subdimensions);
        this.getO().addParameter("whitelist", whitelist);
        this.getO().addParameter("blacklist", blacklist);

        /* Query the DB and return the results. */
        try {

            /* Query the DB and create an output stream. */
            StreamingOutput stream = this.getFaostatapiCore().createDimensionOutputStream("dimensions", this.getO());

            /* Stream result */
            return Response.status(200).entity(stream).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

}