package org.fao.faostat.api.web.utils;

import org.fao.faostat.api.core.beans.MetadataBean;
import org.fao.faostat.api.core.constants.OUTPUTTYPE;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FAOSTATAPIUtils {

    public static String outputType(MetadataBean metadataBean) {
        if (metadataBean.getOutputType().equals(OUTPUTTYPE.CSV)) {
            return MediaType.APPLICATION_OCTET_STREAM + ";charset=utf-8";
        }
        else if(metadataBean.getOutputType().equals(OUTPUTTYPE.OBJECTS)) {
            return MediaType.APPLICATION_JSON + ";charset=utf-8";
        }
        else{
            return MediaType.APPLICATION_JSON + ";charset=utf-8";
        }
    }

    public static List<Map<String, Object>> filterDSD(List<Map<String, Object>> dsdAll, List<String> columnNames, List<String> columnTypes) {

        List<Map<String, Object>> dsd = new ArrayList<>();

        for(Map<String, Object> d : dsdAll) {
            if (columnNames.contains(d.get("key"))) {
                d.put("data_type", columnTypes.get(columnNames.indexOf(d.get("key"))).replace("java.lang.", "").toLowerCase());
                dsd.add(d);
            }
        }

        return dsd;
    }

}
