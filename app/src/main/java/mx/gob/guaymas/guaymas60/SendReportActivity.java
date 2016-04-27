package mx.gob.guaymas.guaymas60;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by julionava on 2/18/16.
 */
public class SendReportActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public TextView tvInterestName;
    public TextView tvFolioResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);

        setUpToolbar();
        initComponents();
    }

    public void initComponents(){
        tvInterestName = (TextView) findViewById(R.id.tvInterestName);
        tvFolioResult = (TextView) findViewById(R.id.tvFolioResult);

        String name = getIntent().getExtras().getString("personName");
        int folio = getIntent().getExtras().getInt("folio");

        tvInterestName.setText("Hola "+name);
        tvFolioResult.setText(String.valueOf(folio));
    }

    public void returnToReport(View view){
        finish();
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_toolbar_report));
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
