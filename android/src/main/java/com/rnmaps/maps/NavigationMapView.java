package com.googlenavigationsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.bridge.Arguments;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.FollowMyLocationOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.libraries.navigation.ArrivalEvent;
import com.google.android.libraries.navigation.DisplayOptions;
import com.google.android.libraries.navigation.ForceNightMode;
import com.google.android.libraries.navigation.ListenableResultFuture;
import com.google.android.libraries.navigation.NavigationApi;
import com.google.android.libraries.navigation.NavigationView;
import com.google.android.libraries.navigation.Navigator;
import com.google.android.libraries.navigation.RoutingOptions;
import com.google.android.libraries.navigation.SpeedAlertOptions;
import com.google.android.libraries.navigation.SpeedAlertSeverity;
import com.google.android.libraries.navigation.StylingOptions;
import com.google.android.libraries.navigation.Waypoint;

public class NavigationMapView extends FrameLayout {

  private NavigationView navigationView = null;
  private GoogleMap mGoogleMap;
  private Navigator mNavigator = null;
  private RoutingOptions mRoutingOptions = null;
  private DisplayOptions mDisplayOptions = null;

  private double fromLatitude = 0;
  private double fromLongitude = 0;
  private double toLatitude = 0;
  private double toLongitude = 0;
  private String toPlaceId = null;
  private int navigationVoiceMuted = 0;
  private int showTripProgressBar = 0;
  private int showCompassButton = 0;
  private int showTrafficLights = 0;
  private int showStopSigns = 0;
  private int showSpeedometer = 0;
  private int showSpeedLimit = 0;
  private boolean navigationAlreadyAdded = false;
  private boolean navigationAlreadyStarted = false;
  private int audioGuidance = Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE;
  private Navigator.RemainingTimeOrDistanceChangedListener remainingTimeOrDistanceChangedListener = null;
  private Navigator.ArrivalListener arrivalListener = null;

  private static final int BLACK_COLOR = 0xFF000000;

  private ReactContext mReactContext = null;

  public NavigationMapView(@NonNull ReactContext reactContext) {
    super(reactContext);
    mReactContext = reactContext;
    addSubview(reactContext);
  }

