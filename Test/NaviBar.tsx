import React, { useEffect, useState } from "react";
import { StyleSheet, Text, View, Platform, TouchableOpacity,  } from 'react-native';
import { createBottomTabNavigator, BottomTabScreenProps } from "@react-navigation/bottom-tabs";
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import { NavigationContainer, useNavigation, } from "@react-navigation/native";
import { NativeStackNavigationProp, createNativeStackNavigator } from "@react-navigation/native-stack";
import { createDrawerNavigator, DrawerNavigationProp, DrawerContentScrollView, DrawerItem } from '@react-navigation/drawer'
import { useMember } from "./MemberProvider";
import { RootStackParamList } from "./types";

import FoodsScreen from './Foods';
import Disease from './Disease';
import CalendarMainScreen from "./Calendar";
import Maps from './Maps';
import UserInfoScreen from './UserInfo';
import DogMgmtScreen from './DogManagement';
import App from './App';
import AsyncStorage from "@react-native-async-storage/async-storage";


const Tab = createBottomTabNavigator();
const Drawer = createDrawerNavigator();
const Stack = createNativeStackNavigator<RootStackParamList>();

type NavigationProps = DrawerNavigationProp<RootStackParamList>;
type DrawerNavigationPropType = DrawerNavigationProp<RootStackParamList>;

const DrawerButton = () => {
  const navigation = useNavigation<NavigationProps>();

  return(
    <TouchableOpacity
      onPress={()=>navigation.openDrawer()}
      style={{marginRight: 16}}
    >
      <MaterialCommunityIcons name='account-circle' size={30} color={"#ffffffff"}/>
    </TouchableOpacity>
  )
}

function TabNaviBar() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  return(
    //아래 네비게이션 바
    <Tab.Navigator
      initialRouteName="Foods"
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
          name="Foods"
          component={FoodsScreen}
          options={{
            title:"음식 검색",
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
            // },
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
      <Tab.Screen             //위 네비게이션 바
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
          headerRight: ()=> <DrawerButton/>,
          tabBarIcon:({color, size, focused}) => (
            <MaterialCommunityIcons 
              name={focused ? 'map-marker-radius' : 'map-marker-radius-outline'}
              color={color}
              size={size}/>
          ),
        }}>
      </Tab.Screen>
      <Tab.Screen
          name="Calender"
          component={CalendarMainScreen}
          options={{
            title:"달력",
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
            // },
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

function CustomDrawerContent(props:any){
  const {setMemberInfo} = useMember();

  //로그아웃
  const handleLogout = async () => {
    try{
      const token = await AsyncStorage.getItem('userToken');

      const response = await fetch('http://10.0.2.2:8090/member/logout', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization' : `Bearer ${token}`
        },
      });

      if(response.ok || response.status === 200 || response.status === 302 || response.status === 204){
        setMemberInfo(null);
        
        if(token){
          await AsyncStorage.removeItem('userToken');
        }

        setMemberInfo(null);

        const naviBarStack = props.navigation.getParent();
        if(naviBarStack){
          const rootStack = naviBarStack.getParent();
          if(rootStack && rootStack.reset){
            rootStack.reset({
              index: 0,
              routes: [{name: 'Home'}]
            })
          }
        }
      } else {
        console.error("Logout failed", response.status);
      }

    } catch (error){
      console.error("Logout error:", error);
    }
  }



  return(
    <DrawerContentScrollView {...props}>
      <DrawerItem
        label="홈"
        labelStyle={{fontWeight:'bold', fontSize: 17,}}
        onPress={()=>props.navigation.navigate("Home")}
      />
      <DrawerItem
        label="회원 정보 관리"
        onPress={()=>props.navigation.navigate("UserInfo")}
      />
      <DrawerItem
        label="반려동물 정보 관리"
        onPress={()=>props.navigation.navigate("DogManagement")}
      />
     <DrawerItem
        label="로그아웃"
        labelStyle={{
          color: '#ff0000ff',
          textAlignVertical: 'bottom',
        }}
        onPress={()=>{
          props.navigation.closeDrawer();
          handleLogout();
        }}
      />
    </DrawerContentScrollView>
  )
}

function DrawerNaviBar(){
  return(
    <Drawer.Navigator
      initialRouteName="Home"
      screenOptions={{
        drawerPosition:'right',
        drawerStyle:{
          backgroundColor: '#fffefbff',
          width: 230,
        },
      }}
      drawerContent={(props)=><CustomDrawerContent {...props}/>}
    >
      <Drawer.Screen
        name="Home"
        component={TabNaviBar}
        options={{
          title: "홈",
          headerShown:false,
        }}
      />
      {/* <Drawer.Screen
        name="UserInfo"
        component={UserInfo}
        options={{
          title: "회원 정보",
        }}
      /> */}
    </Drawer.Navigator>
  )
}

function NaviBar(){
  return(
    <Stack.Navigator>
      <Stack.Screen name="Home" component={DrawerNaviBar} options={{headerShown: false}}/>
      <Stack.Screen name="UserInfo" component={UserInfoScreen} options={{headerShown: false}}/>
      <Stack.Screen name="DogManagement" component={DogMgmtScreen} options={{headerShown: false}}/>
    </Stack.Navigator>
  )
}

export default NaviBar;