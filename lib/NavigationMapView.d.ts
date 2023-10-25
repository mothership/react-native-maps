/// <reference types="react" />
import { type ViewStyle } from 'react-native';
export type GoogleNavigationOnShowResumeButtonEvent = {
    showResumeButton: boolean;
};
export type GoogleNavigationOnUpdateNavigationInfoEvent = {
    distanceRemaining: number;
    durationRemaining: number;
};
type NavigationMapViewProps = {
    style?: ViewStyle;
    from?: [number, number];
    toCoordinate?: [number, number];
    toPlaceId?: string;
    showTripProgressBar?: boolean;
    showCompassButton?: boolean;
    showTrafficLights?: boolean;
    showStopSigns?: boolean;
    showSpeedometer?: boolean;
    showSpeedLimit?: boolean;
    navigationVoiceMuted?: boolean;
    onShowResumeButton?: (showResumeButton: boolean) => void;
    onDidArrive?: () => void;
    onDidLoadRoute?: () => void;
    onUpdateNavigationInfo?: (distanceRemaining: number, durationRemaining: number) => void;
};
export interface NavigationViewRefType {
    isVoiceMuted: () => Promise<{
        isVoiceMuted: boolean;
    }>;
    setVoiceMuted: (muted: boolean) => void;
    recenter: () => void;
}
export declare const NavigationView: import("react").ForwardRefExoticComponent<NavigationMapViewProps & import("react").RefAttributes<NavigationViewRefType>>;
export {};
