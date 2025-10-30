import React, { use } from "react";
import { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity,} from 'react-native';
import { createNativeStackNavigator, NativeStackNavigationProp } from "@react-navigation/native-stack";
import { NavigationContainer, useNavigation } from "@react-navigation/native";
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import { createDrawerNavigator, DrawerNavigationProp } from '@react-navigation/drawer'
import { RootStackParamList } from "./types";

import DogNoteScreen from './DogNote';
import DogAi from './DogAi';

//스텍 화면 관련
const Stack = createNativeStackNavigator<RootStackParamList>();

interface HeaderTitleProps {
    children: string;
    tintColor?: string;
}

interface CustomTitleProps {
    title: string;
}

const CustomTitle = ({ title }:CustomTitleProps) => {
    return (
        <Text style={[styles.headerTitle, { paddingLeft: 5 }]}>
            {title}
        </Text>
    );
};

const HEADER_STYLE = {
    height: 55,
    backgroundColor: '#ffd651ff',
};

//드로어바 관련
const Drawer = createDrawerNavigator();

type NavigationProps = DrawerNavigationProp<RootStackParamList>;

const DrawerButton = () => {
  const navigation = useNavigation<NavigationProps>();

  return(
    <TouchableOpacity
      onPress={()=>navigation.openDrawer()}
      style={{marginRight: 0}}
    >
      <MaterialCommunityIcons name='account-circle' size={30} color={"#ffffffff"}/>
    </TouchableOpacity>
  )
}

function Disease() {
  return(
    <Stack.Navigator>
      <Stack.Screen name='Disease'  component={DiseaseButton} 
                    options={({route})=>({
                      headerShown: true,
                      headerStyle:HEADER_STYLE,
                      headerTitle:(props)=>(
                        <CustomTitle {...props} title="질병 관리"/>
                      ),
                      headerRight: ()=> <DrawerButton/>,
                    })}
        />
      <Stack.Screen name="DogNoteScreen" component={DogNoteScreen} options={{headerShown:false}}/>
      <Stack.Screen name="DogAi" component={DogAi} options={{title: 'Ai 진단', headerStyle: {backgroundColor: '#ffd651ff'}, headerTintColor: '#000'}}/>
    </Stack.Navigator>
  )
}

//실제 질병 관리 화면
function DiseaseButton(){
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();

  return(
    <View style={styles.container}>
      <TouchableOpacity 
        style={[styles.button, {height: 110,}]}
        onPress={()=>navigation.navigate('DogNoteScreen')}>
        <Text style={[styles.buttonText, {paddingLeft:70}]}>증상 작성</Text>
      </TouchableOpacity>
      <TouchableOpacity 
        style={[styles.button,{height: 112,}]}
        onPress={()=>navigation.navigate('DogAi')}>
        <Text style={[styles.buttonText, {paddingLeft:70}]}>AI 진단</Text>
      </TouchableOpacity>
    </View>
  )
}



const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor:'#ffffff',
  },
  block:{
    padding:16,
    backgroundColor: '#ffffffff'
  },
  button: {
    backgroundColor: '#ffffffff',
    borderColor:'#a8a8a8ff',
    borderBottomWidth:2,
    width: 500,
    justifyContent: 'center',
  },
  buttonText: {
    fontSize: 20,
    color: '#000000ff'

  },
  headerTitle:{
    fontSize:20,
    fontWeight:'bold',
    color:'#000000'
  }
});

export default Disease;