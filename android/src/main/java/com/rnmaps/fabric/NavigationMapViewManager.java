package com.rnmaps.fabric;


import static com.rnmaps.maps.MapManager.MY_LOCATION_PRIORITY;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNMapsNavigationMapViewManagerInterface;
import com.facebook.react.viewmanagers.RNMapsNavigationMapViewManagerDelegate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapColorScheme;
import com.rnmaps.maps.MapMarker;
import com.rnmaps.maps.NavigationView;
import com.rnmaps.maps.SizeReportingShadowNode;

import java.util.Map;

@ReactModule(name = NavigationMapViewManager.REACT_CLASS)
public class NavigationMapViewManager extends ViewGroupManager<NavigationView> implements RNMapsNavigationMapViewManagerInterface<NavigationView> {

    private static boolean rendererInitialized = false;
    private final RNMapsNavigationMapViewManagerDelegate<NavigationView, NavigationMapViewManager> delegate =
            new RNMapsNavigationMapViewManagerDelegate<>(this);


    public NavigationMapViewManager(ReactApplicationContext context) {
        super(context);
    }


    @Override
    public ViewManagerDelegate<NavigationView> getDelegate() {
        return delegate;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public NavigationView createViewInstance(ThemedReactContext context) {
        return new NavigationView(context);
    }

    @Override
    public int getChildCount(NavigationView view) {
        return view.getFeatureCount();
    }

    @Override
    public View getChildAt(NavigationView view, int index) {
        return view.getFeatureAt(index);
    }

    @Override
    public void removeViewAt(NavigationView parent, int index) {
        parent.removeFeatureAt(index);
    }

    @Override
    protected void setupViewRecycling() {
        // override parent to block recycling / allow reliable GoogleMapsOptions passing
    }

    private GoogleMapOptions optionsForInitialProps(ReactStylesDiffMap initialProps){
        GoogleMapOptions options = new GoogleMapOptions();
        if (initialProps != null) {
            setGoogleRenderer(null, initialProps.getString("googleRenderer"));
            if (initialProps.hasKey("liteMode")) {
                options.liteMode(initialProps.getBoolean("liteMode", false));
            }
            if (initialProps.hasKey("googleMapId")) {
                String googleMapId = initialProps.getString("googleMapId");
                options.mapId(googleMapId);
            }
            if (initialProps.getMap("initialCamera") != null) {
                ReadableMap initialCamera = initialProps.getMap("initialCamera");
                CameraPosition camera = NavigationView.cameraPositionFromMap(initialCamera);
                if (camera != null) {
                    options.camera(camera);
                }
            }
            if (initialProps.hasKey("mapType")) {
                if (initialProps.getString("mapType") != null) {
                    options.mapType(mapTypeFromStrValue(initialProps.getString("mapType")));
                }
            }
            if (initialProps.hasKey("minZoom")) {
                if (initialProps.getInt("minZoom", 0) != 0) {
                    options.minZoomPreference(initialProps.getInt("minZoom", 0));
                }
            }
            if (initialProps.hasKey("maxZoom")) {
                if (initialProps.getInt("maxZoom", 0) != 0) {
                    options.maxZoomPreference(initialProps.getInt("maxZoom", 0));
                }
            }
            if (initialProps.hasKey("userInterfaceStyle") && !initialProps.hasKey("liteMode")){
                String style = initialProps.getString("userInterfaceStyle");
                if ("system".equals(style)) {
                    options.mapColorScheme(MapColorScheme.FOLLOW_SYSTEM);
                } else if ("light".equals(style)){
                    options.mapColorScheme(MapColorScheme.LIGHT);
                } else if ("dark".equals(style)){
                    options.mapColorScheme(MapColorScheme.DARK);
                }
            }
            if (initialProps.hasKey("pitchEnabled")) {
                options.tiltGesturesEnabled(initialProps.getBoolean("pitchEnabled", true));
            }
            if (initialProps.hasKey("rotateEnabled")) {
                options.rotateGesturesEnabled(initialProps.getBoolean("rotateEnabled", true));
            }
            if (initialProps.hasKey("scrollDuringRotateOrZoomEnabled")) {
                options.scrollGesturesEnabledDuringRotateOrZoom(initialProps.getBoolean("scrollDuringRotateOrZoomEnabled", true));
            }
            if (initialProps.hasKey("scrollEnabled")) {
                options.scrollGesturesEnabled(initialProps.getBoolean("scrollEnabled", true));
            }
            if (initialProps.hasKey("showsCompass")) {
                options.compassEnabled(initialProps.getBoolean("showsCompass", true));
            }
            if (initialProps.hasKey("toolbarEnabled")) {
                options.mapToolbarEnabled(initialProps.getBoolean("toolbarEnabled", true));
            }
            if (initialProps.hasKey("zoomControlEnabled")) {
                options.zoomControlsEnabled(initialProps.getBoolean("zoomControlEnabled", true));
            }
            if (initialProps.hasKey("zoomEnabled")) {
                options.zoomGesturesEnabled(initialProps.getBoolean("zoomEnabled", true));
            }
        }
        return options;
    }
    @Override
    protected NavigationView createViewInstance(int reactTag, @NonNull ThemedReactContext reactContext, @Nullable ReactStylesDiffMap initialProps, @Nullable StateWrapper stateWrapper) {
        NavigationView view = new NavigationView(reactContext);
        view.setId(reactTag);
        this.addEventEmitters(reactContext, view);
        if (initialProps != null) {
            optionsForInitialProps(initialProps);
            this.updateProperties(view, initialProps);
        }
        if (stateWrapper != null) {
            Object extraData = this.updateState(view, initialProps, stateWrapper);
            if (extraData != null) {
                this.updateExtraData(view, extraData);
            }
        }
        return view;
    }


    public static final String REACT_CLASS = "RNMapsNavigationMapView";

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        return NavigationView.getExportedCustomBubblingEventTypeConstants();
    }


