import React, { use } from "react";
import { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, FlatList, Alert } from 'react-native';
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { createNativeStackNavigator,NativeStackNavigationProp } from "@react-navigation/native-stack";
import { useNavigation, useRoute, RouteProp } from "@react-navigation/native";
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import { RootStackParamList } from "./types";

const Stack = createNativeStackNavigator<RootStackParamList>();

interface Note {
  id: string;
  title: string;
  data: string;
}

interface DogNoteProps {
  notes: Note[];
  deleteNote: (id: string) => void;
  navigateToEdit: (note:Note) => void;
}

function DogNoteScreen(){
  const [notes, setNotes] = useState<Note[]>([]);

  const saveOrUpdateNote = (title: string, data:string, id?:string) => {
    if(id){ //수정
      setNotes(currentNotes =>
        currentNotes.map(note =>
          note.id === id ? { ...note, title, data } : note
        )
      );
    } else{ //새로 추가
        const newNote: Note = {
          id: Date.now().toString(),
          title: title,
          data: data,
        };
        setNotes((currentNotes)=>[newNote, ...currentNotes]);
    }
  }

  //삭제
  const deleteNote = (id: string) => {
    setNotes(currentNotes => currentNotes.filter(note => note.id !== id));
  };

  const navigateToEdit = (note: Note, navigation: NativeStackNavigationProp<RootStackParamList>) => {
    navigation.navigate('DogNotePlus', {
      saveNote: (title: string, data: string) => saveOrUpdateNote(title, data, note.id),
      initialNote: note,
      onSaveAndGoBackToDogNote: () => {
        navigation.pop(2);
      },
    });
  };

  return(
    <Stack.Navigator>
      <Stack.Screen
        name='DogNote'
        children={({navigation}) => 
          <DogNote 
            notes={notes}
            deleteNote={deleteNote}
            navigateToEdit={(note)=>navigateToEdit(note, navigation as NativeStackNavigationProp<RootStackParamList>)} />}
        options={({navigation})=>({
          headerShown:true,
          title: "증상 노트",
          headerRight: ()=>(
            <TouchableOpacity
              onPress={()=>navigation.navigate('DogNotePlus', {saveNote: (title: string, data: string) => saveOrUpdateNote(title, data)})}>
                <MaterialCommunityIcons name="plus" size={30}/>
            </TouchableOpacity>
          ),
          headerStyle:{
            backgroundColor:'#ffd651ff',
          },
          headerTitleStyle:{
            fontSize:20,
            textAlignVertical: 'center',
            fontWeight:'bold',
            color:'#000000ff',
            paddingLeft:5,
          }
        })}
        />
      <Stack.Screen name='DogNotePlus' component={DogNotePlus} options={{title:'노트 추가'}}/>
      <Stack.Screen name='NoteDetail' component={NoteDetail} options={{title:'노트 상세'}}/>
    </Stack.Navigator>
  )
}

//노트 기본 화면
function DogNote({ notes, deleteNote, navigateToEdit }: DogNoteProps){
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  
  const renderItem = ({item}:{item:Note})=>(
    <View style={styles.noteCardContainer}>
      <TouchableOpacity //카드
        style={styles.noteCardContent}
        onPress={()=>
          navigation.navigate('NoteDetail', {note:item, deleteNote:deleteNote, navigateToEdit:navigateToEdit})}>
          <Text style={styles.cardTitle} numberOfLines={1}>{item.title}</Text>
          <Text style={styles.cardData} numberOfLines={1}>{item.data.length > 20 ? item.data.substring(0, 20) + '...' : item.data}</Text>
      </TouchableOpacity>
      <TouchableOpacity //카드 삭제
        style={styles.delectButton}
        onPress={()=>{
          Alert.alert(
            "노트 삭제",
            '노트를 삭제하시겠습니까?',
            [
              {text:'취소', style: "cancel"},
              {
                text: "삭제",
                style: "destructive",
                onPress: ()=> deleteNote(item.id)
              }
            ],
            {cancelable: true}
          )
        }}
      >
        <MaterialCommunityIcons name="delete" size={24} color={"#707070ff"}/>
      </TouchableOpacity>
    </View>
  )

  return(
    <View style={styles.container2}>
      <FlatList
        data={notes}
        renderItem={renderItem}
        keyExtractor={item=>item.id}
        ListEmptyComponent={()=>(
          <View style={styles.emptyContainer}>
            <Text style={[styles.emptyText, {textAlignVertical:'center'}]}>노트가 비어있습니다.</Text>
          </View>
        )}
      />
    </View>
  )
}

