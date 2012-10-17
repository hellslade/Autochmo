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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.Editable;
import android.text.InputFilter;
import android.text.format.Time;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class FactAddActivity extends SherlockFragmentActivity implements OnClickListener, OnItemClickListener {
    private class getCarmodels extends AsyncTask<Void, Void, List<Carmodel>> {
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
	private static final int PHOTO_REQUEST_CODE = 0x000002;
    private static final int IMAGE_PICK_REQUEST_CODE = 0x000003;
    private AutochmoApplication mAutochmo;
    //private Uri mPhotoUri;
    private File mPhotoFile;
    public ArrayList<Bitmap> images;
    public ImageAdapter imageAdapter;
    public Gallery gallery;
    public List<Carmodel> carmodels;
    public String mCarmodelId = "0";
    private Map<Bitmap, String> mFiles = new HashMap<Bitmap, String>();
    private EditText nomerEdit;
    public AutoCompleteTextView carmark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfact);
        carmark = (AutoCompleteTextView) findViewById(R.id.carmarkEditText);
        mAutochmo = (AutochmoApplication)getApplication();
        
        ImageButton addShotButton = (ImageButton) findViewById(R.id.buttonAddShot);
        addShotButton.setOnClickListener(this);
        ImageButton addImageButton = (ImageButton) findViewById(R.id.buttonAddImage);
        addImageButton.setOnClickListener(this);
        
        nomerEdit = (EditText) findViewById(R.id.nomerEditText);
        nomerEdit.setFilters(new InputFilter[] {new AGosnomerCheck()});
        // TODO: Обрабатывать KeyDown/KeyUp
        /*String[] nomers = new String[]{"н100ка72", "г100пр72", "n100ka86", "h100ky86"};
        for (String nomer : nomers) {
            AGosnomerCheck gn = new AGosnomerCheck(nomer);
            Log.v(gn.getNomer());
        }*/
        //
        
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                ImageView imgView = (ImageView)findViewById(R.id.imageView);
                String fileSource = (String) imgView.getTag();
                intent.setDataAndType(Uri.parse("file://" + fileSource), "image/*");
                startActivity(intent);
            }
        });
        gallery = (Gallery) findViewById(R.id.photoGallery);
        if (savedInstanceState != null) {
            images = savedInstanceState.getParcelableArrayList("bitmap");
        } else {
            images = new ArrayList<Bitmap>();
        }
        imageAdapter = new  ImageAdapter(this, images);
        gallery.setAdapter(imageAdapter);

        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                ImageView imgView = (ImageView)findViewById(R.id.imageView);
                Bitmap bitmap = (Bitmap)images.get(position);
                imgView.setImageBitmap(bitmap);
                imgView.setTag(mFiles.get(bitmap));
            }
        });
        new getCarmodels().execute();
        //carmodels = api._getCarModels();
        //ArrayAdapter<Carmodel> adapter = new ArrayAdapter<Carmodel>(FactAddActivity.this, R.layout.list_item, carmodels);
        //carmark.setAdapter(adapter);
        carmark.setOnItemClickListener(this);
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
        images.add(bitmapPreview);
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
                        //mFiles.put(bitmap, mPhotoFile.getAbsolutePath());
                        Log.v("Photo path " + mPhotoFile.getAbsolutePath());
                    } else
                        try {
                            bitmap = android.provider.MediaStore.Images.Media
                                    .getBitmap(getContentResolver(), Uri.fromFile(mPhotoFile));
                            Log.v("Getting photo from mPhotoUri");
                            //mFiles.put(mPhotoFile.getName(), mPhotoFile.getAbsolutePath());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    Bitmap bitmapPreview = scale(bitmap);
                    updateImageAdapter(bitmapPreview);
                    mFiles.put(bitmapPreview, mPhotoFile.getAbsolutePath());
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
                    //mFiles.put(new File(fileSrc).getName(), fileSrc);
                    Bitmap bitmap = BitmapFactory.decodeFile(fileSrc); //load preview image
                    Bitmap bitmapPreview = scale(bitmap);
                    updateImageAdapter(bitmapPreview);
                    mFiles.put(bitmapPreview, fileSrc);
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
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;

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
                final String nonomer = gosnomer.isEmpty() ? "true" : "false";
                final ProgressDialog pg = ProgressDialog.show(FactAddActivity.this,
                        "Пожалуйста, подождите...", "Отправка данных на сервер", true);
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Toast.makeText(FactAddActivity.this, (String)message.obj, Toast.LENGTH_SHORT).show();
                    }
                };
                new Thread() {
                    public void run() {
                        Map<String, String> files = new HashMap<String, String>();
                        for (Map.Entry<Bitmap, String> pair : mFiles.entrySet()) {
                            files.put(new File(pair.getValue().toString()).getName(), pair.getValue().toString());
                        }
                        String result = mAutochmo._addFact(mCarmodelId, carmodel, desc_str, gosnomer, nonomer, files);
                        Message message = handler.obtainMessage(0, result);
                        handler.sendMessage(message);
                        pg.dismiss();
                        FactAddActivity.this.finish();
                    };
                }.start();
                break;
            case (R.id.itemCancel):
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
