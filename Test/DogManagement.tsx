import React from 'react';
import { useState, useEffect } from 'react';
import { StatusBar, StyleSheet, useColorScheme, View, Text, Touchable, TouchableOpacity, Alert} from 'react-native';
import { createNativeStackNavigator, NativeStackNavigationProp, NativeStackScreenProps } from '@react-navigation/native-stack';
import { FlatList, GestureHandlerRootView } from 'react-native-gesture-handler';
import { NavigationContainer, useRoute } from '@react-navigation/native';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';

import { RootStackParamList } from './types';

import DogAdd from './DogAdd';
import DogEdit from './DogEdit';

type DogMgmtScreenProps = NativeStackScreenProps<RootStackParamList,"DogManagement">;

//데이터 구조
interface DogItem{
    id: string;
    name: string;
    dogName: string;
    birth: string;
    breed: string;
    gender: string;
    weight: string;
    neutering: string;
}

const DogTemporaryData: DogItem[] = [
    {
        id: 'd1',
        name: '몽실',
        breed: '말티즈',
        birth: '2015-06-13',
        gender: 'male',
        weight: '3',
        neutering: 'yes',
        dogName: '몽실',
    },
    {
        id: 'd2',
        name: '너티',
        breed: '테리어',
        birth: '2020-01-01',
        gender: 'male',
        weight: '5',
        neutering: 'yes',
        dogName: '너티',
    },
    {
        id: 'd3',
        name: '메리',
        breed: '시고르자브종',
        birth: '2020-01-01',
        gender: 'male',
        weight: '5',
        neutering: 'yes',
        dogName: '메리',
    }
];

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
                <Stack.Screen name="DogAdd" component={DogAdd}
                                options={({route})=>({
                                headerShown: true,
                                headerStyle:HEADER_STYLE,
                                headerTitle:(props)=>(
                                    <CustomTitle {...props} title="반려동물 추가"/>
                                ),
                                })}/>
                <Stack.Screen name="DogEdit" component={DogEdit}options={({route})=>({
                                headerShown: true,
                                headerStyle:HEADER_STYLE,
                                headerTitle:(props)=>(
                                    <CustomTitle {...props} title="반려동물 수정"/>
                                ),
                                })}/>
            </Stack.Navigator>
        </GestureHandlerRootView>
    )
}

//모드 변경
interface DogItemProps extends DogItem {
    mode : 'none' | 'edit' | 'delete';
    onEdit: (id: string) => void;
    onSelect: (id: string) => void;
    isSelect: boolean;
}


//강아지 카드 관리
const DogItems = ({id, name, dogName, breed, birth, mode, onEdit, onSelect, isSelect}:DogItemProps)=>{
    const Age = (birthDay: string) => {
        const separator = birthDay.includes('/') ? '/' : '-';
        const parts = birthDay.split(separator);
        const birthDate = new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if(monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())){
            age--;
        }
        return age >= 0 ? `${age}` : '0';
    }

    const displayAge = Age(birth);


    const handlePress = () =>{
        if(mode === 'edit'){
            onEdit(id);
        } else if (mode === 'delete'){
            onSelect(id);
        }
    }

    const deleteStyle = [
        isSelect && mode === 'delete' && styles.selectCard
    ]

    return(
        <TouchableOpacity style={deleteStyle} onPress={handlePress} activeOpacity={mode !== 'none' ? 0.6 : 1.0}>
            {mode === 'delete' && (
                <MaterialCommunityIcons
                    name = 'check-bold'
                    size = {24}
                    color = {isSelect ? '#ffbb00ff' : '#f0f0f0ff'}
                    style = {{paddingLeft: 10}}
                />
            )}
            <View style={styles.CardContainer}>
                <View style={styles.CardContent}>
                    <Text style={styles.cardTitle}>{dogName}</Text>
                    <Text style={styles.cardData}>{displayAge}살</Text>
                    <Text style={styles.cardData}>{breed}</Text>
                </View>
            </View>
        </TouchableOpacity>
    );
}

interface ActionButtonProps {
    mode: 'none' | 'edit' | 'delete'
    setMode: (mode: 'none' | 'edit' | 'delete') => void;
    onAdd: () => void
    onDelete: () => void;
    selectCount: number;
}


const ActionButton = ({mode, setMode, onAdd, onDelete, selectCount}:ActionButtonProps) => {
    if (mode === 'delete'){
        return(
            <View style={styles.deleteContainer}>
                <TouchableOpacity
                    onPress={()=> setMode('none')}
                    style={styles.cancleButton}
                >
                    <Text style={styles.deleteText}>취소</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    onPress={onDelete}
                    disabled={selectCount === 0}
                    style={styles.deleteButton}
                >
                    <Text style={styles.deleteText}>삭제</Text>
                </TouchableOpacity>
            </View>
        )
    }

    if (mode === 'edit'){
        return(
            <View>
                <Text style={[styles.deleteText, {marginBottom: 3}]}>수정할 동물을 선택해주세요.</Text>
            </View>
        )
    }

    return(
        <View>
            <TouchableOpacity style={styles.button} onPress={onAdd}>
                <Text style={styles.buttonText}>
                    <Text style={styles.blackText}>반려동물</Text>
                    <Text style={[{color:'#0c6600ff', fontWeight:'bold'}]}> 추가</Text>
                </Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.button} onPress={()=>setMode('edit')}>
                <Text style={styles.buttonText}>
                    <Text style={styles.blackText}>반려동물</Text>
                    <Text style={[{color:'#002fffff', fontWeight:'bold'}]}> 수정</Text>
                </Text>
            </TouchableOpacity>
            <TouchableOpacity style={[styles.button, {marginBottom: 10}]} onPress={()=>setMode('delete')}>
                <Text style={styles.buttonText}>
                    <Text style={styles.blackText}>반려동물</Text>
                    <Text style={[{color:'#ff0000ff', fontWeight:'bold'}]}> 삭제</Text>
                </Text>
            </TouchableOpacity>
        </View>
    )
}

