package solrTest;


import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.sql.*;
import java.util.function.BooleanSupplier;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.CoreContainer;


public class SolrService {
    private static final String SOLR_INDEX_DIR = "solr";
    private static final String CORE_NAME = "core";
    private static SolrServer solrServer;


    public SolrService() {
        super();
    }

    public static SolrServer getSolrServer() throws SolrServerException {
        if (null == solrServer) {
            String solrHome = "C:\\Users\\vladislav\\solrTest\\src\\main\\resources\\solr";
            CoreContainer coreContainer = new CoreContainer(solrHome);
            coreContainer.load();
            solrServer = new EmbeddedSolrServer(coreContainer, CORE_NAME);
        }

        return solrServer;
    }

    public static void main(String[] args) throws SolrServerException, FileNotFoundException, IOException, ClassNotFoundException, SQLException {
        String sql = "SELECT p.LAST_UPDATE_DATE as LAST_UPDATE_DATE, " +
                "i.LAST_UPDATE_DATE as LAST_UPDATE_DATE_INSTRUMENT, " +
                "p.CREATION_DATE as CREATION_DATE," +
                "i.CREATION_DATE as CREATION_DATE_INSTRUMENT," +
                "p.ID as ID," +
                "p.PRICE," +
                "p.PRICE_DATE," +
                "p.QUALITY_STATUS," +
                "p.SERIES_FK," +
//                "p.OL_VERSION as OL_VERSION," +
                "ps.ACTIVE," +
                "ps.CURRENCY," +
                "ps.DELIVERY_FREQUENCY_CD," +
                "ps.FULL_EXPORT_PARS," +
                "ps.FULL_EXPORT_RISK," +
                "ps.IM_PRICE_CD," +
                "ps.INITIAL_LOAD," +
                "ps.INSTRUMENT_FK," +
                "ps.IS_DERIVED," +
                "ps.IS_IMIINTERNAL," +
                "ps.ORDER_ID," +
                "ps.PROVIDER_CD," +
                "ps.PROVIDER_PRICE_CD," +
                "ps.RESEARCH_ID," +
                "ps.UNIT_CD as UNIT_CD_SERIES," +
//                "ps.OL_VERSION as OL_VERSION_SERIES," +
                "i.EXPIRATION_CD," +
                "i.ISIN," +
                "i.SSP_FI," +
                "i.VALOR," +
                "i.IM_STATUS_CD," +
                "i.FO_TYPE," +
                "i.IM_INTERNAL," +
                "i.PLACEHOLDER," +
                "i.TKT_CD," +
                "i.TRADEABLE_CD," +
                "i.UBS_RELEVANT," +
                "i.UBS_TC," +
                "i.UBS_TCADD," +
                "i.ISSUE_PRICE_CURRENCY," +
                "i.LONG_NAME," +
                "i.MARKETING_LONG_NAME," +
                "i.MARKETING_SHORT_NAME," +
                "i.MINIMAL_DENOMINATION," +
                "i.NOMINAL_AMOUNT," +
                "i.NOMINAL_CURRENCY," +
                "i.PROVIDER_STATUS_CD," +
                "i.UNIT_CD as UNIT_CD_INSTRUMENT," +
                "i.SHORT_NAME," +
//                "i.OL_VERSION as OL_VERSION_INSTRUMENT," +
                "pfc.DESCRIPTION as PRICE_FREQUENCY_DESC," +
                "pfc.SHORT_DESCRIPTION as PRICE_FREQUENCY_SHORT_DESC," +
                "impc.DESCRIPTION as IM_PRICE_DESC," +
                "impc.SHORT_DESCRIPTION as IM_PRICE_SHORT_DESC," +
                "dpc.SHORT_DESCRIPTION as DATA_PROVIDER_SHORT_DESC," +
                "ucs.DESCRIPTION as UNIT_SERIES_DESC," +
                "ppc.SHORT_DESCRIPTION as PROVIDER_PRICE_SHORT_DESC," +
                "ec.DESCRIPTION as EXPIRATION_DESC," +
                "iisc.DESCRIPTION as IM_STATUS_DESC," +
                "iisc.SHORT_DESCRIPTION as IM_STATUS_SHORT_DESC," +
                "iisc.LONG_DESCRIPTION as IM_STATUS_LONG_DESC," +
                "psc.DESCRIPTION as PROVIDER_STATUS_DESC," +
                "uci.DESCRIPTION as UNIT_INSTRUMENT_DESC," +
                "tkc.DESCRIPTION as TKT_DESC," +
                "tc.DESCRIPTION as TRADEABLE_DESCRIPTION " +
                "FROM PR4_T_PRICE p " +
                "INNER JOIN PR4_T_PRICE_SERIES ps ON p.SERIES_FK=ps.ID " +
                "INNER JOIN PR4_T_INSTRUMENT i ON ps.INSTRUMENT_FK=i.ID " +
                "INNER JOIN PR4_M_PRICE_FREQUENCY_CD pfc ON ps.DELIVERY_FREQUENCY_CD=pfc.CODE " +
                "INNER JOIN PR4_M_IMPRICE_CD impc ON ps.IM_PRICE_CD=impc.CODE " +
                "INNER JOIN PR4_M_DATA_PROVIDER_CD dpc ON ps.PROVIDER_CD=dpc.CODE " +
                "INNER JOIN PR4_M_PROVIDER_PRICE_CD ppc ON ps.PROVIDER_PRICE_CD=ppc.CODE " +
                "INNER JOIN PR4_M_UNIT_CD ucs ON ps.UNIT_CD=ucs.CODE " +
                "INNER JOIN PR4_M_EXPIRATION_CD ec ON i.EXPIRATION_CD=ec.CODE " +
                "INNER JOIN PR4_M_IM_INSTR_STATUS_CD iisc ON i.IM_STATUS_CD=iisc.CODE " +
                "INNER JOIN PR4_M_PROVIDER_STATUS_CD psc ON i.PROVIDER_STATUS_CD=psc.CODE " +
                "INNER JOIN PR4_M_UNIT_CD uci ON i.UNIT_CD=uci.CODE " +
                "INNER JOIN PR4_M_TKT_CD tkc ON i.TKT_CD=tkc.CODE " +
                "INNER JOIN PR4_M_TRADEABLE_CD tc ON i.TRADEABLE_CD=tc.CODE " +
                "WHERE pfc.LANGUAGE='en' AND impc.LANGUAGE='en' AND dpc.LANGUAGE='en' AND ppc.LANGUAGE='en' AND ucs.LANGUAGE='en' AND ec.LANGUAGE='en' AND iisc.LANGUAGE='en' AND psc.LANGUAGE='en' AND uci.LANGUAGE='en' AND tkc.LANGUAGE='en' AND tc.LANGUAGE='en'";
//
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.
                getConnection("jdbc:h2:tcp://localhost//C:\\\\Users\\\\vladislav\\\\Desktop\\\\UBSData\\\\InstrumentData\\\\InstrumentData\\\\PR4_DATA;AUTO_SERVER=TRUE", "sa", "sa");
        Statement stmt = conn.createStatement();

        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();

        List<String> codeTablesSeries = new ArrayList<>();
        codeTablesSeries.add("PR4_M_PRICE_FREQUENCY_CD");
        codeTablesSeries.add("PR4_M_IMPRICE_CD");
        codeTablesSeries.add("PR4_M_DATA_PROVIDER_CD");
        codeTablesSeries.add("PR4_M_PROVIDER_PRICE_CD");
        codeTablesSeries.add("PR4_M_UNIT_CD");

        List<String> codeInSeries = new ArrayList<>();
        codeInSeries.add("DELIVERY_FREQUENCY_CD");
        codeInSeries.add("IM_PRICE_CD");
        codeInSeries.add("PROVIDER_CD");
        codeInSeries.add("PROVIDER_PRICE_CD");
        codeInSeries.add("UNIT_CD");

        List<String> codeTablesInstrument = new ArrayList<>();
        codeTablesInstrument.add("PR4_M_EXPIRATION_CD");
        codeTablesInstrument.add("PR4_M_IM_INSTR_STATUS_CD");
        codeTablesInstrument.add("PR4_M_TKT_CD");
        codeTablesInstrument.add("PR4_M_TRADEABLE_CD");
        codeTablesInstrument.add("PR4_M_PROVIDER_STATUS_CD");
        codeTablesInstrument.add("PR4_M_UNIT_CD");

        List<String> codeInInstrument = new ArrayList<>();
        codeInInstrument.add("EXPIRATION_CD");
        codeInInstrument.add("IM_STATUS_CD");
        codeInInstrument.add("TKT_CD");
        codeInInstrument.add("TRADEABLE_CD");
        codeInInstrument.add("PROVIDER_STATUS_CD");
        codeInInstrument.add("UNIT_CD");

//

        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<RecordItem> allRecords = new ArrayList<>();
        ArrayList<String> seriesFks = new ArrayList<>();
        ArrayList<String> instrumentFks = new ArrayList<>();
        int price_count = 0;
        int instrument_count = 0;
        int count = 0;
        while(rs.next()) {
            String string_id = rs.getString("ID");
            String string_last_update_date = rs.getString("LAST_UPDATE_DATE");
            String string_price = rs.getString("PRICE");
            String string_price_date = rs.getString("PRICE_DATE");
            String string_quality_status = rs.getString("QUALITY_STATUS");
            String string_series_fk = rs.getString("SERIES_FK");
            String string_active = rs.getString("ACTIVE");
            String string_currency = rs.getString("CURRENCY");
            String string_delivery_frequency_cd = rs.getString("DELIVERY_FREQUENCY_CD");
            String string_full_export_pars = rs.getString("FULL_EXPORT_PARS");
            String string_full_export_risk = rs.getString("FULL_EXPORT_RISK");
            String string_im_price_cd = rs.getString("IM_PRICE_CD");
            String string_initial_load = rs.getString("INITIAL_LOAD");
            String string_instrument = rs.getString("INSTRUMENT_FK");
            String string_is_derived = rs.getString("IS_DERIVED");
            String string_is_imiinternal = rs.getString("IS_IMIINTERNAL");
            String string_order_id = rs.getString("ORDER_ID");
            String string_provider_cd = rs.getString("PROVIDER_CD");
            String string_provider_price_cd = rs.getString("PROVIDER_PRICE_CD");
            String string_research_id = rs.getString("RESEARCH_ID");
            String string_unit_cd_series = rs.getString("UNIT_CD_SERIES");
            String string_creation_date_instrument = rs.getString("CREATION_DATE_INSTRUMENT");
            String string_expiration_cd = rs.getString("EXPIRATION_CD");
            String string_isin = rs.getString("ISIN");
            String string_ssp_fi = rs.getString("SSP_FI");
            String string_valor = rs.getString("VALOR");
            String string_im_status_cd = rs.getString("IM_STATUS_CD");
            String string_fo_type = rs.getString("FO_TYPE");
            String string_im_internal = rs.getString("IM_INTERNAL");
            String string_placeholder = rs.getString("PLACEHOLDER");
            String string_tkt_cd = rs.getString("TKT_CD");
            String string_tradeable_cd = rs.getString("TRADEABLE_CD");
            String string_ubs_relevant = rs.getString("UBS_RELEVANT");
            String string_ubs_tc = rs.getString("UBS_TC");
            String string_ubs_tcadd = rs.getString("UBS_TCADD");
            String string_issue_price_currency = rs.getString("ISSUE_PRICE_CURRENCY");
            String string_long_name = rs.getString("LONG_NAME");
            String string_marketing_long_name = rs.getString("MARKETING_LONG_NAME");
            String string_marketing_short_name = rs.getString("MARKETING_SHORT_NAME");
            String string_minimal_denomination = rs.getString("MINIMAL_DENOMINATION");
            String string_nominal_amount = rs.getString("NOMINAL_AMOUNT");
            String string_nominal_currency = rs.getString("NOMINAL_CURRENCY");
            String string_short_name = rs.getString("SHORT_NAME");
            String string_unit_cd_instrument = rs.getString("UNIT_CD_INSTRUMENT");
            String string_im_price_desc = rs.getString("IM_PRICE_DESC");
            String string_im_price_short_desc = rs.getString("IM_PRICE_SHORT_DESC");
            String string_delivery_frequency_desc = rs.getString("PRICE_FREQUENCY_DESC");
            String string_delivery_frequency_short_desc = rs.getString("PRICE_FREQUENCY_SHORT_DESC");
            String string_data_provider_short_desc = rs.getString("DATA_PROVIDER_SHORT_DESC");
            String string_unit_series_desc = rs.getString("UNIT_SERIES_DESC");
            String string_provider_price_short_desc = rs.getString("PROVIDER_PRICE_SHORT_DESC");
            String string_expiration_desc = rs.getString("EXPIRATION_DESC");
            String string_im_status_desc = rs.getString("IM_STATUS_DESC");
            String string_im_status_short_desc = rs.getString("IM_STATUS_SHORT_DESC");
            String string_im_status_long_desc = rs.getString("IM_STATUS_LONG_DESC");
            String string_provider_status_desc = rs.getString("PROVIDER_STATUS_DESC");
            String string_unit_instrument_desc = rs.getString("UNIT_INSTRUMENT_DESC");
            String string_tkt_desc = rs.getString("TKT_DESC");
            String string_tradeable_desc = rs.getString("TRADEABLE_DESCRIPTION");

            RecordItem recordItem = new RecordItem();
            recordItem.setId(string_id);
            try{
                recordItem.setLast_updateDate(string_last_update_date.substring(0, 10) + "T00:00:00Z");
            }catch (Exception e){
                recordItem.setLast_updateDate(null);
            }
            recordItem.setPrice(Double.valueOf(string_price));
            try{
                recordItem.setPrice_date(string_price_date.substring(0, 10) + "T00:00:00Z");
            }catch (Exception e){
                recordItem.setPrice_date(null);
            }
            recordItem.setQuality_status(Boolean.valueOf(string_quality_status));
            recordItem.setSeries(string_series_fk);
            recordItem.setActive(Boolean.valueOf(string_active));
            recordItem.setCurrency(string_currency);
            recordItem.setDelivery_frequency(string_delivery_frequency_cd);
            recordItem.setDelivery_frequency_description(string_delivery_frequency_desc);
            recordItem.setFull_export_pars(Boolean.valueOf(string_full_export_pars));
            recordItem.setFull_export_risk(Boolean.valueOf(string_full_export_risk));
            recordItem.setIm_price(string_im_price_cd);
            recordItem.setIm_price_description(string_im_price_desc);
            recordItem.setIm_price_short_description(string_im_price_short_desc);
            recordItem.setInitial_load(Boolean.valueOf(string_initial_load));
            recordItem.setInstrument(string_instrument);
            recordItem.setIs_derived(Boolean.valueOf(string_is_derived));
            recordItem.setIs_imi_internal(Boolean.valueOf(string_is_imiinternal));
            recordItem.setOrder_id(string_order_id);
            recordItem.setProvider(string_provider_cd);
            recordItem.setProvider_price(string_provider_price_cd);
            recordItem.setResearch_id(string_research_id);
            recordItem.setUnit_series(string_unit_cd_series);
            try{
                recordItem.setCreation_date_instrument(string_creation_date_instrument.substring(0, 10) + "T00:00:00Z");
            }catch (Exception e){
                recordItem.setCreation_date_instrument(null);
            }
            recordItem.setExpiration(string_expiration_cd);
            recordItem.setIsin(string_isin);
            recordItem.setSsp_fi(string_ssp_fi);
            recordItem.setValor(Integer.valueOf(string_valor));
            recordItem.setIm_status(string_im_status_cd);
            recordItem.setFo_type(Integer.valueOf(string_fo_type));
            recordItem.setIm_internal(Boolean.valueOf(string_im_internal));
            recordItem.setPlaceholder(Boolean.valueOf(string_placeholder));
            recordItem.setTkt(string_tkt_cd);
            recordItem.setTradeable(string_tradeable_cd);
            recordItem.setUbs_relevant(Boolean.valueOf(string_ubs_relevant));
            recordItem.setUbs_tc(string_ubs_tc);
            recordItem.setUbs_tcadd(string_ubs_tcadd);
            recordItem.setIssue_price_currency(string_issue_price_currency);
            recordItem.setUnit_series_description(string_unit_series_desc);
            recordItem.setUnit_instrument(string_unit_cd_instrument);
            recordItem.setDelivery_frequency_description(string_delivery_frequency_desc);
            recordItem.setProvider_description(string_data_provider_short_desc);
            recordItem.setProvider_price_short_description(string_provider_price_short_desc);
            recordItem.setExpiration_description(string_expiration_desc);
            recordItem.setIm_status_description(string_im_status_desc);
            recordItem.setIm_status_long_description(string_im_status_long_desc);
            recordItem.setIm_status_short_description(string_im_status_short_desc);
            recordItem.setProvider_status_description(string_provider_status_desc);
            recordItem.setTkt_description(string_tkt_desc);
            recordItem.setTradeable_description(string_tradeable_desc);
            recordItem.setUnit_instrument_description(string_unit_instrument_desc);
            recordItem.setLong_name(string_long_name);
            recordItem.setMarketing_long_name(string_marketing_long_name);
            recordItem.setMarketing_short_name(string_marketing_short_name);
            recordItem.setMinimal_denomination(Float.valueOf(string_minimal_denomination));
            recordItem.setNominal_amount(Float.valueOf(string_nominal_amount));
            recordItem.setNominal_currency(string_nominal_currency);
            recordItem.setShort_name(string_short_name);

            allRecords.add(recordItem);
            count++;
            double percentage = (Double.valueOf(count) / 1350737) * 100;
            System.out.println(Math.round(percentage) + "% ");

        }


        stmt.close();
        conn.close();


        long start = System.currentTimeMillis();
        SolrService solrService = new SolrService();
        solrServer = solrService.getSolrServer();


        int f = 0;
        for (RecordItem record : allRecords) {
            solrServer.addBean(record);
            f++;
            double percentage = (Double.valueOf(f) / allRecords.size()) * 100;
            System.out.println(Math.round(percentage) + "% complete of phase 2");
        }
        solrServer.commit();


        System.out.println("Time take to index everything: " + (System.currentTimeMillis() - start) + " ms");

    }

