/**
* This code was generated by [react-native-codegen](https://www.npmjs.com/package/react-native-codegen).
*
* Do not edit this file as changes may cause incorrect behavior and will be lost
* once the code is regenerated.
*
* @generated by codegen project: GeneratePropsJavaInterface.js
*/

package com.facebook.react.viewmanagers;

import android.view.View;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.ReadableMap;

public interface RNMapsMapViewManagerInterface<T extends View> {
  void setCacheEnabled(T view, boolean value);
  void setCamera(T view, @Nullable ReadableMap value);
  void setCompassOffset(T view, @Nullable ReadableMap value);
  void setFollowsUserLocation(T view, boolean value);
  void setPoiClickEnabled(T view, boolean value);
  void setInitialCamera(T view, @Nullable ReadableMap value);
  void setInitialRegion(T view, @Nullable ReadableMap value);
  void setKmlSrc(T view, @Nullable String value);
  void setLegalLabelInsets(T view, @Nullable ReadableMap value);
  void setLiteMode(T view, boolean value);
  void setGoogleMapId(T view, @Nullable String value);
  void setGoogleRenderer(T view, @Nullable String value);
  void setLoadingBackgroundColor(T view, @Nullable Integer value);
  void setLoadingEnabled(T view, boolean value);
  void setLoadingIndicatorColor(T view, @Nullable Integer value);
  void setMapPadding(T view, @Nullable ReadableMap value);
  void setMapType(T view, @Nullable String value);
  void setMaxDelta(T view, double value);
  void setMaxZoom(T view, float value);
  void setMinDelta(T view, double value);
  void setMinZoom(T view, float value);
  void setMoveOnMarkerPress(T view, boolean value);
  void setHandlePanDrag(T view, boolean value);
  void setPaddingAdjustmentBehavior(T view, @Nullable String value);
  void setPitchEnabled(T view, boolean value);
  void setRegion(T view, @Nullable ReadableMap value);
  void setRotateEnabled(T view, boolean value);
  void setScrollDuringRotateOrZoomEnabled(T view, boolean value);
  void setScrollEnabled(T view, boolean value);
  void setShowsBuildings(T view, boolean value);
  void setShowsCompass(T view, boolean value);
  void setShowsIndoorLevelPicker(T view, boolean value);
  void setShowsIndoors(T view, boolean value);
  void setShowsMyLocationButton(T view, boolean value);
  void setShowsScale(T view, boolean value);
  void setShowsUserLocation(T view, boolean value);
  void setTintColor(T view, @Nullable Integer value);
  void setToolbarEnabled(T view, boolean value);
  void setUserInterfaceStyle(T view, @Nullable String value);
  void setUserLocationAnnotationTitle(T view, @Nullable String value);
  void setUserLocationCalloutEnabled(T view, boolean value);
  void setUserLocationFastestInterval(T view, int value);
  void setUserLocationPriority(T view, @Nullable String value);
  void setUserLocationUpdateInterval(T view, int value);
  void setZoomControlEnabled(T view, boolean value);
  void setZoomEnabled(T view, boolean value);
  void setZoomTapEnabled(T view, boolean value);
  void setCameraZoomRange(T view, @Nullable ReadableMap value);
  void animateToRegion(T view, String regionJSON, int duration);
  void setCamera(T view, String cameraJSON);
  void animateCamera(T view, String cameraJSON, int duration);
  void fitToElements(T view, String edgePaddingJSON, boolean animated);
  void fitToSuppliedMarkers(T view, String markersJSON, String edgePaddingJSON, boolean animated);
  void fitToCoordinates(T view, String coordinatesJSON, String edgePaddingJSON, boolean animated);
}
