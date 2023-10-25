package com.googlenavigationsdk;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.UiThreadUtil;

import com.google.android.libraries.navigation.NavigationView;

import java.util.Map;

public class NavigationMapViewManager extends SimpleViewManager<NavigationMapView> {
  public static final String REACT_CLASS = "NavigationMapView";

  private ReactApplicationContext mReactContext;

  public NavigationMapViewManager(ReactApplicationContext reactContext) {
    mReactContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  public Map getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.builder().put(
      "showResumeButton",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onShowResumeButton", "captured", "onShowResumeButtonCapture")
      )).put(
      "didArrive",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onDidArrive", "captured", "onDidArriveCapture")
      )).put(
      "didLoadRoute",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onDidLoadRoute", "captured", "onDidLoadRouteCapture")
      )).put(
      "updateNavigationInfo",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onUpdateNavigationInfo", "captured", "onUpdateNavigationInfoCapture")
      )).build();
  }

  @Override
  @NonNull
  public NavigationMapView createViewInstance(ThemedReactContext reactContext) {
    return new NavigationMapView(reactContext);
  }

  @ReactProp(name = "fromLatitude")
  public void setFromLatitude(NavigationMapView view, double fromLatitude) {
    view.setFromLatitude(fromLatitude);
  }

  @ReactProp(name = "fromLongitude")
  public void setFromLongitude(NavigationMapView view, double fromLongitude) {
    view.setFromLongitude(fromLongitude);
  }

  @ReactProp(name = "toLatitude")
  public void setToLatitude(NavigationMapView view, double toLatitude) {
    view.setToLatitude(toLatitude);
  }

  @ReactProp(name = "toLongitude")
  public void setToLongitude(NavigationMapView view, double toLongitude) {
    view.setToLongitude(toLongitude);
  }

  @ReactProp(name = "toPlaceId")
  public void setToPlaceId(NavigationMapView view, String toPlaceId) {
    view.setToPlaceId(toPlaceId);
  }

  @ReactProp(name = "navigationVoiceMuted")
  public void setNavigationVoiceMuted(NavigationMapView view, int navigationVoiceMuted) {
    view.setNavigationVoiceMuted(navigationVoiceMuted);
  }

  @ReactProp(name = "showTripProgressBar")
  public void setShowTripProgressBar(NavigationMapView view, int showTripProgressBar) {
    view.setShowTripProgressBar(showTripProgressBar);
  }

  @ReactProp(name = "showCompassButton")
  public void setShowCompassButton(NavigationMapView view, int showCompassButton) {
    view.setShowCompassButton(showCompassButton);
  }

  @ReactProp(name = "showTrafficLights")
  public void setShowTrafficLights(NavigationMapView view, int showTrafficLights) {
    view.setShowTrafficLights(showTrafficLights);
  }

  @ReactProp(name = "showStopSigns")
  public void setShowStopSigns(NavigationMapView view, int showStopSigns) {
    view.setShowStopSigns(showStopSigns);
  }

  @ReactProp(name = "showSpeedometer")
  public void setShowSpeedometer(NavigationMapView view, int showSpeedometer) {
    view.setShowSpeedometer(showSpeedometer);
  }

  @ReactProp(name = "showSpeedLimit")
  public void setShowSpeedLimit(NavigationMapView view, int showSpeedLimit) {
    view.setShowSpeedLimit(showSpeedLimit);
  }

  @ReactMethod
  public void recenter(int reactTag, Promise promise) {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        UIManagerModule uiManagerModule = mReactContext.getNativeModule(UIManagerModule.class);
        NavigationMapView view = (NavigationMapView) uiManagerModule.resolveView(reactTag);
        if (view != null) {
          view.recenter();
        }
        promise.resolve(null);
      }
    });
  }

}
