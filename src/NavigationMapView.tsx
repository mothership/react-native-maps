import * as React from 'react';
import {
  Platform,
  type NativeSyntheticEvent,
  type ViewProps,
} from 'react-native';
import {ProviderContext} from './decorateMapComponent';
import type {LatLng, Region} from './sharedTypes';
import type {
  Camera,
  EdgePadding,
  MapStyleElement,
  MapPressEvent,
  LongPressEvent,
  PanDragEvent,
  PoiClickEvent,
  UserLocationChangeEvent,
  Details,
} from './MapView.types';
import GoogleMapView, {
  Commands as GoogleCommands,
} from './specs/NativeComponentGoogleMapView';
import NavigationMapNativeView, {
  Commands as NavigationCommands,
} from './specs/NativeComponentNavigationMapView';
import createFabricNavigationMap, {
  type FabricNavigationMapHandle,
} from './createFabricNavigationMap';

const FabricGoogleNavigationMap =
  Platform.OS === 'ios'
    ? createFabricNavigationMap(GoogleMapView, GoogleCommands)
    : null;

const FabricAndroidNavigationMap =
  Platform.OS === 'android'
    ? createFabricNavigationMap(NavigationMapNativeView, NavigationCommands)
    : null;

export type ShowRecenterButtonEvent = NativeSyntheticEvent<{
  showRecenterButton: boolean;
}>;

export type NavigationInfoUpdatedEvent = NativeSyntheticEvent<{
  distanceRemaining: number;
  durationRemaining: number;
}>;

export type NavigationMapViewProps = ViewProps & {
  camera?: Camera;
  initialCamera?: Camera;
  initialRegion?: Region;
  region?: Region;
  customMapStyle?: MapStyleElement[];
  mapPadding?: EdgePadding;
  scrollEnabled?: boolean;
  zoomEnabled?: boolean;
  pitchEnabled?: boolean;
  rotateEnabled?: boolean;
  showsTraffic?: boolean;
  showsUserLocation?: boolean;
  showsMyLocationButton?: boolean;
  showsCompass?: boolean;
  showsBuildings?: boolean;
  showsIndoors?: boolean;
  showsIndoorLevelPicker?: boolean;
  loadingEnabled?: boolean;
  loadingBackgroundColor?: string;
  minZoomLevel?: number;
  maxZoomLevel?: number;
  toolbarEnabled?: boolean;
  cacheEnabled?: boolean;
  moveOnMarkerPress?: boolean;

  navigationVoiceMuted?: boolean;
  showsNavigationTripProgressBar?: boolean;
  showsTrafficLights?: boolean;
  showsStopSigns?: boolean;
  showsSpeedometer?: boolean;
  showsSpeedLimit?: boolean;
  termsCompanyName?: string;
  showsReportIncidentButton?: boolean;

  onMapReady?: () => void;
  onPress?: (event: MapPressEvent) => void;
  onLongPress?: (event: LongPressEvent) => void;
  onPanDrag?: (event: PanDragEvent) => void;
  onPoiClick?: (event: PoiClickEvent) => void;
  onUserLocationChange?: (event: UserLocationChangeEvent) => void;
  onRegionChange?: (region: Region, details: Details) => void;
  onRegionChangeStart?: (region: Region, details: Details) => void;
  onRegionChangeComplete?: (region: Region, details: Details) => void;

  onShowRecenterButton?: (event: ShowRecenterButtonEvent) => void;
  onNavigationRouteLoaded?: (event: NativeSyntheticEvent<object>) => void;
  onNavigationRouteFailedToLoad?: (
    event: NativeSyntheticEvent<object>,
  ) => void;
  onNavigationInfoUpdated?: (event: NavigationInfoUpdatedEvent) => void;
  onArrivedToDestination?: (event: NativeSyntheticEvent<object>) => void;

  children?: React.ReactNode;
};

type State = {
  isReady: boolean;
};

class NavigationMapView extends React.Component<
  NavigationMapViewProps,
  State
