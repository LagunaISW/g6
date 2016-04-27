package mx.gob.guaymas.guaymas60;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;


public class MainActivity extends AppCompatActivity {



    private static int TIME_SPLASH = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTimerNextScreen();
    }

    public void startTimerNextScreen(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentMenu = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intentMenu);

                finish();
            }
        }, TIME_SPLASH);
    }



    // Initiating Menu XML file (menu.xml)
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }*/

}
