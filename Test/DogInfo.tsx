import React, {useEffect} from "react";
import { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, Pressable, Alert, Modal, FlatList } from 'react-native';
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { useForm, Controller } from "react-hook-form";
import { useNavigation, RouteProp, useRoute } from "@react-navigation/native";
import DateTimePickerModal from "react-native-modal-datetime-picker";
import { createNativeStackNavigator, NativeStackNavigationProp } from "@react-navigation/native-stack";
import { RootStackParamList } from './types';
import axios from 'axios';

const Stack = createNativeStackNavigator();
const API_URL = "http://10.0.2.2:8090";

//회원가입 정보 불러올 때 필요
type DogInfoRouteProp = RouteProp<RootStackParamList, 'DogInfo'>;

//화면 넘어가는 거
type DogInfoScreenNavigationProp = NativeStackNavigationProp<
    RootStackParamList,
    'DogInfo'
  >;

type Breed = {
  breedname: string;
}

//날짜 입력
const formatDate = (date:Date, f:string) => {
  if(!date.valueOf()) return "";

  const d = date;

  const zf = (num:number, len:number) => String(num).padStart(len, '0');

  return f.replace(/(yyyy|MM|dd)/gi, function($1:string){
    let h;
    switch($1){
      case "yyyy":return String(d.getFullYear());
      case "MM": return zf(d.getMonth()+1,2);
      case "dd": return zf(d.getDate(), 2);
      default: return $1;
    }
  });
}

