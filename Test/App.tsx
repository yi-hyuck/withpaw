/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
//import { NewAppScreen } from '@react-native/new-app-screen';
//import { useState } from 'react';
import { StatusBar, StyleSheet, useColorScheme, View, Text, Touchable, TouchableOpacity, ActivityIndicator} from 'react-native';
import { SafeAreaProvider, useSafeAreaInsets} from 'react-native-safe-area-context';
import { NavigationContainer, useNavigation } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from './types';
import { MemberProvider, useMember } from './MemberProvider';
import { AuthProvider } from './AuthProvider';
import { useAuth } from './useAuth';
import { Image } from 'react-native';
//import AsyncStorage from '@react-native-async-storage/async-storage';

import Login from './Login';
import SignUp_Screen from './SignUp';
import NaviBar from './NaviBar';

const Stack = createNativeStackNavigator<RootStackParamList>();

type HomeScreenNavigationProp = NativeStackNavigationProp<
  RootStackParamList,
  'Home'
>;

type Props = {
  navigation: HomeScreenNavigationProp;
};

function NaviContainer(){
  const {isAuthenticated, isLoadingToken} = useAuth();
  const {memberInfo} = useMember();

  if(isLoadingToken) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#FFBB00" />
        <Text style={styles.loadingText}>인증 정보 확인 중...</Text>
      </View>
    )
  }

  return (
    <Stack.Navigator initialRouteName={isAuthenticated ? 'NaviBar' : 'Home'}>
      <Stack.Screen name="Home" component={HomeScreen} options={{headerShown: false}}/>
      <Stack.Screen name="Login" component={Login} options={{headerShown: false}}/>
      <Stack.Screen name="SignUp" component={SignUp_Screen} options={{headerShown: false}}/>
      <Stack.Screen name="NaviBar" component={NaviBar} options={{headerShown: false}}/>
    </Stack.Navigator>
  )
}

function App() {
  return (
    <SafeAreaProvider>
      <MemberProvider>
        <AuthProvider>
          <NavigationContainer>
            <NaviContainer/>
          </NavigationContainer>
        </AuthProvider>
      </MemberProvider>
    </SafeAreaProvider>
  );
}

//실제 홈 화면
function HomeScreen({navigation}: Props) {
  return (
    <View style={styles.container}>
        <Image source={require('./assets/images/logo.png')} style={styles.image}/>
        <TouchableOpacity
          style={[styles.button, {backgroundColor: '#ffcf88ff',}, {top:0}]}
          onPress={()=>navigation.navigate('Login')}>
          <Text style={styles.buttonText}>로그인</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.button, {backgroundColor: '#ecc78fff',}]}
          onPress={()=>navigation.navigate('SignUp')}>
          <Text style={styles.buttonText}>회원가입</Text>
        </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: "#fff8eb"
  },
  button: {
    paddingVertical: 10,
    width: 350,
    alignItems:'center',
    marginBottom: 30,
    borderRadius: 5,
  },
  buttonText: {
    fontSize: 21,
    color: '#ffffffff',
    fontWeight:'bold',
  },
  loadingContainer: { 
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    color: '#444',
  },
  image:{
    width: '120%',
    height: 100,
    top: -60,
  }
  
});

export default App;