    @Override
    public LayoutShadowNode createShadowNodeInstance() {
        // A custom shadow node is needed in order to pass back the width/height of the map to the
        // view manager so that it can start applying camera moves with bounds.
        return new SizeReportingShadowNode();
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return NavigationView.getExportedCustomDirectEventTypeConstants();
    }

    @Override
    public void setCacheEnabled(NavigationView view, boolean value) {
        view.setCacheEnabled(value);
    }

    @Override
    public void setCamera(NavigationView view, @Nullable ReadableMap value) {
        view.setCamera(value);
    }

    @Override
    public void setCompassOffset(NavigationView view, @Nullable ReadableMap value) {
        // not supported
    }

    @Override
    public void setFollowsUserLocation(NavigationView view, boolean value) {
        // not supported
    }

    @Override
    public void setPoiClickEnabled(NavigationView view, boolean value) {
        view.setPoiClickEnabled(value);
    }

    @Override
    public void setInitialCamera(NavigationView view, @Nullable ReadableMap value) {
        CameraPosition camera = NavigationView.cameraPositionFromMap(value);
        if (camera != null) {
            view.setInitialCameraSet(true);
        }
    }


    @Override
    public void setInitialRegion(NavigationView view, @Nullable ReadableMap value) {
        view.setInitialRegion(value);
    }

    @Override
    public void setKmlSrc(NavigationView view, @Nullable String value) {
        view.setKmlSrc(value);
    }

    @Override
    public void setAppleLogoInsets(NavigationView view, @Nullable ReadableMap value) {
        // not supported
    }

    @Override
    public void setLegalLabelInsets(NavigationView view, @Nullable ReadableMap value) {
        // not supported
    }

    @Override
    public void setLiteMode(NavigationView view, boolean value) {
        // do nothing (initialProp)
    }

    @Override
    public void setGoogleMapId(NavigationView view, @Nullable String value) {
        // do nothing (initialProp)
    }

    @Override
    public void setGoogleRenderer(NavigationView view, @Nullable String value) {
        if (!rendererInitialized) {
            MapsInitializer.Renderer renderer = MapsInitializer.Renderer.LATEST;
            if ("LEGACY".equals(value)) {
                renderer = MapsInitializer.Renderer.LEGACY;
            }
            MapsInitializer.initialize(getReactApplicationContext(), renderer, r -> Log.d("AirMapRenderer", "Init with renderer: " + r));
            rendererInitialized = true;
        }

    }