    public static String getShortName(String instrument)throws SolrServerException, FileNotFoundException, IOException  {
        SolrService solrService = new SolrService();
        solrServer = solrService.getSolrServer();
        SolrQuery solrQuery;
        solrQuery = new SolrQuery("instrument:" + instrument);
        solrQuery.setStart(0);                          //startign index
        solrQuery.setRows(1);                          //number of rows
        QueryResponse response = null;
        try {
            float start = System.currentTimeMillis();
            response = getSolrServer().query(solrQuery);
            System.out.println("Solr took: " + response.getQTime());
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        SolrDocumentList results = response.getResults();

        return results.get(0).getFieldValue("short_name").toString();
    }

    public static String getCurrency(String priceSeries)throws SolrServerException, FileNotFoundException, IOException  {
        SolrService solrService = new SolrService();
        solrServer = solrService.getSolrServer();
        SolrQuery solrQuery;
        solrQuery = new SolrQuery("series:" + priceSeries);
        solrQuery.setStart(0);                          //startign index
        solrQuery.setRows(1);                          //number of rows
        QueryResponse response = null;
        try {
            float start = System.currentTimeMillis();
            response = getSolrServer().query(solrQuery);
            System.out.println("Solr took: " + response.getQTime());
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        SolrDocumentList results = response.getResults();

        return results.get(0).getFieldValue("currency").toString();
    }


    public static List<String> getInstruments(String instrument)throws SolrServerException, FileNotFoundException, IOException  {

        SolrService solrService = new SolrService();
        solrServer = solrService.getSolrServer();
        SolrQuery solrQuery;
        solrQuery = new SolrQuery("instrument:*"+instrument+"*");
        solrQuery.setStart(0);                          //startign index
        solrQuery.setRows(1000);                          //number of rows
        QueryResponse response = null;
        solrQuery.addFacetField("instrument");
        solrQuery.setFacetLimit(Integer.MAX_VALUE);
        try {
            float start = System.currentTimeMillis();
            response = getSolrServer().query(solrQuery);
            System.out.println("Solr took: " + response.getQTime());
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        FacetField fieldShortName = response.getFacetField("instrument");
        List<String> allShortNames = new ArrayList<>();
        List<FacetField.Count> valuesShortNames = fieldShortName.getValues();
        for (FacetField.Count count : valuesShortNames){
            if (count.getCount() > 0){
                allShortNames.add(count.getName());

            }
        }

        return allShortNames;
    }

    public static List<String> getInstrumentsFromName(String name)throws SolrServerException, FileNotFoundException, IOException  {

        SolrService solrService = new SolrService();
        solrServer = solrService.getSolrServer();
        SolrQuery solrQuery;
        solrQuery = new SolrQuery("short_name:*"+name+"*");
        solrQuery.setStart(0);                          //startign index
        solrQuery.setRows(1000);                          //number of rows
        QueryResponse response = null;
        solrQuery.addFacetField("instrument");
        solrQuery.setFacetLimit(Integer.MAX_VALUE);
        try {
            float start = System.currentTimeMillis();
            response = getSolrServer().query(solrQuery);
            System.out.println("Solr took: " + response.getQTime());
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        FacetField fieldShortName = response.getFacetField("instrument");
        List<String> allShortNames = new ArrayList<>();
        List<FacetField.Count> valuesShortNames = fieldShortName.getValues();
        for (FacetField.Count count : valuesShortNames){
            if (count.getCount() > 0){
                allShortNames.add(count.getName());
            }
        }

        return allShortNames;
    }

    public static List<String> getSeries(String instrument)throws SolrServerException, FileNotFoundException, IOException  {

        SolrService solrService = new SolrService();
        solrServer = solrService.getSolrServer();
        SolrQuery solrQuery;
        solrQuery = new SolrQuery("instrument:" + instrument);
        solrQuery.setStart(0);                          //startign index
        solrQuery.setRows(1000);                          //number of rows
        QueryResponse response = null;
        solrQuery.addFacetField("series");
        solrQuery.setFacetLimit(Integer.MAX_VALUE);
        try {
            response = getSolrServer().query(solrQuery);
            System.out.println("Solr took: " + response.getQTime());
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        FacetField fieldShortName = response.getFacetField("series");
        List<String> allShortNames = new ArrayList<>();
        List<FacetField.Count> valuesShortNames = fieldShortName.getValues();
        for (FacetField.Count count : valuesShortNames){
            if (count.getCount() > 0){
                allShortNames.add(count.getName());
            }
        }
        return allShortNames;
    }

    public static Item getResultsFromId(String id) throws SolrServerException, FileNotFoundException, IOException {
        SolrService solrService = new SolrService();
        solrServer = solrService.getSolrServer();

        SolrQuery solrQuery;
        solrQuery = new SolrQuery("id:" + id);
        Item item = new Item();
        solrQuery.setStart(0);                          //startign index
        solrQuery.setRows(1);                          //number of rows
        solrQuery.addSort("price_date", SolrQuery.ORDER.asc);
        QueryResponse response = null;
        try {
            response = getSolrServer().query(solrQuery);
            System.out.println("Solr took: " + response.getQTime());
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        SolrDocumentList results = response.getResults();
        for (SolrDocument result : results) {
            item.setId(String.valueOf(result.getFieldValue("id")));

            item.setPrice(String.valueOf(result.getFieldValue("price")));
            try{
                item.setPrice_date(String.valueOf(result.getFieldValue("price_date")));
            }catch (Exception e){
                item.setPrice_date(null);
            }
            item.setQuality_status(String.valueOf(result.getFieldValue("quality_status")));
            item.setSeries(String.valueOf(result.getFieldValue("series")));
            item.setActive(String.valueOf(result.getFieldValue("active")));
            item.setCurrency(String.valueOf(result.getFieldValue("currency")));
            item.setDelivery_frequency(String.valueOf(result.getFieldValue("delivery_frequency")));
            item.setDelivery_frequency_description(String.valueOf(result.getFieldValue("delivery_frequency_description")));
            item.setFull_export_pars(String.valueOf(result.getFieldValue("full_export_pars")));
            item.setFull_export_risk(String.valueOf(result.getFieldValue("full_export_risk")));
            item.setIm_price(String.valueOf(result.getFieldValue("im_price")));
            item.setIm_price_description(String.valueOf(result.getFieldValue("im_price_desc")));
            item.setIm_price_short_description(String.valueOf(result.getFieldValue("im_price_short_description")));
            item.setInitial_load(String.valueOf(result.getFieldValue("initial_load")));
            item.setInstrument(String.valueOf(result.getFieldValue("instrument")));
            item.setIs_derived(String.valueOf(result.getFieldValue("is_derived")));
            item.setIs_imi_internal(String.valueOf(result.getFieldValue("is_imi_internal")));
            item.setOrder_id(String.valueOf(result.getFieldValue("order_id")));
            item.setProvider(String.valueOf(result.getFieldValue("provider")));
            item.setProvider_price(String.valueOf(result.getFieldValue("provider_price")));
            item.setResearch_id(String.valueOf(result.getFieldValue("research_id")));
            item.setUnit_series(String.valueOf(result.getFieldValue("unit_series")));
            item.setCreation_date_instrument(String.valueOf(result.getFieldValue("creation_date_instrument")));
            item.setExpiration(String.valueOf(result.getFieldValue("expiration")));
            item.setIsin(String.valueOf(result.getFieldValue("isin")));
            item.setSsp_fi(String.valueOf(result.getFieldValue("ssp_fi")));
            item.setValor(String.valueOf(result.getFieldValue("valor")));
            item.setIm_status(String.valueOf(result.getFieldValue("im_status")));
            item.setFo_type(String.valueOf(result.getFieldValue("fo_type")));
            item.setIm_internal(String.valueOf(result.getFieldValue("im_internal")));
            item.setPlaceholder(String.valueOf(result.getFieldValue("placeholder")));
            item.setTkt(String.valueOf(result.getFieldValue("tkt")));
            item.setTradeable(String.valueOf(result.getFieldValue("tradeable")));
            item.setUbs_relevant(String.valueOf(result.getFieldValue("ubs_relevant")));
            item.setUbs_tc(String.valueOf(result.getFieldValue("ubs_tc")));
            item.setUbs_tcadd(String.valueOf(result.getFieldValue("ubs_tcadd")));
            item.setIssue_price_currency(String.valueOf(result.getFieldValue("issue_price_currency")));
            item.setUnit_series_description(String.valueOf(result.getFieldValue("unit_series_description")));
            item.setUnit_instrument(String.valueOf(result.getFieldValue("unit_instrument")));
            item.setDelivery_frequency_description(String.valueOf(result.getFieldValue("delivery_frequency_description")));
            item.setProvider_description(String.valueOf(result.getFieldValue("data_provider_short_description")));
            item.setProvider_price_short_description(String.valueOf(result.getFieldValue("provider_price_short_description")));
            item.setExpiration_description(String.valueOf(result.getFieldValue("expiration_description")));
            item.setIm_status_long_description(String.valueOf(result.getFieldValue("im_status_long_description")));
            item.setIm_status_short_description(String.valueOf(result.getFieldValue("im_status_short_description")));
            item.setProvider_status_description(String.valueOf(result.getFieldValue("provider_status_description")));
            item.setTkt_description(String.valueOf(result.getFieldValue("tkt_description")));
            item.setTradeable_description(String.valueOf(result.getFieldValue("tradeable_description")));
            item.setUnit_instrument_description(String.valueOf(result.getFieldValue("unit_instrument_description")));
            item.setLong_name(String.valueOf(result.getFieldValue("long_name")));
            item.setMarketing_long_name(String.valueOf(result.getFieldValue("marketing_long_name")));
            item.setMarketing_short_name(String.valueOf(result.getFieldValue("marketing_short_name")));
            item.setMinimal_denomination(String.valueOf(result.getFieldValue("minimal_denomination")));
            item.setNominal_amount(String.valueOf(result.getFieldValue("nominal_amount")));
            item.setNominal_currency(String.valueOf(result.getFieldValue("nominal_currency")));
            item.setShort_name(String.valueOf(result.getFieldValue("short_name")));
        }
        return item;
    }


    public static List<Item> getResults(String startDate, String endDate, String instrument, String series)throws SolrServerException, FileNotFoundException, IOException  {

        SolrService solrService = new SolrService();
        solrServer = solrService.getSolrServer();


        SolrQuery solrQuery;

        solrQuery = new SolrQuery("instrument:" + instrument);
        solrQuery = new SolrQuery("series:" + series);
        solrQuery.addFilterQuery("price_date:[" + startDate + " TO " + endDate + "]");
//        solrQuery = new SolrQuery("*:*");
        solrQuery.setStart(0);                          //startign index
        solrQuery.setRows(1000);                          //number of rows
        solrQuery.addSort("price_date", SolrQuery.ORDER.asc);
        List<Item> list = new ArrayList<Item>();

        QueryResponse response = null;
        solrQuery.addFacetField("instrument");
        solrQuery.setFacetLimit(Integer.MAX_VALUE);
        try {
            float start = System.currentTimeMillis();
            response = getSolrServer().query(solrQuery);
            System.out.println("Solr took: " + response.getQTime());
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        FacetField fieldShortName = response.getFacetField("instrument");

        List<String> allShortNames = new ArrayList<>();
        List<FacetField.Count> valuesShortNames = fieldShortName.getValues();
        for (FacetField.Count count : valuesShortNames){
            allShortNames.add(count.getName());
        }


        ArrayList<Float> movingAverages = new ArrayList<Float>();
        assert response != null;

        SolrDocumentList results = response.getResults();
        for (SolrDocument result : results) {
            Item item = new Item();
            item.setId(String.valueOf(result.getFieldValue("id")));

            item.setPrice(String.valueOf(result.getFieldValue("price")));
            try{
                item.setPrice_date(String.valueOf(result.getFieldValue("price_date")));
            }catch (Exception e){
                item.setPrice_date(null);
            }
            item.setQuality_status(String.valueOf(result.getFieldValue("quality_status")));
            item.setSeries(String.valueOf(result.getFieldValue("series")));
            item.setActive(String.valueOf(result.getFieldValue("active")));
            item.setCurrency(String.valueOf(result.getFieldValue("currency")));
            item.setDelivery_frequency(String.valueOf(result.getFieldValue("delivery_frequency")));
            item.setDelivery_frequency_description(String.valueOf(result.getFieldValue("delivery_frequency_description")));
            item.setFull_export_pars(String.valueOf(result.getFieldValue("full_export_pars")));
            item.setFull_export_risk(String.valueOf(result.getFieldValue("full_export_risk")));
            item.setIm_price(String.valueOf(result.getFieldValue("im_price")));
            item.setIm_price_description(String.valueOf(result.getFieldValue("im_price_desc")));
            item.setIm_price_short_description(String.valueOf(result.getFieldValue("im_price_short_description")));
            item.setInitial_load(String.valueOf(result.getFieldValue("initial_load")));
            item.setInstrument(String.valueOf(result.getFieldValue("instrument")));
            item.setIs_derived(String.valueOf(result.getFieldValue("is_derived")));
            item.setIs_imi_internal(String.valueOf(result.getFieldValue("is_imi_internal")));
            item.setOrder_id(String.valueOf(result.getFieldValue("order_id")));
            item.setProvider(String.valueOf(result.getFieldValue("provider")));
            item.setProvider_price(String.valueOf(result.getFieldValue("provider_price")));
            item.setResearch_id(String.valueOf(result.getFieldValue("research_id")));
            item.setUnit_series(String.valueOf(result.getFieldValue("unit_series")));
            item.setCreation_date_instrument(String.valueOf(result.getFieldValue("creation_date_instrument")));
            item.setExpiration(String.valueOf(result.getFieldValue("expiration")));
            item.setIsin(String.valueOf(result.getFieldValue("isin")));
            item.setSsp_fi(String.valueOf(result.getFieldValue("ssp_fi")));
            item.setValor(String.valueOf(result.getFieldValue("valor")));
            item.setIm_status(String.valueOf(result.getFieldValue("im_status")));
            item.setFo_type(String.valueOf(result.getFieldValue("fo_type")));
            item.setIm_internal(String.valueOf(result.getFieldValue("im_internal")));
            item.setPlaceholder(String.valueOf(result.getFieldValue("placeholder")));
            item.setTkt(String.valueOf(result.getFieldValue("tkt")));
            item.setTradeable(String.valueOf(result.getFieldValue("tradeable")));
            item.setUbs_relevant(String.valueOf(result.getFieldValue("ubs_relevant")));
            item.setUbs_tc(String.valueOf(result.getFieldValue("ubs_tc")));
            item.setUbs_tcadd(String.valueOf(result.getFieldValue("ubs_tcadd")));
            item.setIssue_price_currency(String.valueOf(result.getFieldValue("issue_price_currency")));
            item.setUnit_series_description(String.valueOf(result.getFieldValue("unit_series_description")));
            item.setUnit_instrument(String.valueOf(result.getFieldValue("unit_instrument")));
            item.setDelivery_frequency_description(String.valueOf(result.getFieldValue("delivery_frequency_description")));
            item.setProvider_description(String.valueOf(result.getFieldValue("data_provider_short_description")));
            item.setProvider_price_short_description(String.valueOf(result.getFieldValue("provider_price_short_description")));
            item.setExpiration_description(String.valueOf(result.getFieldValue("expiration_description")));
            item.setIm_status_long_description(String.valueOf(result.getFieldValue("im_status_long_description")));
            item.setIm_status_short_description(String.valueOf(result.getFieldValue("im_status_short_description")));
            item.setProvider_status_description(String.valueOf(result.getFieldValue("provider_status_description")));
            item.setTkt_description(String.valueOf(result.getFieldValue("tkt_description")));
            item.setTradeable_description(String.valueOf(result.getFieldValue("tradeable_description")));
            item.setUnit_instrument_description(String.valueOf(result.getFieldValue("unit_instrument_description")));
            item.setLong_name(String.valueOf(result.getFieldValue("long_name")));
            item.setMarketing_long_name(String.valueOf(result.getFieldValue("marketing_long_name")));
            item.setMarketing_short_name(String.valueOf(result.getFieldValue("marketing_short_name")));
            item.setMinimal_denomination(String.valueOf(result.getFieldValue("minimal_denomination")));
            item.setNominal_amount(String.valueOf(result.getFieldValue("nominal_amount")));
            item.setNominal_currency(String.valueOf(result.getFieldValue("nominal_currency")));
            item.setShort_name(String.valueOf(result.getFieldValue("short_name")));
            list.add(item);
        }
        int size = results.size()- 1;

        return list;
    }


    public static List<Item> getResultsOnInstrumentAndSeries(String instrument, String series)throws SolrServerException, FileNotFoundException, IOException  {

        SolrService solrService = new SolrService();
        solrServer = solrService.getSolrServer();


        SolrQuery solrQuery;

        solrQuery = new SolrQuery("instrument:" + instrument);
        solrQuery = new SolrQuery("series:" + series);
        solrQuery.setStart(0);                          //startign index
        solrQuery.setRows(1000);                          //number of rows
        List<Item> list = new ArrayList<Item>();
        solrQuery.addSort("price_date", SolrQuery.ORDER.asc);


        QueryResponse response = null;
        try {
            float start = System.currentTimeMillis();
            response = getSolrServer().query(solrQuery);
            System.out.println("Solr took: " + response.getQTime());
        } catch (SolrServerException e) {
            e.printStackTrace();
        }


        assert response != null;
        SolrDocumentList results = response.getResults();

        for (SolrDocument result : results) {
            Item item = new Item();
            item.setId(String.valueOf(result.getFieldValue("id")));

            item.setPrice(String.valueOf(result.getFieldValue("price")));
            try{
                item.setPrice_date(String.valueOf(result.getFieldValue("price_date")));
            }catch (Exception e){
                item.setPrice_date(null);
            }
            item.setQuality_status(String.valueOf(result.getFieldValue("quality_status")));
            item.setSeries(String.valueOf(result.getFieldValue("series")));
            item.setActive(String.valueOf(result.getFieldValue("active")));
            item.setCurrency(String.valueOf(result.getFieldValue("currency")));
            item.setDelivery_frequency(String.valueOf(result.getFieldValue("delivery_frequency")));
            item.setDelivery_frequency_description(String.valueOf(result.getFieldValue("delivery_frequency_description")));
            item.setFull_export_pars(String.valueOf(result.getFieldValue("full_export_pars")));
            item.setFull_export_risk(String.valueOf(result.getFieldValue("full_export_risk")));
            item.setIm_price(String.valueOf(result.getFieldValue("im_price")));
            item.setIm_price_description(String.valueOf(result.getFieldValue("im_price_desc")));
            item.setIm_price_short_description(String.valueOf(result.getFieldValue("im_price_short_description")));
            item.setInitial_load(String.valueOf(result.getFieldValue("initial_load")));
            item.setInstrument(String.valueOf(result.getFieldValue("instrument")));
            item.setIs_derived(String.valueOf(result.getFieldValue("is_derived")));
            item.setIs_imi_internal(String.valueOf(result.getFieldValue("is_imi_internal")));
            item.setOrder_id(String.valueOf(result.getFieldValue("order_id")));
            item.setProvider(String.valueOf(result.getFieldValue("provider")));
            item.setProvider_price(String.valueOf(result.getFieldValue("provider_price")));
            item.setResearch_id(String.valueOf(result.getFieldValue("research_id")));
            item.setUnit_series(String.valueOf(result.getFieldValue("unit_series")));
            item.setCreation_date_instrument(String.valueOf(result.getFieldValue("creation_date_instrument")));
            item.setExpiration(String.valueOf(result.getFieldValue("expiration")));
            item.setIsin(String.valueOf(result.getFieldValue("isin")));
            item.setSsp_fi(String.valueOf(result.getFieldValue("ssp_fi")));
            item.setValor(String.valueOf(result.getFieldValue("valor")));
            item.setIm_status(String.valueOf(result.getFieldValue("im_status")));
            item.setFo_type(String.valueOf(result.getFieldValue("fo_type")));
            item.setIm_internal(String.valueOf(result.getFieldValue("im_internal")));
            item.setPlaceholder(String.valueOf(result.getFieldValue("placeholder")));
            item.setTkt(String.valueOf(result.getFieldValue("tkt")));
            item.setTradeable(String.valueOf(result.getFieldValue("tradeable")));
            item.setUbs_relevant(String.valueOf(result.getFieldValue("ubs_relevant")));
            item.setUbs_tc(String.valueOf(result.getFieldValue("ubs_tc")));
            item.setUbs_tcadd(String.valueOf(result.getFieldValue("ubs_tcadd")));
            item.setIssue_price_currency(String.valueOf(result.getFieldValue("issue_price_currency")));
            item.setUnit_series_description(String.valueOf(result.getFieldValue("unit_series_description")));
            item.setUnit_instrument(String.valueOf(result.getFieldValue("unit_instrument")));
            item.setDelivery_frequency_description(String.valueOf(result.getFieldValue("delivery_frequency_description")));
            item.setProvider_description(String.valueOf(result.getFieldValue("data_provider_short_description")));
            item.setProvider_price_short_description(String.valueOf(result.getFieldValue("provider_price_short_description")));
            item.setExpiration_description(String.valueOf(result.getFieldValue("expiration_description")));
            item.setIm_status_long_description(String.valueOf(result.getFieldValue("im_status_long_description")));
            item.setIm_status_short_description(String.valueOf(result.getFieldValue("im_status_short_description")));
            item.setProvider_status_description(String.valueOf(result.getFieldValue("provider_status_description")));
            item.setTkt_description(String.valueOf(result.getFieldValue("tkt_description")));
            item.setTradeable_description(String.valueOf(result.getFieldValue("tradeable_description")));
            item.setUnit_instrument_description(String.valueOf(result.getFieldValue("unit_instrument_description")));
            item.setLong_name(String.valueOf(result.getFieldValue("long_name")));
            item.setMarketing_long_name(String.valueOf(result.getFieldValue("marketing_long_name")));
            item.setMarketing_short_name(String.valueOf(result.getFieldValue("marketing_short_name")));
            item.setMinimal_denomination(String.valueOf(result.getFieldValue("minimal_denomination")));
            item.setNominal_amount(String.valueOf(result.getFieldValue("nominal_amount")));
            item.setNominal_currency(String.valueOf(result.getFieldValue("nominal_currency")));
            item.setShort_name(String.valueOf(result.getFieldValue("short_name")));
            list.add(item);
        }

        return list;
    }

    public static float median(ArrayList<Float> m) {
        int middle = m.size()/2;
        if (m.size()%2 == 1) {
            return m.get(middle);
        } else {
            return (m.get(middle - 1) + m.get(middle)) / 2;
        }
    }

    public static class RecordItem {
        private static final String ID = "id";
        private static final String LAST_UPDATE_DATE = "last_update_date";
        private static final String PRICE = "price";
        private static final String PRICE_DATE = "price_date";
        private static final String QUALITY_STATUS = "quality_status";
        private static final String SERIES = "series";
        private static final String OL_VERSION = "ol_version";
        private static final String ACTIVE = "active";
        private static final String CURRENCY = "currency";
        private static final String DELIVERY_FREQUENCY = "delivery_frequency";
        private static final String DELIVERY_FREQUENCY_DESCRIPTION = "delivery_frequency_description";
        private static final String FULL_EXPORT_PARS = "full_export_pars";
        private static final String FULL_EXPORT_RISK = "full_export_risk";
        private static final String IM_PRICE = "im_price";
        private static final String IM_PRICE_DESCRIPTION = "im_price_description";
        private static final String IM_PRICE_SHORT_DESCRIPTION = "im_price_short_description";
        private static final String INITIAL_LOAD = "initial_load";
        private static final String INSTRUMENT = "instrument";
        private static final String IS_DERIVED = "is_derived";
        private static final String IS_IMI_INTERNAL = "is_imi_internal";
        private static final String ORDER_ID = "order_id";
        private static final String PROVIDER = "provider";
        private static final String PROVIDER_DESCRIPTION = "provider_description";
        private static final String PROVIDER_PRICE = "provider_price";
        private static final String PROVIDER_PRICE_SHORT_DESCRIPTION = "provider_price_short_description";
        private static final String RESEARCH_ID = "research_id";
        private static final String UNIT_SERIES = "unit_series";
        private static final String UNIT_SERIES_DESCRIPTION = "unit_series_description";
        private static final String OL_VERSION_SERIES = "ol_version_series";
        private static final String CREATION_DATE_INSTRUMENT = "creation_date_instrument";
        private static final String EXPIRATION = "expiration";
        private static final String EXPIRATION_DESCRIPTION = "expiration_description";
        private static final String ISIN = "isin";
        private static final String SSP_FI = "ssp_fi";
        private static final String VALOR = "valor";
        private static final String IM_STATUS = "im_status";
        private static final String IM_STATUS_DESCRIPTION = "im_status_description";
        private static final String IM_STATUS_SHORT_DESCRIPTION = "im_status_short_description";
        private static final String IM_STATUS_LONG_DESCRIPTION = "im_status_long_description";
        private static final String FO_TYPE = "fo_type";
        private static final String IM_INTERNAL = "im_internal";
        private static final String PLACEHOLDER = "placeholder";
        private static final String TKT = "tkt";
        private static final String TKT_DESCRIPTION = "tkt_description";
        private static final String TRADEABLE = "tradeable";
        private static final String TRADEABLE_DESCRIPTION = "tradeable_description";
        private static final String TRADEABLE_SHORT_DESCRIPTION = "tradeable_short_description";
        private static final String UBS_RELEVANT = "ubs_relevant";
        private static final String UBS_TC = "ubs_tc";
        private static final String UBS_TCADD = "ubs_tcadd";
        private static final String ISSUE_PRICE_CURRENCY = "issue_price_currency";
        private static final String LONG_NAME = "long_name";
        private static final String MARKETING_LONG_NAME = "marketing_long_name";
        private static final String MARKETING_SHORT_NAME = "marketing_short_name";
        private static final String MINIMAL_DENOMINATION = "minimal_denomination";
        private static final String NOMINAL_AMOUNT = "nominal_amount";
        private static final String NOMINAL_CURRENCY = "nominal_currency";
        private static final String PROVIDER_STATUS = "provider_status";
        private static final String PROVIDER_STATUS_DESCRIPTION = "provider_status_description";
        private static final String UNIT_INSTRUMENT = "unit_instrument";
        private static final String UNIT_INSTRUMENT_DESCRIPTION = "unit_instrument_description";
        private static final String OL_VERSION_INSTRUMENT = "ol_version_instrument";
        private static final String SHORT_NAME = "short_name";

        @Field(ID)
        private String id;

        @Field(LAST_UPDATE_DATE)
        private String last_updateDate;

        @Field(PRICE)
        private Double price;

        @Field(PRICE_DATE)
        private String price_date;

        @Field(QUALITY_STATUS)
        private Boolean quality_status;

        @Field(SERIES)
        private String series;

        @Field(OL_VERSION)
        private int ol_version;

        @Field(ACTIVE)
        private Boolean active;

        @Field(CURRENCY)
        private String currency;

        @Field(DELIVERY_FREQUENCY)
        private String delivery_frequency;

        @Field(DELIVERY_FREQUENCY_DESCRIPTION)
        private String delivery_frequency_description;

        @Field(FULL_EXPORT_PARS)
        private Boolean full_export_pars;

        @Field(FULL_EXPORT_RISK)
        private Boolean full_export_risk;

        @Field(IM_PRICE)
        private String im_price;

        @Field(IM_PRICE_DESCRIPTION)
        private String im_price_description;

        @Field(IM_PRICE_SHORT_DESCRIPTION)
        private String im_price_short_description;

        @Field(INITIAL_LOAD)
        private Boolean initial_load;

        @Field(INSTRUMENT)
        private String instrument;

        @Field(IS_DERIVED)
        private Boolean is_derived;

        @Field(IS_IMI_INTERNAL)
        private Boolean is_imi_internal;

        @Field(ORDER_ID)
        private String order_id;

        @Field(PROVIDER)
        private String provider;

        @Field(PROVIDER_DESCRIPTION)
        private String provider_description;

        @Field(PROVIDER_PRICE)
        private String provider_price;

        @Field(PROVIDER_PRICE_SHORT_DESCRIPTION)
        private String provider_price_short_description;

        @Field(RESEARCH_ID)
        private String research_id;

        @Field(UNIT_SERIES)
        private String unit_series;

        @Field(UNIT_SERIES_DESCRIPTION)
        private String unit_series_description;

        @Field(OL_VERSION_SERIES)
        private String ol_version_series;

        @Field(CREATION_DATE_INSTRUMENT)
        private String creation_date_instrument;

        @Field(EXPIRATION)
        private String expiration;

        @Field(EXPIRATION_DESCRIPTION)
        private String expiration_description;

        @Field(ISIN)
        private String isin;

        @Field(SSP_FI)
        private String ssp_fi;

        @Field(VALOR)
        private int valor;

        @Field(IM_STATUS)
        private String im_status;

        @Field(IM_STATUS_DESCRIPTION)
        private String im_status_description;

        @Field(IM_STATUS_LONG_DESCRIPTION)
        private String im_status_long_description;

        @Field(IM_STATUS_SHORT_DESCRIPTION)
        private String im_status_short_description;

        @Field(FO_TYPE)
        private int fo_type;

        @Field(IM_INTERNAL)
        private Boolean im_internal;

        @Field(PLACEHOLDER)
        private Boolean placeholder;

        @Field(TKT)
        private String tkt;

        @Field(TKT_DESCRIPTION)
        private String tkt_description;

        @Field(TRADEABLE)
        private String tradeable;

        @Field(TRADEABLE_DESCRIPTION)
        private String tradeable_description;

        @Field(TRADEABLE_SHORT_DESCRIPTION)
        private String tradeable_short_description;

        @Field(UBS_RELEVANT)
        private Boolean ubs_relevant;

        @Field(UBS_TC)
        private String ubs_tc;

        @Field(UBS_TCADD)
        private String ubs_tcadd;

        @Field(ISSUE_PRICE_CURRENCY)
        private String issue_price_currency;

        @Field(LONG_NAME)
        private String long_name;

        @Field(MARKETING_LONG_NAME)
        private String marketing_long_name;

        @Field(MARKETING_SHORT_NAME)
        private String marketing_short_name;

        @Field(MINIMAL_DENOMINATION)
        private float minimal_denomination;

        @Field(NOMINAL_AMOUNT)
        private float nominal_amount;

        @Field(NOMINAL_CURRENCY)
        private String nominal_currency;

        @Field(PROVIDER_STATUS)
        private String provider_status;

        @Field(PROVIDER_STATUS_DESCRIPTION)
        private String provider_status_description;

        @Field(UNIT_INSTRUMENT)
        private String unit_instrument;

        @Field(UNIT_INSTRUMENT_DESCRIPTION)
        private String unit_instrument_description;

        @Field(OL_VERSION_INSTRUMENT)
        private String ol_version_instrument;

        @Field(SHORT_NAME)
        private String short_name;

        public void setId(String id) {
            this.id = id;
        }

        public void setLast_updateDate(String last_updateDate) {
            this.last_updateDate = last_updateDate;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public void setPrice_date(String price_date) {
            this.price_date = price_date;
        }

        public void setQuality_status(Boolean quality_status) {
            this.quality_status = quality_status;
        }

        public void setSeries(String series) {
            this.series = series;
        }

        public void setOl_version(int ol_version) {
            this.ol_version = ol_version;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public void setDelivery_frequency(String delivery_frequency) {
            this.delivery_frequency = delivery_frequency;
        }

        public void setDelivery_frequency_description(String delivery_frequency_description) {
            this.delivery_frequency_description = delivery_frequency_description;
        }

        public void setFull_export_pars(Boolean full_export_pars) {
            this.full_export_pars = full_export_pars;
        }

        public void setFull_export_risk(Boolean full_export_risk) {
            this.full_export_risk = full_export_risk;
        }

        public void setIm_price(String im_price) {
            this.im_price = im_price;
        }

        public void setIm_price_description(String im_price_description) {
            this.im_price_description = im_price_description;
        }

        public void setIm_price_short_description(String im_price_short_description) {
            this.im_price_short_description = im_price_short_description;
        }

        public void setInitial_load(Boolean initial_load) {
            this.initial_load = initial_load;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
        }

        public void setIs_derived(Boolean is_derived) {
            this.is_derived = is_derived;
        }

        public void setIs_imi_internal(Boolean is_imi_internal) {
            this.is_imi_internal = is_imi_internal;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public void setProvider_description(String provider_description) {
            this.provider_description = provider_description;
        }

        public void setProvider_price(String provider_price) {
            this.provider_price = provider_price;
        }

        public void setProvider_price_short_description(String provider_price_short_description) {
            this.provider_price_short_description = provider_price_short_description;
        }

        public void setResearch_id(String research_id) {
            this.research_id = research_id;
        }

        public void setUnit_series(String unit_series) {
            this.unit_series = unit_series;
        }

        public void setUnit_series_description(String unit_series_description) {
            this.unit_series_description = unit_series_description;
        }

        public void setOl_version_series(String ol_version_series) {
            this.ol_version_series = ol_version_series;
        }

        public void setCreation_date_instrument(String creation_date_instrument) {
            this.creation_date_instrument = creation_date_instrument;
        }

        public void setExpiration(String expiration) {
            this.expiration = expiration;
        }

        public void setExpiration_description(String expiration_description) {
            this.expiration_description = expiration_description;
        }

        public void setIsin(String isin) {
            this.isin = isin;
        }

        public void setSsp_fi(String ssp_fi) {
            this.ssp_fi = ssp_fi;
        }

        public void setValor(int valor) {
            this.valor = valor;
        }

        public void setIm_status(String im_status) {
            this.im_status = im_status;
        }

        public void setIm_status_description(String im_status_description) {
            this.im_status_description = im_status_description;
        }

        public void setIm_status_long_description(String im_status_long_description) {
            this.im_status_long_description = im_status_long_description;
        }

        public void setIm_status_short_description(String im_status_short_description) {
            this.im_status_short_description = im_status_short_description;
        }

        public void setFo_type(int fo_type) {
            this.fo_type = fo_type;
        }

        public void setIm_internal(Boolean im_internal) {
            this.im_internal = im_internal;
        }

        public void setPlaceholder(Boolean placeholder) {
            this.placeholder = placeholder;
        }

        public void setTkt(String tkt) {
            this.tkt = tkt;
        }

        public void setTkt_description(String tkt_description) {
            this.tkt_description = tkt_description;
        }

        public void setTradeable(String tradeable) {
            this.tradeable = tradeable;
        }

        public void setTradeable_description(String tradeable_description) {
            this.tradeable_description = tradeable_description;
        }

        public void setTradeable_short_description(String tradeable_short_description) {
            this.tradeable_short_description = tradeable_short_description;
        }

        public void setUbs_relevant(Boolean ubs_relevant) {
            this.ubs_relevant = ubs_relevant;
        }

        public void setUbs_tc(String ubs_tc) {
            this.ubs_tc = ubs_tc;
        }

        public void setUbs_tcadd(String ubs_tcadd) {
            this.ubs_tcadd = ubs_tcadd;
        }

        public void setIssue_price_currency(String issue_price_currency) {
            this.issue_price_currency = issue_price_currency;
        }

        public void setLong_name(String long_name) {
            this.long_name = long_name;
        }

        public void setMarketing_long_name(String marketing_long_name) {
            this.marketing_long_name = marketing_long_name;
        }

        public void setMarketing_short_name(String marketing_short_name) {
            this.marketing_short_name = marketing_short_name;
        }

        public void setMinimal_denomination(float minimal_denomination) {
            this.minimal_denomination = minimal_denomination;
        }

        public void setNominal_amount(float nominal_amount) {
            this.nominal_amount = nominal_amount;
        }

        public void setNominal_currency(String nominal_currency) {
            this.nominal_currency = nominal_currency;
        }

        public void setProvider_status(String provider_status) {
            this.provider_status = provider_status;
        }

        public void setProvider_status_description(String provider_status_description) {
            this.provider_status_description = provider_status_description;
        }

        public void setUnit_instrument(String unit_instrument) {
            this.unit_instrument = unit_instrument;
        }

        public void setUnit_instrument_description(String unit_instrument_description) {
            this.unit_instrument_description = unit_instrument_description;
        }

        public void setOl_version_instrument(String ol_version_instrument) {
            this.ol_version_instrument = ol_version_instrument;
        }

        public void setShort_name(String short_name) {
            this.short_name = short_name;
        }

        public String getId() {

            return id;
        }

        public String getLast_updateDate() {
            return last_updateDate;
        }

        public Double getPrice() {
            return price;
        }

        public String getPrice_date() {
            return price_date;
        }

        public Boolean getQuality_status() {
            return quality_status;
        }

        public String getSeries() {
            return series;
        }

        public int getOl_version() {
            return ol_version;
        }

        public Boolean getActive() {
            return active;
        }

        public String getCurrency() {
            return currency;
        }

        public String getDelivery_frequency() {
            return delivery_frequency;
        }

        public String getDelivery_frequency_description() {
            return delivery_frequency_description;
        }

        public Boolean getFull_export_pars() {
            return full_export_pars;
        }

        public Boolean getFull_export_risk() {
            return full_export_risk;
        }

        public String getIm_price() {
            return im_price;
        }

        public String getIm_price_description() {
            return im_price_description;
        }

        public String getIm_price_short_description() {
            return im_price_short_description;
        }

        public Boolean getInitial_load() {
            return initial_load;
        }

        public String getInstrument() {
            return instrument;
        }

        public Boolean getIs_derived() {
            return is_derived;
        }

        public Boolean getIs_imi_internal() {
            return is_imi_internal;
        }

        public String getOrder_id() {
            return order_id;
        }

        public String getProvider() {
            return provider;
        }

        public String getProvider_description() {
            return provider_description;
        }

        public String getProvider_price() {
            return provider_price;
        }

        public String getProvider_price_short_description() {
            return provider_price_short_description;
        }

        public String getResearch_id() {
            return research_id;
        }

        public String getUnit_series() {
            return unit_series;
        }

        public String getUnit_series_description() {
            return unit_series_description;
        }

        public String getOl_version_series() {
            return ol_version_series;
        }

        public String getCreation_date_instrument() {
            return creation_date_instrument;
        }

        public String getExpiration() {
            return expiration;
        }

        public String getExpiration_description() {
            return expiration_description;
        }

        public String getIsin() {
            return isin;
        }

        public String getSsp_fi() {
            return ssp_fi;
        }

        public int getValor() {
            return valor;
        }

        public String getIm_status() {
            return im_status;
        }

        public String getIm_status_description() {
            return im_status_description;
        }

        public String getIm_status_long_description() {
            return im_status_long_description;
        }

        public String getIm_status_short_description() {
            return im_status_short_description;
        }

        public int getFo_type() {
            return fo_type;
        }

        public Boolean getIm_internal() {
            return im_internal;
        }

        public Boolean getPlaceholder() {
            return placeholder;
        }

        public String getTkt() {
            return tkt;
        }

        public String getTkt_description() {
            return tkt_description;
        }

        public String getTradeable() {
            return tradeable;
        }

        public String getTradeable_description() {
            return tradeable_description;
        }

        public String getTradeable_short_description() {
            return tradeable_short_description;
        }

        public Boolean getUbs_relevant() {
            return ubs_relevant;
        }

        public String getUbs_tc() {
            return ubs_tc;
        }

        public String getUbs_tcadd() {
            return ubs_tcadd;
        }

        public String getIssue_price_currency() {
            return issue_price_currency;
        }

        public String getLong_name() {
            return long_name;
        }

        public String getMarketing_long_name() {
            return marketing_long_name;
        }

        public String getMarketing_short_name() {
            return marketing_short_name;
        }

        public float getMinimal_denomination() {
            return minimal_denomination;
        }

        public float getNominal_amount() {
            return nominal_amount;
        }

        public String getNominal_currency() {
            return nominal_currency;
        }

        public String getProvider_status() {
            return provider_status;
        }

        public String getProvider_status_description() {
            return provider_status_description;
        }

        public String getUnit_instrument() {
            return unit_instrument;
        }

        public String getUnit_instrument_description() {
            return unit_instrument_description;
        }

        public String getOl_version_instrument() {
            return ol_version_instrument;
        }

        public String getShort_name() {
            return short_name;
        }

        public RecordItem() {
            this.id = id;
            this.last_updateDate = last_updateDate;
            this.price = price;
            this.price_date = price_date;
            this.quality_status = quality_status;
            this.series = series;
            this.ol_version = ol_version;
            this.active = active;
            this.currency = currency;
            this.delivery_frequency = delivery_frequency;
            this.delivery_frequency_description = delivery_frequency_description;
            this.full_export_pars = full_export_pars;
            this.full_export_risk = full_export_risk;
            this.im_price = im_price;
            this.im_price_description = im_price_description;
            this.im_price_short_description = im_price_short_description;
            this.initial_load = initial_load;
            this.instrument = instrument;
            this.is_derived = is_derived;
            this.is_imi_internal = is_imi_internal;
            this.order_id = order_id;
            this.provider = provider;
            this.provider_description = provider_description;
            this.provider_price = provider_price;
            this.provider_price_short_description = provider_price_short_description;
            this.research_id = research_id;
            this.unit_series = unit_series;
            this.unit_series_description = unit_series_description;
            this.ol_version_series = ol_version_series;
            this.creation_date_instrument = creation_date_instrument;
            this.expiration = expiration;
            this.expiration_description = expiration_description;
            this.isin = isin;
            this.ssp_fi = ssp_fi;
            this.valor = valor;
            this.im_status = im_status;
            this.im_status_description = im_status_description;
            this.im_status_long_description = im_status_long_description;
            this.im_status_short_description = im_status_short_description;
            this.fo_type = fo_type;
            this.im_internal = im_internal;
            this.placeholder = placeholder;
            this.tkt = tkt;
            this.tkt_description = tkt_description;
            this.tradeable = tradeable;
            this.tradeable_description = tradeable_description;
            this.tradeable_short_description = tradeable_short_description;
            this.ubs_relevant = ubs_relevant;
            this.ubs_tc = ubs_tc;
            this.ubs_tcadd = ubs_tcadd;
            this.issue_price_currency = issue_price_currency;
            this.long_name = long_name;
            this.marketing_long_name = marketing_long_name;
            this.marketing_short_name = marketing_short_name;
            this.minimal_denomination = minimal_denomination;
            this.nominal_amount = nominal_amount;
            this.nominal_currency = nominal_currency;
            this.provider_status = provider_status;
            this.provider_status_description = provider_status_description;
            this.unit_instrument = unit_instrument;
            this.unit_instrument_description = unit_instrument_description;
            this.ol_version_instrument = ol_version_instrument;
            this.short_name = short_name;
        }
    }
    public static String toUtcDate(String dateStr) {
        HashMap<String, String> monthToMonth = new HashMap<String, String>();
        monthToMonth.put("Jan", "01");
        monthToMonth.put("Feb", "02");
        monthToMonth.put("Mar", "03");
        monthToMonth.put("Apr", "04");
        monthToMonth.put("May", "05");
        monthToMonth.put("Jun", "06");
        monthToMonth.put("Jul", "07");
        monthToMonth.put("Aug", "08");
        monthToMonth.put("Sep", "09");
        monthToMonth.put("Oct", "10");
        monthToMonth.put("Nov", "11");
        monthToMonth.put("Dec", "12");
        String month = monthToMonth.get(dateStr.substring(4, 7));
        String year = dateStr.substring(24);
        String day = dateStr.substring(8, 10);
        dateStr = year + "-" + month + "-" + day;
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String[] dateFormats = {"yyyy-MM-dd", "MMM dd, yyyy hh:mm:ss Z"};
        for (String dateFormat : dateFormats) {
            try {
                return out.format(new SimpleDateFormat(dateFormat).parse(dateStr));
            } catch (ParseException ignore) { }
        }
        throw new IllegalArgumentException("Invalid date: " + dateStr);
    }
}