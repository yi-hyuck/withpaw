import React from 'react';
import { StatusBar, StyleSheet, useColorScheme, View, Text, Touchable, TouchableOpacity} from 'react-native';
import { RootStackParamList } from './types';
import { createNativeStackNavigator, NativeStackNavigationProp, NativeStackScreenProps } from '@react-navigation/native-stack';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';

//임시데이터
interface userType{
    id: string;
    email: string;
    password: string;
}

const userItem: userType[] = [
    {
        id: 'asdf1234',
        email: 'asdf1234@email.com',
        password: '1234',
    }
]

const Stack = createNativeStackNavigator<RootStackParamList>();
type userInfoScreenProps = NativeStackScreenProps<RootStackParamList, 'UserInfo'>

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
                            options={({route})=>({
                            headerShown: true,
                            headerStyle:HEADER_STYLE,
                            headerTitle:(props)=>(
                                <CustomTitle {...props} title="회원 정보"/>
                            ),
                            })}/>
        </Stack.Navigator>
    )
}

//관리 화면
function UserInfo({navigation, route}: userInfoScreenProps){
    const userData = userItem[0];

    return(
        <View style={{backgroundColor:'#ffffffff', alignItems:'center'}}>
            <MaterialCommunityIcons
                style={{marginTop: 30}}
                name='account-circle'
                size={120}
                color='#b9b9b9fb'
            />
            <Text style={styles.textStyle}>{userData.id}</Text>
        </View>
    )
}



const styles = StyleSheet.create({
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
  }
});

export default UserInfoScreen;