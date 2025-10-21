import React, { use } from "react";
import { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, } from 'react-native';

function Foods() {
  return(
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        placeholder="음식 검색"
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
  block:{
    padding:16,
    backgroundColor: '#ffffffff'
  },
  input:{
    width:350,
    height:45,
    borderColor: '#444444ff',
    backgroundColor: '#ffffffff',
    borderWidth: 1,
    paddingLeft:10,
    marginBottom:20,
    marginTop: 15,
    borderRadius: 10,
  },
  button: {
    backgroundColor: '#ffd000ff',
    paddingVertical: 10,
    paddingHorizontal: 151,
    marginBottom: 20
  },
  buttonText: {
    fontSize: 20,
    color: '#ffffffff'
  },
  errorText: {
    fontSize: 10,
    color: '#ff0000ff',
    textAlign: 'left',
  }
});

export default Foods;