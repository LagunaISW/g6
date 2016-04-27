package mx.gob.guaymas.guaymas60;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import mx.gob.guaymas.guaymas60.utils.Constant;
import mx.gob.guaymas.guaymas60.utils.SharedPreferenceHelper;

public class MenuActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView imgNews;
    private ImageView imgEvents;
    private ImageView imgRadio;
    private ImageView imgReport;
    private ImageView imgDirectory;
    private TextView txvLinkToPage;
    private ImageButton imgBtFacebook;
    private ImageButton imgBtYoutube;

    AQuery aq;
    SharedPreferenceHelper sharPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sharPrefHelper = new SharedPreferenceHelper(Constant.SHARED_PREF_NAME, this);

        setUpToolbar();
        setUpUIElements();
        preferencias();
    }

    public void  preferencias(){
        sharPrefHelper.writeString(Constant.PAGINA, "1");
        sharPrefHelper.writeString(Constant.PAGINA_EVENTO, "1");
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
    }

    private void setUpUIElements(){
        imgNews = (ImageView) findViewById(R.id.imgNews);
        imgEvents = (ImageView) findViewById(R.id.imgEvents);
        imgRadio = (ImageView) findViewById(R.id.imgRadio);
        imgReport = (ImageView) findViewById(R.id.imgReport);
        imgDirectory = (ImageView) findViewById(R.id.imgDirectory);

        txvLinkToPage = (TextView) findViewById(R.id.txvLinkToPage);
        imgBtFacebook = (ImageButton) findViewById(R.id.imgBtFacebook);
        imgBtYoutube = (ImageButton) findViewById(R.id.imgBtYoutube);

        imgNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to news
                Intent intentNews = new Intent(MenuActivity.this, NoticiasActivity.class);
                startActivity(intentNews);
            }
        });

        imgEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to events
                Intent intentEvent = new Intent(MenuActivity.this, EventosActivity.class);
                startActivity(intentEvent);
            }
        });

        imgRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to radio
                Intent intentRadio = new Intent(MenuActivity.this, RadioActivity.class);
                startActivity(intentRadio);
            }
        });

        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to reports
                Intent intentReport = new Intent(MenuActivity.this, ReportActivity.class);
                startActivity(intentReport);
            }
        });

        imgDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to directory
            }
        });

        imgBtFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to fan page from the government
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_FACEBOOK_FANPAGE));
                startActivity(intent);
            }
        });

        imgBtYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to channel from the government
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_YOUTUBE_CHANNEL));
                startActivity(intent);
            }
        });

        txvLinkToPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to web page from the government
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_GOVERNMENT_PAGE));
                startActivity(intent);
            }
        });

    }



}
