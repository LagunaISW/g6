package mx.gob.guaymas.guaymas60;

import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;

import mx.gob.guaymas.guaymas60.utils.Constant;

/**
 * Created by julionava on 2/20/16.
 */
public class RadioActivity extends AppCompatActivity {

    WebView browser;
    private Toolbar toolbar;
    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setUpToolbar();
        preventSleep();

        browser = (WebView) findViewById(R.id.webRadio);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.loadUrl(Constant.RADIO_URL);
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_toolbar_radio));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id ==  android.R.id.home){

            browser.loadUrl("");
            browser.stopLoading();

            wakeLock.release();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void preventSleep(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==event.KEYCODE_BACK)
        {
            browser.loadUrl("");
            browser.stopLoading();

            wakeLock.release();
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        browser.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        browser.onPause();
    }



}
