package mx.gob.guaymas.guaymas60.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mx.gob.guaymas.guaymas60.R;
import mx.gob.guaymas.guaymas60.classes.Noticia;

/**
 * Created by Arturo García Saiza on 12-feb-16.
 */
public class NoticiaAdapter  extends ArrayAdapter{

    private Context context;
    private ArrayList<Noticia>  datos;

    public NoticiaAdapter(Context context, ArrayList<Noticia> datos) {
        super(context, R.layout.item_noticia, datos);
        this.context = context;
        this.datos = datos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // En primer lugar "inflamos" una nueva vista, que será la que se
        // mostrará en la celda del ListView. Para ello primero creamos el
        // inflater, y después inflamos la vista.
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.item_noticia, null);

        // A partir de la vista, recogeremos los controles que contiene para
        // poder manipularlos.
        // Recogemos el ImageView y le asignamos una foto.
         ImageView imagen = (ImageView) item.findViewById(R.id.imgNoticiaItem);
      //  imagen.setImageResource(datos.get(position).getImg());
      //  aq.id(R.id.imgNoticiaItem).image(datos.get(position).getUrlimg());
      //  Picasso.with(context).setIndicatorsEnabled(true);
        Picasso.with(context).load(datos.get(position).getUrlimg())
                             //.placeholder(R.drawable.noticiadefault)
                             .error(R.drawable.noticiadefault)
                             .into(imagen);

        // Recogemos el TextView para mostrar el nombre y establecemos el
        // nombre.
        TextView nombre = (TextView) item.findViewById(R.id.txvTituloNoticiaItem);
        nombre.setText(datos.get(position).getTitle());

        return item;
    }



}