    @Override
    public void setLoadingBackgroundColor(NavigationView view, @Nullable Integer value) {
        view.setLoadingBackgroundColor(value);
    }

    @Override
    public void setLoadingEnabled(NavigationView view, boolean value) {
        view.setLoadingEnabled(value);
    }

    @Override
    public void setLoadingIndicatorColor(NavigationView view, @Nullable Integer value) {
        view.setLoadingIndicatorColor(value);
    }

    @Override
    public void setMapPadding(NavigationView view, @Nullable ReadableMap padding) {
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        double density = (double) view.getResources().getDisplayMetrics().density;

        if (padding != null) {
            if (padding.hasKey("left")) {
                left = (int) (padding.getDouble("left") * density);
            }

            if (padding.hasKey("top")) {
                top = (int) (padding.getDouble("top") * density);
            }

            if (padding.hasKey("right")) {
                right = (int) (padding.getDouble("right") * density);
            }

            if (padding.hasKey("bottom")) {
                bottom = (int) (padding.getDouble("bottom") * density);
            }
        }

        view.applyBaseMapPadding(left, top, right, bottom);
    }

    @Override
    public void addView(NavigationView parent, View child, int index) {
        parent.addFeature(child, index);
        if (child instanceof MapMarker && ((MapMarker) child).isLoadingImage()){
            MapMarker markerView = (MapMarker) child;
            // Marker is already added as invisible, restore visibility when image loads
            markerView.setImageLoadedListener((uri, drawable, b) -> {
                com.google.android.gms.maps.model.Marker googleMarker =
                    (com.google.android.gms.maps.model.Marker) markerView.getFeature();
                if (googleMarker != null) {
                    // Restore original visibility (marker was hidden temporarily during image load)
                    if (View.VISIBLE == markerView.getVisibility()) {
                        googleMarker.setVisible(true);
                    }
                }
                markerView.update(true);
            });
        }
    }

    @Override
    public void setMapType(NavigationView view, @Nullable String value) {
        view.setMapType(mapTypeFromStrValue(value));
    }

    private static int mapTypeFromStrValue(String value) {
        int mapType;
        //hybrid | none | satellite | standard | terrain
        if ("hybrid".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_HYBRID;
        } else if ("none".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_NONE;
        } else if ("satellite".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_SATELLITE;
        } else if ("standard".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_NORMAL;
        } else if ("terrain".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_TERRAIN;
        } else {
            mapType = GoogleMap.MAP_TYPE_NORMAL;
        }
        return mapType;
    }

    @Override
    public void setMaxDelta(NavigationView view, double value) {
        // not supported
    }

    @Override
    public void setMaxZoom(NavigationView view, float value) {
        view.setMaxZoomLevel(value);
    }

    @Override
    public void setMinDelta(NavigationView view, double value) {
        // not supported
    }

    @Override
    public void setMinZoom(NavigationView view, float value) {
        view.setMinZoomLevel(value);
    }

    @Override
    public void setMoveOnMarkerPress(NavigationView view, boolean value) {
        view.setMoveOnMarkerPress(value);
    }

    @Override
    public void setHandlePanDrag(NavigationView view, boolean value) {
        view.setHandlePanDrag(value);
    }

    @Override
    public void setPaddingAdjustmentBehavior(NavigationView view, @Nullable String value) {
        // not supported
    }

    @Override
    public void setPitchEnabled(NavigationView view, boolean value) {
        view.setPitchEnabled(value);
    }

    @Override
    public void setRegion(NavigationView view, @Nullable ReadableMap value) {
        view.setRegion(value);
    }

    @Override
    public void setRotateEnabled(NavigationView view, boolean value) {
        view.setRotateEnabled(value);
    }

    @Override
    public void setScrollDuringRotateOrZoomEnabled(NavigationView view, boolean value) {
        view.setScrollDuringRotateOrZoomEnabled(value);
    }

