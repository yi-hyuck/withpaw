import React, { useEffect } from 'react';
import { useAuth } from './useAuth';
import { useMember } from './MemberProvider';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const { authToken, isAuthenticated, isLoadingToken, removeToken } = useAuth();
    const { fetchMemberInfo, setMemberInfo } = useMember();

    useEffect(() => {
        if (isLoadingToken) {
            return;
        }

        if (isAuthenticated && authToken) {
            fetchMemberInfo(authToken).catch(error => {
                console.warn("회원 정보 로드 실패, 토큰 제거 시도");
                removeToken(); 
            });
        } else {
            setMemberInfo(null);
        }
    }, [isAuthenticated, authToken, isLoadingToken, fetchMemberInfo, setMemberInfo, removeToken]);

    return <>{children}</>;
};

const LoadingScreen = () => (
    <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#FFBB00" />
        <Text style={styles.loadingText}>인증 및 회원 정보 로딩 중...</Text>
    </View>
);

const styles = StyleSheet.create({
    loadingContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#FFFFFF',
    },
    loadingText: {
        marginTop: 10,
        fontSize: 16,
        color: '#444',
    }
});