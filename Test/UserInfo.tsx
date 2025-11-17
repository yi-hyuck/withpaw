import React, { useState } from 'react';
import { StatusBar, StyleSheet, useColorScheme, View, Text, Touchable, TouchableOpacity, Alert} from 'react-native';
import { RootStackParamList } from './types';
import { createNativeStackNavigator, NativeStackNavigationProp, NativeStackScreenProps } from '@react-navigation/native-stack';
import { useNavigation, NavigationProp } from '@react-navigation/native';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import { useForm, Controller } from "react-hook-form";
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view';
import { TextInput, } from "react-native-paper";

//임시데이터
interface userType{
    id: string;
    email: string;
    password: string;
}

const userItem: userType = {
    id: 'asdf1234',
    email: 'asdf1234@email.com',
    password: '1234',
}

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


function UserEdit({navigation, route}: userInfoScreenProps){
    const {currentData, onSave} = route.params;

    const defaultFormValues = {
        userid: currentData?.id,
        email: currentData?.email,
        password: currentData?.password,
        passwordChk: '',
    };

    const {control, handleSubmit, formState: {errors}, setValue, watch} = useForm({
        defaultValues: defaultFormValues
    });

    const [seePw, setSeePw] = useState(false);
    const [seePwChk, setSeePwChk] = useState(false);

    //폼 제출
    const onSubmit = (data:any) => {
        //api로 수정 (호출 -> 서버 저장)
        const updatedData: userType={
            id: data.userid,
            email: data.email,
            password: data.password,
        }

        onSave(updatedData);
        
        navigation.goBack();
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
                rules={{required: '비밀번호를 입력해주세요.'}}
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
                        placeholder="비밀번호 입력"
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
                rules={{required: '비밀번호를 확인해주세요.'}}
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
    const [userData, setUserData] = useState<userType>(userItem);

    const handleSave = (updatedData: userType) => {
        setUserData(updatedData);
    }

    const UserDelete = () => {
        Alert.alert(
            '탈퇴', `회원 탈퇴를 진행하시겠습니까`,
            [
                {text: '취소'},
                {text: '삭제',
                    onPress : () =>{
                        const rootStack = navigation.getParent()?.getParent();

                        if(rootStack && rootStack.reset){
                        rootStack.reset({
                            index: 0,
                            routes: [{name: 'Home'}]
                        })
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
            <Text style={styles.textStyle}>{userData.id}</Text>
            <Text style={styles.textStyle2}>{userData.email}</Text>
            <TouchableOpacity style={[styles.button, {marginTop: 40}]} onPress={()=>navigation.navigate('UserEdit', {currentData: userData, onSave: handleSave})}>
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