    @Override
    public void setScrollEnabled(NavigationView view, boolean value) {
        view.setScrollEnabled(value);
    }

    @Override
    public void setShowsBuildings(NavigationView view, boolean value) {
        view.setShowBuildings(value);
    }

    @Override
    public void setShowsCompass(NavigationView view, boolean value) {
        view.setShowsCompass(value);
    }

    @Override
    public void setShowsIndoorLevelPicker(NavigationView view, boolean value) {
        view.setShowsIndoorLevelPicker(value);
    }

    @Override
    public void setShowsIndoors(NavigationView view, boolean value) {
        view.setShowIndoors(value);
    }

    @Override
    public void setShowsPointsOfInterests(NavigationView view, boolean value) {
        // not supported
    }

    @Override
    public void setPointsOfInterestFilter(NavigationView view, @Nullable ReadableArray value) {
        // not supported
    }

    @Override
    public void setShowsMyLocationButton(NavigationView view, boolean value) {
        view.setShowsMyLocationButton(value);
    }

    @Override
    public void setShowsScale(NavigationView view, boolean value) {
        // not supported
    }

    @Override
    public void setShowsUserLocation(NavigationView view, boolean value) {
        view.setShowsUserLocation(value);
    }

    @Override
    public void setTintColor(NavigationView view, @Nullable Integer value) {
        // not supported
    }

    @Override
    public void setToolbarEnabled(NavigationView view, boolean value) {
        view.setToolbarEnabled(value);
    }

    @Override
    public void setUserInterfaceStyle(NavigationView view, @Nullable String value) {
        // do nothing (initialProp)
    }

    @Override
    public void setCustomMapStyleString(NavigationView view, @Nullable String value) {
        view.setMapStyle(value);
    }

    @Override
    public void setUserLocationAnnotationTitle(NavigationView view, @Nullable String value) {
        // not supported
    }

    @Override
    public void setUserLocationCalloutEnabled(NavigationView view, boolean value) {
        // not supported
    }

    @Override
    public void setUserLocationFastestInterval(NavigationView view, int value) {
        view.setUserLocationFastestInterval(value);
    }

    @Override
    public void setUserLocationPriority(NavigationView view, @Nullable String value) {
        view.setUserLocationPriority(MY_LOCATION_PRIORITY.get(value));
    }

    @Override
    public void setUserLocationUpdateInterval(NavigationView view, int value) {
        view.setUserLocationUpdateInterval(value);
    }

    @Override
    public void setZoomControlEnabled(NavigationView view, boolean value) {
        view.setZoomControlEnabled(value);
    }

    @Override
    public void setZoomEnabled(NavigationView view, boolean value) {
        view.setZoomEnabled(value);
    }

    @Override
    public void setShowsTraffic(NavigationView view, boolean value) {
        view.setShowsTraffic(value);
    }

    @Override
    public void setZoomTapEnabled(NavigationView view, boolean value) {
        // not supported
    }

    @Override
    public void setCameraZoomRange(NavigationView view, @Nullable ReadableMap value) {
        // not supported
    }

