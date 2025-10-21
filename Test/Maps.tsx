import React from "react";
import { StyleSheet, Text, View, Platform, } from 'react-native';
import { createBottomTabNavigator, BottomTabScreenProps } from "@react-navigation/bottom-tabs";
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';

import Foods from './Foods';
import Disease from './Disease';
import CalendarScreen from "./Calendar";

const Tab = createBottomTabNavigator();


//아래 네비게이션 바
function Maps_Screen() {
  return(
    <Tab.Navigator
      initialRouteName="Maps"
      screenOptions={{
        tabBarStyle:{
          backgroundColor: '#ffd651ff',
          height:55,
          borderTopWidth: 1,
          paddingBottom: 5,
        },
        tabBarActiveTintColor: '#ff8800ff',
        tabBarInactiveTintColor: '#ffffff',
        tabBarLabelStyle: {
          fontSize: 14,
          fontWeight: '900',
        },
      }}>
      <Tab.Screen
          name="Maps"
          component={Maps}
          options={{
            title:"지도",
            headerStyle:{
              height:55,
              backgroundColor:'#ffd651ff',
            },
            headerTitleStyle:{
              fontSize:20,
              textAlignVertical: 'center',
              fontWeight:'bold',
              color:'#000000ff',
              paddingLeft:5,
            },
            tabBarIcon:({color, size, focused}) => (
              <MaterialCommunityIcons 
                name={focused ? 'map-marker-radius' : 'map-marker-radius-outline'}
                color={color}
                size={size}/>
            ),    
          }}>
      </Tab.Screen>
      <Tab.Screen
          name="Foods"
          component={Foods}
          options={{
            title:"음식 검색",
            headerStyle:{
              height:55,
              backgroundColor:'#ffd651ff',
            },
            headerTitleStyle:{
              fontSize:20,
              textAlignVertical: 'center',
              fontWeight:'bold',
              color:'#000000ff',
              paddingLeft:5,
            },
            tabBarIcon:({color, size, focused}) => (
              <MaterialCommunityIcons 
                name='magnify'
                color={focused ? '#ff8800ff':'#ffffffff'}
                size={size}/>
            ),
          }}>
      </Tab.Screen>
      <Tab.Screen
          name="Disease"
          component={Disease}
          options={{
            title:"질병 관리",
            headerShown: false,
            // headerStyle:{
            //   height:55,
            //   backgroundColor:'#ffd651ff',
            // },
            // headerTitleStyle:{
            //   fontSize:20,
            //   textAlignVertical: 'center',
            //   fontWeight:'bold',
            //   color:'#000000ff',
            //   paddingLeft:5,
            // }
            tabBarIcon:({color, size, focused}) => (
              <MaterialCommunityIcons 
                name={focused ? 'hospital-box':'hospital-box-outline'}
                color={color}
                size={size}/>
            ),
          }}>
      </Tab.Screen>
      <Tab.Screen
          name="Calender"
          component={CalendarScreen}
          options={{
            title:"달력",
            headerStyle:{
              height:55,
              backgroundColor:'#ffd651ff',
            },
            headerTitleStyle:{
              fontSize:20,
              textAlignVertical: 'center',
              fontWeight:'bold',
              color:'#000000ff',
              paddingLeft:5,
            },
            tabBarIcon:({color, size, focused}) => (
              <MaterialCommunityIcons 
                name={focused ? 'calendar-month':'calendar-month-outline'}
                color={color}
                size={size}/>
            ),
          }}>
      </Tab.Screen>
    </Tab.Navigator> 
  )
}

//맵
function Maps(){
    return(
      <View style={styles.container}>
        <Text>Maps</Text>
      </View>
    )
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor:'#ffffff',
  },
});

export default Maps_Screen;