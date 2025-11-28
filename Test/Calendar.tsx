import React, { useCallback, useState, useEffect } from "react";
import { StyleSheet, Text, View, TextInput, TouchableOpacity, ScrollView, ActivityIndicator, Alert} from 'react-native';
import { Calendar, DateData, LocaleConfig } from "react-native-calendars";
import { createNativeStackNavigator, NativeStackNavigationProp, NativeStackScreenProps } from "@react-navigation/native-stack";
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import DateTimePickerModal from "react-native-modal-datetime-picker";
import { useNavigation, RouteProp } from "@react-navigation/native";
import { createDrawerNavigator, DrawerNavigationProp } from '@react-navigation/drawer'
import axios from "axios";

import { RootStackParamList } from "./types";
import { MarkedDates } from "react-native-calendars/src/types";

const API_URL = "http://10.0.2.2:8090/api/schedules";
//임시 id
const MEMBER_ID = 1;

//드로어바 관련
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


//타입 정의
interface Schedule {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  repeats: boolean;    //반복 여부
  repeatWeeks: number; //몇 주마다 반복?
  originalId: number;
  time: string;
}

const DEFAULT_TIME = 'T10:00:00';

interface scheduleResponseDTO{
  id: number;
  memberId: number;
  title: string;
  time: string;
  recurring: boolean;
  interval: number | null;
  startDate: string;
  endDate: string;
  remindBeforeMinutes: number|null;
}

interface scheduleInstanceResponseDTO{
  id:number;
  occurrenceTime: string;
  completed: boolean;
  scheduleTitle: string;
}

//UTC 설정
const formatDate = (date : Date) : string => date.toISOString().split('T')[0];

const parseDate = (dateString :string): Date => {
  const [year, month, day] = dateString.split('-').map(Number);
  return new Date(Date.UTC(year,month-1,day));
}

//끝-시작 날짜 계산
const getDateDifferenceInDays = (startDateString: string, endDateString: string): number => {
  const start = parseDate(startDateString)
  const end = parseDate(endDateString)
  const diffTime = end.getTime()-start.getTime();
  return Math.floor(diffTime / (1000 * 60 * 60 * 24));
}

//달력 한글패치
LocaleConfig.locales.kr={
  monthNames:[
    '01월', '02월', '03월', '04월', '05월', '06월', '07월', '08월', '09월', '10월', '11월', '12월',
  ],
  monthNamesShort:[
    '01월', '02월', '03월', '04월', '05월', '06월', '07월', '08월', '09월', '10월', '11월', '12월',
  ],
  dayNames:['일요일','월요일','화요일','수요일','목요일','금요일','토요일'],
  dayNamesShort:['일','월','화','수','목','금','토'],
  today: "오늘",
};

LocaleConfig.defaultLocale = 'kr';


//api
//추가
const createSchedule = async (schedule: any) => {
  const requestBody = {
    memberId: MEMBER_ID,
    id: schedule.id,
    title: schedule.name,
    time: schedule.time,
    recurring: schedule.repeats,
    interval: schedule.repeatWeeks,
    startDate: schedule.startDate,
    endDate: schedule.endDate,
    remindBeforeMinutes: 5,
  };

  const response = await axios.post(API_URL, requestBody);
  return response.data.data;
}

//조회
const fetchSchedules = async (): Promise<Schedule[]> => {
  const scheduleResponse = await axios.get(`${API_URL}/member/${MEMBER_ID}`);
  const originalSchedules: scheduleResponseDTO[] = scheduleResponse.data.data;

  let allInstances: Schedule[] = [];

  for (const s of originalSchedules) {
    try{
      const instanceResponse = await axios.get(`${API_URL}/${s.id}/instances`);
      const instances: scheduleInstanceResponseDTO[] = instanceResponse.data.data;

      const instanceSchedules: Schedule[] = instances.map((i:scheduleInstanceResponseDTO) => ({
        id: i.id,
        name: i.scheduleTitle,
        startDate: i.occurrenceTime.substring(0,10),
        endDate: i.occurrenceTime.substring(0,10),
        repeats: false,
        repeatWeeks: 0,
        originalId: s.id,
        time: i.occurrenceTime.substring(11, 16),
      }));

      allInstances = allInstances.concat(instanceSchedules);
    } catch(e:any){
      console.error(`인스턴스 조회실패 for ID ${s.id}`, e.message);
    }
  }
  return allInstances;
}

