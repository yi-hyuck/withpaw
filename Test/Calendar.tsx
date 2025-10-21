import React, { use } from "react";
import { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, } from 'react-native';
import { Calendar, DateData, LocaleConfig } from "react-native-calendars";

//달력 한글패치
LocaleConfig.locales.kr={
  monthNames:[
    '01월',
    '02월',
    '03월',
    '04월',
    '05월',
    '06월',
    '07월',
    '08월',
    '09월',
    '10월',
    '11월',
    '12월',
  ],
  monthNamesShort:[
    '01월',
    '02월',
    '03월',
    '04월',
    '05월',
    '06월',
    '07월',
    '08월',
    '09월',
    '10월',
    '11월',
    '12월',
  ],
  dayNames:['일요일','월요일','화요일','수요일','목요일','금요일','토요일'],
  dayNamesShort:['일','월','화','수','목','금','토'],
  today: "오늘",
};

LocaleConfig.defaultLocale = 'kr';

function CalendarScreen() {
  const [selected, setSelected] = useState<string>('');

  const datePress = (day:DateData)=>{
    setSelected(day.dateString);
  }

  return(
    <View style={{backgroundColor:'#ffffff'}}>
      <Calendar
        style={[styles.calendar]}
        monthFormat={'yyyy년 MM월'}
        onDayPress={datePress}
        markedDates={{[selected]:{selected:true, disableTouchEvent: true}}}
        theme={{
          selectedDayBackgroundColor:'#ff7b00ff',
        }}
      />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor:'#ffffff',
  },
  calendar:{
    borderBottomWidth: 3,
    borderBottomColor: '#e0e0e0',
    height:370,
    borderTopWidth: 0,
  },
});

export default CalendarScreen;