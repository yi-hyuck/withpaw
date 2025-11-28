import { NativeStackScreenProps } from "@react-navigation/native-stack";
import React, {useState, useRef, useEffect} from "react";
import { StyleSheet, Text, View, TextInput, Button, FlatList, SafeAreaView, KeyboardAvoidingView, ActivityIndicator, Alert} from 'react-native';
import { RootStackParamList } from "./types";
import AsyncStorage from "@react-native-async-storage/async-storage"; // JWT 토큰 사용을 위한 임포트

// API URL 정의
const API_URL = "http://10.0.2.2:8090";

//타입 정의
interface Message {
  id: string;
  text: string;
  sender: 'user' | 'ai';
}

type DogAiProps = NativeStackScreenProps<RootStackParamList, 'DogAi'>;

const DogAi = ({navigation, route}: DogAiProps) => {

  //메세지 목록(id, text, sender)
  const [messages, setMessages] = useState<Message[]>([
    {id: '1', text: 'Ai 진단 봇 입니다. 반려동물의 증상을 입력해 주세요.', sender: 'ai'}
  ]);

  //사용자 입력값
  const [input, setInput] = useState('');

  //API 응답 대기
  const [isLoading, setIsLoading] = useState(false);

  //flatflist(채팅 목록)에 가장 최신 메세지 표시
  const flatListRef = useRef<FlatList<Message>>(null);

  //messages 변동사항 발생시 스크롤을 맨 아래로 이동
  useEffect(() => {
    if (flatListRef.current) {
      flatListRef.current.scrollToEnd({animated: true});
    }
  }, [messages]);


  //입력 처리
  const handleSend = async () => {
    // 입력 유효성 및 로딩 상태 검사
    if (input.trim().length === 0 || isLoading) {
      return;
    }

    const userMessage: Message = {
      id: `user-${Date.now()}`,
      text: input,
      sender: 'user',
    };

    // 사용자 메시지 텍스트를 별도 변수에 저장 후, input 상태 초기화
        const userMessageText = input;

        // 사용자 메시지 목록에 추가 및 UI 업데이트
        setMessages((prevMessages) => [...prevMessages, userMessage]);
        setInput('');
        setIsLoading(true);

        // 챗봇 API 호출 로직
        try {
          // JWT 토큰 가져오기
          const token = await AsyncStorage.getItem('userToken');

          if (!token) {
            Alert.alert("인증 오류", "챗봇을 이용하려면 로그인이 필요합니다.");
            setIsLoading(false);
            return;
          }

          // 서버에 POST 요청 전송
          const response = await fetch(`${API_URL}/api/chatbot/ask`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ //
                userMessage: userMessageText,
            }),
          });

          // 응답 처리
          setIsLoading(false); // 로딩 종료

          if (response.ok) {
            // 성공 (HTTP 200) 시 AI 응답을 메시지 목록에 추가
            const aiResponseText = await response.text();
            const aiMessage: Message = {
              id: `ai-${Date.now()}`,
              text: aiResponseText,
              sender: 'ai'
            };
            setMessages((prevMessages) => [...prevMessages, aiMessage]);
          } else if (response.status === 401 || response.status === 403) {
            // 인증 오류 또는 권한 문제 처리
            Alert.alert("인증 만료", "세션이 만료되었거나 권한이 없습니다. 다시 로그인해 주세요.");
          } else {
            // 기타 서버 오류 처리
            const errorText = await response.text();
            console.error('Chat API Error:', response.status, errorText);
            Alert.alert("챗봇 오류", `응답 실패: ${response.status}`);
          }
        } catch (error) {
          // 네트워크 오류 처리
          console.error("Network or Fetch Error:", error);
          setIsLoading(false);
          Alert.alert("네트워크 오류", "서버에 연결할 수 없습니다. 서버 실행 상태를 확인하세요.");
        }
      };
  
  const renderMessage = ({item}: {item: Message}) => (
    <View
    style= {[styles.messageBubble, item.sender == 'user' ? styles.userMessage : styles.aiMessage]}
  >
    <Text style= {styles.messageText}>{item.text}</Text>
  </View>
  );


  return (
  <SafeAreaView style={styles.safeArea}>
    <KeyboardAvoidingView
      behavior= "height"
      style={styles.container}
      keyboardVerticalOffset={135}
    >
      <FlatList
        ref= {flatListRef}
        data= {messages}
        renderItem= {renderMessage}
        keyExtractor= {(item)=> item.id}
        style={styles.chatContainer}
        contentContainerStyle={{paddingBottom: 10}}
        />

        {isLoading && (
          <View style={styles.loadingContainer}>
          <ActivityIndicator size="small" color="#888" />
          <Text style={styles.loadingText}>답변 생성중...</Text>
        </View>
        )}

        <View style={styles.inputcontainer}>
          <TextInput
            style={styles.input}
            value={input}
            onChangeText={setInput}
            placeholder="증상을 입력하세요.."
            editable={!isLoading}
          />
          <Button title="전송" onPress={handleSend} disabled={isLoading} />
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
 );
};



const styles = StyleSheet.create({
  container: {
    flex: 1
  },
  safeArea: {
    flex: 1,
    backgroundColor: '#fff'
  },
  chatContainer: {
    flex: 1,
    paddingHorizontal: 10,
    paddingTop: 10
  },
  messageBubble: {
    maxWidth: '80%',
    padding: 12,
    borderRadius: 15,
    marginVertical: 5
  },
  userMessage: {
    alignSelf: 'flex-end',
    borderBottomRightRadius: 0,
    backgroundColor: '#F0F0F0',
  },
  aiMessage: {
    alignSelf: 'flex-start',
    borderBottomLeftRadius: 0,
    backgroundColor: '#ffe44cff'
  },
  messageText: {
    fontSize: 16,
    color: '#000'
  },
  loadingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 8
  },
  loadingText: {
    marginLeft: 10,
    fontSize: 14,
    color: '#888'
  },
  inputcontainer: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 10,
    borderTopWidth: 1,
    borderTopColor: '#e0e0e0ff',
    backgroundColor: '#ffffffff',
  },
  input: {
    flex: 1,
    height: 40, 
    borderColor: '#e0e0e0ff',
    borderWidth: 1,
    borderRadius: 20,
    paddingHorizontal: 15,
    marginRight: 10,
    backgroundColor: '#ffffffff'
  }
});

export default DogAi;