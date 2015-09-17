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
public class V10 {

    @InjectParam
    DefaultOptionsBean o;

    @InjectParam
    FAOSTATAPICore faostatapiCore;

    public void storeUserOptions(String datasource, String lang, String apiKey, String clientKey, String outputType) {
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