//삭제
const deleteScheduleApi = async (scheduleId: number) => {
  await axios.delete(`${API_URL}/${scheduleId}`)
}


//캘린더 스텍 화면
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

function CalendarMainScreen() { //스텍 화면
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  const [isSaving, setIsSaving] = useState<boolean>(false);
  // const mockApi = getMockApi({schedules, setSchedules});

  //초기 데이터 불러오기
  useEffect(() => {
    const loadSchedules = async () => {
        try {
            const data = await fetchSchedules(); 
            setSchedules(data.sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime()));
        } catch (e) {
            console.error("Fetch Error:", e);
        }
    };
    loadSchedules();
  }, []);

  //일정 추가
  const handleScheduleAdd = async (newSchedules: Schedule[]) => {
    if(newSchedules.length === 0) return;

    setIsSaving(true);
    try {
        await createSchedule(newSchedules[0]);
        const updatedList = await fetchSchedules(); 
        setSchedules(updatedList.sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime()));
    } catch (e) {
        Alert.alert('오류', '일정 추가 중 오류가 발생했습니다.');
        throw e;
    } finally {
        setIsSaving(false);
    }
  };

  const handleDeleteSchedule = async (idToDelete: number) => {
    try {
        await deleteScheduleApi(idToDelete);
        setSchedules(prev => prev.filter(s => s.originalId !== idToDelete));
    } catch (e) {
        Alert.alert('오류', '일정 삭제 중 오류가 발생했습니다.');
        throw e;
    }
  };

  return(
    <Stack.Navigator>
      <Stack.Screen
        name="CalendarScreen"
        children={({navigation, route}) => (
          <CalendarScreen
            navigation={navigation}
            route={route}
            schedules={schedules}
            deleteSchedule={handleDeleteSchedule}
          />
        )}
        options={({route})=>({
                      headerShown: true,
                      headerStyle:HEADER_STYLE,
                      headerTitle:(props)=>(
                        <CustomTitle {...props} title="달력"/>
                      ),
                      headerRight: ()=> <DrawerButton/>,
                    })}
      >
      </Stack.Screen>
      <Stack.Screen
        name="ScheduleAdd"
        options={{
          headerStyle: HEADER_STYLE,
          headerTitle: "일정 추가",
          headerTitleStyle: {fontSize:20, fontWeight:'bold', color:'#000000',}
        }}>
        {({ navigation, route }) => (
          <ScheduleAdd
            navigation={navigation}
            route={route}
            onGoBack={navigation.goBack}
            handleScheduleAddition={handleScheduleAdd}
            isSaving={isSaving}
          />
        )}
      </Stack.Screen>
    </Stack.Navigator>
  )
}


//일정 보기--------------------------------------
interface ScheduleItemProps {
  schedule: Schedule;
  onDelete: (id: number) => Promise<void>;
  isDeleting: boolean;
}

function ScheduleItem({schedule, onDelete, isDeleting} : ScheduleItemProps){
  const [deleting, setDeleting] = useState(false);

  const handelDelete = async () => {
    if(deleting || isDeleting) return;
    setDeleting(true);
    try{
      await onDelete(schedule.originalId);
    }catch (e){
      Alert.alert("오류", "일정 삭제에 실패했습니다.");
    } finally {
      setDeleting(false);
    }
  }

  return(
    <View style={styles.itemContainer}>
      <Text style={styles.itemTitle}>{schedule.name}</Text>
      <TouchableOpacity
        style={styles.deleteButton}
        onPress={handelDelete}
        disabled={deleting || isDeleting}
      >
        {deleting ? (
          <MaterialCommunityIcons name="delete" size={20} color="#aaaaaaff"/>
        ) : (
          <MaterialCommunityIcons name="delete" size={20} color="#dfdfdfff"/>
        )}
      </TouchableOpacity>
    </View>
  )
}

//일정 추가--------------------------------
type ScheduleAddScreenNavigationProps = NativeStackScreenProps<RootStackParamList, 'ScheduleAdd'>;

interface ScheduleAddCustomProps {
    onGoBack: () => void;
    handleScheduleAddition: (schedules: Schedule[]) => Promise<void>;
    isSaving: boolean;
}

export type ScheduleAddScreenProps = ScheduleAddScreenNavigationProps & ScheduleAddCustomProps;

// const formatDateToString = (date: Date): string => {
//   const yyyy = date.getFullYear();
//   const mm = String(date.getMonth() + 1).padStart(2, '0');
//   const dd = String(date.getDate()).padStart(2, '0');
//   return `${yyyy}-${mm}-${dd}`;
// };

