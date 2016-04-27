package mx.gob.guaymas.guaymas60;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.gob.guaymas.guaymas60.adapter.NoticiaAdapter;
import mx.gob.guaymas.guaymas60.classes.Noticia;

import mx.gob.guaymas.guaymas60.utils.Constant;
import mx.gob.guaymas.guaymas60.utils.SharedPreferenceHelper;
import mx.gob.guaymas.guaymas60.ws.WebServiceName;

public class NoticiasActivity extends AppCompatActivity {

    private Toolbar toolbar;

    AQuery aq ;
    Noticia noticia= new Noticia();
    ArrayList listaNoticias;
    NoticiaAdapter adapter;
    ListView lvNoticias;

    int totalNoticiasPagina;
    int totalNoticias;
    int totalPaginas;
    JSONArray jsonNoticias;
    Context context;
    SharedPreferenceHelper sharPrefHelper;
    int pagina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);

        setUpToolbar();

        context=this;
        aq = new AQuery(this);
        sharPrefHelper = new SharedPreferenceHelper(Constant.SHARED_PREF_NAME, this);
        pagina= Integer.parseInt(  sharPrefHelper.getStringFromShprf(Constant.PAGINA) );

        listaNoticias = new ArrayList<Noticia>();
        totalPaginas=-1;
        asyncJson(pagina);

        aq.id(R.id.btnAtrasNoticias).clicked(this, "atrasNoticias");
        aq.id(R.id.btnSiguienteNoticias).clicked(this, "SiguienteNoticias");
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_toolbar_noticia));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id ==  android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void atrasNoticias(View button){
        pagina = pagina - 1;
        sharPrefHelper.writeString(Constant.PAGINA, String.valueOf(pagina));
        asyncJson(pagina);
    }


    public void SiguienteNoticias(View button){
        pagina = pagina + 1;
        sharPrefHelper.writeString(Constant.PAGINA, String.valueOf(pagina));
        asyncJson(pagina);
    }

    public void  validarBtnPaginador(){
        getSupportActionBar().setTitle("Noticias - páginas ( " + pagina + " de " + totalPaginas + " )");

         if(pagina>1){
             aq.id(R.id.btnAtrasNoticias).getButton().setVisibility(View.VISIBLE);
         }else{
             aq.id(R.id.btnAtrasNoticias).getButton().setVisibility(View.GONE);
         }
         if(pagina==totalPaginas){
             aq.id(R.id.btnSiguienteNoticias).getButton().setVisibility(View.GONE);
         }else{
             aq.id(R.id.btnSiguienteNoticias).getButton().setVisibility(View.VISIBLE);
         }
    }


    public void asyncJson(int pagina){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true); dialog.setCancelable(true); dialog.setInverseBackgroundForced(false); dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Cargando noticias...");
        String url = WebServiceName.getUrlNews(pagina);
        aq.progress(dialog).ajax(url, JSONObject.class, this, "jsonCallback");
    }

    public void jsonCallback(String url, JSONObject json, AjaxStatus status){

        if(json != null){

            try {
                jsonNoticias =  json.getJSONArray("posts");
                totalNoticias=json.getInt("count_total");
                totalNoticiasPagina=json.getInt("count");
                totalPaginas=json.getInt("pages");
                // Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();

                listaNoticias.clear();
                for (int i = 0; i < jsonNoticias.length(); i++) {

                    JSONObject item = jsonNoticias.getJSONObject(i);
                    //  Toast.makeText(aq.getContext(), item.getString("title")+" ||| "+item.getString("thumbnail"), Toast.LENGTH_LONG).show();
                    JSONObject imgitemtipo=item.getJSONObject("thumbnail_images");
                    JSONObject imgitem=imgitemtipo.getJSONObject("large");

                    JSONArray categoriaa=   item.getJSONArray("categories");

                    listaNoticias.add(new Noticia(
                            item.getInt("id"),
                            item.getString("url"),
                            item.getString("status"),
                            item.getString("date"),
                            item.getString("content"),
                            item.getString("title"),
                            categoriaa.getJSONObject(0).getString("title"),
                            imgitem.getString("url")
                    ));



               //     listaNoticias.add(new Noticia(1, item.getString("title"), imgitem.getString("url")));
                } //listaNoticias.add(new Noticia(0,"", "http://pitalla.mx/img.jpg"));
                // Inicializamos el adapter.
                validarBtnPaginador();
                adapter = new  NoticiaAdapter(this, listaNoticias);

                // Asignamos el Adapter al ListView
                lvNoticias= (ListView) findViewById(R.id.lvNoticias);
                lvNoticias.setAdapter(adapter);

                lvNoticias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Noticia datosNoticia = (Noticia) parent.getAdapter().getItem(position);
                        Intent i = new Intent(context, NoticiaActivity.class);
                        i.putExtra("datosNoticia", datosNoticia);
                        startActivity(i);
                        // Toast.makeText(aq.getContext(), "pos: "+pos+"  title: "+datosNoticia.getTitle(), Toast.LENGTH_LONG).show();
                    }
                });

                validarBtnPaginador();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            //ajax error, show error code
            Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
        }
    }

    public void itemClicked(AdapterView parent, View v, int pos, long id) {
        Toast.makeText(aq.getContext(), "pos: "+pos+"  long: "+id, Toast.LENGTH_LONG).show();
    }

}
