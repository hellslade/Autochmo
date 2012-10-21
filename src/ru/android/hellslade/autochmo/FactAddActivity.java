package ru.android.hellslade.autochmo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Video.Media;
import android.provider.MediaStore.Video.VideoColumns;
import android.text.InputFilter;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class FactAddActivity extends SherlockFragmentActivity implements OnClickListener, OnItemClickListener {
    private class GetCarmodelsTask extends AsyncTask<Void, Void, List<Carmodel>> {
    	@Override
    	protected List<Carmodel> doInBackground(Void... params) {
    		return mAutochmo._getCarModels(); 
    	}
    	@Override
    	protected void onPostExecute(List<Carmodel> result) {
    		carmodels = result;
    		ArrayAdapter<Carmodel> adapter = new ArrayAdapter<Carmodel>(FactAddActivity.this, R.layout.list_item, result);
    		carmark.setAdapter(adapter);
    		super.onPostExecute(result);
    	}
    }
    private class GetLastLocationTask extends AsyncTask<Location, Void, String[]> {
    	@Override
    	protected void onPreExecute() {
    		mLocationView.setText("Определяем местоположение...");
    		super.onPreExecute();
    	}
    	@Override
    	protected String[] doInBackground(Location... params) {
    	    if (params.length > 0 && params[0] != null) {
    	        return mAutochmo.GetLastLocation(params[0]);
    	    }
    		return mAutochmo.GetLastLocation(null);
    	}
    	@Override
    	protected void onPostExecute(String[] result) {
    		 mLocationView.setText(result[1]);
    		 mLocationView.setTag(result[0]);
    		super.onPostExecute(result);
    	}
    }
    private class SendFactTask extends AsyncTask<Void, Void, String> {
    	ProgressDialog pg = new ProgressDialog(FactAddActivity.this);
    	String carmodel_id;
    	String carmodel;
    	String desc;
    	String gosnomer;
    	String nonomer;
    	String location;
    	Map<String, String> files;
    	@Override
    	protected void onPreExecute() {
    		pg.setMessage("Пожалуйста, подождите...");
    		pg.setTitle("Отправка данных на сервер");
    		pg.show();
    		super.onPreExecute();
    	}
    	@Override
    	protected String doInBackground(Void... params) {
    		return mAutochmo._addFact(carmodel_id, carmodel, desc, gosnomer, nonomer, location, files);
    	}
    	@Override
    	protected void onPostExecute(String result) {
    		pg.dismiss();
    		Log.v("result: " + result);
    		String result_ok = FactAddActivity.this.getResources().getString(R.string.add_fact_ok);
    		Toast.makeText(FactAddActivity.this, result, Toast.LENGTH_LONG).show();
    		if (result.equalsIgnoreCase(result_ok)) {
    			FactAddActivity.this.finish();
    		}
    		super.onPostExecute(result);
    	}
    	protected void execute(String carmodel_id, String carmodel, String desc, String gosnomer, String nonomer, Map<String, String> files) {
    		this.carmodel_id = carmodel_id;
    		this.carmodel = carmodel;
    		this.desc = desc;
    		this.gosnomer = gosnomer;
    		this.nonomer = nonomer;
    		this.location = (String)mLocationView.getTag();
    		this.files = files;
    		this.execute();
    	}
    }
    
	private static final int PHOTO_REQUEST_CODE = 0x000002;
    private static final int IMAGE_PICK_REQUEST_CODE = 0x000003;
    private AutochmoApplication mAutochmo;
    private File mPhotoFile;
    public ImageAdapter imageAdapter;
    public Gallery gallery;
    public List<Carmodel> carmodels;
    public String mCarmodelId = "0";
    private Map<String, String> mFiles = new HashMap<String, String>();
    private EditText nomerEdit;
    private TextView mLocationView;
    public AutoCompleteTextView carmark;
    private LocationManager mLocationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfact);
        carmark = (AutoCompleteTextView) findViewById(R.id.carmarkEditText);
        mAutochmo = (AutochmoApplication)getApplication();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        ImageButton addShotButton = (ImageButton) findViewById(R.id.buttonAddShot);
        addShotButton.setOnClickListener(this);
        ImageButton addImageButton = (ImageButton) findViewById(R.id.buttonAddImage);
        addImageButton.setOnClickListener(this);
        
        nomerEdit = (EditText) findViewById(R.id.nomerEditText);
        mLocationView = (TextView)findViewById(R.id.locationbar);
        mLocationView.setOnClickListener(this);
        nomerEdit.setFilters(new InputFilter[] {new AGosnomerCheck()});
        // TODO: Обрабатывать KeyDown/KeyUp
        /*String[] nomers = new String[]{"н100ка72", "г100пр72", "n100ka86", "h100ky86"};
        for (String nomer : nomers) {
            AGosnomerCheck gn = new AGosnomerCheck(nomer);
            Log.v(gn.getNomer());
        }*/
        
        gallery = (Gallery) findViewById(R.id.photoGallery);
        ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        if (savedInstanceState != null) {
        	images = savedInstanceState.getParcelableArrayList("bitmap");
        } else {
        	images = new ArrayList<Bitmap>();
            images.add(null);
        }
        imageAdapter = new  ImageAdapter(this, images);
        gallery.setAdapter(imageAdapter);

        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	Bitmap bmp = (Bitmap)imageAdapter.getItem(position);
            	if (bmp == null) {
            		// Вызвать диалог добавления файла
            		takePicture();
            	} else {
            		// Показать QuickAction [Удалить, Просмотреть]
            	}
