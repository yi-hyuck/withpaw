import React from 'react';
import { StatusBar, StyleSheet, useColorScheme, View, Text, Touchable, TouchableOpacity} from 'react-native';
import { createNativeStackNavigator, NativeStackNavigationProp } from '@react-navigation/native-stack';
import { FlatList, GestureHandlerRootView } from 'react-native-gesture-handler';
import { NavigationContainer } from '@react-navigation/native';

import { RootStackParamList } from './types';

//헤더 바 관련
const Stack = createNativeStackNavigator<RootStackParamList>();

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
function DogMgmtScreen(){
    return (
        <GestureHandlerRootView style={{flex:1}}>
            <Stack.Navigator>
                <Stack.Screen name="DogManagement" component={DogManagement}
                                options={({route})=>({
                                headerShown: true,
                                headerStyle:HEADER_STYLE,
                                headerTitle:(props)=>(
                                    <CustomTitle {...props} title="반려동물 정보"/>
                                ),
                                })}/>
            </Stack.Navigator>
        </GestureHandlerRootView>
    )
}


//임시 데이터
interface DogItem{
    id: string;
    name: string;
    age: number;
    breed: string;
}

const DogData: DogItem[] = [
    {
        id: 'd1',
        name: '몽실이',
        age: 10,
        breed: '말티즈',
    },
    {
        id: 'd2',
        name: '너티',
        age: 6,
        breed: '테리어',
    },
    {
        id: 'd3',
        name: '메리',
        age: 1,
        breed: '시고르자브종' 
    }
];


//강아지 카드
const DogCard = ({name, age, breed}:DogItem)=>(
    <View style={styles.CardContent}>
        <Text style={styles.cardTitle}>{name}</Text>
        <Text style={styles.cardData}>{age}살</Text>
        <Text style={styles.cardData}>{breed}</Text>
    </View>
)

//관리 화면
function DogManagement(){
    return(
        <View style={styles.container}>
            <FlatList
                data={DogData}
                renderItem={({item}) => <DogCard {...item}/>}
                keyExtractor={(item) => item.id}
                contentContainerStyle={styles.listPadding}
            />
        </View>
    )
}



const styles = StyleSheet.create({
  container:{
    flex: 1,
    backgroundColor:'#ffffffff',
  },
  listPadding: {
        paddingVertical: 10,
  },
  headerTitle:{
    fontSize:20,
    fontWeight:'bold',
    color:'#000000',
  },
  Title:{
    fontSize: 16,
    fontWeight: 'bold',
  },
  CardContainer:{
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#ffffffff',
    borderColor: '#acacacff',
    borderWidth: 2,
    borderRadius: 10,
    marginBottom: 5,
    marginHorizontal: 5,
    paddingRight: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  CardContent: {
    flex: 1,
    paddingVertical: 5,
  },
  cardTitle:{
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 15,
    marginBottom: 10,
    paddingLeft: 10,
  },
  cardData:{
    fontSize: 14,
    paddingLeft: 10,
    marginBottom: 15,
  },
});

export default DogMgmtScreen;