    @Override
    public void animateToRegion(NavigationView view, String regionJSON, int duration) {
        try {
            JSONObject region = new JSONObject(regionJSON);
            double lng = region.getDouble("longitude");
            double lat = region.getDouble("latitude");
            double lngDelta = region.getDouble("longitudeDelta");
            double latDelta = region.getDouble("latitudeDelta");
            LatLngBounds bounds = new LatLngBounds(
                    new LatLng(lat - latDelta / 2, lng - lngDelta / 2), // southwest
                    new LatLng(lat + latDelta / 2, lng + lngDelta / 2)  // northeast
            );
            view.animateToRegion(bounds, duration);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setCamera(NavigationView view, String cameraJSON) {
        try {
            JSONObject camera = new JSONObject(cameraJSON);
            CameraPosition position = view.cameraPositionFromJSON(camera);
            view.moveToCamera(position);
        } catch (JSONException e) {
            Log.e("NavigationViewManager", "parse camera exception " + e);
        }
    }

    @Override
    public void animateCamera(NavigationView view, String cameraJSON, int duration) {
        try {
            JSONObject camera = new JSONObject(cameraJSON);
            CameraPosition position = view.cameraPositionFromJSON(camera);
            view.animateToCamera(position, duration);
        } catch (JSONException e) {
            Log.e("NavigationViewManager", "parse camera exception " + e);
        }
    }

    @Override
    public void fitToElements(NavigationView view, String edgePaddingJSON, boolean animated) {
        try {
            WritableMap map = null;
            if (edgePaddingJSON != null) {
                JSONObject jsonObject = new JSONObject(edgePaddingJSON);
                map = JSONUtil.convertJsonToWritable(jsonObject);
            }
            view.fitToElements(map, animated);
        } catch (JSONException e){
            Log.e("NavigationViewManager", "parse edgePaddingJSON exception " + e);
        }

    }

    @Override
    public void fitToSuppliedMarkers(NavigationView view, String markersJSON, String edgePaddingJSON, boolean animated) {
        try {
            WritableArray markers = null;
            if (markersJSON != null) {
                JSONArray array = new JSONArray(markersJSON);
                markers = JSONUtil.convertJsonArrayToWritable(array);
            }
            WritableMap edgePadding = null;
            if (edgePaddingJSON != null) {
                JSONObject jsonObject = new JSONObject(edgePaddingJSON);
                edgePadding = JSONUtil.convertJsonToWritable(jsonObject);
            }
            view.fitToSuppliedMarkers(markers, edgePadding, animated);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void fitToCoordinates(NavigationView view, String coordinatesJSON, String edgePaddingJSON, boolean animated) {
        try {
            WritableArray coordinates = null;
            if (coordinatesJSON != null) {
                JSONArray array = new JSONArray(coordinatesJSON);
                coordinates = JSONUtil.convertJsonArrayToWritable(array);
            }
            WritableMap edgePadding = null;
            if (edgePaddingJSON != null) {
                JSONObject jsonObject = new JSONObject(edgePaddingJSON);
                edgePadding = JSONUtil.convertJsonToWritable(jsonObject);
            }
            view.fitToCoordinates(coordinates, edgePadding, animated);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setIndoorActiveLevelIndex(NavigationView view, int activeLevelIndex) {
        view.setIndoorActiveLevelIndex(activeLevelIndex);
    }

    @Override
    public void setShowsNavigationTripProgressBar(NavigationView view, boolean value) {
        view.setShowsNavigationTripProgressBar(value);
    }

    @Override
    public void setShowsTrafficLights(NavigationView view, boolean value) {
        view.setShowsTrafficLights(value);
    }

    @Override
    public void setShowsStopSigns(NavigationView view, boolean value) {
        view.setShowsStopSigns(value);
    }

    @Override
    public void setShowsSpeedometer(NavigationView view, boolean value) {
        view.setShowsSpeedometer(value);
    }

    @Override
    public void setShowsSpeedLimit(NavigationView view, boolean value) {
        view.setShowsSpeedLimit(value);
    }

    @Override
    public void setShowsReportIncidentButton(NavigationView view, boolean value) {
        view.setShowsReportIncidentButton(value);
    }

    @Override
    public void setNavigationVoiceMuted(NavigationView view, boolean value) {
        view.setNavigationVoiceMuted(value);
    }

    @Override
    public void setTermsCompanyName(NavigationView view, @Nullable String value) {
        view.setTermsCompanyName(value);
    }

    @Override
    public void startNavigation(NavigationView view, String coordinateJSON, String placeId) {
        try {
            JSONObject coordinate = new JSONObject(coordinateJSON);
            WritableMap map = JSONUtil.convertJsonToWritable(coordinate);
            view.startNavigation(map, placeId);
        } catch (JSONException e) {
            Log.e("NavigationMapViewManager", "parse coordinateJSON exception " + e);
        }
    }

    @Override
    public void recenter(NavigationView view) {
        view.recenter();
    }

    @Override
    public void updateExtraData(NavigationView view, Object extraData) {
        view.updateExtraData(extraData);
    }

    @Override
    public void onDropViewInstance(NavigationView view) {
        view.doDestroy();
        super.onDropViewInstance(view);
    }
}