> {
  static defaultProps: Partial<NavigationMapViewProps> = {
    showsTraffic: true,
    showsTrafficLights: true,
    showsStopSigns: true,
    showsSpeedometer: true,
    showsSpeedLimit: true,
    showsNavigationTripProgressBar: false,
    showsReportIncidentButton: true,
    navigationVoiceMuted: false,
  };

  state: State = {
    isReady: false,
  };

  private fabricMap = React.createRef<FabricNavigationMapHandle>();

  getMapBoundaries = async () => {
    return this.fabricMap.current?.getMapBoundaries();
  };

  setCamera = (camera: Partial<Camera>) => {
    this.fabricMap.current?.setCamera(camera);
  };

  animateCamera = (camera: Partial<Camera>, opts?: {duration?: number}) => {
    this.fabricMap.current?.animateCamera(camera, opts?.duration || 500);
  };

  animateToRegion = (region: Region, duration?: number) => {
    this.fabricMap.current?.animateToRegion(region, duration || 500);
  };

  fitToElements = (options: {
    edgePadding?: EdgePadding;
    animated?: boolean;
  } = {}) => {
    const {
      edgePadding = {top: 0, right: 0, bottom: 0, left: 0},
      animated = true,
    } = options;
    this.fabricMap.current?.fitToElements(edgePadding, animated);
  };

  fitToSuppliedMarkers = (
    markers: string[],
    options: {edgePadding?: EdgePadding; animated?: boolean} = {},
  ) => {
    const {
      edgePadding = {top: 0, right: 0, bottom: 0, left: 0},
      animated = true,
    } = options;
    this.fabricMap.current?.fitToSuppliedMarkers(
      markers,
      edgePadding,
      animated,
    );
  };

  fitToCoordinates = (
    coordinates: LatLng[] = [],
    options: {edgePadding?: EdgePadding; animated?: boolean} = {},
  ) => {
    const {
      edgePadding = {top: 0, right: 0, bottom: 0, left: 0},
      animated = true,
    } = options;
    this.fabricMap.current?.fitToCoordinates(
      coordinates,
      edgePadding,
      animated,
    );
  };

  startNavigation = (coordinate: LatLng, placeId?: string) => {
    this.fabricMap.current?.startNavigation(coordinate, placeId);
  };

  recenter = () => {
    this.fabricMap.current?.recenter();
  };

  private _onMapReady = () => {
    this.setState({isReady: true}, () => {
      this.props.onMapReady?.();
    });
  };

  private handleRegionChange = (event: NativeSyntheticEvent<any>) => {
    this.props.onRegionChange?.(event.nativeEvent.region, {
      isGesture: event.nativeEvent.isGesture,
    });
  };

  private handleRegionChangeStarted = (event: NativeSyntheticEvent<any>) => {
    this.props.onRegionChangeStart?.(event.nativeEvent.region, {
      isGesture: event.nativeEvent.isGesture,
    });
  };

  private handleRegionChangeComplete = (event: NativeSyntheticEvent<any>) => {
    this.props.onRegionChangeComplete?.(event.nativeEvent.region, {
      isGesture: event.nativeEvent.isGesture,
    });
  };

  render() {
    /* eslint-disable @typescript-eslint/no-unused-vars */
    const {
      onMapReady,
      onRegionChange,
      onRegionChangeStart,
      onRegionChangeComplete,
      customMapStyle,
      minZoomLevel,
      maxZoomLevel,
      children,
      region,
      ...restProps
    } = this.props;
    /* eslint-enable @typescript-eslint/no-unused-vars */

    const customMapStyleString = customMapStyle
      ? JSON.stringify(customMapStyle)
      : undefined;

    const fabricProps: Record<string, unknown> = {
      ...restProps,
      onMapReady: this._onMapReady,
      customMapStyleString,
      minZoom: minZoomLevel,
      maxZoom: maxZoomLevel,
      onRegionChange: this.handleRegionChange,
      onRegionChangeStart: this.handleRegionChangeStarted,
      onRegionChangeComplete: this.handleRegionChangeComplete,
    };

    if (region) {
      fabricProps.region = {
        latitude: region.latitude,
        longitude: region.longitude,
        latitudeDelta: region.latitudeDelta,
        longitudeDelta: region.longitudeDelta,
      };
    }

    if (!this.state.isReady) {
      fabricProps.style = this.props.style;
      fabricProps.initialCamera = this.props.initialCamera;
      fabricProps.onLayout = this.props.onLayout;
    }

    const childrenNodes = this.state.isReady ? children : null;
    const FabricComponent =
      Platform.OS === 'ios'
        ? FabricGoogleNavigationMap
        : FabricAndroidNavigationMap;

    if (!FabricComponent) {
      return null;
    }

    return (
      <ProviderContext.Provider value="google">
        <FabricComponent {...(fabricProps as any)} ref={this.fabricMap}>
          {childrenNodes}
        </FabricComponent>
      </ProviderContext.Provider>
    );
  }
}

export default NavigationMapView;
