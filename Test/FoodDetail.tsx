import React from "react";
import { StyleSheet, Text, View, ScrollView } from "react-native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { RootStackParamList } from "./types";

//임시 데이터
type FoodDetailData = {
  foodname: string;
  edible: boolean;
  description: string;
  cautions: string;
}

const FOODS_DATA_DETAILS: { [key: string]: FoodDetailData} = {
  '1': {
    foodname: '김치찌개',
    edible: true,
    description: '김치찌개 설명--------------------',
    cautions: '김치찌개 주의사항-------------------'
  },
  '2': {
    foodname: '김치',
    edible: false,
    description: '김치 설명--------------------',
    cautions: '김치 주의사항--------------------',
  },
  '3': {
    foodname: '돼지고기',
    edible: true,
    description: '돼지고기 설명--------------------',
    cautions: '돼지고기 주의사항---------------------------',
  },
  '4': {
    foodname: '소고기',
    edible: false,
    description: '소고기 설명----------------------',
    cautions: '소고기 주의사항---------------------',
  },
  '5': {
    foodname: '배추',
    edible: false,
    description: '배추----------------------',
    cautions: '배추---------------------',
  },
  '6': {
    foodname: '양상추',
    edible: false,
    description: '양상추 설명----------------------',
    cautions: '양상추 주의사항---------------------',
  },
  '7': {
    foodname: '멸치 조림',
    edible: true,
    description: '멸치 조림 설명----------------------',
    cautions: '멸치 조림 주의사항---------------------',
  },
  '8': {
    foodname: '장조림',
    edible: false,
    description: '장조림 설명----------------------',
    cautions: '장조림 주의사항---------------------',
  }
};


type FoodDetailProps = NativeStackScreenProps<RootStackParamList, 'FoodDetail'>;

function FoodDetail({route}: FoodDetailProps) {
    
    const {foodId} = route.params;

    const foodData = FOODS_DATA_DETAILS[foodId];


    if (!foodData) {
        return (
            <View style={styles.container}>
                <Text style={styles.errorText}>상세 정보를 찾을 수 없습니다. (ID: {foodId}) </Text>
            </View>
        )
    }

    return (
        <ScrollView style={styles.container}>

            <Text style={styles.title}>{foodData.foodname}</Text>


            <View style={styles.infoBox}>
                <Text style={styles.infoTitle}>섭취 가능 여부</Text>
                <Text style={foodData.edible ? styles.safeText : styles.warningText}>
                    {foodData.edible ? '안전 (섭취 가능)' : '주의 필요'}
                </Text>
            </View>


            <View style={styles.infoBox}>
                <Text style={styles.infoTitle}>설명</Text>
                <Text style={styles.infoContent}>{foodData.description}</Text>
            </View>

            <View style={styles.infoBox}>
                <Text style={styles.infoTitle}>주의사항</Text>
                <Text style={styles.infoContent}>{foodData.cautions}</Text>
            </View>
        </ScrollView>
    );
}



const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff',
    padding: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  infoBox: {
    marginBottom: 25,
  },
  infoTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#555',
    marginBottom: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    paddingBottom: 5,
  },
  infoContent: {
    fontSize: 16,
    lineHeight: 24,
    color: '#333',
  },
  safeText: {
    fontSize: 18,
    color: 'green',
    fontWeight: 'bold',
  },
  warningText: {
    fontSize: 18,
    color: 'red',
    fontWeight: 'bold',
  },
  errorText: {
    fontSize: 16,
    color: 'red',
    textAlign: 'center',
    marginTop: 20,
  },
});

export default FoodDetail;