type DateType = 'start' | 'end' | null;

function ScheduleAdd({navigation, route, onGoBack, handleScheduleAddition, isSaving}: ScheduleAddScreenProps){
  const today = formatDate(new Date());

  const [name, setName] = useState('');
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');
  const [visible, setVisible] = useState(false); //날짜 선택
  const [timeVisible ,setTimeVisible] = useState(false); //시간 선택
  const [currentPickingDate, setCurrentPickingDate] = useState<DateType>(null); //start end 구분
  const [repeats, setRepeats] = useState(false); //반복 여부
  const [repeatWeeks, setRepeatWeeks] = useState<number>(0);
  const [message, setMessage] = useState('');
  const [isSubmitting, setisSubmitting] = useState<boolean>(false);
  const [selectTime, setSelectTime] = useState<string>(DEFAULT_TIME);

  //날짜 선택
  const onPressDate = (type: DateType) => {
    setCurrentPickingDate(type);
    setVisible(true);
  }

  //날짜 취소
  const onCancel = () => {
    setVisible(false);
    setCurrentPickingDate(null);
  }

  //날짜 선택
  const handleDateConfirm = (date: Date) => {
    const formattedDate = formatDate(date);

    if (currentPickingDate === 'start') {
      setStartDate(formattedDate);
    } else if (currentPickingDate === 'end') {
      setEndDate(formattedDate);
    }
    
    onCancel();
  };

  //시간 선택 / 취소
  const onPressTime = () => {
    setTimeVisible(true);
  }
  
  const onTimeCancle = () => {
    setTimeVisible(false);
  }

  //선택 완료
  const handleTimeConfirm = (date:Date) => {
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    setSelectTime(`${hours}:${minutes}`);

    onTimeCancle();
  }

  //초기 date값
  const initialDate = () => {
    if (currentPickingDate === 'start' && startDate) return new Date(startDate);
    if (currentPickingDate === 'end' && endDate) return new Date(endDate);
    return new Date();
  };

  //초기 time값
  const initialTime = () => {
    const [hours, minutes] = selectTime.split(":").map(Number);
    const date = new Date();

    date.setHours(hours);
    date.setMinutes(minutes);
    return date;
  }

  //최소 날짜
  const minimumDate = () => {
    if (currentPickingDate === 'end' && startDate) {
        return new Date(startDate);
    }
    return undefined;
  };

  //0보다 큰가? 검사
  const weekChk = (): number => {
    return repeats ? (repeatWeeks <= 0 ? 1 : repeatWeeks) : 0;
  }

  //문자열->숫자 변환
  const handleRepeatWeeksChange = (text: string) => {
    const newNumber = Number(text.replace(/[^0-9]/g, ''));

    if (!isNaN(newNumber)) {
      setRepeatWeeks(newNumber);
    }
  };

  //일정 저장
  const handelSubmit = async () => {
    if(isSubmitting || isSaving) return;

    if(!name.trim()) { 
      setMessage('일정 이름을 입력해주세요.');
      return; 
    }

    if (!startDate || !endDate) {
      setMessage('시작 날짜와 종료 날짜를 모두 선택해주세요.');
      return; 
    }

    const start = parseDate(startDate);
    const end = parseDate(endDate);
    
    if(start.getTime() > end.getTime()) {
      setMessage('시작 날짜는 종료 날짜보다 빠를 수 없습니다.');
      return;
    }

    setisSubmitting(true);

    try {
      const schedulesToSave: Schedule[] = [];
      const originalId = Number(Date.now());
      const repeatWeeksInt = weekChk();
      const durationDays = 0;
      // const durationDays = getDateDifferenceInDays(startDate, endDate);

      // const baseSchedule: Schedule = { id: originalId, name, startDate, endDate, repeats, repeatWeeks: repeatWeeksInt, originalId: originalId };
      // schedulesToSave.push(baseSchedule);

      const baseSchedule: Schedule = { 
          id: originalId, 
          name, 
          startDate, 
          endDate: startDate,
          repeats, 
          repeatWeeks: repeatWeeksInt, 
          originalId: originalId,
          time: selectTime,
      };
      // schedulesToSave.push(baseSchedule);

      await handleScheduleAddition([baseSchedule]);

      setMessage('일정이 성공적으로 추가되었습니다.');
      setTimeout(onGoBack, 1000);

      // if (repeats && repeatWeeksInt > 0) {
      //     const repeatIntervalDays = repeatWeeksInt * 7;
      //     const maxDate = parseDate(endDate);

      //     let currentStart = parseDate(baseSchedule.startDate);
      //     currentStart.setUTCDate(currentStart.getUTCDate() + repeatIntervalDays); 

      //     while (currentStart.getTime() <= maxDate.getTime()) {
      //         const newEnd = new Date(currentStart);
      //         newEnd.setUTCDate(currentStart.getUTCDate() + durationDays);

      //         const occurrence: Schedule = {
      //             id: `${originalId}-${schedulesToSave.length}`,
      //             name: baseSchedule.name,
      //             startDate: formatDate(currentStart),
      //             endDate: formatDate(newEnd),
      //             repeats: false,
      //             repeatWeeks: 0,
      //             originalId: originalId,
      //             time: baseSchedule.time,
      //         };
      //         schedulesToSave.push(occurrence);
      //         const nextStart = new Date(currentStart);
      //         nextStart.setUTCDate(nextStart.getUTCDate() + repeatIntervalDays);
      //         currentStart = nextStart;
      //         }
      //       }
      //       await handleScheduleAddition(schedulesToSave);
      //       setMessage('일정이 성공적으로 추가되었습니다.');
      //       setTimeout(onGoBack, 1000);

    } catch (e) {
        setMessage('일정 추가 중 오류가 발생했습니다.');
    } finally {
        setisSubmitting(false);
    }
  };

  return(
    <ScrollView>
      <View style={{backgroundColor:'#ffffffff'}}>
        <Text style={[styles.addTitle, {marginTop:15}]}>일정 이름</Text>
        <TextInput
          style={styles.input}
          value={name}
          onChangeText={setName}
          placeholder="일정 이름"
        />
        <View style={styles.dateGroup}>
          <View style={styles.dateInputContainer}>
            <Text style={styles.addTitle}>시작 날짜</Text>
            <TouchableOpacity onPress={() => onPressDate('start')} activeOpacity={1}>
                <TextInput
                  style={styles.addInput}
                  placeholder="시작 날짜 입력"
                  value={startDate}
                  editable={false}
                  placeholderTextColor={startDate ? '#000000ff' : '#838383ff'}
                  underlineColorAndroid="transparent"
                />
            </TouchableOpacity>
          </View>
          <View style={styles.dateInputContainer}>
            <Text style={styles.addTitle}>종료 날짜</Text>
            <TouchableOpacity onPress={() => onPressDate('end')} activeOpacity={1}>
                <TextInput
                  style={styles.addInput}
                  placeholder="종료 날짜 입력"
                  value={endDate}
                  editable={false}
                  placeholderTextColor={endDate ? '#000000ff' : '#838383ff'}
                  underlineColorAndroid="transparent"
                />
            </TouchableOpacity>
          </View>
        </View>
        <Text style={[styles.addTitle, {marginTop: 10}]}>시간</Text>
        <View style={styles.timeInputContainer}>
          <TouchableOpacity onPress={onPressTime} activeOpacity={1}>
            <TextInput
              style={styles.addInput}
              placeholder="시간 선택"
              value={selectTime} // <-- selectedTime 상태 표시
              editable={false}
              placeholderTextColor={'#000000ff'}
              underlineColorAndroid="transparent"
            />
          </TouchableOpacity>
        </View>
        <DateTimePickerModal
          isVisible={visible} 
          mode="date"
          date={initialDate()} 
          minimumDate={minimumDate()}
          onConfirm={handleDateConfirm}
          onCancel={onCancel}
          locale="ko_KR"
        />
        <DateTimePickerModal
          isVisible={timeVisible}
          mode="time"
          date={initialTime()}
          onConfirm={handleTimeConfirm}
          onCancel={onTimeCancle}
          locale="ko_KR"
        />

        <Text style={styles.addTitle}>반복 여부</Text>
        <View style={styles.buttonRow}>
          <TouchableOpacity
            onPress={() => setRepeats(true)}
            style={[styles.repeatButton, repeats ? styles.repeatSelected : styles.repeatUnselected]}>
            <Text style={[styles.repeatButtonText, repeats ? { color: 'white' } : {}]}>O</Text>
          </TouchableOpacity>
          <TouchableOpacity
            onPress={() => setRepeats(false)}
            style={[styles.repeatButton, !repeats ? styles.repeatSelected : styles.repeatUnselected]}>
            <Text style={[styles.repeatButtonText, !repeats ? { color: 'white' } : {}]}>X</Text>
          </TouchableOpacity>
        </View>

        {repeats && (
          <View style={styles.repeatPeriodBox}>
            <Text style={[styles.addTitle, {paddingLeft: 10,}]}>반복 설정</Text>
            <View style={styles.inputRow}>
              <TextInput 
                style={[styles.inputWeek, styles.repeatInput]} 
                value={String(repeatWeeks)} 
                onChangeText={handleRepeatWeeksChange} 
                keyboardType="numeric" 
                maxLength={2} 
              />
              <Text style={{ fontSize: 16, alignSelf: 'center', marginRight: 15,}}>주</Text>
            </View>
          </View>
        )}

        <TouchableOpacity onPress={handelSubmit} disabled={isSubmitting || isSaving} style={[styles.submitButton, (isSubmitting || isSaving) ? styles.submitDisabled : {}]}>
          {isSubmitting || isSaving ? (
              <ActivityIndicator color="white" />
          ) : (
              <Text style={styles.submitButtonText}>일정 저장</Text>
          )}
        </TouchableOpacity>

        <TouchableOpacity onPress={onGoBack} disabled={isSubmitting || isSaving} style={styles.cancelButton}>
                <Text style={styles.cancelButtonText}>취소</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  )
}