type DogNotePlusRouteProp = RouteProp<RootStackParamList, 'DogNotePlus'>;

//노트 추가
function DogNotePlus() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const route = useRoute<DogNotePlusRouteProp>();
  const {saveNote, initialNote, onSaveAndGoBackToDogNote} = route.params;

  const [noteTitle, setNoteTitle] = useState(initialNote?.title || '');
  const [noteData, setNoteData] = useState(initialNote?.data || '');

  React.useLayoutEffect(()=>{
    navigation.setOptions({
      title: initialNote ? "노트 수정" : "노트 추가",
      headerRight: ()=> (
        <TouchableOpacity
          onPress={()=>{
            saveNote(noteTitle, noteData, initialNote?.id);
            if (onSaveAndGoBackToDogNote) {
               onSaveAndGoBackToDogNote();
            } else {
              navigation.goBack();
            }
          }}
          disabled={!noteTitle && !noteData}
        >
          <MaterialCommunityIcons
            name="check-circle-outline"
            size={30}
            color={(!noteTitle && !noteData) ? '#acacacff' : '#ff9100ff'}/>
        </TouchableOpacity>
      ),
    })
  }, [navigation, saveNote, noteTitle, noteData])

  return(
    <KeyboardAwareScrollView>
      <View style={styles.container}>
        <TextInput
          style={[styles.inputTitle, {height:55}]}
          placeholder="제목"
          value={noteTitle}
          onChangeText={setNoteTitle}>
        </TextInput>
        <TextInput
          style={styles.input}
          placeholder="내용을 입력하세요."
          textAlign="left"
          textAlignVertical="top"
          value={noteData}
          onChangeText={setNoteData}
          multiline={true}>
        </TextInput>
      </View>
    </KeyboardAwareScrollView>
  )
}

//노트 상세 보기
type NoteDetailRouteProp = RouteProp<RootStackParamList, 'NoteDetail'>;

function NoteDetail(){
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const route = useRoute<NoteDetailRouteProp>();
  const { note, navigateToEdit } = route.params;

  React.useLayoutEffect(()=>{
    navigation.setOptions({
      headerRight: ()=>(
        <TouchableOpacity
          onPress={()=> navigateToEdit(note)}
          style={{marginRight: 15}}
        >
          <MaterialCommunityIcons name='pencil' size={24} color="#ff9100ff"/>
        </TouchableOpacity>
      )
    })
  }, [navigateToEdit, note, navigateToEdit]);

  return(
    <KeyboardAwareScrollView>
      <View style={styles.container}>
        <Text style={styles.detailTitle}>{note.title}</Text>
        <Text style={styles.detailData}>{note.data}</Text>
      </View>
    </KeyboardAwareScrollView>
  )
}






//스타일 정의
const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
  },

  //노트 기본 화면 스타일
  container2:{
    flex: 1,
    paddingHorizontal: 1,
    paddingTop: 3,
  },
  emptyText:{
    color:'#707070ff',
    fontSize: 16,
    marginTop: 5,
  },
  emptyContainer:{
    alignItems: 'center',
    padding: 30,
  },
  noteCardContainer:{
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#ffffffff',
    borderColor: '#acacacff',
    borderWidth: 2,
    borderRadius: 10,
    marginBottom: 5,
    marginHorizontal: 5,
    paddingRight: 10,
  },
  noteCardContent: {
    flex: 1,
    paddingVertical: 5,
  },
  delectButton:{
    padding: 5,
    justifyContent: 'center',
    alignItems: 'center',
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
    marginBottom: 15,
  },

  //노트 추가 스타일
  inputTitle:{
    width:'100%',
    backgroundColor:'#ffffffff',
    borderBottomWidth:1,
    borderColor:'#707070ff',
    paddingLeft: 15,
    fontWeight: 'bold',
    fontSize:20,
  },
  input:{
    width:'100%',
    height:'600%',
    backgroundColor:'#ffffffff',
    paddingLeft: 15,
  },

  //노트 상세 스타일
  detailTitle:{
    width:'100%',
    height: 50,
    backgroundColor:'#ffffffff',
    borderBottomWidth:1,
    borderColor:'#707070ff',
    paddingLeft: 15,
    fontWeight: 'bold',
    fontSize:20,
    textAlignVertical:'center',
  },
  detailData:{
    width:'100%',
    height:'1220%',
    backgroundColor:'#ffffffff',
    fontSize: 16,
    paddingLeft: 18,
    paddingTop: 10,
  },
});

export default DogNoteScreen;