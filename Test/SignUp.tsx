import React, { use, useEffect } from "react";
import { useState } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, Button, } from 'react-native';
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { useForm, Controller } from "react-hook-form";
import { useNavigation } from "@react-navigation/native";
import { createNativeStackNavigator, NativeStackNavigationProp } from '@react-navigation/native-stack';
import { TextInput, Icon } from "react-native-paper";
import { RootStackParamList } from './types';
import axios from "axios";

import App from './App';
import DogInfo from './DogInfo';

const Stack = createNativeStackNavigator<RootStackParamList>();
const API_URL = "http://10.0.2.2:8090";

function SignUp_Screen(){
  return(
    <Stack.Navigator>
      <Stack.Screen name="SignUp" component={SignUp} options={{headerShown:false}}/>
      <Stack.Screen name="DogInfo" component={DogInfo} options={{headerShown:false}}/>
    </Stack.Navigator>
  )
}

type SignUpScreenNavigationProp = NativeStackNavigationProp<
  RootStackParamList,
  'SignUp'
>;

function SignUp (){
  const navigation = useNavigation<SignUpScreenNavigationProp>();

  const {control, handleSubmit, formState: {errors}, setError, watch} = useForm();

  const password = watch('password');

  const onSubmit = async (data:any)=>{
    if(data.password !== data.passwordChk){
      setError("passwordChk", {type:"manual", message: "비밀번호가 일치하지 않습니다."})
      return;
    }

    const step1Data = {
      loginId: data.userid,
      password: data.password,
      email: data.email
    }

    try{
      const response = await axios.post(`${API_URL}/member/signup`, step1Data);
      navigation.navigate('DogInfo', {userData: step1Data});
    } catch (error: any) {
      console.error('Signup Step 1 Error:', error.response?.data || error.message);
        
        const errorData = error.response?.data;
        if (errorData) {
            // 백엔드에서 받은 필드별 에러를 표시
            if (errorData.loginId) {
                setError("userid", { type: "server", message: errorData.loginId });
            }
            if (errorData.email) {
                setError("email", { type: "server", message: errorData.email });
            }
            if (errorData.password) {
                setError("password", { type: "server", message: errorData.password });
            }
            // 기타 비즈니스 로직 에러 (중복 등)
            if (errorData.field && errorData.error) {
                if (errorData.field === 'loginId') {
                    setError("userid", { type: "server", message: errorData.error });
                }
            }
        }
    }
  }

  //비밀번호 보이게
  const [seePw, setSeePw] = useState(false);
  const [seePwChk, setSeePwChk] = useState(false);

  return(
    <KeyboardAwareScrollView style={[{backgroundColor:'#fff3dcff'}]}>
      <View style={styles.container}>
        <Text style={[styles.title2]}>회원가입</Text>
      </View>
      <Text style={[styles.title, {marginTop:60}]}>아이디</Text>
      <View style={styles.container}>
        <Controller
          control={control}
          name="userid"
          render={({field: {onChange, value, onBlur}}) => (
            <View>
              <TextInput
                placeholder="아이디 입력"
                mode="outlined"
                outlineStyle={styles.inputOutline}
                style={styles.input}
                onBlur={onBlur}
                value={value}
                onChangeText={(value)=>onChange(value)}
                autoCapitalize="none"
              />
              {errors?.userid?.message && <Text style={styles.error}>{String(errors.userid.message)}</Text>}
          </View>
          )}
          rules={{required: '아이디를 입력해주세요.'}}
        />
      </View>
      <Text style={[styles.title, {marginTop:20}]}>비밀번호</Text>
      <View style={styles.container}>
        <Controller
          control={control}
          name="password"
          render={({field: {onChange, value, onBlur}}) => (
            <View>
              <TextInput
                placeholder="비밀번호 입력"
                mode="outlined"
                outlineStyle={styles.inputOutline}
                style={styles.input}
                onBlur={onBlur}
                value={value}
                onChangeText={(value)=>onChange(value)}
                autoCapitalize="none"
                secureTextEntry={!seePw}
                right={
                  <TextInput.Icon icon={seePw ? "eye-off" : "eye"}
                                  onPress={()=> setSeePw(!seePw)}
                                  forceTextInputFocus={false}/>}
              />
              {errors?.password?.message && <Text style={styles.error}>{String(errors.password.message)}</Text>}
            </View>
          )}
          rules={{
            validate: (value) => {
              const pattern = /.{4,}/;
              if(!pattern.test(value)){
                  return "비밀번호는 4자 이상이어야 합니다.";
              }
              return true;
            }
          }}
        />
      </View>
      <Text style={[styles.title, {marginTop:20}]}>비밀번호 확인</Text>
      <View style={styles.container}>
        <Controller
          control={control}
          name="passwordChk"
          render={({field: {onChange, value, onBlur}}) => (
            <View>
              <TextInput
                placeholder="비밀번호 확인"
                mode="outlined"
                outlineStyle={styles.inputOutline}
                style={styles.input}
                onBlur={onBlur}
                value={value}
                onChangeText={(value)=>onChange(value)}
                autoCapitalize="none"
                secureTextEntry={!seePwChk}
                right={
                  <TextInput.Icon icon={seePwChk ? "eye-off" : "eye"}
                                  onPress={()=> setSeePwChk(!seePwChk)}
                                  forceTextInputFocus={false}/>}
              />
              {errors?.passwordChk?.message && <Text style={styles.error}>{String(errors.passwordChk.message)}</Text>}
            </View>
          )}
          rules={{required: '비밀번호를 입력해주세요.'}}
        />
      </View>
      <Text style={[styles.title, {marginTop:20}]}>이메일</Text>
      <View style={styles.container}>
        <Controller
          control={control}
          name="email"
          render={({field: {onChange, value, onBlur}}) => (
            <View>
              <TextInput
                placeholder="이메일 입력"
                mode="outlined"
                outlineStyle={styles.inputOutline}
                style={styles.input}
                onBlur={onBlur}
                value={value}
                onChangeText={(value)=>onChange(value)}
                autoCapitalize="none"
                keyboardType="email-address"
              />
              {errors?.email?.message && <Text style={styles.error}>{String(errors.email.message)}</Text>}
            </View>
          )}
          rules={{
            required: '이메일을 입력해주세요.',
            pattern: {
              value:/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
              message: '이메일 형식으로 입력해주세요.'
            }
          }}
        />
        <View style={{marginTop:30}}>
          <Button title="다음" onPress={handleSubmit(onSubmit)}></Button>
        </View>
      </View>
    </KeyboardAwareScrollView>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
  },
  input:{
    width:350,
    height:45,
    backgroundColor:'#ffffff',
    borderRadius: 8,
    
  },
  inputOutline: {
    borderColor: '#444444ff',
    borderWidth: 1,
    borderRadius: 8,
    height: 45,
  },
  title:{
    fontSize: 16,
    textAlign: 'left',
    marginLeft: 30,
    fontWeight:'bold',
  },
  button:{
    backgroundColor: '#ffd000ff',
    paddingVertical: 10,
    paddingHorizontal: 151,
    marginBottom: 20
  },
  buttonText:{
    fontSize: 20,
    color: '#ffffffff'
  },
  error:{
    color: '#ff0000ff'
  },
  title2:{
    marginTop:50,
    fontWeight: 'bold',
    fontSize: 25,
    alignContent: 'center',
    justifyContent: 'center',
    color: '#7e4700ff',
    top: 30,
  }
});

export default SignUp_Screen;