import React, {useState, useRef, useEffect} from "react";
import { StyleSheet, Text, View, TextInput, Button, FlatList, SafeAreaView, KeyboardAvoidingView, ActivityIndicator} from 'react-native';

//타입 정의
interface Message {
  id: string;
  text: string;
  sender: 'user' | 'ai';
}

const DogAi = () => {

  //메세지 목록(id, text, sender)
  const [messages, setMessages] = useState<Message[]>([
    {id: '1', text: 'Ai 진단 봇 입니다. 반려동물의 증상을 입력해 주세요.', sender: 'ai'}
  ]);

  //사용자 입력값
  const [input, setInput] = useState('');

  //API 응답 대기
  const [isLoading, setIsLoading] = useState(false);

  //flatflist에 가장 최신 메세지 표시
  const flatListRef = useRef<FlatList<Message>>(null);

  //messages 변동사항 발생시 스크롤을 맨 아래로 이동
  useEffect(() => {
    if (flatListRef.current) {
      flatListRef.current.scrollToEnd({animated: true});
    }
  }, [messages]);


  //입력 처리
  const handleSend = async () => {
    if (input.trim().length === 0) {
      return;
    }

    const userMessage: Message = {
      id: `user-${Date.now()}`,
      text: input,
      sender: 'user',
    };

    setMessages((prevMessages) => [...prevMessages, userMessage]);
    setInput('');
    setIsLoading(true);

    // chat bot API 호출 코드 작성 위치
    //->

    setTimeout(() => {
      const aiText = "'" + input + "' 증상에 대해 분석 중입니다.";

    const aiMessage: Message = {
        id:`ai-${Date.now()}`,
        text: aiText,
        sender: 'ai'
      };

      setMessages((prevMessages) => [...prevMessages, aiMessage]);
      setIsLoading(false);
      
    }, 1500);
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