//관리 화면
function DogManagement({navigation, route}:DogMgmtScreenProps){
    const [dogData, setDogData] = useState(DogTemporaryData);
    const [mode, setMode] = useState<'none' | 'edit' | 'delete'>('none');
    const [selectIds, setSelectIds] = useState<string[]>([]);

    useEffect(()=>{
        //새로운 강아지 추가
        if(route.params?.newDog){
            const newDog = route.params.newDog;

            const newDogItem: DogItem = {
                id: Date.now().toString(),
                name: newDog.dogName,
                dogName: newDog.dogName,
                breed: newDog.dogBreed,
                birth: newDog.dogBirth,
                gender: newDog.dogGender,
                weight: newDog.dogWeight,
                neutering: newDog.neutering,
            };

            setDogData(prev => [...prev, newDogItem]);

            navigation.setParams({newDog: undefined});
        }

        //강아지 수정
        if(route.params?.updatedDog){
            const updatedDog = route.params.updatedDog;
            
            const updatedDogItem: DogItem = {
                id: updatedDog.id,
                name: updatedDog.dogName,
                dogName: updatedDog.dogName,
                breed: updatedDog.dogBreed,
                birth: updatedDog.dogBirth,
                gender: updatedDog.dogGender,
                weight: updatedDog.dogWeight,
                neutering: updatedDog.neutering,
            }

            setDogData(prev => prev.map(dog => dog.id === updatedDog.id ? updatedDogItem : dog));

            navigation.setParams({updatedDog: undefined});
            setMode('none');
        }
    }, [route.params?.newDog, route.params?.updatedDog, navigation]);


    const handleEdit = (dogId: string) => {
        navigation.navigate('DogEdit', {dogId});
        setMode('none')
    }

    const handleSelectDelete = (dogId: string) => {
        setSelectIds(prev => prev.includes(dogId) ? prev.filter(id => id !== dogId) : [...prev, dogId])
    }

    const handleDelete = () => {
        if(selectIds.length === 0){
            Alert.alert('알림', '삭제할 반려동물을 선택해주세요.');
            return;
        }

        Alert.alert(
            '삭제',
            `선택한 반려동물 ${selectIds.length}마리를 삭제하시겠습니까?`,
            [
                {text: '취소'},
                {text: '삭제',
                    onPress: () => {
                        const newDogData = dogData.filter(dog => !selectIds.includes(dog.id))
                        setDogData(newDogData)
                        setSelectIds([])
                        setMode('none')
                    }
                }
            ]
        )
    }

    const handleAdd = () => {
        navigation.navigate('DogAdd');
    }

    return(
        <View style={styles.container2}>
            <FlatList
                data={dogData}
                renderItem={({item}) => <DogItems
                                            {...item}
                                            mode = {mode}
                                            onEdit={handleEdit}
                                            onSelect={handleSelectDelete}
                                            isSelect={selectIds.includes(item.id)}
                                        />}
                keyExtractor={(item) => item.id}
                contentContainerStyle={styles.listPadding}
            />
            <ActionButton
                mode={mode}
                setMode={setMode}
                onAdd={handleAdd}
                onDelete={handleDelete}
                selectCount={selectIds.length}
            />
        </View>
    )
}


const styles = StyleSheet.create({
  container:{
    flex: 1,
    backgroundColor:'#ffffffff',
    paddingVertical: 3,
  },
  container2:{
    flex: 1,
    paddingHorizontal: 1,
    paddingTop: 3,
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
    backgroundColor: '#fffcf3ff',
    borderColor: '#ffd000ff',
    borderWidth: 2,
    borderRadius: 20,
    marginBottom: 5,
    marginHorizontal: 5,
    paddingRight: 10,
    paddingLeft: 7,
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
    marginBottom: 10,
  },
  button:{
    backgroundColor: '#ffffffff',
    paddingVertical: 15,
    borderTopWidth: 1,
    borderColor: '#c5c5c5ff',
    marginHorizontal: 25,
    alignItems: 'center',
  },
  buttonText:{
    fontSize: 16,
  },
  blackText:{
    fontSize: 16,
    color:'#000000ff',
    fontWeight: 'bold',
  },
  selectCard:{
    backgroundColor: '#e9e7e1ff',
    borderColor: '#ffd000ff',
  },

  //삭제 스타일
  cancleButton:{
    flex: 1,
    borderRadius: 5,
    justifyContent: 'center',
    padding: 10,
    backgroundColor: '#9e9e9e',
    marginRight: 5,
  },
  deleteText:{
    fontWeight: 'bold',
    fontSize: 16,
    textAlign: 'center',
  },
  deleteButton:{
    flex: 1,
    borderRadius: 5,
    justifyContent: 'center',
    padding: 10,
    backgroundColor: '#ff5145ff',
    marginLeft: 5,
  },
  deleteContainer:{
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignContent: 'center',
    alignItems: 'center',
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    paddingBottom: 5,
    paddingHorizontal: 10,
  },
});

export default DogMgmtScreen;