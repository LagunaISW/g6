package mx.gob.guaymas.guaymas60;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.gob.guaymas.guaymas60.classes.ReporteCiudadano;
import mx.gob.guaymas.guaymas60.ws.CallWebService;

public class ConsultReportActivity extends AppCompatActivity {

    private Toolbar toolbar;

    public EditText etFolioReport;

    public View progress_consult;

    public View no_result;
    public TextView tvErrorNotFound;

    public View result;
    public TextView tvFolio;
    public TextView tvFecha;
    public TextView tvColonia;
    public TextView tvTipo;
    public TextView tvEstatus;
    public TextView tvAsunto;
    public TextView tvSeguimiento;

    public static final int HIDE_ALL = 0;
    public static final int SHOW_PROGRESS = 1;
    public static final int SHOW_ERROR = 2;
    public static final int SHOW_INFO = 3;

    ConsultReportTask mConsultReportTask = null;

    CallWebService ws = new CallWebService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult_report);


        setUpToolbar();
        initComponents();
    }

    public void initComponents(){
        etFolioReport = (EditText) findViewById(R.id.etFolioReport);
        progress_consult = findViewById(R.id.progress_consult);
        no_result = findViewById(R.id.no_result);
        tvErrorNotFound = (TextView) findViewById(R.id.tvErrorNotFound);
        result = findViewById(R.id.result);
        tvFolio = (TextView) findViewById(R.id.tvFolio);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        tvColonia = (TextView) findViewById(R.id.tvColonia);
        tvTipo = (TextView) findViewById(R.id.tvTipo);
        tvEstatus = (TextView) findViewById(R.id.tvEstatus);
        tvAsunto = (TextView) findViewById(R.id.tvAsunto);
        tvSeguimiento = (TextView) findViewById(R.id.tvSeguimiento);
        showHideProgress(HIDE_ALL);

        etFolioReport.requestFocus();
        hideSoftKeyboard();
    }

    public void showHideProgress(int type){
        switch (type){
            case HIDE_ALL: {
                progress_consult.setVisibility(View.GONE);
                no_result.setVisibility(View.GONE);
                result.setVisibility(View.GONE);
                break;
            }
            case SHOW_PROGRESS: {
                progress_consult.setVisibility(View.VISIBLE);
                no_result.setVisibility(View.GONE);
                result.setVisibility(View.GONE);
                break;
            }
            case SHOW_ERROR: {
                progress_consult.setVisibility(View.GONE);
                no_result.setVisibility(View.VISIBLE);
                result.setVisibility(View.GONE);
                break;
            }
            case SHOW_INFO: {
                progress_consult.setVisibility(View.GONE);
                no_result.setVisibility(View.GONE);
                result.setVisibility(View.VISIBLE);
                break;
            }

        }
    }

    public void checkReport(View view){
        if (mConsultReportTask != null) {
            return;
        }

        hideSoftKeyboard();

        etFolioReport.setError(null);
        String folio = etFolioReport.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(folio)){
            etFolioReport.setError(getString(R.string.error_no_folio));
            focusView = etFolioReport;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {

            int folioInt = Integer.parseInt(folio);
            mConsultReportTask = new ConsultReportTask(folioInt);
            mConsultReportTask.execute((Void) null);

        }

    }

    public void setDataFromReport(ReporteCiudadano report){
        tvFolio.setText(String.valueOf(report.getFolio()));
        try {
            String dateTemp = report.getFechain();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dateTemp);

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String reportDate = df.format(date);

            tvFecha.setText(reportDate);
        } catch (ParseException e) {
            tvFecha.setText(report.getFechain());
        }
        tvColonia.setText(report.getColonia());
        tvTipo.setText(report.getTipo());
        tvEstatus.setText(report.getStatus());
        tvAsunto.setText(report.getAsunto());
        tvSeguimiento.setText(report.getAvance());

        showHideProgress(SHOW_INFO);
    }

    public void setShowError(String message){
        tvErrorNotFound.setText(message);
        showHideProgress(SHOW_ERROR);
    }

    public class ConsultReportTask extends AsyncTask<Void, Void, Boolean> {

        private ReporteCiudadano response;
        //private ProgressDialog dialog;
        //private Context context;
        private int folio;
        private String messageError;

        public ConsultReportTask(int folio){
            //context = activity;
            //dialog = new ProgressDialog(context);
            this.folio = folio;
        }

        @Override
        protected void onPreExecute() {
            //this.dialog.setMessage(getString(R.string.searching_report));
            //this.dialog.show();
            showHideProgress(SHOW_PROGRESS);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            response = ws.consultReport(folio);
            boolean success = false;
            if(response != null){
                if(response.getError() == 0){
                    success = true;
                }else{
                    messageError = response.getMensaje();
                    success = false;
                }
            }else{
                messageError = getString(R.string.error_searching_report);
                success = false;
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mConsultReportTask = null;
            //if (dialog.isShowing()) {
            //    dialog.dismiss();
            //}
            if(aBoolean){
                //show successful message
                setDataFromReport(response);
            }else{
                //show error message
                //showMessage(getString(R.string.title_error_prompt), messageError, getString(R.string.btn_accept));
                setShowError(messageError);
            }

        }

        @Override
        protected void onCancelled() {
            mConsultReportTask = null;
            showHideProgress(HIDE_ALL);
            //if (dialog.isShowing()) {
            //    dialog.dismiss();
            //}
        }

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

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }



}