//                ImageView imgView = (ImageView)findViewById(R.id.imageView);
//                Bitmap bitmap = (Bitmap)images.get(position);
//                imgView.setImageBitmap(bitmap);
//                imgView.setTag(mFiles.get(bitmap));
            }
        });
        //new GetCurrentLocationTask().execute();
        get_location();
        new GetCarmodelsTask().execute();
        carmark.setOnItemClickListener(this);
    }
    LocationListener locationListener = new LocationListener() {
        
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v("status " + status);
            if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                mLocationManager.removeUpdates(locationListener);
                new GetLastLocationTask().execute();
            }
        }
        
        @Override
        public void onProviderEnabled(String provider) {
        }
        
        @Override
        public void onProviderDisabled(String provider) {
        }
        
        @Override
        public void onLocationChanged(Location location) {
            new GetLastLocationTask().execute(location);
//            mLocationView.setText(String.format("%s,%s", location.getLongitude(), location.getLatitude()));
//            mLocationView.setTag(String.format("%s,%s", location.getLongitude(), location.getLatitude()));
        }
    };
    public void get_location() {
        // exceptions will be thrown if provider is not permitted.
        
        boolean gps_enabled = false;
        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        if (gps_enabled) {
            Log.v("gps enabled");
            GpsStatus currentGPSStatus = mLocationManager.getGpsStatus(null);
            Log.v("currentGPSStatus " + currentGPSStatus.getSatellites());
            mLocationView.setText("Определяем координаты по GPS...");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            Log.v("gps disabled");
            new GetLastLocationTask().execute();
        }
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.buttonAddShot):
                // Сделать снимок на камеру
                launchCamera();
                break;
            case (R.id.buttonAddImage):
                // Выбрать изображение из галереи
                takePicture();
                break;
            case (R.id.locationbar):
                Toast.makeText(this, "Открыть Яндекс.Карты на текущем местоположении.", Toast.LENGTH_LONG).show();
                break;
        }
    }
    public void launchCamera() {
        // name consists of Photo + time taken
        Time now = new Time();
        now.setToNow();
        final String fileName = "/photo_" + now.toString().substring(0, 15) + ".jpg";
        // create parameters for Intent with filename
        // imageUri is the current activity attribute, define and save it
        // for later usage (also in onSaveInstanceState)
        //mPhotoUri = getContentResolver().insert(
        //        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        mPhotoFile = new File(getExternalCacheDir() + fileName);
        //mPhotoUri = Uri.fromFile(mPhotoFile);
        // create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, PHOTO_REQUEST_CODE);
    }
    public void takePicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE);
    }
    public void updateImageAdapter(Bitmap bitmapPreview) {
        imageAdapter.insert(bitmapPreview, 1);
        imageAdapter.notifyDataSetChanged();
        gallery.invalidate();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap = null;
                    if (data != null) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        Log.v("Bitmap size " + bitmap.getWidth() + " " + bitmap.getHeight());
                        Log.v("Photo path " + mPhotoFile.getAbsolutePath());
                    } else
                        try {
                            bitmap = android.provider.MediaStore.Images.Media
                                    .getBitmap(getContentResolver(), Uri.fromFile(mPhotoFile));
                            Log.v("Getting photo from mPhotoUri");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    Bitmap bitmapPreview = scale(bitmap);
                    updateImageAdapter(bitmapPreview);
                    mFiles.put(mPhotoFile.getName(), mPhotoFile.getAbsolutePath());
                    bitmap.recycle();
                }
                break;
            case IMAGE_PICK_REQUEST_CODE:
                if (data != null) {
                    Log.v("idButSelPic Photopicker: " + data.getDataString());
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    cursor.moveToFirst();  //if not doing this, 01-22 19:17:04.564: ERROR/AndroidRuntime(26264): Caused by: android.database.CursorIndexOutOfBoundsException: Index -1 requested, with a size of 1
                    int idx = cursor.getColumnIndex(ImageColumns.DATA);
                    String fileSrc = cursor.getString(idx);
                    Log.v("Picture:" + fileSrc);
                    Bitmap bitmap = BitmapFactory.decodeFile(fileSrc); //load preview image
                    Bitmap bitmapPreview = scale(bitmap);
                    updateImageAdapter(bitmapPreview);
                    mFiles.put(new File(fileSrc).getName(), fileSrc);
                    bitmap.recycle();
                }
                else {
                    Log.v("idButSelPic Photopicker canceled");
                }
            default:
                break;
        }
    }
    public Bitmap scale(Bitmap source) {
        Bitmap scaled = null;
        int width = source.getWidth();
        int height = source.getHeight();
        Log.v("Original size " + width + " " + height);
        int newWidth = 200;
        int newHeight = 200;
        float scaleWidth, scaleHeight;
        // calculate the scale - in this case = 0.4f
        if (width >= height) {
            scaleWidth = ((float) newWidth) / width;
            scaleHeight = scaleWidth;
        } else {
            scaleHeight = ((float) newHeight) / height;
            scaleWidth = scaleHeight;
        }

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        scaled = Bitmap.createBitmap(source, 0, 0, 
                          width, height, matrix, true);
        Log.v("New size " + scaled.getWidth() + " " + scaled.getHeight());
        return scaled;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_add_comment, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.itemOk):
                EditText model = (EditText) findViewById(R.id.carmarkEditText);
                final String carmodel = model.getText().toString().trim();
                EditText desc = (EditText) findViewById(R.id.commentEditText);
                final String desc_str = desc.getText().toString().trim();
                EditText nomer = (EditText) findViewById(R.id.nomerEditText);
                final String gosnomer = nomer.getText().toString().trim();
                final String nonomer = gosnomer.equalsIgnoreCase("") ? "true" : "false";

//                multipartEntity.addPart("video_url_input_0", new StringBody("http://www.youtube.com/watch?v=Dh8nej_R-Yo&feature=g-vrec", Charset.forName(HTTP.UTF_8)));
                new SendFactTask().execute(mCarmodelId, carmodel, desc_str, gosnomer, nonomer, mFiles);

                break;
            case (R.id.itemCancel):
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    	for (int i=0; i<imageAdapter.getCount(); i++) {
    		images.add(imageAdapter.getItem(i));
    	}
        outState.putParcelableArrayList("bitmap", images);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        AutoCompleteTextView carmark = (AutoCompleteTextView) findViewById(R.id.carmarkEditText);
        ArrayAdapter<Carmodel> adapter = (ArrayAdapter<Carmodel>) carmark.getAdapter();
        
        //mCarmodelId = String.valueOf(carmodels.get((int)id).getModelId());
        mCarmodelId = adapter.getItem(position).getModelId(); 
        Log.v("mCarmodelId " + mCarmodelId);
    }
}