//캘린더 메인 화면
interface CalendarScreenProps extends NativeStackScreenProps<RootStackParamList, 'CalendarScreen'>{
  schedules: Schedule[];
  deleteSchedule: (id: number) => Promise<void>;
}

function CalendarScreen({navigation, schedules, deleteSchedule}:CalendarScreenProps){
  //const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();

  const today = formatDate(new Date());
  const [selectDate, setSelectDate] = useState<string>('');
  //const [currentScreen, setCurrentScreen] = useState<'calendar' | 'add'>('calendar');

  //API 관리
  const [isDeleting, setIsDeleting] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  //일정 삭제
  const handleDeleteSchedule = async (idToDelete: number) => {
    setIsDeleting(true);
    setError(null);
    try{
      await deleteSchedule(idToDelete);
    }catch(e){
      setError('일정 삭제에 실패했습니다.');
      throw e;
    } finally{
      setIsDeleting(false);
    }
  }

  //표시 및 목록 불러오기
  const markedDates = () => {
    // const marks: MarkedDates = { [selectDate]: {selected:true, disableTouchEvent: true}}
    const marks: MarkedDates = {};

    schedules.forEach(schedule => {
      let current = parseDate(schedule.startDate);
      const end = parseDate(schedule.endDate);
      const color = '#ffe883ff';
      const isMultiDay = getDateDifferenceInDays(schedule.startDate, schedule.endDate) > 0;

      while(current.getTime() <= end.getTime()){
        const dateString = formatDate(current);
        const existingMark = marks[dateString] || {};

        marks[dateString] = {
          ...existingMark,
          startingDay: dateString === schedule.startDate,
          endingDay: dateString === schedule.endDate,
          color: color,
        };
        const nextDay = new Date(current);
        nextDay.setUTCDate(nextDay.getUTCDate()+1);
        current=nextDay;
      }
    })
    if (selectDate){
      marks[selectDate] = {
        ...(marks[selectDate] || {}),
        textColor: '#00ff0dff',
        selected: true,
        disableTouchEvent: true,
      }
    }
    return marks;
  }

  //날짜 선택했을 때
  const schedulesForSelectDate = () => {
    return schedules.filter(schedule => {
      const start = parseDate(schedule.startDate);
      const end = parseDate(schedule.endDate);
      const selected = parseDate(selectDate);
      return selected.getTime() >= start.getTime() && selected.getTime() <= end.getTime();
    }).sort((a, b) => new Date(a.startDate) as any - (new Date(b.startDate) as any));
  }

  const datePress = (day:DateData)=>{
    setSelectDate(day.dateString);
  }

  return(
    <View>
      <Calendar
        style={[styles.calendar]}
        monthFormat={'yyyy년 MM월'}
        onDayPress={datePress}
        markedDates={markedDates()}
        markingType={'period'}
        theme={{
          todayTextColor:'#ff0040ff',
        }}
      />
      <ScrollView style={styles.scheduleListContainer}>
        {schedulesForSelectDate().map((schedule) => (
          <ScheduleItem
            key={schedule.id}
            schedule={schedule}
            onDelete={handleDeleteSchedule}
            isDeleting={isDeleting}
          />
        ))}
      </ScrollView>
      <View>
        <TouchableOpacity
          style={[styles.plusButton]}
          onPress={()=>navigation.navigate('ScheduleAdd')}
        >
          <MaterialCommunityIcons name="plus" size={30}/>
        </TouchableOpacity>
      </View>
    </View>
  )
  
}

