import React, { useEffect, useState } from "react";
import { StyleSheet, Text, View, TextInput, TouchableOpacity, FlatList, Alert, ActivityIndicator } from 'react-native';
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { createNativeStackNavigator, NativeStackNavigationProp, NativeStackScreenProps } from "@react-navigation/native-stack";
import { useNavigation, useRoute, RouteProp } from "@react-navigation/native";
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import AsyncStorage from "@react-native-async-storage/async-storage";
import { createDrawerNavigator, DrawerNavigationProp } from '@react-navigation/drawer'
import axios, { AxiosRequestConfig } from 'axios';
import { RootStackParamList } from "./types";

import DogAi from './DogAi';

const Stack = createNativeStackNavigator<RootStackParamList>();
const API_URL = "http://10.0.2.2:8090";

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
    backgroundColor: '#ffcf88ff',
};


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

interface ApiResponse {
    success: boolean;
    message: string;
    status: number;
    data: any;
}

interface Note {
  id: number;
  title: string;
  description: string;
  symptomDate: string;
  createdAt: string;
}

// JWT 토큰을 사용해 인증된 API 요청을 처리하는 헬퍼 함수(fetchWithAuth) 사용
const fetchWithAuth = async (url: string, method: 'GET' | 'POST' | 'PUT' | 'DELETE' = 'GET', data?: any): Promise<ApiResponse> => {
  const token = await AsyncStorage.getItem('userToken');
  if (!token) {
    throw new Error('인증 토큰이 없습니다. 로그인 상태를 확인해주세요.');
  }

  const config: AxiosRequestConfig = {
      method: method,
      url: `${API_URL}${url}`,
      headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
      },
      data: data,
      // 204 No Content도 오류로 처리하지 않도록 설정
      validateStatus: (status) => status >= 200 && status < 300
  };

  try {
    const response = await axios(config);

    // 서버가 204 No Content를 반환한 경우 (DELETE 성공)
    if (response.status === 204) {
      // 클라이언트가 기대하는 ApiResponse 형태로 변환하여 반환
      return { success: true, message: "삭제 성공", status: 204, data: null };
    }

    // 200, 201 등의 응답일 경우, 서버에서 온 ApiResponse 구조를 그대로 반환
    return response.data as ApiResponse;

  } catch (error: any) {
    // 4xx/5xx 등 HTTP 오류 처리
    const errorMessage = error.response?.data?.message || '네트워크 오류 또는 서버 오류가 발생했습니다.';
    const status = error.response?.status || 500;

    // fetchWithAuth를 사용하는 모든 곳에서 catch 블록으로 예외를 처리
    throw new Error(errorMessage);
  }
};


