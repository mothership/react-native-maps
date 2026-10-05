import React, {useRef, useState} from 'react';
import {
  StyleSheet,
  View,
  Text,
  TouchableOpacity,
  Platform,
} from 'react-native';
import {NavigationMapView} from 'react-native-maps';

const DESTINATION = {
  latitude: 40.4168,
  longitude: -3.7038,
};

const INITIAL_REGION = {
  latitude: 37.9922,
  longitude: -1.1307,
  latitudeDelta: 0.35,
  longitudeDelta: 0.25,
};

export default function GoogleNavigation() {
  const mapRef = useRef<any>(null);
  const [voiceMuted, setVoiceMuted] = useState(false);
  const [status, setStatus] = useState('Waiting for map…');
  const [remainingTime, setRemainingTime] = useState('-');
  const [remainingDistance, setRemainingDistance] = useState('-');
  const [etaClock, setEtaClock] = useState('-');
  const [mapCentered, setMapCentered] = useState(true);

  return (
    <View style={styles.container}>
      <View style={styles.mapWrap}>
        <NavigationMapView
          ref={mapRef}
          style={StyleSheet.absoluteFill}
          initialRegion={INITIAL_REGION}
          termsCompanyName="react-native-maps"
          navigationVoiceMuted={voiceMuted}
          showsNavigationTripProgressBar={false}
          showsTraffic
          showsTrafficLights
          showsStopSigns
          showsSpeedometer
          showsSpeedLimit
          showsReportIncidentButton={false}
          scrollEnabled
          zoomEnabled
          pitchEnabled
          rotateEnabled
          onMapReady={() => {
            setStatus('Map ready — starting navigation');
            mapRef.current?.startNavigation(DESTINATION);
          }}
          onNavigationRouteLoaded={() => setStatus('Route loaded')}
          onNavigationRouteFailedToLoad={event => {
            const routeStatus = event.nativeEvent?.status;
            setStatus(
              routeStatus
                ? `Route failed to load (${routeStatus})`
                : 'Route failed to load',
            );
          }}
          onArrivedToDestination={() => setStatus('Arrived')}
          onShowRecenterButton={event => {
            setMapCentered(!event.nativeEvent.showRecenterButton);
          }}
          onNavigationInfoUpdated={event => {
            const {distanceRemaining, durationRemaining} = event.nativeEvent;
            const minutes = Math.ceil(durationRemaining / 60);
            const arrival = new Date(Date.now() + durationRemaining * 1000);
            setRemainingDistance(`${Math.round(distanceRemaining)}`);
            setRemainingTime(`${minutes}`);
            setEtaClock(
              arrival.toLocaleTimeString([], {
                hour: '2-digit',
                minute: '2-digit',
              }),
            );
          }}
        />
        <View style={styles.overlayButtons}>
          {!mapCentered ? (
            <TouchableOpacity
              style={styles.overlayButton}
              onPress={() => mapRef.current?.recenter()}>
              <Text style={styles.overlayButtonText}>Recenter</Text>
            </TouchableOpacity>
          ) : null}
          <TouchableOpacity
            style={styles.overlayButton}
            onPress={() => setVoiceMuted(value => !value)}>
            <Text style={styles.overlayButtonText}>
              {voiceMuted ? 'Unmute' : 'Mute'}
            </Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={styles.bottomContainer}>
        <View style={styles.metrics}>
          <View style={styles.metric}>
            <Text style={styles.metricValue}>{etaClock}</Text>
            <Text style={styles.metricLabel}>ETA</Text>
          </View>
          <View style={styles.metric}>
            <Text style={styles.metricValue}>{remainingTime}</Text>
            <Text style={styles.metricLabel}>min</Text>
          </View>
          <View style={styles.metric}>
            <Text style={styles.metricValue}>{remainingDistance}</Text>
            <Text style={styles.metricLabel}>m</Text>
          </View>
        </View>
        <Text style={styles.status}>{status}</Text>
        <TouchableOpacity
          style={styles.rerouteButton}
          onPress={() => mapRef.current?.startNavigation(DESTINATION)}>
          <Text style={styles.rerouteButtonText}>Reroute</Text>
        </TouchableOpacity>
        <Text style={styles.platform}>Platform: {Platform.OS}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: '#fff',
  },
  mapWrap: {
    flex: 1,
  },
  overlayButtons: {
    position: 'absolute',
    right: 16,
    bottom: 16,
    flexDirection: 'row',
    gap: 8,
  },
  overlayButton: {
    backgroundColor: '#fff',
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderRadius: 20,
    shadowColor: '#000',
    shadowOpacity: 0.2,
    shadowRadius: 6,
    shadowOffset: {width: 0, height: 2},
    elevation: 3,
  },
  overlayButtonText: {
    color: '#111',
    fontSize: 13,
    fontWeight: '600',
  },
  bottomContainer: {
    width: '100%',
    backgroundColor: '#fff',
    paddingTop: 16,
    paddingHorizontal: 16,
    paddingBottom: 28,
    gap: 12,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: '#ddd',
  },
  status: {fontSize: 13, color: '#111'},
  metrics: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  metric: {flex: 1},
  metricValue: {fontSize: 20, fontWeight: '700', color: '#111'},
  metricLabel: {fontSize: 13, color: '#555', marginTop: 2},
  rerouteButton: {
    backgroundColor: '#b42318',
    minHeight: 52,
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
  },
  rerouteButtonText: {color: '#fff', fontSize: 16, fontWeight: '600'},
  platform: {fontSize: 12, color: '#888'},
});