  private void addSubview(Context context) {
    navigationView = new NavigationView(context);
    addView(navigationView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    navigationView.onCreate(null);
  }

  public void setFromLatitude(double fromLatitude) {
    this.fromLatitude = fromLatitude;
  }

  public void setFromLongitude(double fromLongitude) {
    this.fromLongitude = fromLongitude;
  }

  public void setToLatitude(double toLatitude) {
    this.toLatitude = toLatitude;
  }

  public void setToLongitude(double toLongitude) {
    this.toLongitude = toLongitude;
  }

  public void setToPlaceId(String toPlaceId) {
    this.toPlaceId = toPlaceId;
  }

  public void setNavigationVoiceMuted(int navigationVoiceMuted) {
    this.navigationVoiceMuted = navigationVoiceMuted;
    audioGuidance = navigationVoiceMuted != 0 ? Navigator.AudioGuidance.SILENT : Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE;
    if (mNavigator != null && mNavigator.isGuidanceRunning()) {
      mNavigator.setAudioGuidance(audioGuidance);
    }
  }

  public void setShowTripProgressBar(int showTripProgressBar) {
    this.showTripProgressBar = showTripProgressBar;
  }

  public void setShowCompassButton(int showCompassButton) {
    this.showCompassButton = showCompassButton;
  }

  public void setShowTrafficLights(int showTrafficLights) {
    this.showTrafficLights = showTrafficLights;
  }

  public void setShowStopSigns(int showStopSigns) {
    this.showStopSigns = showStopSigns;
  }

  public void setShowSpeedometer(int showSpeedometer) {
    this.showSpeedometer = showSpeedometer;
  }

  public void setShowSpeedLimit(int showSpeedLimit) {
    this.showSpeedLimit = showSpeedLimit;
  }

  public void recenter() {
    if (mGoogleMap != null && mNavigator != null) {
      mGoogleMap.followMyLocation(0);
      sendShowResumeButton(false);
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();

    navigationView.onStop();
    navigationView.onDestroy();

    if (mNavigator != null) {
      mNavigator.stopGuidance();

      if (arrivalListener != null) {
        mNavigator.removeArrivalListener(arrivalListener);
      }
      if (remainingTimeOrDistanceChangedListener != null) {
        mNavigator.removeRemainingTimeOrDistanceChangedListener(remainingTimeOrDistanceChangedListener);
      }

      //mNavigator.getSimulator().unsetUserLocation();
      mNavigator.cleanup();
    }
    mNavigator = null;
    navigationView = null;
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    if (navigationView != null) {
      navigationView.onStart();
      startNavigationIfAllCoordinatesSet();
    }
  }

  private void sendShowResumeButton(boolean showResumeButton) {
      WritableMap event = Arguments.createMap();
      event.putBoolean("showResumeButton", showResumeButton);
      mReactContext
              .getJSModule(RCTEventEmitter.class)
              .receiveEvent(getId(), "showResumeButton", event);
  }

  private void sendArrivalEvent() {
    WritableMap event = Arguments.createMap();
    mReactContext
      .getJSModule(RCTEventEmitter.class)
      .receiveEvent(getId(), "didArrive", event);
  }

  private void sendLoadRouteEvent() {
    WritableMap event = Arguments.createMap();
    mReactContext
      .getJSModule(RCTEventEmitter.class)
      .receiveEvent(getId(), "didLoadRoute", event);
  }

  private void sendCurrentNavigationInfo() {
    if (mNavigator == null) {
      return;
    }
    WritableMap event = Arguments.createMap();
    event.putDouble("distanceRemaining", (double)mNavigator.getCurrentTimeAndDistance().getMeters());
    event.putDouble("durationRemaining", (double)mNavigator.getCurrentTimeAndDistance().getSeconds());
    mReactContext
      .getJSModule(RCTEventEmitter.class)
      .receiveEvent(getId(), "updateNavigationInfo", event);
  }

  private void startNavigationIfAllCoordinatesSet() {
    if (navigationAlreadyStarted || fromLatitude == 0 || fromLongitude == 0 || ((toLatitude == 0 || toLongitude == 0) && toPlaceId == null)) {
      return;
    }

    FragmentActivity activity = (FragmentActivity) mReactContext.getCurrentActivity();
    if (activity == null) {
      return;
    }

    navigationAlreadyStarted = true;

    NavigationApi.getNavigator(activity, new NavigationApi.NavigatorListener() {
      /**
       * Sets up the navigation UI when the navigator is ready for use.
       */
      @Override
      public void onNavigatorReady(Navigator navigator) {
        mNavigator = navigator;

        // Create the waypoint and if it fails, return early
        Waypoint waypoint = null;
        boolean waypointCreated = false;
        if (toPlaceId != null) {
          try {
            waypoint = Waypoint.builder().setPlaceIdString(
                    toPlaceId
                  ).build();
            waypointCreated = true;
          } catch(Exception e) {}
        }
        if (!waypointCreated && toLatitude != 0 && toLongitude != 0) {
          waypoint = Waypoint.builder().setLatLng(
                    toLatitude, toLongitude
                  ).build();
        }

        if (waypoint == null) {
          return;
        }

        if (navigationView == null) {
          return;
        }

        // Follow user
        navigationView.getMapAsync(new OnMapReadyCallback() {
          @SuppressLint("MissingPermission")
          @Override
          public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            googleMap.setTrafficEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(showCompassButton != 0);
            googleMap.followMyLocation(0);
            googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                @Override
                public void onCameraMoveStarted(int reason) {
                    if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                        sendShowResumeButton(true);
                    }
                }
            });
          }
        });

        // UI settings
        navigationView.setNavigationUiEnabled(true);
        navigationView.setHeaderEnabled(true);
        navigationView.setEtaCardEnabled(false);
        navigationView.setRecenterButtonEnabled(false);
        navigationView.setSpeedLimitIconEnabled(showSpeedLimit != 0);
        navigationView.setSpeedometerEnabled(showSpeedometer != 0);
        navigationView.setTripProgressBarEnabled(showTripProgressBar != 0);
//        navigationView.setTrafficIncidentCardsEnabled(true);
//        navigationView.setTrafficPromptsEnabled(true);
        navigationView.setForceNightMode(ForceNightMode.FORCE_DAY);
       navigationView.setStylingOptions(new StylingOptions()
         .primaryDayModeThemeColor(BLACK_COLOR)
         .primaryNightModeThemeColor(BLACK_COLOR)
         .secondaryDayModeThemeColor(BLACK_COLOR)
         .secondaryNightModeThemeColor(BLACK_COLOR));

        // Setup SpeedAlertOptions
        mNavigator.setSpeedAlertOptions(new SpeedAlertOptions.Builder()
          .setSpeedAlertThresholdPercentage(SpeedAlertSeverity.MINOR, 5.0f)
          .setSpeedAlertThresholdPercentage(SpeedAlertSeverity.MAJOR, 10.0f)
          .setSeverityUpgradeDurationSeconds(5)
          .build());

        // Add listeners
        remainingTimeOrDistanceChangedListener = new Navigator.RemainingTimeOrDistanceChangedListener() {
          @Override
          public void onRemainingTimeOrDistanceChanged() {
            // send event
            sendCurrentNavigationInfo();
          }
        };
        arrivalListener = new Navigator.ArrivalListener() {
          @Override
          public void onArrival(ArrivalEvent arrivalEvent) {
            if (arrivalEvent.isFinalDestination() && mNavigator != null) {
              mNavigator.stopGuidance();

              // Stop simulating vehicle movement.
              //mNavigator.getSimulator().unsetUserLocation();

              // send event
              sendArrivalEvent();
            }
          }
        };
        mNavigator.addArrivalListener(arrivalListener);
        mNavigator.addRemainingTimeOrDistanceChangedListener(10, 100, remainingTimeOrDistanceChangedListener);

        // Set the last digit of the car's license plate to get route restrictions
        // in supported countries. (optional)
        // mNavigator.setLicensePlateRestrictionInfo(12, "US");

        // Set the travel mode (DRIVING, WALKING, CYCLING, TWO_WHEELER, or TAXI).
        mRoutingOptions =  new RoutingOptions();
        mRoutingOptions = mRoutingOptions.travelMode(RoutingOptions.TravelMode.DRIVING);

        mDisplayOptions =
          new DisplayOptions().showTrafficLights(showTrafficLights != 0).showStopSigns(showStopSigns != 0);

        // Navigate to the waypoint
        ListenableResultFuture<Navigator.RouteStatus> result = mNavigator.setDestination(waypoint, mRoutingOptions, mDisplayOptions);
        result.setOnResultListener(new ListenableResultFuture.OnResultListener<Navigator.RouteStatus>() {
          @Override
          public void onResult(Navigator.RouteStatus routeStatus) {
            if (routeStatus == Navigator.RouteStatus.OK && mNavigator != null) {
              // send event
              sendLoadRouteEvent();

              // Audio guidance
              mNavigator.setAudioGuidance(audioGuidance);

              //mNavigator.getSimulator().simulateLocationsAlongExistingRoute();

              mNavigator.startGuidance();
            }
          }
        });
      }

      /**
       * Handles errors from the Navigation SDK.
       * @param errorCode The error code returned by the navigator.
       */
      @Override
      public void onError(@NavigationApi.ErrorCode int errorCode) {
        Log.e("NavigationMapView", "Error loading Google Navigation View: " + errorCode);
      }
    });
  }
}
