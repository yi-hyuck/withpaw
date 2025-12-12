import React, { use, useEffect, useState } from "react";
import { StyleSheet, Text, View, TextInput, FlatList, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp, createNativeStackNavigator } from "@react-navigation/native-stack";
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import { createDrawerNavigator, DrawerNavigationProp } from '@react-navigation/drawer'
import type { RootStackParamList } from "./types";
import axios from 'axios';

import FoodDetail from './FoodDetail';

type FoodItem = {
  id: number;
  name: string;

}

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
    backgroundColor: '#ffcb7dff',
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
        headerShadowVisible: false,
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

  const [allFoods, setAllFoods] = useState<FoodItem[]>([]);
  const [filteredFoodList, setFilteredFoodList] = useState<FoodItem[]>([]);

// 데이터 가져오기
  useEffect(() => {
    fetchFoods();
  }, []);

  const fetchFoods = async () => {
    try {
      const response = await axios.get('http://10.0.2.2:8090/api/foods');
      setAllFoods(response.data);
      setFilteredFoodList(response.data);
    } catch (error) {
      console.error("데이터 가져오기 실패:", error);
    }
  }


// 검색 필터링
  useEffect(() => {
    if (searchText.trim() === '') {
      setFilteredFoodList(allFoods); // 검색어 없으면 전체 목록

    } else {
      const filteredResults = allFoods.filter(food => 
        food.name.toLowerCase().includes(searchText.toLowerCase())
      );
      setFilteredFoodList(filteredResults);
    }
  }, [searchText, allFoods]);

  const handlePressFood = (item: FoodItem) => {
    // ID를 문자열로 변환해서 전달
    navigation.navigate('FoodDetail', { foodId: String(item.id) });
  };

      const renderFoodItem = ({item}: {item: FoodItem}) => (
        
        <TouchableOpacity style={styles.itemContainer}
        onPress={() => handlePressFood(item)}>
          <Text style={styles.itemText}>{item.name}</Text>
        </TouchableOpacity>
      );
  

  return(
    <View style={styles.container}>
      <View style={styles.inputContainer}>
        <TextInput
          style={styles.input}
          placeholder="음식 검색"
          value={searchText}
          onChangeText={setSearchText}
        />
        <MaterialCommunityIcons name='magnify' size={26} color={'#9e9e9eff'} style={[{paddingRight:12}]} />
      </View>
      <Text style={styles.listText}>목록</Text>
      {/*검색 결과 표시*/}
      <FlatList
        data={filteredFoodList}
        renderItem={renderFoodItem}
        keyExtractor={(item) => String(item.id)}
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
  inputContainer:{
    flexDirection: 'row',
    alignItems: 'center',
    width:350,
    height:45,
    borderColor: '#e46600ff',
    backgroundColor: '#ffffffff',
    borderWidth: 2,
    paddingLeft:15,
    marginBottom:10,
    marginTop: 15,
    borderRadius: 20,
  },
  input:{
    flex: 1,
    height: '100%',
    fontSize: 16,
  },
  list: {
    width: 350,
  },
  itemContainer: {
    paddingVertical: 20,
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
  listText:{
    fontSize: 17,
    fontWeight:'bold',
    paddingTop: 10,
    width:'100%',
    paddingLeft: 30,
    paddingBottom: 15,
  }
});

export default FoodsScreen;