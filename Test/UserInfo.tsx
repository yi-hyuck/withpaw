import React, { useCallback, useEffect, useState } from 'react';
import { StatusBar, StyleSheet, useColorScheme, View, Text, Touchable, TouchableOpacity, Alert} from 'react-native';
import { RootStackParamList } from './types';
import { createNativeStackNavigator, NativeStackNavigationProp, NativeStackScreenProps } from '@react-navigation/native-stack';
import { useNavigation, NavigationProp } from '@react-navigation/native';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import { useForm, Controller } from "react-hook-form";
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view';
import { TextInput, } from "react-native-paper";
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useMember } from './MemberProvider';
import { useAuth } from './useAuth';

const API_URL = 'http://10.0.2.2:8090';

const editMemberInfoApi = async (payload: any) => {
    try{
        const token = await AsyncStorage.getItem('userToken');

        if(!token){
            console.error("토큰 없음");
            return null;
        }

        const response = await axios.post(`${API_URL}/member/update`, payload, {
            headers: {
                'Authorization' : `Bearer ${token}`,
                'Content-Type': 'application/json',
            }
        });
        return response.data;
    } catch (error){
        console.error("오류");
        return null;
    }
}

// 임시데이터
// interface userType{
//     id: string;
//     email: string;
//     password: string;
// }

interface PetDto{
    petId: number;
    name: string,
    breed: string;
    gender: string;
    birthDate: string;
    neuter: boolean;
    weight: number;
}

interface userType {
    loginId: string;
    email: string;
    password: string;
    pets: PetDto[];
}

// const userItem: userType = {
//     id: 'asdf1234',
//     email: 'asdf1234@email.com',
//     password: '1234',
// }

const Stack = createNativeStackNavigator<RootStackParamList>();
type userInfoScreenProps = NativeStackScreenProps<RootStackParamList, 'UserInfo'>
type userEditScreenProps = NativeStackScreenProps<RootStackParamList, 'UserEdit'>;

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

//정보 화면 (스텍)
function UserInfoScreen(){
    return (
        <Stack.Navigator>
            <Stack.Screen name="UserInfo" component={UserInfo}
                            options={()=>({
                            headerShown: true,
                            headerStyle:HEADER_STYLE,
                            headerTitle:(props)=>(
                                <CustomTitle {...props} title="회원 정보"/>
                            ),
                            })}/>
            <Stack.Screen name="UserEdit" component={UserEdit}
                            options={()=>({
                            headerShown: true,
                            headerStyle:HEADER_STYLE,
                            headerTitle:(props)=>(
                                <CustomTitle {...props} title="회원 정보 수정"/>
                            ),
                            })}/>
        </Stack.Navigator>
    )
}


function UserEdit({navigation, route}: userEditScreenProps){
    const {currentData, onSave} = route.params;

    const defaultFormValues = {
        userid: currentData?.loginId,
        email: currentData?.email,
        password: currentData?.password,
        newPasswordChk: '',
        newPassword: '',
    };

    const {control, handleSubmit, formState: {errors}, setValue, watch} = useForm({
        defaultValues: defaultFormValues
    });

    const newPasswordValue = watch('newPassword');

    const [seePw, setSeePw] = useState(false);
    const [seePw2, setSeePw2] = useState(false);
    const [seePwChk, setSeePwChk] = useState(false);



    //폼 제출
    const onSubmit = async (data:any) => {
        const newPassword = data.newPassword.trim();
        // const finalPassword = newPassword.length>0 ? newPassword : currentData.password;

        const updateData = {
            email: data.email,
            currentPassword: data.password,
            newPassword: data.newPassword.trim(),
            newPasswordConfirm: data.new
        }

        const result = await editMemberInfoApi(updateData);

        if(result && result.error){
            const errors = result.data;
            if (errors.email) {
                Alert.alert("수정 실패", `이메일 오류: ${errors.email}`);
            } else if (errors.currentPassword) {
                Alert.alert("수정 실패", `비밀번호 오류: ${errors.currentPassword}`);
            } else if (errors.newPasswordConfirm) {
                Alert.alert("수정 실패", `비밀번호 확인 오류: ${errors.newPasswordConfirm}`);
            } else {
                Alert.alert("수정 실패", errors.message || "회원 정보 수정 중 오류가 발생했습니다.");
            }
        } else if(result){
            onSave();
            navigation.goBack();
        }
    }


    return(
        <KeyboardAwareScrollView style={{backgroundColor:'white'}}>
            <Text style={[styles.title, {marginTop:30}]}>아이디</Text>
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
                        style={[styles.input2, {backgroundColor:'#e6e6e6ff'}]}
                        onBlur={onBlur}
                        value={value}
                        onChangeText={(value)=>onChange(value)}
                        autoCapitalize="none"
                        editable={false}
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
                        style={[styles.input2, {backgroundColor:'#e6e6e6ff'}]}
                        onBlur={onBlur}
                        value={value}
                        onChangeText={(value)=>onChange(value)}
                        autoCapitalize="none"
                        editable={false}
                        secureTextEntry={!seePw}
                        right={
                        <TextInput.Icon icon={seePw ? "eye-off" : "eye"}
                                        onPress={()=> setSeePw(!seePw)}
                                        forceTextInputFocus={false}/>}
                    />
                    </View>
                )}
                />
            </View>
            <Text style={[styles.title, {marginTop:20}]}>새 비밀번호</Text>
            <View style={styles.container}>
                <Controller
                control={control}
                name="newPassword"
                render={({field: {onChange, value, onBlur}}) => (
                    <View>
                    <TextInput
                        placeholder="새 비밀번호 입력"
                        mode="outlined"
                        outlineStyle={styles.inputOutline}
                        style={styles.input}
                        onBlur={onBlur}
                        value={value}
                        onChangeText={(value)=>onChange(value)}
                        autoCapitalize="none"
                        secureTextEntry={!seePw2}
                        right={
                        <TextInput.Icon icon={seePw2 ? "eye-off" : "eye"}
                                        onPress={()=> setSeePw2(!seePw2)}
                                        forceTextInputFocus={false}/>}
                    />
                    {errors?.newPassword?.message && <Text style={styles.error}>{String(errors.newPassword.message)}</Text>}
                    </View>
                )}
                rules={{
                    validate: (value) => {
                        if(!value || value.trim().length === 0){
                            return true;
                        }
                        const pattern = /.{4,}/;
                        if(!pattern.test(value)){
                            return "비밀번호는 4자 이상이어야 합니다.";
                        }
                        return true;
                    }
                }}
                />
            </View>
            <Text style={[styles.title, {marginTop:20}]}>새 비밀번호 확인</Text>
            <View style={styles.container}>
                <Controller
                control={control}
                name="newPasswordChk"
                render={({field: {onChange, value, onBlur}}) => (
                    <View>
                    <TextInput
                        placeholder="새 비밀번호 확인"
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
                    {errors?.newPasswordChk?.message && <Text style={styles.error}>{String(errors.newPasswordChk.message)}</Text>}
                    </View>
                )}
                rules={{
                    validate: (value) => {
                        const newPw = (String(newPasswordValue || '')).trim();
                        const chkPw = (String(value || '')).trim();

                        if (newPw.length > 0) {
                            if (chkPw.length === 0) {
                                return '새 비밀번호를 확인해주세요.';
                            }
                            if (chkPw !== newPw) {
                                return '새 비밀번호가 일치하지 않습니다.';
                            }
                        }
                        return true;
                    }
                }}
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
            </View>
            <View style={styles.container}>
            <TouchableOpacity style={[styles.editButton, {backgroundColor: '#ffc400ff'}]} onPress={handleSubmit(onSubmit)}>
                <Text style={styles.editButtonText}>수정 완료</Text>
            </TouchableOpacity>
            </View>
        </KeyboardAwareScrollView>
    )
}


