import React, { use } from "react";
import { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, } from 'react-native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from './types';
import { useNavigation } from "@react-navigation/native";
import { useMember } from "./MemberProvider";
import axios from "axios";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useAuth } from "./useAuth";

import Maps from './Maps';


function Login() {
  const {saveToken} = useAuth();
  const {fetchMemberInfo} = useMember();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

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
    setError(null);
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

  const loginHandler = async () => {
    console.log("Login button pressed");
    if(loading) return;

    if(!values.userid || !values.password){
      setError("아이디와 비밀번호를 입력해주세요.");
      return;
    }

    setLoading(true);
    setError(null);

    // const payload = {
    //   username: values.userid, 
    //   password: values.password,
    // };

    // console.log("RN Sending Payload:", payload);

    try{
      const response = await axios.post('http://10.0.2.2:8090/member/login',
      {
        loginId: values.userid,
        password: values.password,
      },
      {
        headers: {
        'Content-Type': 'application/json',
        },
      });

      if(response.status == 200 && response.data.token){
        const token = response.data.token;

        await AsyncStorage.setItem('userToken', token);
        await saveToken(token);
        await fetchMemberInfo(token);
        navigation.reset({index:0, routes:[{name:'NaviBar'}]});
        
        // navigation.navigate('NaviBar');
      }
    } catch (err) {
      setError("로그인 중 오류가 발생했습니다.")
    } finally {
      setLoading(false);
    }
  }

  return(
    <View style={[styles.container]}>
      <Text style={[styles.login]}>로그인</Text>
      <TextInput
        style={[styles.input, {marginTop:20}]}
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
      {error && <Text style={styles.errorText}>{error}</Text>}
      <TouchableOpacity
        style={[styles.button, {marginTop: 30}]}
        onPress={loginHandler}>
        <Text style={styles.buttonText}>Login</Text>
      </TouchableOpacity>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#fff3dcff',
  },
  block:{
    padding:16,
    backgroundColor: '#ffffffff'
  },
  input:{
    width:350,
    height:45,
    borderColor: '#a15b00ff',
    borderWidth: 1,
    paddingLeft:20,
    marginBottom:20,
    borderRadius: 15,
    backgroundColor: '#fffaf0ff',
  },
  button: {
    backgroundColor: '#ffc164ff',
    paddingVertical: 10,
    paddingHorizontal: 151,
    marginBottom: 20,
    borderRadius: 15,
  },
  buttonText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#ffffffff'
  },
  errorText: {
    fontSize: 10,
    color: '#ff0000ff',
    textAlign: 'left',
  },
  login:{
    fontWeight: 'bold',
    fontSize: 25,
    color: '#7e4700ff',
  }
});

export default Login;