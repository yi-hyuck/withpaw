import React, { useState, useEffect } from "react";
import { StyleSheet, Text, View, PermissionsAndroid, Platform } from 'react-native';
import Geolocation from 'react-native-geolocation-service';

// 권한 요청 함수
async function requestLocationPermission(): Promise<boolean> {
  
  try{
    
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      {
        title: '위치 정보 권한 요청',
        message: '주변 동물병원 위치를 찾으려면 위치 정보 접근 권한이 필요합니다.',
        buttonNeutral: '나중에',
        buttonNegative: '거부',
        buttonPositive: '허용'
      }
    );

    return granted === 'granted' // ==PermissionsAndroid.RESULT_GRANTED

  } catch (err) {

    console.warn(err);
    return false;

  }
}

const Maps: React.FC = () => {

  const [location, setLocation] = useState<Geolocation.GeoCoordinates | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [loading, setLoading] = useState(true); 

    useEffect(() => {
    fetchLocation();
  }, []);
  
  const fetchLocation = async () => {

    setLoading(true);

    const hasPermission = await requestLocationPermission();

    if (!hasPermission) {
      
      setErrorMsg('위치 권한이 거부되었습니다.');
      setLoading(false);
      
      return;
    }

    Geolocation.getCurrentPosition(

      (position) => {
        setLocation(position.coords);
        setErrorMsg(null);
        setLoading(false);
      },

      (error) => {
        setErrorMsg(`위치 정보를 불러오는 데 실패했습니다: ${error.message}`);
        setLoading(false);
        console.log(error.code, error.message);
      },

      {enableHighAccuracy: true, timeout: 15000, maximumAge: 10000} // 최대 대기시간 ms, 위치정보 유지시간 ms

    );
  };
  
  if(loading) {
    return <Text>현재 위치를 불러오는 중...</Text>
  }

  if(errorMsg) {
    return <Text>{errorMsg}</Text>
  }

  if(!location) {
    return <Text>위치 정보를 불러올 수 없습니다.</Text>
  }



  // 위치정보 확인용 return
  return (
    <View>
      <Text>현재 위도: {location.latitude}</Text>
      <Text>현재 경도: {location.longitude}</Text>
    </View>
  )
}

export default Maps;