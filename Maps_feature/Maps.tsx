import React, { useState, useEffect, useRef, useCallback } from "react";
import { StyleSheet, View, PermissionsAndroid, Platform, Alert, ActivityIndicator } from 'react-native';
import MapView, { PROVIDER_GOOGLE, Marker } from "react-native-maps";
import { useFocusEffect } from '@react-navigation/native';
import Config from "react-native-config";

const GOOGLE_API_KEY = Config.GOOGLE_MAPS_API_KEY;

interface Place {
  displayName?: {
    text: string;
  };
  formattedAddress?: string;
  location: {
    latitude: number;
    longitude: number;
  };
}

function Maps() {
  const mapRef = useRef<MapView>(null);
  const [places, setPlaces] = useState<Place[]>([]);
  const [hasSearched, setHasSearched] = useState<boolean>(false);
  
  // 로딩 상태
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const initialRegion = {
    latitude: 37.5665,
    longitude: 126.9780,
    latitudeDelta: 0.005,
    longitudeDelta: 0.005,
  };

  useFocusEffect(
    useCallback(() => {
      setHasSearched(false);
      return () => {
        setPlaces([]);
      };
    }, [])
  );

  useEffect(() => {
    if (Platform.OS === 'android') {
      requestLocationPermission();
    }
  }, []);

  const requestLocationPermission = async () => {
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION
      );
      if (granted !== PermissionsAndroid.RESULTS.GRANTED) {
        Alert.alert("권한 필요", "위치 권한을 허용해야 주변 동물병원을 찾을 수 있습니다.");
      }

    } catch (err) {
      console.warn(err);
      Alert.alert("오류", "권한 요청 중 알 수 없는 오류가 발생했습니다.");
    }
  };
  // 병원 검색 함수
  const fetchNearbyHospital = async (latitude: number, longitude: number) => {

    setIsLoading(true);

    const url = 'https://places.googleapis.com/v1/places:searchNearby';
    
    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-Goog-Api-Key': GOOGLE_API_KEY,
          'X-Goog-FieldMask': 'places.displayName,places.location,places.formattedAddress',
        },
        body: JSON.stringify({
          includedTypes: ['veterinary_care'],
          maxResultCount: 10, // 검색결과 최대 갯수
          locationRestriction: {
            circle: {
              center: { latitude, longitude },
              radius: 3000.0, // 주변 병원 검색 범위
            },
          },
        }),
      });

      // HTTP 상태 예외처리
      if (!response.ok) {
        throw new Error(`HTTP Status: ${response.status}`);
      }

      const json = await response.json();

      // 동물병원 
      if (json.places && json.places.length > 0) {
        setPlaces(json.places);
        Alert.alert("검색 완료", `주변에서 ${json.places.length}개의 동물병원을 찾았습니다.`);
      } else {
        // 주변에 동물병원 없음
        setPlaces([]);
        Alert.alert("알림", "주변 3km 내에서 동물병원을 찾을수 없습니다.");
      }

    } catch (error) {
      console.error("API 에러:", error);
      Alert.alert(
        "오류", 
        "정보를 불러오는데 실패했습니다.\n네트워크 상태나 위치 서비스가 켜져 있는지 확인하세요."
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <MapView
        ref={mapRef}
        style={styles.map}
        provider={PROVIDER_GOOGLE}
        initialRegion={initialRegion}
        showsUserLocation={true}
        showsMyLocationButton={true}
        
        onUserLocationChange={(e) => {
          if (!hasSearched) {
            const coordinate = e.nativeEvent.coordinate;
            if (coordinate) {
              const { latitude, longitude } = coordinate;
              setHasSearched(true);
              fetchNearbyHospital(latitude, longitude);

              if (mapRef.current) {
                mapRef.current.animateToRegion({
                  latitude,
                  longitude,
                  latitudeDelta: 0.005,
                  longitudeDelta: 0.005,
                }, 1000);
              }
            }
          }
        }}
      >
        {places.map((place, index) => (
          <Marker
            key={index}
            coordinate={{
              latitude: place.location.latitude,
              longitude: place.location.longitude,
            }}
            title={place.displayName?.text}
            description={place.formattedAddress}
          />
        ))}
      </MapView>

      {/* 로딩 중일 때 로딩 마크 표시 */}
      {isLoading && (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#0000ff" />
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  map: { flex: 1 },

  loadingContainer: {
    position: 'absolute',
    top: 0, 
    left: 0, 
    right: 0, 
    bottom: 0,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.3)'
  }
});

export default Maps;