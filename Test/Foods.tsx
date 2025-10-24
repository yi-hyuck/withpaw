import React, { use, useEffect, useState } from "react";
import { StyleSheet, Text, View, TextInput, FlatList, TouchableOpacity } from 'react-native';

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



function Foods() {

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

      const renderFoodItem = ({item}: {item: FoodItem}) => (
        
        <TouchableOpacity style={styles.itemContainer}>
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
  }
});

export default Foods;