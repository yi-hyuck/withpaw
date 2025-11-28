import React, { useCallback } from "react";
import { useState, useEffect } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, Pressable, Alert } from 'react-native';
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { useForm, Controller } from "react-hook-form";
import { useNavigation, RouteProp, useRoute } from "@react-navigation/native";
import DateTimePickerModal from "react-native-modal-datetime-picker";
import { createNativeStackNavigator, NativeStackNavigationProp } from "@react-navigation/native-stack";
import { RootStackParamList } from './types';
import axios from "axios";
import AsyncStorage from "@react-native-async-storage/async-storage";

const API_URL = 'http://10.0.2.2:8090/pet'


//타입 정의
interface petDto{
  petId: number;
  name: string;
  breed: string;
  gender: string;
  birthDate: string;
  neuter: number;
  weight: number;
}

//폼 필드 타입 정의
interface PetEditForm{
  dogName: string;
  dogGender: string;
  dogBirth: string;
  dogBreed: string;
  dogWeight: string;
  neuter: number;
}

type DogEditScreenNavigationProp = NativeStackNavigationProp<RootStackParamList, 'DogEdit'>;
type DogEditRouteProp = RouteProp<RootStackParamList, 'DogEdit'>;

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


//API
const fetchPetDetailApi = async (petId: number): Promise<petDto | null> => {
  try{
    const token = await AsyncStorage.getItem('userToken');
    if(!token){
        console.error("토큰 없음");
        return null;
    }

    const response = await axios.get(`${API_URL}/detail/${petId}`, {
        headers: {
            'Authorization' : `Bearer ${token}`
        }
    });

    return response.data;
  } catch (error){
      console.error("오류");
      if (axios.isAxiosError(error) && error.response) {
        console.error("펫 수정 오류 - 응답 상태 코드:", error.response.status);
        console.error("펫 수정 오류 - 응답 데이터:", error.response.data);
      } else {
          console.error("네트워크 또는 기타 오류:", error);
      }
      return null;
  }
}

const updatePetApi = async (petId: number, requestBody: any) => {
  try{
    const token = await AsyncStorage.getItem('userToken');
    if(!token){
        console.error("토큰 없음");
        return false;
    }
    
    await axios.post(`${API_URL}/modify/${petId}`, requestBody, {
      headers:{
        'Authorization' : `Bearer ${token}`,
        'Content-Type' : 'application/json',
      }
    });
    return true;
  } catch (error:any){
    console.error("펫 수정 오류", error.response.data||error.message);
    return false;
  }
}


