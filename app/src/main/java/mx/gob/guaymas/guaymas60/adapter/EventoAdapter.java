package mx.gob.guaymas.guaymas60.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mx.gob.guaymas.guaymas60.R;
import mx.gob.guaymas.guaymas60.classes.Evento;

/**
 * Created by julionava on 2/21/16.
 */
public class EventoAdapter  extends ArrayAdapter {

    private Context context;
    private ArrayList<Evento>  datos;

    public EventoAdapter(Context context, ArrayList<Evento> datos) {
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
        View item = inflater.inflate(R.layout.item_evento2, null);

        TextView titulo = (TextView) item.findViewById(R.id.tvEventoNombre);
        titulo.setText(datos.get(position).getNombre_evento());

        TextView info = (TextView) item.findViewById(R.id.tvEventoInfo);
        info.setText(datos.get(position).getFecha()+"  "+datos.get(position).getOrganiza());

        return item;
    }

}