function DogInfo(){
    const route = useRoute<DogInfoRouteProp>();
    const navigation = useNavigation<DogInfoScreenNavigationProp>();

    const {userData} = route.params;
    const [isLoading, setIsLoading] = useState(false);
    const [breeds, setBreeds] = useState<Breed[]>([]);
    const [isBreedModalVisible, setIsBreedModalVisible] = useState(false);
    const [searchText, setSearchText] = useState('');

    //폼 관리
    const {control, handleSubmit, formState: {errors}, setValue, watch} = useForm({
      defaultValues: {
        petname: '',
        gender: '',
        birthdate: '',
        breed: '',
        weight: '',
        neuter: false,
      }
    });

    //성별 변수
    const [selectGender, setSelectGender] = useState("");

    //날짜 변수
    const [text, onChangeText] = useState("");
    const [visible, setVisible] = useState(false); //날짜 선택
    const dogBirthValue = watch('birthdate');

    //중성화 변수
    const [selectNeutering, setSelectNeutering] = useState('');
    
    //Signup에서 사용자 정보 받아옴
    const onSubmit = async (data:any)=>{
      if(isLoading) return;

      setIsLoading(true);

      const petInfo = {
        petname: data.petname,
        breed: data.breed,
        gender: data.gender,
        birthdate: data.birthdate,
        neuter: data.neuter,
        weight: parseFloat(data.weight)
      }

      const finalData = {
        ...userData,
        pets: [petInfo]
      }

      try{
        const response = await axios.post(`${API_URL}/member/signup/pet`, finalData);
        if(response.data && response.data.token){
          // const token = response.data.token;
          // await saveToken(token);
          // await fetchMemberInfo(token);

          // await new Promise<void>(resolve => setTimeout(resolve, 100));

          setIsLoading(false);

          navigation.reset({index:0, routes:[{name:'Home'}]});
        }
      } catch (error: any){
        console.error('Signup Complete Error:', error.response?.data || error.message);
            
        const errorData = error.response?.data;
        if (errorData) {
            if (errorData.field && errorData.error) {
                // 아이디 중복 에러 등 특정 필드 에러 처리
                if (errorData.field === 'loginId') {
                    Alert.alert("가입 실패", "회원가입 중 오류가 발생했습니다: " + errorData.error);
                } 
                // 반려동물 필수 에러 등
                else if (errorData.field === 'pets') {
                    Alert.alert("필수 정보 누락", errorData.error);
                }
            } else if (errorData.message) {
                Alert.alert("가입 실패", errorData.message);
            }
        } else {
            Alert.alert("서버 연결 실패", "서버에 연결할 수 없습니다. 다시 시도해 주세요.");
        }
        navigation.reset({index:0, routes:[{name:'Home'}]});
      } finally{
        setIsLoading(false);
      }
    }

    //성별 설정
    const handleGenderSelect = (value:string) => {
      setSelectGender(value);
      setValue('gender', value, { shouldValidate: true });
    }

    //성별 버튼 설정
    const renderButton = (label:string, value:string)=>(
      <TouchableOpacity
        key={value}
        style={[styles.button2, selectGender===value&&styles.selectButton]}
        onPress={()=>handleGenderSelect(value)}>
          <Text style={[styles.buttonText2, selectGender===value&&styles.selectButtonText]}>
            {label}
          </Text>
      </TouchableOpacity>
    )

    //중성화 설정
    const handleNeuteringSelect = (value: string) => {
      const booleanValue = value === '1';
      setSelectNeutering(value);
      setValue('neuter', booleanValue, {shouldValidate: true});
    }
    
    const renderNeuteringButton = (label:string, value: string) => (
      <TouchableOpacity
        key={value}
        style={[styles.button2, selectNeutering===value&&styles.selectButton]}
        onPress={()=>handleNeuteringSelect(value)}>
          <Text style={[styles.buttonText2, selectNeutering===value&&styles.selectButtonText]}>
            {label}
          </Text>
      </TouchableOpacity>
    )


    //날짜 클릭시
    const onPressDate = () => {
      setVisible(true);
    }
    
    //날짜 취소
    const onCancel = () => {
      setVisible(false);
    }

    //날짜 확정
    const handleConfirm = (date:Date)=> {
      const formattedDate = formatDate(date, 'yyyy-MM-dd');
      setValue('birthdate', formattedDate, {shouldValidate:true});
      setVisible(false);
    }

    //품종 목록을 가져옴
    const fetchBreeds = async () => {
      try{
        const response = await axios.get<Breed[]>(`${API_URL}/pet/breeds`);
        setBreeds(response.data);
      } catch (error) {
        Alert.alert("오류", "품종 목록을 불러오지 못했습니다.");
      }
    }

    useEffect(() => {
      fetchBreeds();
    }, []);

    const handleBreedSelect = (breedname: string) => {
      setValue('breed', breedname, {shouldValidate: true});
      setIsBreedModalVisible(false);
    }

    const filteredBreeds = breeds.filter(breed =>
      breed.breedname.toLowerCase().includes(searchText.toLowerCase())
    )

    const renderBreedItem = ({ item }: { item: Breed }) => (
      <Pressable
        style={styles.breedItem}
        onPress={() => handleBreedSelect(item.breedname)}>
        <Text style={styles.breedText}>{item.breedname}</Text>
      </Pressable>
    );

    const dogBreedValue = watch('breed');


    return (
      <KeyboardAwareScrollView>
        <Text style={[styles.title, {marginTop:40}]}>이름</Text>
        <View style={styles.container}>
          <Controller
            control={control}
            name="petname"
            render={({field: {onChange, value, onBlur}}) => (
              <View>
                <TextInput
                  placeholder=" 이름 입력"
                  style={[styles.input, {marginTop:5}]}
                  onBlur={onBlur}
                  value={value}
                  onChangeText={(value)=>onChange(value)}
                  autoCapitalize="none"
                />
                {errors?.petname?.message && <Text style={styles.error}>{String(errors.petname.message)}</Text>}
            </View>
            )}
            rules={{required: '이름을 입력해주세요.'}}
          />
        </View>
        <Text style={[styles.title, {marginTop:30}]}>성별</Text>
        <View style={styles.container}>
          <Controller
            control={control}
            name="gender"
            rules={{required: '성별을 선택해주세요.'}}
            render={()=>(
              <View>
                <View style={styles.buttonGroup}>
                  {renderButton('남', 'M')}
                  {renderButton('여', 'F')}
                </View>
                {errors?.gender?.message && <Text style={styles.error}>{String(errors.gender.message)}</Text>}
              </View>
            )}
          />
        </View>
        <Text style={[styles.title, {marginTop:30}]}>중성화 여부</Text>
        <View style={styles.container}>
          <Controller
            control={control}
            name="neuter"
            rules={{required: '중성화 여부를 선택해주세요.'}}
            render={()=>(
              <View>
                <View style={styles.buttonGroup}>
                  {renderNeuteringButton('O', '1')}
                  {renderNeuteringButton('X', '0')}
                </View>
                {errors?.neuter?.message && <Text style={styles.error}>{String(errors.neuter.message)}</Text>}
              </View>
            )}
          />
        </View>
        <Text style={[styles.title, {marginTop:30}]}>생년월일</Text>
        <View style={styles.container}>
          <Controller
            control={control}
            name="birthdate"
            rules={{required: '생년월일을 입력해주세요.'}}
            render={()=>(
              <View>
                <TouchableOpacity onPress={onPressDate}>
                  <TextInput
                    pointerEvents="none"
                    style={[styles.input, {marginTop:5}]}
                    placeholder=" 날짜를 입력해주세요."
                    placeholderTextColor={dogBirthValue ? '#000000' : '#838383ff'}
                    underlineColorAndroid="transparent"
                    editable={false}
                    value={dogBirthValue}
                  />
                </TouchableOpacity>
                {errors?.birthdate?.message && <Text style={styles.error}>{String(errors.birthdate.message)}</Text>}
              </View>
            )}
          />
        </View>

        <DateTimePickerModal
                isVisible={visible}
                mode="date"
                onConfirm={handleConfirm}
                onCancel={onCancel}
                maximumDate={new Date()}
        />

        <Text style={[styles.title, {marginTop:30}]}>품종</Text>
        <View style={styles.container}>
          <Controller
            control={control}
            name="breed"
            render={() => (
              <View>
                <TouchableOpacity onPress={() => setIsBreedModalVisible(true)}>
                  <TextInput
                    pointerEvents="none"
                    placeholder=" 품종 입력"
                    style={[styles.input, {marginTop:5}]}
                    placeholderTextColor={dogBreedValue ? '#000000' : '#838383ff'}
                    // onBlur={onBlur}
                    underlineColorAndroid="transparent"
                    editable={false}
                    value={dogBreedValue}
                    // onChangeText={(value)=>onChange(value)}
                    // autoCapitalize="none"
                  />
                </TouchableOpacity>
                {errors?.breed?.message && <Text style={styles.error}>{String(errors.breed.message)}</Text>}
            </View>
            )}
            rules={{required: '품종을 입력해주세요.'}}
          />
        </View>
        <Modal
          animationType="slide"
          transparent={true}
          visible={isBreedModalVisible}
          onRequestClose={() => setIsBreedModalVisible(false)}>
          <View style={styles.modalOverlay}>
            <View style={styles.modalView}>
              <Text style={styles.modalTitle}>품종 선택</Text>
              <TextInput
               style={styles.searchBar}
               placeholder="품종 검색"
               value={searchText}
               onChangeText={setSearchText}
              />
              <FlatList
                data={filteredBreeds}
                keyExtractor={(item) => item.breedname}
                renderItem={renderBreedItem}
                style={styles.flatList}
              />
              <Pressable
                style={[styles.closeButton]}
                onPress={() => setIsBreedModalVisible(false)}>
                <Text style={styles.closeButtonText}>닫기</Text>
              </Pressable>
            </View>
          </View>
        </Modal>

        <Text style={[styles.title, {marginTop:30}]}>몸무게</Text>
        <View style={styles.container}>
          <Controller
            control={control}
            name="weight"
            render={({field: {onChange, value, onBlur}}) => (
              <View>
                <TextInput
                  placeholder=" 몸무게 입력"
                  style={[styles.input, {marginTop:5}]}
                  onBlur={onBlur}
                  value={value}
                  onChangeText={(value)=>onChange(value)}
                  keyboardType="number-pad"
                />
                {errors?.weight?.message && <Text style={styles.error}>{String(errors.weight.message)}</Text>}
            </View>
            )}
            rules={{
              required: '몸무게를 입력해주세요.',
              pattern: {
                value: /^\d+(\.\d+)?$/,
                message: '유효한 숫자를 입력해주세요.',
              },
            }}
          />
        </View>

        <View style={styles.container}>
          <Pressable 
            style={[styles.button,
            {marginTop:50},
            {backgroundColor: isLoading ? '#f3f3f3ff' : '#ffbb00ff'}]}
            onPress={handleSubmit(onSubmit)}
            // disabled={isLoading}
          >
            <Text style={styles.buttonText}>회원가입</Text>
          </Pressable>
        </View>
      </KeyboardAwareScrollView>
    );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  button: {
    paddingVertical: 10,
    width: 350,
    alignItems:'center',
    marginBottom: 30,
    borderRadius:10,
  },
  buttonText: {
    fontSize: 20,
    color: '#ffffffff',
    fontWeight:'bold',
  },
  input:{
    width:350,
    height:45,
    borderColor: '#444444ff',
    borderWidth: 1,
    paddingHorizontal: 10,
    borderRadius: 8,
    backgroundColor: '#ffffff',
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
  buttonGroup:{
    flexDirection: 'row',
    width: 380,
    justifyContent: 'space-between',
    marginTop: 5,
  },
  button2:{
    paddingVertical: 10,
    alignItems:'center',
    justifyContent: 'space-between',
    backgroundColor:'#ffffffff',
    borderColor:'#ffc400ff',
    borderWidth: 1,
    borderRadius: 5,
    marginHorizontal: 15,
    flex:1,
  },
  selectButton:{
    borderColor:'#ffc400ff',
    backgroundColor:'#ffc400ff',
  },
  buttonText2:{
    fontSize:18,
    color:'#000000',
  },
  selectButtonText:{
    color:'#ffffff',
    fontWeight: 'bold',
  },
  modalOverlay:{
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalView:{
    width: '80%',
    backgroundColor: 'white',
    borderRadius: 20,
    padding: 20,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  modalTitle:{
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 15,
  },
  flatList:{
    maxHeight: 300, 
    width: '100%',
  },
  breedItem:{
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ffffffff',
    width: '100%',
    alignItems: 'center',
  },
  breedText:{
    fontSize: 16,
  },
  closeButton:{
    marginTop: 20,
    backgroundColor: '#ffbb00ff',
    padding: 10,
    borderRadius: 10,
    width: '50%',
    alignItems: 'center',
  },
  closeButtonText:{
    color: 'white',
    fontWeight: 'bold',
    fontSize: 16,
  },
  searchBar:{
    width: '100%',
    height: 40,
    borderColor: '#eeeeeeff',
    borderWidth: 1,
    borderRadius: 5,
    backgroundColor: '#ffffffff',
    paddingLeft: 10,
    marginBottom: 10,
  }
});

export default DogInfo;