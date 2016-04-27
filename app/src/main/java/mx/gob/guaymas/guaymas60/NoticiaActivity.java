package mx.gob.guaymas.guaymas60;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.squareup.picasso.Picasso;

import mx.gob.guaymas.guaymas60.classes.Noticia;

public class NoticiaActivity extends AppCompatActivity {

    private Toolbar toolbar;

    AQuery aq ;
    Noticia noticia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_noticia);

        aq = new AQuery(this);
        noticia= getIntent().getParcelableExtra("datosNoticia");
        setUpToolbar();

        aq.id(R.id.txvTituloNoticiaDetalle).text(noticia.getTitle());
        aq.id(R.id.txvFechaNoticiaDetalle).text(noticia.getDate());
        aq.id(R.id.txvContenidoNoticiaDetalle).text(Html.fromHtml(noticia.getContent()));

        Picasso.with(this).load(noticia.getUrlimg()).error(R.drawable.noticiadefault).into(aq.id(R.id.imgDetalleNoticia).getImageView());
    }

    private void setUpToolbar(){
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(noticia.getTitle());
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
