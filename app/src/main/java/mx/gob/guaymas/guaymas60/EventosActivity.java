package mx.gob.guaymas.guaymas60;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import mx.gob.guaymas.guaymas60.adapter.EventoAdapter;
import mx.gob.guaymas.guaymas60.classes.Evento;
import mx.gob.guaymas.guaymas60.utils.Constant;
import mx.gob.guaymas.guaymas60.utils.SharedPreferenceHelper;
import mx.gob.guaymas.guaymas60.ws.WebServiceName;

/**
 * Created by julionava on 2/21/16.
 */
public class EventosActivity extends AppCompatActivity {

    private Toolbar toolbar;

    AQuery aq ;
    Evento evento = new Evento();
    ArrayList listaEventos;
    EventoAdapter adapter;
    ListView lvEventos;

    int totalEventosPagina;
    int totalEventos;
    int totalPaginas;
    JSONArray jsonEventos;
    Context context;
    SharedPreferenceHelper sharPrefHelper;
    int pagina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        setUpToolbar();

        context=this;
        aq = new AQuery(this);
        sharPrefHelper = new SharedPreferenceHelper(Constant.SHARED_PREF_NAME, this);
        pagina= Integer.parseInt(  sharPrefHelper.getStringFromShprf(Constant.PAGINA_EVENTO) );

        listaEventos = new ArrayList<Evento>();
        totalPaginas = -1;
        asyncJson(pagina);

        aq.id(R.id.btnAtrasEventos).clicked(this, "atrasEventos");
        aq.id(R.id.btnSiguienteEventos).clicked(this, "SiguienteEventos");

    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_toolbar_event));
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

    public void atrasEventos(View button){
        pagina = pagina - 1;
        sharPrefHelper.writeString(Constant.PAGINA_EVENTO, String.valueOf(pagina));
        asyncJson(pagina);
    }


    public void SiguienteEventos(View button){
        pagina = pagina + 1;
        sharPrefHelper.writeString(Constant.PAGINA_EVENTO, String.valueOf(pagina));
        asyncJson(pagina);
    }

    public void  validarBtnPaginador(){
        getSupportActionBar().setTitle("Eventos - páginas ( " + pagina + " de " + totalPaginas + " )");

        if(pagina>1){
            aq.id(R.id.btnAtrasEventos).getButton().setVisibility(View.VISIBLE);
        }else{
            aq.id(R.id.btnAtrasEventos).getButton().setVisibility(View.GONE);
        }
        if(pagina==totalPaginas){
            aq.id(R.id.btnSiguienteEventos).getButton().setVisibility(View.GONE);
        }else{
            aq.id(R.id.btnSiguienteEventos).getButton().setVisibility(View.VISIBLE);
        }
    }

    public void asyncJson(int pagina){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true); dialog.setCancelable(true); dialog.setInverseBackgroundForced(false); dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Cargando eventos...");
        String url = WebServiceName.getUrlEvents(pagina);
        //Log.i("Url event", url);
        aq.progress(dialog).ajax(url, JSONObject.class, this, "jsonCallback");
    }

    public void jsonCallback(String url, JSONObject json, AjaxStatus status){

        if(json != null){

            //Log.i("Json event", json.toString());

            try {
                jsonEventos =  json.getJSONArray("events");
                //Log.i("Json array ", jsonEventos.toString());
                totalEventos = json.getInt("count_total");
                totalEventosPagina = json.getInt("count");
                totalPaginas= json.getInt("pages");
                // Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();

                listaEventos.clear();
                for (int i = 0; i < jsonEventos.length(); i++) {

                    JSONObject item = jsonEventos.getJSONObject(i);
                    //  Toast.makeText(aq.getContext(), item.getString("title")+" ||| "+item.getString("thumbnail"), Toast.LENGTH_LONG).show();

                    listaEventos.add(new Evento(
                            item.getInt("id"),
                            item.getString("nombre_evento"),
                            item.getString("desc"),
                            item.getString("fecha"),
                            item.getString("lugar"),
                            item.getString("hora"),
                            item.getString("organiza"),
                            item.getString("contacto"),
                            item.getString("imagen")
                    ));

                }

                validarBtnPaginador();
                adapter = new EventoAdapter(this, listaEventos);

                // Asignamos el Adapter al ListView
                lvEventos= (ListView) findViewById(R.id.lvEventos);
                lvEventos.setAdapter(adapter);

                lvEventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Evento datosEvento = (Evento) parent.getAdapter().getItem(position);
                        Intent i = new Intent(context, EventoActivity.class);
                        i.putExtra("datosEvento", datosEvento);
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
        Toast.makeText(aq.getContext(), "pos: " + pos + "  long: " + id, Toast.LENGTH_LONG).show();
    }




}
