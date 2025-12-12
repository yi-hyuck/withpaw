import React, { useEffect, useState } from "react";
import { StyleSheet, Text, View, ScrollView, ActivityIndicator } from "react-native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { RootStackParamList } from "./types";
import axios from "axios";

type FoodDetailData = {
  id : number;
  name: string;
  edible: boolean;
  description: string;
  cautions: string;
}


type FoodDetailProps = NativeStackScreenProps<RootStackParamList, 'FoodDetail'>;

function FoodDetail({ route }: FoodDetailProps) {
    const { foodId } = route.params;
    
    const [foodData, setFoodData] = useState<FoodDetailData | null>(null);
    const [loading, setLoading] = useState(true); // 로딩 상태

    useEffect(() => {
        fetchFoodDetail();
    }, [foodId]);

    const fetchFoodDetail = async () => {
        try {
            // 백엔드 요청
            const response = await axios.get(`http://10.0.2.2:8090/api/foods/${foodId}`);
            setFoodData(response.data);
        } catch (error) {
            console.error("상세 정보 가져오기 실패:", error);
        } finally {
            setLoading(false);
        }
    };

    // 로딩 중
    if (loading) {
        return (
            <View style={styles.container}>
                <ActivityIndicator size="large" color="#ffd651ff" />
            </View>
        );
    }

    // 데이터가 없을 때
    if (!foodData) {
        return (
            <View style={styles.container}>
                <Text style={styles.errorText}>상세 정보를 찾을 수 없습니다.</Text>
            </View>
        )
    }

    return (
        <ScrollView style={styles.container}>
          <View style={styles.titleContainer}>
            <Text style={styles.title}>{foodData.name}</Text>
          </View>
            <View style={styles.infoBox}>
                <Text style={foodData.edible ? styles.safeText : styles.warningText}>
                    {foodData.edible ? '안전 (섭취 가능)' : '주의 필요'}
                </Text>
            </View>
            
            <View style={styles.infoBox}>
              <View style={styles.titleWrapper}>
                <Text style={styles.infoTitle}>설명</Text>
              </View>
                <Text style={styles.infoContent}>{foodData.description}</Text>
            </View>

            <View style={styles.infoBox}>
              <View style={styles.titleWrapper}>
                <Text style={styles.infoTitle}>주의사항</Text>
              </View>
                <Text style={styles.infoContent}>{foodData.cautions}</Text>
            </View>
        </ScrollView>
    );
}



const styles = StyleSheet.create({
  container: {
    backgroundColor: '#ffffff',
  },
  titleContainer:{
    alignItems: 'center',
    borderBottomEndRadius: 20,
    borderBottomLeftRadius: 20,
    backgroundColor: '#ffcb7dff',
    width: '100%',
    marginBottom: 10,
  },
  title: {
    marginTop: 30,
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 50,
  },
  infoBox: {
    marginBottom: 20,
    paddingHorizontal: 20,
  },
  infoTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#555',
  },
  infoTitle2:{
    marginTop: 30,
    fontSize: 20,
    fontWeight: 'bold',
    color: '#555',
    marginBottom: 8,
    paddingBottom: 10,
  },
  infoContent: {
    fontSize: 16,
    lineHeight: 24,
    color: '#333',
    marginBottom: 20,
  },
  safeText: {
    marginTop: 50,
    fontSize: 24,
    color: 'green',
    fontWeight: 'bold',
    paddingBottom: 40,
    textAlign: 'center'
  },
  warningText: {
    marginTop: 50,
    fontSize: 24,
    color: 'red',
    fontWeight: 'bold',
    paddingBottom: 40,
    textAlign: 'center'
  },
  errorText: {
    fontSize: 16,
    color: 'red',
    textAlign: 'center',
    marginTop: 20,
  },
  titleWrapper: {
    marginTop: 10,
    paddingLeft: 15,
    borderRadius: 15,
    marginBottom: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#ffd99fff',
    backgroundColor: '#ffd99fff',
    justifyContent: 'center',
    paddingVertical: 5,
  }
});

export default FoodDetail;