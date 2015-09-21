package org.fao.faostat.rest;

import com.sun.jersey.api.core.InjectParam;
import org.fao.faostat.beans.DefaultOptionsBean;
import org.fao.faostat.core.FAOSTATAPICore;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class V10 {

    @InjectParam
    DefaultOptionsBean o;

    @InjectParam
    FAOSTATAPICore faostatapiCore;

    public V10() {
        this.setO(new DefaultOptionsBean());
    }

    public void storeUserOptions(String datasource, String apiKey, String clientKey, String outputType) {
        this.getO().setDatasource(datasource != null ? datasource : this.getO().getDatasource());
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