//관리 화면
function UserInfo({navigation}: userInfoScreenProps){
    const {memberInfo: userData, isLoading, fetchMemberInfo} = useMember();
    const {removeToken} = useAuth();

    //백엔드 api 호출
    const handleSave = useCallback( async () => {
        const token = await AsyncStorage.getItem('userToken');

        if(!token){
            console.error("정보 없음");
            return null;
        }

        fetchMemberInfo(token);

    }, [fetchMemberInfo])

    const UserDelete = () => {
        Alert.alert(
            '탈퇴', `회원 탈퇴를 진행하시겠습니까`,
            [
                {text: '취소'},
                {text: '삭제',
                    onPress : async () =>{
                        try{
                            const token = await AsyncStorage.getItem('userToken');
                            await axios.post(`${API_URL}/member/delete`, {}, {
                                headers: {'Authorization' : `Bearer ${token}`}
                            });

                            await removeToken();

                            const rootStack = navigation.getParent()?.getParent();

                            if(rootStack && rootStack.reset){
                            rootStack.reset({
                                index: 0,
                                routes: [{name: 'Home'}]
                            })
                            }
                        } catch(error){
                            console.error("탈퇴 오류");
                        }


                    }
                }
            ]
        )
    }

    return(
        <View style={styles.container}>
            <MaterialCommunityIcons
                style={{marginTop: 30}}
                name='account-circle'
                size={120}
                color='#b9b9b9fb'
            />
            <Text style={styles.textStyle}>{userData?.loginId}</Text>
            <Text style={styles.textStyle2}>{userData?.email}</Text>
            <TouchableOpacity style={[styles.button, {marginTop: 40}]} 
                onPress={()=>{
                    if(userData) {
                        navigation.navigate('UserEdit', {currentData: {loginId: userData.loginId, email: userData.email, password:userData?.password}, onSave: handleSave});
                    } else{
                        console.error("회원 정보 불러오는 중")
                    }}}
                    disabled={!userData}>
                <Text style={styles.buttonText}>
                    <Text>회원정보 수정</Text>
                </Text>
            </TouchableOpacity>
            <TouchableOpacity style={[styles.button]} onPress={UserDelete}>
                <Text style={[styles.buttonText, {color:'red'}]}>
                    <Text>회원 탈퇴</Text>
                </Text>
            </TouchableOpacity>
        </View>
    )
}


const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    backgroundColor:'#ffffffff',
  },
  headerTitle:{
    fontSize:20,
    fontWeight:'bold',
    color:'#000000',
  },
  textStyle:{
    fontSize: 25,
    fontWeight: 'bold',
    color: '#000000ff',
    marginTop: 5,
  },
  textStyle2:{
    fontSize: 15,
    color: '#9b9b9bff',
    marginTop: 5,
    marginBottom: 20,
  },
  button:{
    backgroundColor: '#ffffffff',
    marginBottom: 15,
    borderColor: '#c5c5c5ff',
    marginHorizontal: 25,
    alignItems: 'center',
    textAlign: 'center',
  },
  buttonText:{
    fontSize: 16,
  },
  
  //수정
  input:{
    width:350,
    height:45,
    backgroundColor:'#ffffff',
    borderRadius: 8,
  },
  input2:{
    width:350,
    height:45,
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
  error:{
    color: '#ff0000ff'
  },
  editButton:{
    paddingVertical: 10,
    width: 350,
    alignItems:'center',
    borderRadius:10,
    justifyContent: 'center',
    marginTop: 30,
  },
  editButtonText:{
    fontSize: 20,
    color: '#ffffffff',
    fontWeight:'bold',
  }
});

export default UserInfoScreen;