function DogEdit(){
    //초기값
    const navigation = useNavigation<DogEditScreenNavigationProp>();
    const route = useRoute<DogEditRouteProp>();
    const {petId} = route.params;

    // const initialDogData = watch(fetchPetDetailApi);

    // const defaultFormValues = {
    //     dogName: initialDogData,
    //     dogGender: initialDogData?.gender,
    //     dogBirth: initialDogData?.birth.replace(/-/g, '-'),
    //     dogBreed: initialDogData?.breed,
    //     dogWeight: initialDogData?.weight,
    //     neutering: initialDogData?.neutering,
    // };

    const {control, handleSubmit, formState: {errors}, setValue, watch, reset} = useForm<PetEditForm>({
        defaultValues: {
          dogName: '',
          dogBirth: '',
          dogWeight: '',
          neuter: 0,
        }
    });

    //변수
    const [selectGender, setSelectGender] = useState<string>('');
    const [visible, setVisible] = useState(false); //날짜 선택
    const dogBirthValue = watch('dogBirth');
    const [selectNeutering, setSelectNeutering] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const [initialData, setInitialData] = useState<petDto|null>(null);

    const loadPetDto = useCallback(async () => {
      setIsLoading(true);
      const data = await fetchPetDetailApi(petId);

      if(data) {
        setInitialData(data);

        setValue('dogName', data.name);
        setValue('dogGender', data.gender);
        setValue('dogBirth', data.birthDate);
        setValue('dogBreed', data.breed);
        setValue('dogWeight', data.weight.toString());
        const neuterNumericValue = data.neuter ? 1 : 0;
        setValue('neuter', neuterNumericValue);

        setSelectGender(data.gender);
        setSelectNeutering(String(neuterNumericValue));
      }
      setIsLoading(false);
    },[petId, setValue, navigation]);

    useEffect(()=>{
      loadPetDto();
    }, [loadPetDto]);


    //수정완료
    const onSubmit = async(data:PetEditForm) => {
      if(isLoading || !initialData) return;

      const finalName = data.dogName.trim() === '' ? initialData.name : data.dogName;
      const finalBirthDate = data.dogBirth.trim() === '' ? initialData.birthDate : data.dogBirth;
      const finalBreed = initialData.breed;
      const finalGender = initialData.gender;

      let finalWeight = parseFloat(data.dogWeight);
      if(isNaN(finalWeight) || finalWeight < 0.1){
        finalWeight = initialData.weight;
      }

      const finalNeuter = (typeof data.neuter === 'number' && (data.neuter === 0 || data.neuter === 1)) ? data.neuter : (initialData.neuter ? 1 : 0);

      const requestBody = {
        petname : finalName,
        birthDate : finalBirthDate,
        neuter : finalNeuter,
        weight: finalWeight,
      }

      const success = await updatePetApi(petId, requestBody);
      
      if(success) {
        navigation.goBack();
      }else{
        Alert.alert("오류", "정보 수정에 실패했습니다.");
      }
    }


    //성별 설정
    const handleGenderSelect = (value:string) => {
      setSelectGender(value);
      setValue('dogGender', value, { shouldValidate: true });
    }


    //성별 버튼 설정
    // const renderButton = (label:string, value:string)=>(
    //   <TouchableOpacity
    //     key={value}
    //     style={[styles.button2, selectGender===value&&styles.selectButton]}
    //     onPress={()=>handleGenderSelect(value)}>
    //       <Text style={[styles.buttonText2, selectGender===value&&styles.selectButtonText]}>
    //         {label}
    //       </Text>
    //   </TouchableOpacity>
    // )
    const renderButton = (label:string, value:string)=>(
      <View
        key={value}
        style={[styles.button2, selectGender===value ? styles.selectButton : styles.button3]}>
          <Text style={[styles.buttonText2, selectGender===value&&styles.selectButtonText, selectGender!==value && {color: '#000000'}]}>
            {label}
          </Text>
      </View>
    )

    //중성화 설정
    const handleNeuteringSelect = (value: string) => {
      const numericValue = Number(value);
      setSelectNeutering(value);
      setValue('neuter', numericValue, {shouldValidate: true});
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
      setValue('dogBirth', formattedDate, {shouldValidate:true});
      setVisible(false);
    }
    


    return(
        <KeyboardAwareScrollView style={{backgroundColor:'#f8f8f8ff'}}>
            <Text style={[styles.title, {marginTop:40}]}>이름</Text>
            <View style={styles.container}>
                <Controller
                control={control}
                name="dogName"
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
                    {errors?.dogName?.message && <Text style={styles.error}>{String(errors.dogName.message)}</Text>}
                </View>
                )}
                rules={{required: '이름을 입력해주세요.'}}
                />
            </View>
            <Text style={[styles.title, {marginTop:30}]}>성별</Text>
            <View style={styles.container}>
                <Controller
                control={control}
                name="dogGender"
                rules={{required: '성별을 선택해주세요.'}}
                render={()=>(
                    <View>
                    <View style={styles.buttonGroup}>
                        {renderButton('남', 'M')}
                        {renderButton('여', 'F')}
                    </View>
                    {errors?.dogGender?.message && <Text style={styles.error}>{String(errors.dogGender.message)}</Text>}
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
                name="dogBirth"
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
                    {errors?.dogBirth?.message && <Text style={styles.error}>{String(errors.dogBirth.message)}</Text>}
                    </View>
                )}
                />
            </View>
    
            <DateTimePickerModal
                    isVisible={visible}
                    mode="date"
                    onConfirm={handleConfirm}
                    onCancel={onCancel}
            />
    
            <Text style={[styles.title, {marginTop:30}]}>품종</Text>
            <View style={styles.container}>
                <Controller
                control={control}
                name="dogBreed"
                render={({field: {onChange, value, onBlur}}) => (
                    <View>
                    <TextInput
                        placeholder=" 품종 입력"
                        style={[styles.input2, {marginTop:5, backgroundColor:'#e6e6e6ff'}]}
                        onBlur={onBlur}
                        value={value}
                        onChangeText={(value)=>onChange(value)}
                        autoCapitalize="none"
                        editable={false}
                    />
                    {errors?.dogBreed?.message && <Text style={styles.error}>{String(errors.dogBreed.message)}</Text>}
                </View>
                )}
                rules={{required: '품종을 입력해주세요.'}}
                />
            </View>
    
            <Text style={[styles.title, {marginTop:30}]}>몸무게</Text>
            <View style={styles.container}>
                <Controller
                control={control}
                name="dogWeight"
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
                    {errors?.dogWeight?.message && <Text style={styles.error}>{String(errors.dogWeight.message)}</Text>}
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
                {backgroundColor: isLoading ? '#aaaaaaff':'#ffbb00ff'}]}
                onPress={handleSubmit(onSubmit)}
                disabled={isLoading}
                >
                <Text style={styles.buttonText}>수정 완료</Text>
                </Pressable>
            </View>
        </KeyboardAwareScrollView>
    )
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
  input2:{
    width:350,
    height:45,
    borderColor: '#444444ff',
    borderWidth: 1,
    paddingHorizontal: 10,
    borderRadius: 8,
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
  button3:{
    paddingVertical: 10,
    alignItems:'center',
    justifyContent: 'space-between',
    backgroundColor:'#e6e6e6ff',
    borderColor:'#e6e6e6ff',
    borderWidth: 1,
    borderRadius: 5,
    marginHorizontal: 15,
    flex:1,
  },
  selectButton:{
    borderColor:'#ffc400ff',
    backgroundColor:'#ffc400ff',
  },
  selectButton2:{
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

});

export default DogEdit;