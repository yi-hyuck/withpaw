import React, { use } from "react";
import { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity,} from 'react-native';
import { createNativeStackNavigator, NativeStackNavigationProp } from "@react-navigation/native-stack";
import { NavigationContainer, useNavigation } from "@react-navigation/native";
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import { createDrawerNavigator, DrawerNavigationProp } from '@react-navigation/drawer'
import { RootStackParamList } from "./types";

//맵
function Maps(){
    return(
      <View style={styles.container}>
        <Text>Maps</Text>
      </View>
    )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor:'#ffffff',
  },
});

export default Maps;