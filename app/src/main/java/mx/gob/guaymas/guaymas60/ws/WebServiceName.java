package mx.gob.guaymas.guaymas60.ws;

/**
 * Created by julionava on 2/11/16.
 */
public class WebServiceName {

    public static String BASE_URL = "http://guaymas.gob.mx/";

    public static String getUrlSuburbs(){
        return BASE_URL+"appg6/reporteciudadano/colonias.php";
    }

    public static String getUrlSendReport(){ return BASE_URL+"appg6/reporteciudadano/enviar_reporte.php"; }

    public static String getUrlConsultReport(){ return BASE_URL+"appg6/reporteciudadano/consultar_reporte.php"; }

    public static String getUrlNews(int page){ return BASE_URL+"api/get_posts/?count=10&page="+page; }

    public static String getUrlEvents(int page){ return BASE_URL+"appg6/eventos/get_events.php?count=10&page="+page; }

}
