package mx.gob.guaymas.guaymas60;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import mx.gob.guaymas.guaymas60.classes.Evento;

/**
 * Created by julionava on 2/21/16.
 */
public class EventoActivity extends AppCompatActivity {

    private Toolbar toolbar;

    AQuery aq ;
    Evento evento;

    //ImageView imgFotoEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_evento);

        aq = new AQuery(this);
        evento= getIntent().getParcelableExtra("datosEvento");
        setUpToolbar();

        aq.id(R.id.tvNombre).text(evento.getNombre_evento());
        aq.id(R.id.tvLugar).text(evento.getLugar());
        aq.id(R.id.tvFecha).text(evento.getFecha());
        aq.id(R.id.tvHora).text(evento.getHora());
        aq.id(R.id.tvTipoEvento).text(Html.fromHtml(evento.getDesc()));
        aq.id(R.id.tvLugar).text(evento.getLugar());
        aq.id(R.id.tvContacto).text(Html.fromHtml(evento.getContacto()));

        /*imgFotoEvent = (ImageView) findViewById(R.id.imgFotoEvento);
        Target target = new Target() {
            @Override
            public void onPrepareLoad(Drawable drawable) {}

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                if(bitmap != null) {
                    imgFotoEvent.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {}
        };

        imgFotoEvent.setTag(target);
        Picasso.with(this).load("http://guaymas.gob.mx/wp-content/themes/aquiesguaymas/img/calendario/carnaval_deportes.jpg").into((Target) imgFotoEvent.getTag());*/

        //Toast.makeText(this, evento.getImagen(), Toast.LENGTH_LONG).show();
        Picasso.with(this).load("http://guaymas.gob.mx/wp-content/themes/aquiesguaymas/img/calendario/carnaval_deportes.jpg").error(R.drawable.eventodefault).into(aq.id(R.id.imgFotoEvento).getImageView());
        //Glide.with(this).load(evento.getImagen()).into(aq.id(R.id.imgFotoEvento).getImageView());
        /*Glide
                .with(this)
                .load(evento.getImagen())
                .centerCrop()
                .placeholder(R.drawable.eventodefault)
                .crossFade()
                .into(aq.id(R.id.imgFotoEvento).getImageView());*/
    }

    private void setUpToolbar(){
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(evento.getNombre_evento());
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

}
