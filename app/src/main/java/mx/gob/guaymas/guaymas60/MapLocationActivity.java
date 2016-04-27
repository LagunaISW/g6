package mx.gob.guaymas.guaymas60;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapLocationActivity extends AppCompatActivity {

    private Toolbar toolbar;

    Double latitud = 27.920659;
    Double longitud = -110.895612;
    float Zoom = 15;

    Marker marker;

    private LatLng MX = new LatLng(latitud, longitud);

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);

        setUpToolbar();

        if(getIntent().getExtras().getDouble("latitud") == 0.0){
            latitud = 27.920659;
        }else{
            latitud = getIntent().getExtras().getDouble("latitud");
        }

        if(getIntent().getExtras().getDouble("longitud") == 0.0 ){
            longitud = -110.895612;
        }else{
            longitud = getIntent().getExtras().getDouble("longitud");
        }


        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);
        googleMap = mySupportMapFragment.getMap();

        MX = new LatLng(latitud, longitud);


        if (googleMap != null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setCompassEnabled(false);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            try {

                MapsInitializer.initialize(this);

                marker = googleMap.addMarker(new MarkerOptions()
                        .position(MX)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(MX, Zoom);
                googleMap.animateCamera(cameraUpdate);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng position) {

                marker.remove();
                marker = googleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));
                latitud = position.latitude;
                longitud = position.longitude;

            }
        });


        setResult(RESULT_CANCELED);
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_toolbar_location_report));
    }

    public void regresar(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void setPosicion(View view){
        Intent data = new Intent();
        data.putExtra("latitud", latitud);
        data.putExtra("longitud", longitud);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id ==  android.R.id.home){
            setResult(RESULT_CANCELED);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
