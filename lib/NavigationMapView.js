"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.NavigationView = void 0;
const react_native_1 = require("react-native");
const react_1 = require("react");
const LINKING_ERROR = `The package 'react-native-google-navigation-sdk' doesn't seem to be linked. Make sure: \n\n` +
    react_native_1.Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
    '- You rebuilt the app after installing the package\n' +
    '- You are not using Expo Go\n';
const NavigationMapViewComponentName = 'NavigationMapView';
const NavigationMapView = react_native_1.UIManager.getViewManagerConfig(NavigationMapViewComponentName) != null
    ? (0, react_native_1.requireNativeComponent)(NavigationMapViewComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
    };
exports.NavigationView = (0, react_1.forwardRef)((props, ref) => {
    // Refs
    const viewRef = (0, react_1.useRef)(null);
    (0, react_1.useImperativeHandle)(ref, () => ({ isVoiceMuted, setVoiceMuted, recenter }));
    const _onShowResumeButton = (event) => {
        if (!props.onShowResumeButton) {
            return;
        }
        // process raw event...
        props.onShowResumeButton(event.nativeEvent.showResumeButton);
    };
    const _onUpdateNavigationInfo = (event) => {
        if (!props.onUpdateNavigationInfo) {
            return;
        }
        // process raw event...
        props.onUpdateNavigationInfo(event.nativeEvent.distanceRemaining, event.nativeEvent.durationRemaining);
    };
    const _onDidArrive = (_) => {
        if (!props.onDidArrive) {
            return;
        }
        // process raw event...
        props.onDidArrive();
    };
    const _onDidLoadRoute = (_) => {
        if (!props.onDidLoadRoute) {
            return;
        }
        // process raw event...
        props.onDidLoadRoute();
    };
    // Exposed public functions
    const setVoiceMuted = (muted) => {
        react_native_1.NativeModules.NavigationMapView.setVoiceMuted((0, react_native_1.findNodeHandle)(viewRef.current), muted);
    };
    const recenter = () => {
        react_native_1.NativeModules.NavigationMapView.recenter((0, react_native_1.findNodeHandle)(viewRef.current));
    };
    const isVoiceMuted = () => {
        return react_native_1.NativeModules.NavigationMapView.isVoiceMuted((0, react_native_1.findNodeHandle)(viewRef.current));
    };
    return (
    // @ts-ignore
    <NavigationMapView ref={viewRef} style={props.style} fromLatitude={props.from ? props.from[0] : 0} fromLongitude={props.from ? props.from[1] : 0} toLatitude={props.toCoordinate ? props.toCoordinate[0] : 0} toLongitude={props.toCoordinate ? props.toCoordinate[1] : 0} toPlaceId={props.toPlaceId} showTripProgressBar={props.showTripProgressBar === undefined ? true : props.showTripProgressBar} showCompassButton={props.showCompassButton === undefined ? true : props.showCompassButton} showTrafficLights={props.showTrafficLights === undefined ? true : props.showTrafficLights} showStopSigns={props.showStopSigns === undefined ? true : props.showStopSigns} showSpeedometer={props.showSpeedometer === undefined ? true : props.showSpeedometer} showSpeedLimit={props.showSpeedLimit === undefined ? true : props.showSpeedLimit} navigationVoiceMuted={props.navigationVoiceMuted === undefined ? false : props.navigationVoiceMuted} 
    // @ts-ignore
    onShowResumeButton={_onShowResumeButton} 
    // @ts-ignore
    onUpdateNavigationInfo={_onUpdateNavigationInfo} 
    // @ts-ignore
    onDidArrive={_onDidArrive} 
    // @ts-ignore
    onDidLoadRoute={_onDidLoadRoute}/>);
});
