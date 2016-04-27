package mx.gob.guaymas.guaymas60.ws;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import mx.gob.guaymas.guaymas60.classes.Noticia;
import mx.gob.guaymas.guaymas60.classes.ReporteCiudadano;
import mx.gob.guaymas.guaymas60.utils.Constant;
import mx.gob.guaymas.guaymas60.utils.JSONParser;

/**
 * Created by julionava on 2/11/16.
 */
public class CallWebService {

    public JSONParser jsonParser;

    public CallWebService(){
        jsonParser = new JSONParser();
    }

    public List<String>  getSuburbs(){
        List<String> listSuburbs = new ArrayList<String>();
        JSONArray json = jsonParser.getJSONFromUrl(WebServiceName.getUrlSuburbs());
        for (int i = 0; i < json.length(); i++) {
            try {

                JSONObject c = json.getJSONObject(i);
                String tempSuburb = c.getString("nombre");
                listSuburbs.add(tempSuburb);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  listSuburbs;
    }

    public ReporteCiudadano saveProfile(ReporteCiudadano reporte){

        ReporteCiudadano reportResult = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpPost httppost = new HttpPost(WebServiceName.getUrlSendReport());
            Charset chars = Charset.forName("UTF-8");
            MultipartEntity mpEntity = new MultipartEntity();
            if(!reporte.getFoto().isEmpty()) {
                File file = new File(reporte.getFoto());
                if(file.exists()){
                    ContentBody foto = new FileBody(file, "image/jpeg");
                    mpEntity.addPart("foto", foto);
                }
            }

            mpEntity.addPart("medio", new StringBody(Constant.MEDIO, chars));
            mpEntity.addPart("nombre", new StringBody(reporte.getInteresado(), chars));
            mpEntity.addPart("dir", new StringBody(reporte.getDireccion(), chars));
            mpEntity.addPart("colonia", new StringBody(reporte.getColonia(), chars));
            mpEntity.addPart("tel", new StringBody(reporte.getTelefono(), chars));
            mpEntity.addPart("email", new StringBody(reporte.getCorreo(), chars));
            mpEntity.addPart("asunto", new StringBody(reporte.getAsunto(), chars));
            mpEntity.addPart("lat", new StringBody(reporte.getLatitud(), chars));
            mpEntity.addPart("long", new StringBody(reporte.getLongitud(), chars));
            mpEntity.addPart("tipo", new StringBody(reporte.getTipo(), chars));
            httppost.setEntity(mpEntity);
            HttpResponse response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            StringBuilder builder = new StringBuilder();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            //httpclient.getConnectionManager().shutdown();

            try {
                Log.i("info return from send", builder.toString());

                JSONObject jobject = new JSONObject(builder.toString());

                //build the response
                int error = jobject.getInt("error");
                String mensaje = jobject.getString("mensaje");
                int folio = jobject.getInt("folio");

                reportResult = new ReporteCiudadano();
                reportResult.setError(error);
                reportResult.setMensaje(mensaje);
                reportResult.setFolio(folio);

                if(error == 0) {

                    JSONObject peticion = jobject.getJSONObject("peticion");
                    reportResult.setInteresado((peticion.getString("interesado") != null) ? peticion.getString("interesado") : "");
                    reportResult.setDireccion((peticion.getString("direccion") != null) ? peticion.getString("direccion") : "");
                    reportResult.setColonia((peticion.getString("colonia") != null) ? peticion.getString("colonia") : "");
                    reportResult.setTelefono((peticion.getString("telefono") != null) ? peticion.getString("telefono") : "");
                    reportResult.setCorreo((peticion.getString("correo") != null) ? peticion.getString("correo") : "");
                    reportResult.setAsunto((peticion.getString("asunto") != null) ? peticion.getString("asunto") : "");
                    reportResult.setLatitud((peticion.getString("latitud") != null) ? peticion.getString("latitud") : "");
                    reportResult.setLongitud((peticion.getString("longitud") != null) ? peticion.getString("longitud") : "");
                    reportResult.setFoto((peticion.getString("foto") != null) ? peticion.getString("foto") : "");
                    reportResult.setTipo((peticion.getString("tipo") != null) ? peticion.getString("tipo") : "");
                    reportResult.setFechain((peticion.getString("fechain") != null) ? peticion.getString("fechain") : "");
                    reportResult.setFechaout((peticion.getString("fechaout") != null) ? peticion.getString("fechaout") : "");
                    reportResult.setMedio((peticion.getString("medio") != null) ? peticion.getString("medio") : "");
                    reportResult.setStatus((peticion.getString("status") != null) ? peticion.getString("status") : "");
                    reportResult.setTema((peticion.getString("tema") != null) ? peticion.getString("tema") : "");
                    reportResult.setAvance((peticion.getString("avance") != null) ? peticion.getString("avance") : "");
                    reportResult.setLn((peticion.getString("ln") != null) ? peticion.getString("ln") : "");
                    reportResult.setAsignado((peticion.getInt("asignado") != 0) ? peticion.getInt("asignado") : 0);
                    reportResult.setCapturo((peticion.getInt("capturo") != 0) ? peticion.getInt("capturo") : 0);
                    reportResult.setUrge((peticion.getString("urge") != null) ? peticion.getString("urge") : "");
                    reportResult.setIndicador((peticion.getString("indicador") != null) ? peticion.getString("indicador") : "");
                    reportResult.setTimestamp((peticion.getString("timestamp") != null) ? peticion.getString("timestamp") : "");

                    reportResult.setColonia((reportResult.getColonia().equals("null")) ? "" : reportResult.getColonia());
                    reportResult.setTipo((reportResult.getTipo().equals("null")) ? "" : reportResult.getTipo());
                    reportResult.setStatus((reportResult.getStatus().equals("null")) ? "" : reportResult.getStatus());
                    reportResult.setAsunto((reportResult.getAsunto().equals("null")) ? "" : reportResult.getAsunto());
                    reportResult.setAvance((reportResult.getAvance().equals("null")) ? "" : reportResult.getAvance());

                }

            } catch (JSONException e) {
                Log.i("JSON Parser", "Error parsing data " + e.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("info return from send", "ERROR SENDING THE INFO" );
        }

        return reportResult;
    }

    public ReporteCiudadano consultReport(int folio){
        ReporteCiudadano report = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpPost httppost = new HttpPost(WebServiceName.getUrlConsultReport());
            MultipartEntity mpEntity = new MultipartEntity();
            mpEntity.addPart("folio", new StringBody(String.valueOf(folio)));
            httppost.setEntity(mpEntity);
            HttpResponse response = httpclient.execute(httppost);
            //httpclient.getConnectionManager().shutdown();
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            StringBuilder builder = new StringBuilder();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }

            try {
                JSONObject jobject = new JSONObject(builder.toString());

                //build the response
                int error = jobject.getInt("error");
                String mensaje = jobject.getString("error_msg");

                report = new ReporteCiudadano();
                report.setError(error);
                report.setMensaje(mensaje);

                if(error == 0) {

                    JSONObject peticion = jobject.getJSONObject("peticion");
                    report.setFolio((peticion.getInt("folio") != 0) ? peticion.getInt("folio") : 0);
                    report.setInteresado((peticion.getString("interesado") != null) ? peticion.getString("interesado") : "");
                    report.setDireccion((peticion.getString("direccion") != null) ? peticion.getString("direccion") : "");
                    report.setColonia((peticion.getString("colonia") != null) ? peticion.getString("colonia") : "");
                    report.setTelefono((peticion.getString("telefono") != null) ? peticion.getString("telefono") : "");
                    report.setCorreo((peticion.getString("correo") != null) ? peticion.getString("correo") : "");
                    report.setAsunto((peticion.getString("asunto") != null) ? peticion.getString("asunto") : "");
                    report.setLatitud((peticion.getString("latitud") != null) ? peticion.getString("latitud") : "");
                    report.setLongitud((peticion.getString("longitud") != null) ? peticion.getString("longitud") : "");
                    report.setFoto((peticion.getString("foto") != null) ? peticion.getString("foto") : "");
                    report.setTipo((peticion.getString("tipo") != null) ? peticion.getString("tipo") : "");
                    report.setFechain((peticion.getString("fechain") != null) ? peticion.getString("fechain") : "");
                    report.setFechaout((peticion.getString("fechaout") != null) ? peticion.getString("fechaout") : "");
                    report.setMedio((peticion.getString("medio") != null) ? peticion.getString("medio") : "");
                    report.setStatus((peticion.getString("status") != null) ? peticion.getString("status") : "");
                    report.setTema((peticion.getString("tema") != null) ? peticion.getString("tema") : "");
                    report.setAvance((peticion.getString("avance") != null) ? peticion.getString("avance") : "");
                    report.setLn((peticion.getString("ln") != null) ? peticion.getString("ln") : "");
                    report.setAsignado((peticion.getInt("asignado") != 0) ? peticion.getInt("asignado") : 0);
                    report.setCapturo((peticion.getInt("capturo") != 0) ? peticion.getInt("capturo") : 0);
                    report.setUrge((peticion.getString("urge") != null) ? peticion.getString("urge") : "");
                    report.setIndicador((peticion.getString("indicador") != null) ? peticion.getString("indicador") : "");
                    report.setTimestamp((peticion.getString("timestamp") != null) ? peticion.getString("timestamp") : "");

                    report.setColonia((report.getColonia().equals("null")) ? "" : report.getColonia());
                    report.setTipo((report.getTipo().equals("null")) ? "" : report.getTipo());
                    report.setStatus((report.getStatus().equals("null")) ? "" : report.getStatus());
                    report.setAsunto((report.getAsunto().equals("null")) ? "" : report.getAsunto());
                    report.setAvance((report.getAvance().equals("null")) ? "" : report.getAvance());

                }

            } catch (JSONException e) {
                report = null;
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return report;
    }


}