const styles = StyleSheet.create({
  //메인 화면 스타일
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor:'#ffffff',
  },
  headerTitle:{
    fontSize:20,
    fontWeight:'bold',
    color:'#000000',
  },
  calendar:{
    borderBottomWidth: 3,
    borderBottomColor: '#e0e0e0',
    height:370,
    borderTopWidth: 0,
  },
  plusButton:{
    position: 'absolute',
    borderWidth:1,
    borderColor:"#ffcb59ff",
    backgroundColor:"#ffcb59ff",
    borderRadius:27,
    height: 54,
    width: 54,
    alignItems: 'center',
    justifyContent: 'center',
    bottom:15,
    right:20,
    zIndex:999
  },
  scheduleListContainer: {
    flex: 1,
    paddingHorizontal: 15,
    paddingTop: 10,
    minHeight: 250,
    backgroundColor:"#f5f5f5ff",
  },

  //스케줄 아이템 스타일
  itemContainer: {
    flexDirection: 'row',
  },
  itemTitle:{
    fontSize: 16,
    fontWeight: 'bold',
  },
  deleteButton:{
    padding: 10,
    borderRadius: 50,
  },

  //스케줄 추가
  addTitle:{
    fontSize: 16,
    fontWeight: 'bold',
    alignContent:'center',
    marginBottom: 10,
    marginLeft: 10,
  },
  input:{
    width:350,
    height:45,
    backgroundColor:'#ffffff',
    borderBottomWidth: 1,
    borderColor:'#cfcfcfff',
    marginBottom: 40,
    marginLeft: 15,
  },
  dateGroup:{
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginBottom: 10,
  },
  dateInputContainer:{
    width: '45%',
    borderBottomWidth: 1,
    borderColor:'#cfcfcfff',
    marginLeft: 3,
    marginBottom: 20,
  },
  addInput:{
    borderWidth: 1,
    borderColor: '#ffffffff',
    padding: 12,
    borderRadius: 8,
    fontSize: 16,
    backgroundColor: '#ffffffff',
  },
  buttonRow:{
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 20,
    marginBottom: 30,
    marginHorizontal: 5,
  },
  repeatButton:{
    flex: 1,
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
    marginHorizontal: 8,
  },
  repeatSelected:{
    backgroundColor: '#ffcb59ff',
    elevation: 3,
  },
  repeatUnselected:{ 
    backgroundColor: '#e7e7e7ff',
  },
  repeatButtonText:{ 
    fontWeight: 'bold',
    fontSize: 16,
    color: '#374151',
  },
  repeatPeriodBox:{
  paddingTop: 10,
  backgroundColor: '#fff0e0ff',
  borderWidth: 1,
  borderColor: '#ffdfacff',
  flexDirection: 'row', 
  justifyContent: 'space-between', 
  alignItems: 'center'
  },
  submitButton:{
    backgroundColor: '#ffbb00ff',
    paddingVertical: 15,
    borderRadius: 10,
    alignItems: 'center',
    marginTop: 20,
  },
  submitDisabled:{ 
    backgroundColor: '#9ca3af',
  },
  submitButtonText:{ 
    fontSize: 18,
    color: 'white',
    fontWeight: 'bold',
  },
  cancelButton:{
    paddingVertical: 10,
    alignItems: 'center',
    marginTop: 10,
  },
  cancelButtonText:{
    fontSize: 16,
    color: '#000000ff',
    fontWeight: 'bold',
    marginBottom: 10,
  },
  inputWeek:{
    backgroundColor:'#ffffff',
    borderBottomWidth: 1,
    borderColor:'#cfcfcfff',
    marginBottom: 20,
    marginLeft: 15,
  },
  inputRow:{ 
    flexDirection: 'row',
    alignItems: 'center', 
  },
  repeatInput:{
    marginTop: 10,
    width: 60,
    height: 40,
    textAlign: 'center',
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 5,
    backgroundColor: '#ffffff',
    marginRight: 5,
  },
  timeInputContainer: {
    width: '90%', 
    borderBottomWidth: 1,
    borderColor:'#cfcfcfff',
    marginLeft: 15,
    marginBottom: 20,
  },

  
});

export default CalendarMainScreen;