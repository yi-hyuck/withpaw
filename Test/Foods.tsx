import React, { use, useEffect, useState } from "react";
import { StyleSheet, Text, View, TextInput, FlatList, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp, createNativeStackNavigator } from "@react-navigation/native-stack";
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import { createDrawerNavigator, DrawerNavigationProp } from '@react-navigation/drawer'
import type { RootStackParamList } from "./types";

import FoodDetail from './FoodDetail';

//임시 타입 지정
type FoodItem = {
  id: string;
  name: string;
}

//임시 데이터
const ALL_FOODS_DATA = [
  { id: '1', name: '김치찌개' },
  { id: '2', name: '김치' },
  { id: '3', name: '돼지고기' },
  { id: '4', name: '소고기' },
  { id: '5', name: '배추' },
  { id: '6', name: '양상추' },
  { id: '7', name: '멸치 조림' },
  { id: '8', name: '장조림' },
];

//헤더바
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

//드로어바 
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

//음식 화면
function FoodsScreen(){
  return(
  <Stack.Navigator>
    <Stack.Screen name="Foods" component={Foods}
                    options={({route})=>({
                      headerShown: true,
                      headerStyle:HEADER_STYLE,
                      headerTitle:(props)=>(
                        <CustomTitle {...props} title="음식 검색"/>
                      ),
                      headerRight: ()=> <DrawerButton/>
                    })}/>
    <Stack.Screen
      name="FoodDetail" 
      component={FoodDetail}
      options={{
        headerStyle:HEADER_STYLE,
        headerTitle: "음식 정보",
        headerTitleStyle: {fontSize:20, fontWeight:'bold', color:'#000000'}
      }}
    />
  </Stack.Navigator>
  )


}



function Foods() {

  const navigation = useNavigation<FoodsProps>();

  const [searchText, setSearchText] = useState('');

  const [filteredFoodList, setFilteredFoodList] = useState<FoodItem[]>([]);

// 검색 필터링
  useEffect(() => {

      if(searchText.trim() === '') {

        setFilteredFoodList([]);

      }
      else {

        const filteredResults = ALL_FOODS_DATA.filter(food => food.name.toLowerCase().includes(searchText.toLowerCase()));
        setFilteredFoodList(filteredResults);

      }

      }, [searchText]);

      // 음식 클릭시 이동 함수
      const handlePressFood = (item: FoodItem) => {
        navigation.navigate('FoodDetail', {foodId: item.id});
      };

      const renderFoodItem = ({item}: {item: FoodItem}) => (
        
        <TouchableOpacity style={styles.itemContainer}
        onPress={() => handlePressFood(item)}>
          <Text style={styles.itemText}>{item.name}</Text>
        </TouchableOpacity>
      );
  

  return(
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        placeholder="음식 검색"
        value={searchText}
        onChangeText={setSearchText}
      />
      
      {/*검색 결과 표시*/}
      <FlatList
        data={filteredFoodList}
        renderItem={renderFoodItem}
        keyExtractor={(item) => item.id}
        style={styles.list}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor:'#ffffff',
  },
  input:{
    width:350,
    height:45,
    borderColor: '#444444ff',
    backgroundColor: '#ffffffff',
    borderWidth: 1,
    paddingLeft:10,
    marginBottom:10, // 목록과 검색창 간격
    marginTop: 15,
    borderRadius: 10,
  },
  list: {
    width: 350,
  },
  itemContainer: {
    paddingVertical: 15,
    paddingHorizontal: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#eeeeee',
  },
  itemText: {
    fontSize: 16,
  },
  headerTitle:{
    fontSize:20,
    fontWeight:'bold',
    color:'#000000',
  },
});

export default FoodsScreen;