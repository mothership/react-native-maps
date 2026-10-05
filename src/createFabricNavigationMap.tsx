import React, {forwardRef, useImperativeHandle, useRef} from 'react';
import type {LatLng, Point, Region} from './sharedTypes';
import type {Camera, EdgePadding, SnapshotOptions} from './MapView.types';
import type {FabricMapHandle} from './createFabricMap';
import NativeAirMapsModule, {
  type MapBoundaries,
} from './specs/NativeAirMapsModule';
import {findNodeHandle} from 'react-native';
import GoogleMapView, {
  Commands as GoogleCommands,
  type MapFabricNativeProps as GoogleMapFabricNativeProps,
} from './specs/NativeComponentGoogleMapView';
import NavigationMapNativeView, {
  Commands as NavigationCommands,
  type MapFabricNativeProps as NavigationMapFabricNativeProps,
} from './specs/NativeComponentNavigationMapView';

export type NavigationFabricMapProps =
  | GoogleMapFabricNativeProps
  | NavigationMapFabricNativeProps;

export interface FabricNavigationMapHandle extends FabricMapHandle {
  startNavigation: (coordinate: LatLng, placeId?: string) => void;
  recenter: () => void;
}

type ViewComponent = typeof GoogleMapView | typeof NavigationMapNativeView;
type CommandModule = typeof GoogleCommands | typeof NavigationCommands;

const createFabricNavigationMap = (
  ViewComponent: ViewComponent,
  Commands: CommandModule,
) => {
  return forwardRef<FabricNavigationMapHandle | null, NavigationFabricMapProps>(
    (props, ref) => {
      const fabricRef = useRef<React.ElementRef<ViewComponent> | null>(null);
      const node = findNodeHandle(fabricRef.current) ?? -1;

      useImperativeHandle(ref, () => ({
        async getMarkersFrames(onlyVisible: boolean) {
          if (fabricRef.current) {
            return NativeAirMapsModule.getMarkersFrames(node, onlyVisible);
          }
          throw new Error(
            'getMarkersFrames is only supported on iOS with Fabric.',
          );
        },
        async getCoordinateForPoint(point: Point) {
          if (fabricRef.current) {
            return NativeAirMapsModule.getCoordinateForPoint(node, point);
          }
          throw new Error(
            'getCoordinateForPoint is only supported on iOS with Fabric.',
          );
        },
        async getPointForCoordinate(coordinate: LatLng) {
          if (fabricRef.current) {
            return NativeAirMapsModule.getPointForCoordinate(node, coordinate);
          }
          throw new Error(
            'getPointForCoordinate is not supported on this platform.',
          );
        },
        async getAddressFromCoordinates(coordinate: LatLng) {
          if (fabricRef.current) {
            return NativeAirMapsModule.getAddressFromCoordinates(
              node,
              coordinate,
            );
          }
          throw new Error(
            'getAddressFromCoordinates is not supported on this platform',
          );
        },
        async takeSnapshot(config: SnapshotOptions) {
          if (fabricRef.current) {
            return NativeAirMapsModule.takeSnapshot(
              node,
              JSON.stringify(config),
            );
          }
          throw new Error('takeSnapshot is only supported on iOS with Fabric.');
        },
        async getCamera(): Promise<Camera> {
          if (fabricRef.current) {
            return NativeAirMapsModule.getCamera(node);
          }
          throw new Error('getCamera is only supported on iOS with Fabric.');
        },
        async getMapBoundaries(): Promise<MapBoundaries> {
          if (fabricRef.current) {
            return NativeAirMapsModule.getMapBoundaries(node);
          }
          throw new Error(
            'getMapBoundaries is only supported on iOS with Fabric.',
          );
        },
        animateToRegion(region: Region, duration: number) {
          if (fabricRef.current) {
            (Commands as any).animateToRegion(
              fabricRef.current,
              JSON.stringify(region),
              duration,
            );
          }
        },
        fitToElements(edgePadding: EdgePadding, animated: boolean) {
          if (fabricRef.current) {
            (Commands as any).fitToElements(
              fabricRef.current,
              JSON.stringify(edgePadding),
              animated,
            );
          }
        },
        fitToSuppliedMarkers(
          markers: string[],
          edgePadding: EdgePadding,
          animated: boolean,
        ) {
          if (fabricRef.current) {
            (Commands as any).fitToSuppliedMarkers(
              fabricRef.current,
              JSON.stringify(markers),
              JSON.stringify(edgePadding),
              animated,
            );
          }
        },
        animateCamera(camera: Partial<Camera>, duration: number) {
          if (fabricRef.current) {
            (Commands as any).animateCamera(
              fabricRef.current,
              JSON.stringify(camera),
              duration,
            );
          }
        },
        fitToCoordinates(
          coordinates: LatLng[],
          edgePadding: EdgePadding,
          animated: boolean,
        ) {
          if (fabricRef.current) {
            (Commands as any).fitToCoordinates(
              fabricRef.current,
              JSON.stringify(coordinates),
              JSON.stringify(edgePadding),
              animated,
            );
          }
        },
        setIndoorActiveLevelIndex(activeLevelIndex: number) {
          if (fabricRef.current) {
            (Commands as any).setIndoorActiveLevelIndex(
              fabricRef.current,
              activeLevelIndex,
            );
          }
        },
        setCamera(camera: Partial<Camera>) {
          if (fabricRef.current) {
            (Commands as any).setCamera(
              fabricRef.current,
              JSON.stringify(camera),
            );
          }
        },
        startNavigation(coordinate: LatLng, placeId?: string) {
          if (!fabricRef.current) {
            console.warn('startNavigation called before map was ready');
            return;
          }
          (Commands as any).startNavigation(
            fabricRef.current,
            JSON.stringify(coordinate),
            placeId ?? '',
          );
        },
        recenter() {
          if (!fabricRef.current) {
            console.warn('recenter called before map was ready');
            return;
          }
          (Commands as any).recenter(fabricRef.current);
        },
      }));

      // @ts-ignore host component props
      return <ViewComponent {...props} ref={fabricRef} />;
    },
  );
};

export default createFabricNavigationMap;
