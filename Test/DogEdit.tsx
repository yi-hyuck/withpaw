import React from "react";
import { useState, useEffect } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, Pressable, Alert } from 'react-native';
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { useForm, Controller } from "react-hook-form";
import { useNavigation, RouteProp, useRoute } from "@react-navigation/native";
import DateTimePickerModal from "react-native-modal-datetime-picker";
import { createNativeStackNavigator, NativeStackNavigationProp } from "@react-navigation/native-stack";
import { RootStackParamList } from './types';

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

//임시데이터 - API로 불러오는 걸로 변경
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

function DogEdit(){
    //초기값
    const navigation = useNavigation<DogEditScreenNavigationProp>();
    const route = useRoute<DogEditRouteProp>();
    const {dogId} = route.params;

    const initialDogData = DogTemporaryData.find(dog => dog.id === dogId);

    const defaultFormValues = {
        dogName: initialDogData?.dogName,
        dogGender: initialDogData?.gender,
        dogBirth: initialDogData?.birth.replace(/-/g, '/'),
        dogBreed: initialDogData?.breed,
        dogWeight: initialDogData?.weight,
        neutering: initialDogData?.neutering,
    };

    const {control, handleSubmit, formState: {errors}, setValue, watch} = useForm({
        defaultValues: defaultFormValues
    });

    //변수
    const [selectGender, setSelectGender] = useState(defaultFormValues.dogGender);
    const [visible, setVisible] = useState(false); //날짜 선택
    const dogBirthValue = watch('dogBirth');
    const [selectNeutering, setSelectNeutering] = useState(defaultFormValues.neutering);


    //수정완료
    const onSubmit = (data:any) => {
        const updatedData = {...data, id: dogId};
        navigation.navigate('DogManagement', {updatedDog: updatedData});
    }

    //성별 설정
    const handleGenderSelect = (value:string) => {
        setSelectGender(value);
        setValue('dogGender', value, { shouldValidate: true });
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
        setSelectNeutering(value);
        setValue('neutering', value, {shouldValidate: true});
    }
    
    const renderNeuteringButton = (label:string, value: string) => (
        <TouchableOpacity
        key={value}
        style={[styles.button2, selectNeutering===value&&styles.selectButton]}
        onPress={()=>handleNeuteringSelect(value)}>
            <Text style={[styles.buttonText2, selectGender===value&&styles.selectButtonText]}>
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
      const formattedDate = formatDate(date, 'yyyy/MM/dd');
      setValue('dogBirth', formattedDate, {shouldValidate:true});
      setVisible(false);
    }
    


    return(
        <KeyboardAwareScrollView>
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
                        {renderButton('남', 'male')}
                        {renderButton('여', 'female')}
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
                name="neutering"
                rules={{required: '중성화 여부를 선택해주세요.'}}
                render={()=>(
                    <View>
                    <View style={styles.buttonGroup}>
                        {renderNeuteringButton('O', 'yes')}
                        {renderNeuteringButton('X', 'no')}
                    </View>
                    {errors?.neutering?.message && <Text style={styles.error}>{String(errors.neutering.message)}</Text>}
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
                        style={[styles.input, {marginTop:5}]}
                        onBlur={onBlur}
                        value={value}
                        onChangeText={(value)=>onChange(value)}
                        autoCapitalize="none"
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
                {backgroundColor:'#ffbb00ff'}]}
                onPress={handleSubmit(onSubmit)}
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

});

export default DogEdit;