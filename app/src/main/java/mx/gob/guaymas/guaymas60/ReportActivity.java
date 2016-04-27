package mx.gob.guaymas.guaymas60;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mx.gob.guaymas.guaymas60.classes.ReporteCiudadano;
import mx.gob.guaymas.guaymas60.utils.photo.*;
import mx.gob.guaymas.guaymas60.ws.CallWebService;

public class ReportActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public EditText etInterested;
    public EditText etAddress;
    public Spinner spSuburb;
    public EditText etTelephone;
    public EditText etEmail;
    public EditText etSubject;

    public ImageButton imgBump;
    public ImageButton imgLighting;
    public ImageButton imgOther;

    public View containerPreview;
    public ImageView imgPreview;

    public View progressLoader;
    public View formReport;

    Button btnSearchRequest;

    public String photoPathToSend;
    public String type;


    List<String> suburbs;

    LoadSuburdTask mLoadSuburdTask = null;
    SendReportTask mSendReportTask = null;

    CallWebService ws = new CallWebService();

    Resources res;

    /* Foto */
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";


    // Google Map
    private GoogleMap googleMap;
    public TextView tvIndicateMap;
    public View containerMap;
    public double latitudDefault = 27.920659;
    public double longitudDefault = -110.895612;
    Double latitud = 27.920659;
    Double longitud = -110.895612;
    float Zoom = 15;
    public static final int MAP_ACTIVITY = 2;
    Marker marker;
    private LatLng MX = new LatLng(latitud, longitud);
    boolean preparedMap;

    //Result activity sent request
    public static final int SENT_REQUEST = 3;

    //PHOTO GALLERY SELECT
    private static final int SELECT_PHOTO = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setUpToolbar();
        startMap();
        initComponents();
        hideSoftKeyboard();
        loadSuburds();
    }


    public void initComponents(){
        etInterested = (EditText) findViewById(R.id.etInterested);
        etAddress = (EditText) findViewById(R.id.etAddress);
        spSuburb = (Spinner) findViewById(R.id.spSuburb);
        etTelephone = (EditText) findViewById(R.id.etTelephone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etSubject = (EditText) findViewById(R.id.etSubject);

        imgBump = (ImageButton) findViewById(R.id.imgBump);
        imgLighting = (ImageButton) findViewById(R.id.imgLighting);
        imgOther = (ImageButton) findViewById(R.id.imgOther);

        containerPreview = findViewById(R.id.containerPreview);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);

        progressLoader = findViewById(R.id.report_progress);
        formReport = findViewById(R.id.scrollViewFormReport);

        tvIndicateMap = (TextView) findViewById(R.id.tvIndicateMap);
        containerMap = findViewById(R.id.containerMap);

        btnSearchRequest = (Button) findViewById(R.id.btnCheckReport);

        photoPathToSend = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        Button picBtn = (Button) findViewById(R.id.btnAddPhoto);
        setBtnListenerOrDisable(
                picBtn,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        res = getResources();
        resetData();
    }

    private void resetData(){
        etInterested.setText("");
        etAddress.setText("");
        spSuburb.setSelection(0);
        etTelephone.setText("");
        etEmail.setText("");
        etSubject.setText("");

        //set the image from the type
        selectOther(null);

        containerPreview.setVisibility(View.GONE);
        photoPathToSend = "";

        if(preparedMap) {
            //restart map
            latitud = latitudDefault;
            longitud = longitudDefault;
            if (marker != null) {
                marker.remove();
            }
            MX = new LatLng(latitud, longitud);
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(MX)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(MX, Zoom);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    public void loadSuburds(){
        showProgress(true);
        mLoadSuburdTask = new LoadSuburdTask();
        mLoadSuburdTask.execute((Void) null);
    }

    public void startMap(){
        preparedMap = false;
        int codigoGooglePlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (codigoGooglePlay != ConnectionResult.SUCCESS) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(codigoGooglePlay, this, 6);
            if (dialog != null) {
                dialog.show();
            } else {
                tvIndicateMap.setVisibility(View.GONE);
                containerMap.setVisibility(View.GONE);
            }
        }else{
            preparedMap = true;
        }

        if(preparedMap) {
            FragmentManager myFragmentManager = getSupportFragmentManager();
            SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);
            googleMap = mySupportMapFragment.getMap();

            if (googleMap != null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                //googleMap.getUiSettings().setAllGesturesEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);

                // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
                try {

                    MapsInitializer.initialize(this);

                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(MX)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent intent = new Intent(ReportActivity.this, MapLocationActivity.class);
                            intent.putExtra("latitud", latitud);
                            intent.putExtra("longitud", longitud);

                            startActivityForResult(intent, MAP_ACTIVITY);
                            return false;
                        }
                    });

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(MX, Zoom);
                    googleMap.animateCamera(cameraUpdate);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng arg0) {
                        Intent intent = new Intent(ReportActivity.this, MapLocationActivity.class);
                        intent.putExtra("latitud", latitud);
                        intent.putExtra("longitud", longitud);

                        startActivityForResult(intent, MAP_ACTIVITY);
                    }
                });

            }else{
                tvIndicateMap.setVisibility(View.GONE);
                containerMap.setVisibility(View.GONE);
            }

        }
    }


    public void goToConsultReport(View view){
        //go to the other activity
        Intent intentSearch = new Intent(ReportActivity.this, ConsultReportActivity.class);
        startActivity(intentSearch);
    }

    //try intent photo things
    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*if(photoPathToSend.isEmpty()){
                        dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                    }else{
                        showPromptOptions();
                    }*/

                    showPromptOptions();

                }
            };

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action The Intent action to check for availability.
     *
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

    public boolean showDelete;

    private void showPromptOptions(){
        //prompt to ask what to do
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        showDelete = (photoPathToSend.isEmpty()) ? false : true;
        String[] options;
        if(showDelete) {
            options = new String[]{res.getString(R.string.photo_from_gallery), res.getString(R.string.photo_camera), res.getString(R.string.photo_delete), res.getString(R.string.photo_cancel)};
        }else{
            options = new String[]{res.getString(R.string.photo_from_gallery), res.getString(R.string.photo_camera), res.getString(R.string.photo_cancel)};
        }

        builder.setTitle(res.getString(R.string.photo_evidence))
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(showDelete) {

                            if (which == 0) {
                                selectPictureFromDevice();
                            } else if (which == 1) {
                                dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);//Camera
                            } else if (which == 2) {
                                deletePhoto();
                            } else if (which == 3){
                                //Nothing
                                //containerPreview.setVisibility(View.VISIBLE);
                            }
                        }else{

                            if (which == 0) {
                                selectPictureFromDevice();
                            } else if (which == 1) {
                                dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);//Camera
                            } else if (which == 2) {
                                //Nothing
                                //containerPreview.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                });
        builder.create();
        builder.show();
    }

    public void selectPictureFromDevice(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    public void deletePhoto(){
        photoPathToSend = "";
        containerPreview.setVisibility(View.GONE);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }


    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = imgPreview.getWidth();
        int targetH = imgPreview.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        imgPreview.setImageBitmap(bitmap);
        containerPreview.setVisibility(View.VISIBLE);
        photoPathToSend = mCurrentPhotoPath;
        containerPreview.requestFocus();
        View focusView = imgPreview;
        focusView.requestFocus();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B
            case MAP_ACTIVITY: {
                if (resultCode == RESULT_OK) {
                    marker.remove();
                    latitud = data.getExtras().getDouble("latitud");
                    longitud = data.getExtras().getDouble("longitud");
                    MX = new LatLng(latitud, longitud);
                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(MX)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(MX, Zoom);
                    googleMap.animateCamera(cameraUpdate);
                }
                break;
            }
            case SENT_REQUEST: {
                etInterested.requestFocus();
                hideSoftKeyboard();
                break;
            }
            case SELECT_PHOTO: {
                if(resultCode == RESULT_OK){
                    try {
                        Uri selectedImage = data.getData();
                        //Toast.makeText(this, selectedImage.getPath(), Toast.LENGTH_LONG).show();
                        InputStream imageStream = null;
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);


                        imgPreview.setImageBitmap(yourSelectedImage);
                        containerPreview.setVisibility(View.VISIBLE);

                        photoPathToSend = getRealPathFromURI(selectedImage);
                        containerPreview.requestFocus();
                        View focusView = imgPreview;
                        focusView.requestFocus();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, getString(R.string.errror_getting_the_photo), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }

        } // switch
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    public void sendReportButton(View view){
        //validate fields
        sendReport();
    }

    public void sendReport(){
        if (mSendReportTask != null) {
            return;
        }

        etInterested.setError(null);
        etAddress.setError(null);
        etTelephone.setError(null);
        etEmail.setError(null);
        etSubject.setError(null);

        String interested = etInterested.getText().toString();
        String address = etAddress.getText().toString();
        int suburdPosition = spSuburb.getSelectedItemPosition();
        String suburd = spSuburb.getSelectedItem().toString();
        String telephone = etTelephone.getText().toString();
        String email = etEmail.getText().toString();
        String subject = etSubject.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(interested)){
            etInterested.setError(getString(R.string.error_field_required));
            focusView = etInterested;
            cancel = true;
        }else if(TextUtils.isEmpty(address)){
            etAddress.setError(getString(R.string.error_field_required));
            focusView = etAddress;
            cancel = true;
        }else if(suburdPosition == 0){
            focusView = spSuburb;
            cancel = true;
            Toast.makeText(this, getString(R.string.error_select_suburb), Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(telephone)){
            etTelephone.setError(getString(R.string.error_field_required));
            focusView = etTelephone;
            cancel = true;
        }else if(!isEmailValid(email)){
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }else  if(TextUtils.isEmpty(subject)){
            etSubject.setError(getString(R.string.error_field_required));
            focusView = etSubject;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            ReporteCiudadano report = new ReporteCiudadano();
            report.setInteresado(interested.toUpperCase());
            report.setDireccion(address.toUpperCase());
            report.setColonia(suburd.toUpperCase());
            report.setTelefono(telephone.toUpperCase());
            report.setCorreo(email.toUpperCase());
            report.setAsunto(subject.toUpperCase());
            report.setFolio(0);
            String latSend = "";
            if( latitud != latitudDefault )
                latSend = latitud.toString();
            String lonSend = "";
            if ( longitud != longitudDefault )
                lonSend = longitud.toString();

            report.setLatitud(latSend);
            report.setLongitud(lonSend);
            report.setFoto(photoPathToSend);
            report.setTipo(type.toUpperCase());

            mSendReportTask = new SendReportTask(report, ReportActivity.this);
            mSendReportTask.execute((Void) null);

        }


    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formReport.setVisibility(show ? View.GONE : View.VISIBLE);
            formReport.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formReport.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressLoader.setVisibility(show ? View.VISIBLE : View.GONE);
            progressLoader.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressLoader.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressLoader.setVisibility(show ? View.VISIBLE : View.GONE);
            formReport.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    public void selectBump(View view){
        imgBump.setImageDrawable(getResources().getDrawable(R.drawable.bache));
        imgLighting.setImageDrawable(getResources().getDrawable(R.drawable.grayscale_alumbrado));
        imgOther.setImageDrawable(getResources().getDrawable(R.drawable.grayscale_otro));
        type = "BACHE";
    }
    public void selectLighting(View view){
        imgBump.setImageDrawable(getResources().getDrawable(R.drawable.grayscale_bache));
        imgLighting.setImageDrawable(getResources().getDrawable(R.drawable.alumbrado));
        imgOther.setImageDrawable(getResources().getDrawable(R.drawable.grayscale_otro));
        type = "ALUMBRADO";
    }
    public void selectOther(View view){
        imgBump.setImageDrawable(getResources().getDrawable(R.drawable.grayscale_bache));
        imgLighting.setImageDrawable(getResources().getDrawable(R.drawable.grayscale_alumbrado));
        imgOther.setImageDrawable(getResources().getDrawable(R.drawable.otro));
        type = "OTRO";
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

    public void showMessage(String title, String message, String button){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        //builder.setIcon(R.drawable.tick);
        //builder.setInverseBackgroundForced(true);
        builder.setMessage(message);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class LoadSuburdTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            try{

                suburbs = ws.getSuburbs();

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if(success){
                if(suburbs.size() > 0){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, suburbs);
                    //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSuburb.setAdapter(adapter);
                }
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.message_no_internet), Toast.LENGTH_LONG).show();
                finish();
            }
            mLoadSuburdTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mLoadSuburdTask = null;
            showProgress(false);
        }

    }

    public class SendReportTask extends AsyncTask<Void, Void, Boolean>{

        private ReporteCiudadano reporte;
        private ProgressDialog dialog;
        private Context context;
        private String messageError;

        private ReporteCiudadano response;

        public SendReportTask(ReporteCiudadano reporte, Activity activity){
            this.reporte = reporte;
            context = activity;
            dialog = new ProgressDialog(context);
            messageError = null;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(res.getString(R.string.sending_report));
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            response = ws.saveProfile(reporte);

            boolean success = false;
            if(response != null){
                if(response.getError() == 0){
                    success = true;
                }else{
                    messageError = response.getMensaje();
                    success = false;
                }
            }else{
                messageError = getString(R.string.error_sending_the_request);
                success = false;
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mSendReportTask = null;
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(aBoolean){
                //show successful message
                resetData();
                Intent intentReportResult = new Intent(ReportActivity.this, SendReportActivity.class);
                intentReportResult.putExtra("personName", response.getInteresado());
                intentReportResult.putExtra("folio", response.getFolio());
                startActivityForResult(intentReportResult, SENT_REQUEST);
            }else{
                //show error message
                showMessage(getString(R.string.title_error_prompt), messageError, getString(R.string.btn_accept));
            }

        }

        @Override
        protected void onCancelled() {
            mSendReportTask = null;
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }

}
