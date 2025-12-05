//토큰 관리
import React from 'react';
import { useState, useEffect, useCallback } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage'; // 실제 프로젝트에서 사용

const TOKEN_KEY = 'jwt_token';

const MockAsyncStorage = {
    getItem: async (key: string): Promise<string | null> => {
        return AsyncStorage.getItem(key);
    },
    setItem: async (key: string, value: string): Promise<void> => {
        AsyncStorage.setItem(key, value);
    },
    removeItem: async (key: string): Promise<void> => {
        AsyncStorage.removeItem(key);
    }
};

interface AuthContextType {
    authToken: string | null;
    isAuthenticated: boolean;
    isLoadingToken: boolean;
    saveToken: (token: string) => Promise<void>;
    removeToken: () => Promise<void>;
}

export const useAuth = (): AuthContextType => {
    const [authToken, setAuthToken] = useState<string | null>(null);
    const [isLoadingToken, setIsLoadingToken] = useState(true);

    //토큰 로드
    useEffect(() => {
        const loadToken = async () => {
            try {
                const token = await MockAsyncStorage.getItem(TOKEN_KEY);
                setAuthToken(token);
            } catch (error) {
                console.error("토큰 로드 실패:", error);
                setAuthToken(null);
            } finally {
                setIsLoadingToken(false);
            }
        };
        loadToken();
    }, []);

    //토큰 저장
    const saveToken = useCallback(async (token: string) => {
        try {
            await MockAsyncStorage.setItem(TOKEN_KEY, token);
            setAuthToken(token);
        } catch (error) {
            console.error("토큰 저장 실패:", error);
        }
    }, []);

    //토큰 제거 (로그아웃)
    const removeToken = useCallback(async () => {
        setIsLoadingToken(true);

        try {
            await MockAsyncStorage.removeItem(TOKEN_KEY);

            await new Promise<void>(resolve => setTimeout(resolve, 300));

            setAuthToken(null);
        } catch (error) {
            console.error("토큰 제거 실패:", error);
        }
    }, []);

    return {
        authToken,
        isAuthenticated: !!authToken,
        isLoadingToken,
        saveToken,
        removeToken,
    };
};