// DogNoteScreen
function DogNoteScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList, 'DogNote'>>();

  const [notes, setNotes] = useState<Note[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // API로부터 증상 기록 목록을 불러오는 함수
  const fetchNotes = async () => {
    setIsLoading(true);
    try {
      const response = await fetchWithAuth('/symptoms/my');

      if (response.success && Array.isArray(response.data)) {
        const fetchedNotes: Note[] = response.data.map((item: any) => ({
          id: item.id,
          title: item.title,
          description: item.description,
          // symptomDate가 LocalDateTime 형태이므로 YYYY-MM-DD로 변환
          symptomDate: item.symptomDate ? item.symptomDate.split('T')[0] : '',
          createdAt: item.createdAt,
        }));
        setNotes(fetchedNotes);
      } else {
        // 서버가 논리적 실패(success: false)를 반환했을 때 (ex: 기록 없음)
        // 백엔드에서 빈 리스트를 반환하도록 수정했으므로, 이 else 블록은 예상치 못한 오류에만 해당됨
        setNotes([]);
      }
    } catch (error) {
      // fetchWithAuth에서 throw된 심각한 오류 처리 (네트워크 오류, 토큰 오류 등)
      const errorMessage = error instanceof Error ? error.message : "알 수 없는 오류";
      Alert.alert("조회 오류", errorMessage);
      setNotes([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    const unsubscribe = navigation.addListener('focus', () => {
      fetchNotes();
    });
    return unsubscribe;
  }, [navigation]);


  // 증상 기록 저장 또는 수정 함수 (API 연동)
  const saveOrUpdateNote = async (title: string, description: string, symptomDate: string, id?: number): Promise<boolean> => {
    // 백엔드는 LocalDateTime을 기대하므로 YYYY-MM-DDT00:00:00 형태로 전송
    const noteData = { title, description, symptomDate: symptomDate + "T00:00:00" };
    try {
      if (id) { // 수정 (PUT 요청)
        const response = await fetchWithAuth(`/symptoms/${id}`, 'PUT', noteData);
        if (response.success) {
          Alert.alert("성공", "증상 기록이 수정되었습니다.");
        } else {
          Alert.alert("실패", response.message || "수정에 실패했습니다.");
        }
      } else { // 새로 추가 (POST 요청)
        const response = await fetchWithAuth('/symptoms', 'POST', noteData);
        if (response.success) {
          
        } else {
          Alert.alert("실패", response.message || "추가에 실패했습니다.");
        }
      }
      fetchNotes(); // 목록 새로고침
      return true; // 성공 시 true 반환
    } catch (error) {
      Alert.alert("오류", `작업 중 오류 발생: ${error instanceof Error ? error.message : "알 수 없는 오류"}`);
      return false; // 실패 시 false 반환
    }
  };


  // 증상 기록 삭제 함수 (API 연동)
  const deleteNote = async (id: number) => {
    Alert.alert(
      "삭제 확인",
      "정말로 이 기록을 삭제하시겠습니까?",
      [
        {
          text: "취소",
          style: "cancel"
        },
        {
          text: "삭제",
          style: "destructive",
          onPress: async () => {
            try {
              // fetchWithAuth는 204 응답 시 { success: true }를 반환
              const response = await fetchWithAuth(`/symptoms/${id}`, 'DELETE');

              // fetchWithAuth에서 성공적으로 { success: true }를 반환받았는지 확인
              if (response.success) {
                  Alert.alert("삭제 성공", "증상 기록이 삭제되었습니다.");
              } else {
                  // 서버에서 논리적 실패(4xx 응답을 받음)를 명시적으로 처리
                  Alert.alert(
                      "삭제 실패",
                      response.message || "서버 응답에서 삭제 실패가 보고되었습니다."
                  );
              }

              fetchNotes();

              // 삭제 후 상세 화면이었다면 목록 화면으로 이동
              const currentRoute = navigation.getState().routes.find(route => route.name === 'NoteDetail');
              if (currentRoute) {
                  navigation.navigate('DogNoteList' as any);
              }
            } catch (error) {
              // fetchWithAuth에서 HTTP 오류 등으로 예외가 throw된 경우 (403 권한 없음, 404 기록 없음, 네트워크 문제 등)
              const errorMessage = error instanceof Error ? error.message : "알 수 없는 오류";
              Alert.alert(
                  "삭제 오류",
                  `삭제 중 오류 발생: ${errorMessage}`
              );
            }
          }
        }
      ]
    );
  };

  return (
    <Stack.Navigator initialRouteName="DogNoteList">
      <Stack.Screen
        name="DogNoteList"
        options={({ navigation }) => ({
          title: '증상 기록',
          headerRight: () => (
            <View style={styles.headerRightContainer}>
              <TouchableOpacity
                onPress={() => navigation.navigate('AddNote', { saveNote: saveOrUpdateNote })}
              >
                <MaterialCommunityIcons name="plus" size={30} color="#a75e00ff" style={{marginHorizontal: 5}} />
              </TouchableOpacity>
              <DrawerButton/>
            </View>
          ),
          headerStyle: {
            backgroundColor: '#ffcb7dff',
          },
          headerTitleStyle: {
            fontSize: 20,
            fontWeight: 'bold',
            color: '#000000ff',
            paddingLeft: 5,
          }
        })}
      >
        {props => (
          <DogNoteList
            {...props}
            notes={notes}
            deleteNote={deleteNote}
            navigateToEdit={(note) => props.navigation.navigate('EditNote', { note, saveNote: saveOrUpdateNote })}
            isLoading={isLoading}
            fetchNotes={fetchNotes}
          />
        )}
      </Stack.Screen>

      <Stack.Screen
        name="AddNote"
        options={{ title: '새 기록 추가' ,
          headerStyle: {
            backgroundColor: '#ffcb7dff',
            },
            headerTitleStyle: {
              fontSize: 20,
              fontWeight: 'bold',
              color: '#000000ff',
          }}}
      >
        {props => <AddNote {...props} />}
      </Stack.Screen>

      <Stack.Screen
        name="EditNote"
        options={{ title: '기록 수정' ,
          headerStyle: {
            backgroundColor: '#ffcb7dff',
            },
            headerTitleStyle: {
              fontSize: 20,
              fontWeight: 'bold',
              color: '#000000ff',
          }
        }}
      >
        {props => <EditNote {...props} />}
      </Stack.Screen>

      <Stack.Screen
        name="NoteDetail"
        options={{ title: '기록 상세' ,
          headerStyle: {
            backgroundColor: '#ffcb7dff',
            },
            headerTitleStyle: {
              fontSize: 20,
              fontWeight: 'bold',
              color: '#000000ff',
          }
        }}
      >
        {props => <NoteDetail {...props} deleteNote={deleteNote} />}
      </Stack.Screen>

      <Stack.Screen name="DogAi" component={DogAi} options={({route})=>({
                      headerShown: true,
                      headerStyle:HEADER_STYLE,
                      headerTitle:(props)=>(
                        <CustomTitle {...props} title="AI 진단"/>
                      ),
                    })}/>
    </Stack.Navigator>
  );
}

// 증상 기록 목록 화면 (DogNoteList)
interface DogNoteListProps {
  notes: Note[];
  deleteNote: (id: number) => void;
  navigateToEdit: (note: Note) => void;
  isLoading: boolean;
  fetchNotes: () => void;
  navigation: NativeStackNavigationProp<RootStackParamList, 'DogNoteList'>;
}

function DogNoteList({ notes, deleteNote, navigateToEdit, isLoading, navigation }: DogNoteListProps) {

  const renderItem = ({ item }: { item: Note }) => (
    <View>
      <View style={styles.noteCardContainer}>
        <TouchableOpacity // 카드
          style={styles.noteCardContent}
          onPress={() =>
            navigation.navigate('NoteDetail', {
              note: item,
              navigateToEdit: navigateToEdit,
              deleteNote: deleteNote
            })}
        >
          <Text style={styles.cardTitle} numberOfLines={1}>{item.title} ({item.symptomDate})</Text>
          <Text style={styles.cardData} numberOfLines={1}>
            {item.description.length > 30 ? item.description.substring(0, 30) + '...' : item.description}
          </Text>
        </TouchableOpacity>
        <TouchableOpacity // 카드 삭제
          style={styles.delectButton}
          onPress={() => deleteNote(item.id)}
        >
          <MaterialCommunityIcons name="delete" size={24} color={"#707070ff"} />
        </TouchableOpacity>
      </View>
    </View>
  );

  if (isLoading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#ff9100ff" />
        <Text style={styles.emptyText}>기록을 불러오는 중...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container2}>
      <FlatList
        style={{marginTop: 15}}
        data={notes}
        renderItem={renderItem}
        keyExtractor={item => item.id.toString()}
        ListEmptyComponent={() => (
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>노트가 비어있습니다.</Text>
            <Text style={styles.emptyText}>우측 상단의 + 버튼을 눌러 추가하세요.</Text>
          </View>
        )}
      />
      <View style={styles.aiContainer}>
        <TouchableOpacity
          style={[styles.aiButton]}
          onPress={()=>navigation.navigate('DogAi')}
        >
          <View style={{flexDirection:'row'}}>
          {/* <MaterialCommunityIcons name="chat" size={30} color={'#ff9100ff'}/> */}
            <Text style={{fontWeight: 'bold', textAlign:'center', color:'#351500ff'}}>AI 진단하러 가기 </Text>
            <MaterialCommunityIcons name="arrow-right" size={20} color={'#351500ff'}/>
          </View>
        </TouchableOpacity>
      </View>
    </View>
  );
}


// 증상 기록 추가 화면 (AddNote)
type AddNoteRouteProp = RouteProp<RootStackParamList, 'AddNote'>;
type AddNoteScreenProps = NativeStackScreenProps<RootStackParamList, 'AddNote'>;

function AddNote({navigation, route} : AddNoteScreenProps) {
  // const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList, 'AddNote'>>();
  // const route = useRoute<AddNoteRouteProp>();
  const { saveNote } = route.params;

  const [noteTitle, setNoteTitle] = useState('');
  const [noteDescription, setNoteDescription] = useState('');
  // 기본값을 오늘 날짜 (YYYY-MM-DD)로 설정
  const [symptomDate, setSymptomDate] = useState(new Date().toISOString().split('T')[0]);

  const handleSave = async () => {
    if (!noteTitle.trim() || !noteDescription.trim() || !symptomDate.trim()) {
      Alert.alert("경고", "제목, 내용, 날짜는 필수 입력 사항입니다.");
      return;
    }
    const success = await saveNote(noteTitle, noteDescription, symptomDate);
    if (success) {
      navigation.goBack();
    }
  };

  React.useLayoutEffect(() => {
    navigation.setOptions({
      title: "새 기록 추가",
      headerRight: () => (
        <TouchableOpacity
          onPress={handleSave}
          disabled={!noteTitle.trim() || !noteDescription.trim() || !symptomDate.trim()}
        >
          <MaterialCommunityIcons
            name="check-circle-outline"
            size={30}
            color={(!noteTitle.trim() || !noteDescription.trim() || !symptomDate.trim()) ? '#acacacff' : '#00a500ff'} />
        </TouchableOpacity>
      ),
    });
  }, [navigation, noteTitle, noteDescription, symptomDate]);

  return (
    <KeyboardAwareScrollView contentContainerStyle={styles.scrollContainer}>
      <View style={styles.container}>
        <TextInput
          style={[styles.inputTitle, { height: 55 }]}
          placeholder="제목 (예: 잦은 기침)"
          value={noteTitle}
          onChangeText={setNoteTitle}>
        </TextInput>
        <TextInput
          style={[styles.inputDate, { height: 55 }]}
          placeholder="증상 발생 날짜 (YYYY-MM-DD)"
          value={symptomDate}
          onChangeText={setSymptomDate}
          keyboardType="default"
        />
        <TextInput
          style={styles.input}
          placeholder="내용을 입력하세요. (자세한 증상, 행동 변화 등)"
          textAlign="left"
          textAlignVertical="top"
          value={noteDescription}
          onChangeText={setNoteDescription}
          multiline={true}>
        </TextInput>
      </View>
    </KeyboardAwareScrollView>
  );
}


// 증상 기록 수정 화면 (EditNote)
type EditNoteScreenProps = NativeStackScreenProps<RootStackParamList, 'EditNote'>;
type EditNoteRouteProp = RouteProp<RootStackParamList, 'EditNote'>;

function EditNote({navigation, route}: EditNoteScreenProps) {
  // const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList, 'EditNote'>>();
  // const route = useRoute<EditNoteRouteProp>();
  const { note, saveNote } = route.params;

  const [noteTitle, setNoteTitle] = useState(note.title || '');
  const [noteDescription, setNoteDescription] = useState(note.description || '');
  const [symptomDate, setSymptomDate] = useState(note.symptomDate || new Date().toISOString().split('T')[0]); // YYYY-MM-DD

  const handleSave = async () => {
    if (!noteTitle.trim() || !noteDescription.trim() || !symptomDate.trim()) {
      Alert.alert("경고", "제목, 내용, 날짜는 필수 입력 사항입니다.");
      return;
    }
    const success = await saveNote(noteTitle, noteDescription, symptomDate, note.id);
    if (success) {
      // 수정 후 상세 화면을 건너뛰고 목록 화면으로 돌아가기 위해 pop(2)를 사용
      navigation.pop(2);
    }
  };

  React.useLayoutEffect(() => {
    navigation.setOptions({
      title: "기록 수정",
      headerRight: () => (
        <TouchableOpacity
          onPress={handleSave}
          disabled={!noteTitle.trim() || !noteDescription.trim() || !symptomDate.trim()}
        >
          <MaterialCommunityIcons
            name="check-circle-outline"
            size={30}
            color={(!noteTitle.trim() || !noteDescription.trim() || !symptomDate.trim()) ? '#acacacff' : '#00a500ff'} />
        </TouchableOpacity>
      ),
    });
  }, [navigation, noteTitle, noteDescription, symptomDate, note.id]);

  return (
    <KeyboardAwareScrollView contentContainerStyle={styles.scrollContainer}>
      <View style={styles.container}>
        <TextInput
          style={[styles.inputTitle, { height: 55 }]}
          placeholder="제목"
          value={noteTitle}
          onChangeText={setNoteTitle}>
        </TextInput>
        <TextInput
          style={[styles.inputDate, { height: 55 }]}
          placeholder="증상 발생 날짜 (YYYY-MM-DD)"
          value={symptomDate}
          onChangeText={setSymptomDate}
          keyboardType="default"
        />
        <TextInput
          style={styles.input}
          placeholder="내용을 입력하세요."
          textAlign="left"
          textAlignVertical="top"
          value={noteDescription}
          onChangeText={setNoteDescription}
          multiline={true}>
        </TextInput>
      </View>
    </KeyboardAwareScrollView>
  );
}


// 증상 기록 상세 보기 화면 (NoteDetail)
type NoteDetailRouteProp = RouteProp<RootStackParamList, 'NoteDetail'>;

interface NoteDetailProps {
  navigation: NativeStackNavigationProp<RootStackParamList, 'NoteDetail'>;
  route: NoteDetailRouteProp;
  deleteNote: (id: number) => void;
}

function NoteDetail({ navigation, route, deleteNote }: NoteDetailProps) {
  const { note, navigateToEdit } = route.params;

  React.useLayoutEffect(() => {
    navigation.setOptions({
      headerRight: () => (
        <View style={{ flexDirection: 'row' }}>
          {/* 수정 버튼 */}
          <TouchableOpacity
            onPress={() => navigateToEdit(note)}
            style={{ marginRight: 15 }}
          >
            <MaterialCommunityIcons name='pencil' size={24} color="#a75e00ff" />
          </TouchableOpacity>
          {/* 삭제 버튼 */}
          <TouchableOpacity
            onPress={() => deleteNote(note.id)}
          >
            <MaterialCommunityIcons name="delete" size={24} color="#a75e00ff" />
          </TouchableOpacity>
        </View>
      )
    });
  }, [navigateToEdit, note, deleteNote, note.id]);

  return (
    <KeyboardAwareScrollView contentContainerStyle={styles.scrollContainer}>
      <View style={styles.container}>
        <Text style={styles.detailTitle}>{note.title}</Text>
        <Text style={styles.detailDate}>증상 날짜: {note.symptomDate}</Text>
        <Text style={styles.detailData}>{note.description}</Text>
        <Text style={styles.detailCreated}>작성일: {new Date(note.createdAt).toLocaleString('ko-KR')}</Text>
      </View>
    </KeyboardAwareScrollView>
  );
}

// 스타일 정의
const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    width: '100%',
  },
  scrollContainer: {
    flexGrow: 1,
    backgroundColor: '#f0f0f0',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },

  // 노트 기본 화면 스타일
  container2: {
    flex: 1,
    paddingHorizontal: 1,
    paddingTop: 3,
    backgroundColor: '#ffffffff',
    alignContent: 'center',
    justifyContent: 'center',
  },
  emptyText: {
    color: '#707070ff',
    fontSize: 16,
    marginTop: 5,
    textAlign: 'center',
  },
  emptyContainer: {
    alignItems: 'center',
    padding: 30,
    marginTop: 50,
  },
  noteCardContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(255, 247, 240, 1)',
    borderColor: '#ffcb8fff',
    borderWidth: 1,
    borderRadius: 15,
    marginBottom: 8,
    marginHorizontal: 10,
    paddingRight: 10,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 1.5,
  },
  noteCardContent: {
    flex: 1,
    paddingVertical: 5,
    paddingHorizontal: 5,
  },
  delectButton: {
    padding: 5,
    justifyContent: 'center',
    alignItems: 'center',
  },

  cardTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginTop: 12,
    marginBottom: 10,
    paddingLeft: 10,
  },
  cardData: {
    fontSize: 14,
    color: '#555',
    paddingLeft: 10,
    marginBottom: 15,
  },

  // 노트 추가/수정 스타일
  inputTitle: {
    width: '100%',
    backgroundColor: '#ffffffff',
    borderBottomWidth: 1,
    borderColor: '#e0e0e0',
    paddingLeft: 15,
    fontWeight: 'bold',
    fontSize: 20,
  },
  inputDate: {
    width: '100%',
    backgroundColor: '#ffffffff',
    borderBottomWidth: 1,
    borderColor: '#e0e0e0',
    paddingLeft: 15,
    fontSize: 16,
    color: '#333',
  },
  input: {
    width: '100%',
    flex: 1,
    minHeight: 300,
    backgroundColor: '#ffffffff',
    paddingLeft: 15,
    paddingTop: 15,
    fontSize: 16,
    textAlign: 'left',
    textAlignVertical: 'top',
  },

  // 노트 상세 스타일
  detailTitle: {
    width: '100%',
    minHeight: 50,
    backgroundColor: '#ffffffff',
    borderBottomWidth: 1,
    borderColor: '#e0e0e0',
    paddingLeft: 15,
    fontWeight: 'bold',
    fontSize: 20,
    textAlignVertical: 'center',
    paddingVertical: 10,
  },
  detailDate: {
    width: '100%',
    minHeight: 40,
    backgroundColor: '#ffffffff',
    borderBottomWidth: 1,
    borderColor: '#e0e0e0',
    paddingLeft: 15,
    fontSize: 16,
    color: '#555',
    textAlignVertical: 'center',
    paddingVertical: 5,
  },
  detailData: {
    width: '100%',
    flex: 1,
    backgroundColor: '#ffffffff',
    fontSize: 16,
    paddingLeft: 15,
    paddingTop: 15,
    paddingRight: 15,
    lineHeight: 24,
    paddingBottom: 50,
  },
  detailCreated: {
    width: '100%',
    padding: 15,
    fontSize: 12,
    color: '#888',
    backgroundColor: '#ffffffff',
    textAlign: 'right',
  },

  //AI
  aiContainer:{
    alignItems: 'center',
    justifyContent: 'center',
  },
  aiButton:{
    position: 'absolute',
    backgroundColor:"#ffe6a1ff",
    borderColor: '#ff7b00ff',
    borderRadius:50,
    width: 250,
    height: 35,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex:999,
    bottom: 50
  },
  headerTitle:{
    fontSize:20,
    fontWeight:'bold',
    color:'#000000'
  },
  headerRightContainer:{
    flexDirection: 'row'
  }
});

export default DogNoteScreen;