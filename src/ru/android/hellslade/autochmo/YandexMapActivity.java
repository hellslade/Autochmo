package ru.android.hellslade.autochmo;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.drag.DragAndDropItem;
import ru.yandex.yandexmapkit.overlay.drag.DragAndDropOverlay;
import ru.yandex.yandexmapkit.overlay.location.MyLocationItem;
import ru.yandex.yandexmapkit.overlay.location.OnMyLocationListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class YandexMapActivity extends FragmentActivity implements OnMyLocationListener {
	private MapView mMap;
	private MapController mMapController;
	private OverlayManager mOverlayManager;
	
	private DragAndDropItem dragItem;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.map_layout);
		mMap = (MapView)findViewById(R.id.map);
		mMapController = mMap.getMapController();
		mOverlayManager = mMapController.getOverlayManager();
		showObject(new GeoPoint(0,0));
		Bundle b = getIntent().getExtras();
		if (b != null) {
			Log.v("get location");
			String l = b.getString("location");
			double lat = Double.valueOf(l.split(",")[0]);
			double lon = Double.valueOf(l.split(",")[1]);
			GeoPoint gp = new GeoPoint(lon, lat);
			mMapController.setPositionAnimationTo(gp);
			float zoom = 15;
			mMapController.setZoomCurrent(zoom);
			// Disable determining the user's location
	        mOverlayManager.getMyLocation().setEnabled(false);
		}
		this.setResult(RESULT_CANCELED);
	}
	public void showObject(GeoPoint gp){
        // Load the required resources
        Resources res = getResources();

        float density = getResources().getDisplayMetrics().density;
        int offsetX = (int)(-7 * density);
        int offsetY = (int)(20 * density);
        
        // Create a layer of objects for the map
        DragAndDropOverlay overlay = new DragAndDropOverlay(mMapController);

        // Create an object for the layer
        dragItem = new DragAndDropItem(gp, res.getDrawable(R.drawable.location_2));
        // Set offsets of the image to match the balloon tail with the specified GeoPoint
        
        dragItem.setOffsetX(offsetX);
        dragItem.setOffsetY(offsetY);
        // Make the object draggable
        dragItem.setDragable(true);
        
        overlay.addOverlayItem(dragItem);
        // Add the layer to the map
        mOverlayManager.addOverlay(overlay);
    }
	@Override
	public void onMyLocationChange(MyLocationItem item) {
		Log.v("onMyLocationChange");
		mOverlayManager.getMyLocation().setEnabled(false);
		dragItem.setGeoPoint(item.getGeoPoint());
		dragItem.notify();
		
	}
}
