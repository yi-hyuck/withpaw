/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
//import { NewAppScreen } from '@react-native/new-app-screen';
//import { useState } from 'react';
import { StatusBar, StyleSheet, useColorScheme, View, Text, Touchable, TouchableOpacity} from 'react-native';
import { SafeAreaProvider, useSafeAreaInsets} from 'react-native-safe-area-context';
import { NavigationContainer, useNavigation } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from './types';
//import AsyncStorage from '@react-native-async-storage/async-storage';

import Login from './Login';
import SignUp_Screen from './SignUp';
import Maps from './Maps';
import Foods from './Foods';
import FoodDetail from './FoodDetail';

const Stack = createNativeStackNavigator<RootStackParamList>();

type HomeScreenNavigationProp = NativeStackNavigationProp<
  RootStackParamList,
  'Home'
>;

type Props = {
  navigation: HomeScreenNavigationProp;
};

function App() {
  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <Stack.Navigator>
          <Stack.Screen name="Home" component={HomeScreen} options={{headerShown: false}}/>
          <Stack.Screen name="Login" component={Login}/>
          <Stack.Screen name="SignUp" component={SignUp_Screen} options={{headerShown: false}}/>
          <Stack.Screen name="Maps" component={Maps} options={{headerShown: false}}/>
          <Stack.Screen name="Foods" component={Foods} options={{headerShown: false}}/>
          <Stack.Screen name="FoodDetail" component={FoodDetail} options={{headerShown: false}}/>
        </Stack.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
}

//실제 홈 화면
function HomeScreen({navigation}: Props) {
  const safeAreaInsets = useSafeAreaInsets();

  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={[styles.button, {backgroundColor: '#ffc830ff',}]}
        onPress={()=>navigation.navigate('Login')}>
        <Text style={styles.buttonText}>로그인</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={[styles.button, {backgroundColor: '#ffbb00ff',}]}
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
    backgroundColor: "#ffffff"
  },
  button: {
    paddingVertical: 10,
    width: 350,
    alignItems:'center',
    marginBottom: 30
  },
  buttonText: {
    fontSize: 20,
    color: '#ffffffff',
    fontWeight:'bold',
  },
  
});

export default App;
