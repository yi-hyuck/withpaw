import React from 'react';
import { StatusBar, StyleSheet, useColorScheme, View, Text, Touchable, TouchableOpacity} from 'react-native';
import { RootStackParamList } from './types';
import { createNativeStackNavigator, NativeStackNavigationProp } from '@react-navigation/native-stack';

const Stack = createNativeStackNavigator<RootStackParamList>();

type FoodsProps = NativeStackNavigationProp<RootStackParamList>;

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
function UserInfo(){
    return(
        <View style={styles.container}>
            <Text>회원 정보</Text>
        </View>
    )
}



const styles = StyleSheet.create({
  container:{
    backgroundColor:'#ffffffff',
  },
  headerTitle:{
    fontSize:20,
    fontWeight:'bold',
    color:'#000000',
  },
});

export default UserInfoScreen;