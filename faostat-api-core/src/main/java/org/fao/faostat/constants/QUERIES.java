package org.fao.faostat.constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class QUERIES {

    private Map<String, String> queries;

    public QUERIES() {
        this.setQueries(new HashMap<String, String>());
        this.getQueries().put("groups", "SELECT D.GroupCode AS code, D.GroupName{{lang}} AS label FROM Domain D GROUP BY D.GroupCode, D.GroupName{{lang}}");
        this.getQueries().put("domains", "SELECT D.DomainCode AS code, D.DomainName{{lang}} AS label, D.Ord AS ord FROM Domain D WHERE D.GroupCode = '{{group_code}}' ORDER BY D.Ord");
        this.getQueries().put("groupsanddomains", "SELECT D.GroupCode AS code, D.GroupName{{lang}} AS label, D.DomainCode, D.DomainName{{lang}}, D.Ord AS ord FROM Domain D ORDER BY D.Ord");
        this.getQueries().put("dimensions", "EXEC Warehouse.dbo.usp_GetDomainListBoxes @DomainCode = N'{{domain_code}}', @Lang = N'{{lang}}'");
        this.getQueries().put("methodologies", "SELECT M.MethodologyCode AS code, M.MethodologyTitle{{lang}} AS label FROM Metadata_Methodology AS M GROUP BY M.MethodologyCode, M.MethodologyTitle{{lang}} ORDER BY M.MethodologyTitle{{lang}} ASC");
        this.getQueries().put("methodology", "SELECT M.MethodologyNote{{lang}} AS note, M.MethodologyCoverage{{lang}} AS coverage, M.MethodologyReferences{{lang}} AS reference, M.MethodologyCollection{{lang}} AS collection, M.MethodologyEstimation{{lang}} AS estimation FROM Metadata_Methodology AS M WHERE M.MethodologyCode='{{methodology_code}}'");
        this.getQueries().put("classifications", "SELECT M.ItemCode AS code, M.ItemName{{lang}} AS label, M.ItemDescription{{lang}} AS description FROM Metadata_Item AS M WHERE M.domaincode = '{{domain_code}}' ORDER BY M.ItemName{{lang}} ASC");
        this.getQueries().put("units", "SELECT E.UnitAbbreviation{{lang}} AS code, E.UnitTitle{{lang}} AS label FROM Metadata_Unit AS E ORDER BY E.UnitAbbreviation{{lang}} ASC");
        this.getQueries().put("glossary", "SELECT M.GlossaryName{{lang}} AS code, M.GlossaryDefinition{{lang}} AS label, M.GlossarySource{{lang}} AS source FROM Metadata_Glossary AS M ORDER BY M.GlossaryName{{lang}} ASC");
        this.getQueries().put("abbreviations", "SELECT M.AbbreviationTitle{{lang}} AS code, AbbreviationDefinition{{lang}} AS label FROM Metadata_Abbreviation AS M ORDER BY AbbreviationTitle{{lang}} ASC");
        this.getQueries().put("codes", "EXEC Warehouse.dbo.usp_GetListBox @DomainCode = N'{{domain_code}}', @Lang = N'{{lang}}', @ListBoxNO = {{dimension}}, @TabOrder = {{subdimension}}");
        this.getQueries().put("bulkdownloads", "SELECT B.Domain AS code, B.Source AS label, B.Filename AS filename, B.FileContent AS content, B.CreatedDate AS date FROM BulkDownloads B WHERE B.LanguageID = '{{lang}}' AND B.Domain = '{{domain_code}}'");
        this.getQueries().put("data", "EXECUTE Warehouse.dbo.usp_GetData @DomainCode = '{{domain_code}}', @lang = '{{lang}}', @List1Codes = '{{list_1_codes}}', @List2Codes = '{{list_2_codes}}', @List3Codes = '{{list_3_codes}}', @List4Codes = '{{list_4_codes}}', @List5Codes = '{{list_5_codes}}', @List6Codes = '{{list_6_codes}}', @List7Codes = '{{list_7_codes}}', @NullValues = {{null_values}}, @Thousand = '{{thousand_separator}}', @Decimal = '{{decimal_separator}}', @DecPlaces = {{decimal_places}}, @Limit = {{limit}}");
        this.getQueries().put("data_structure", "EXEC Warehouse.dbo.usp_GetDataSchema @DomainCode = N'{{domain_code}}', @Lang = N'{{lang}}'");
    }

    public String getQuery(String id, Map<String, Object> procedureParameters) throws Exception {
        try {
            String query = this.getQueries().get(id.toLowerCase());
            for (String key : procedureParameters.keySet()) {
                String tmp = "\\{\\{" + key + "\\}\\}";
                if (procedureParameters.get(key) != null) {
                    if (procedureParameters.get(key) instanceof List) {
                        List<String> l = (List<String>)procedureParameters.get(key);
                        String s = "";
                        if (l != null && l.size() > 0 && l.get(0).length() > 0) {
                            s = "(";
                            for (int z = 0; z < l.size(); z += 1) {
                                s += "''" + l.get(z) + "''";
                                if (z < l.size() - 1)
                                    s += ",";
                            }
                            s += ")";
                        } else {
                            s = "";
                        }
                        query = query.replaceAll(tmp, s);
                    } else {
                        query = query.replaceAll(tmp, procedureParameters.get(key).toString());
                    }
                } else {
                    query = query.replaceAll(tmp, "null");
                }
            }
            return query;
        } catch (Exception e) {
            throw e;
        }
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
    }

}
