import React, { use } from "react";
import { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, } from 'react-native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from './types';
import { useNavigation } from "@react-navigation/native";

import Maps from './Maps';

function Login() {
  //아이디 비번 변수
  const[values, setValues] = useState({
    userid: "",
    password: "",
  });

  //비번 보이게
  const[seePw, setSeePw] = useState(true)

  const seePwHandler = ()=>{
    setSeePw(!seePw)
  }

  //id, password 입력했는가?
  const[touched, setTouched] = useState({
    userid: false,
    password: false,
  });

  const handleChangeText=(name: string, text: string)=>{
    setValues({
      ...values,
      [name]:text,
    });
  };

  const handleBlur=(name:string)=>{
    setTouched({
      ...touched,
      [name]: true,
    });
  };

  type LoginScreenNavigationProp = NativeStackNavigationProp<
    RootStackParamList,
    'Login'
  >;

  const navigation = useNavigation<LoginScreenNavigationProp>();

  return(
    <View style={styles.container}>
      <TextInput
        style={[styles.input, {marginTop:60}]}
        onChangeText={(text)=>handleChangeText('userid',text)}
        value={values.userid}
        placeholder="아이디"
        autoCapitalize="none"
        onBlur={()=>handleBlur("userid")}
      />
      <TextInput
        style={styles.input}
        onChangeText={(text)=>handleChangeText('password',text)}
        value={values.password}
        secureTextEntry={seePw}
        placeholder="비밀번호"
        autoCapitalize="none"
        onBlur={()=>handleBlur("password")}
      />
      <TouchableOpacity
        style={styles.button}
        onPress={()=>navigation.reset({index:0, routes:[{name:'NaviBar'}]})}>
        <Text style={styles.buttonText}>Login</Text>
      </TouchableOpacity>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
  },
  block:{
    padding:16,
    backgroundColor: '#ffffffff'
  },
  input:{
    width:350,
    height:45,
    borderColor: '#444444ff',
    borderWidth: 1,
    paddingLeft:10,
    marginBottom:20,
  },
  button: {
    backgroundColor: '#ffd000ff',
    paddingVertical: 10,
    paddingHorizontal: 151,
    marginBottom: 20
  },
  buttonText: {
    fontSize: 20,
    color: '#ffffffff'
  },
  errorText: {
    fontSize: 10,
    color: '#ff0000ff',
    textAlign: 'left',
  }
});

export default Login;