import React, { useCallback, useContext, useEffect, useState } from "react";
import axios from "axios";

interface PetDto{
  petId: number;
  name: string;
  breed: string,
  gender: string;
  birthDate: string;
  neuter: boolean;
  weight: number;
}

interface MemberData{
  userId: number;
  loginId: string;
  email: string;
  pets: PetDto[]
  password: string;
}

interface MemberContextType{
  memberInfo: MemberData | null;
  setMemberInfo: (info: MemberData | null) => void;
  isLoading: boolean;
  fetchMemberInfo: (token: string | null) => Promise<void>;
}

export const MemberContext = React.createContext<MemberContextType | undefined>(undefined);

export const useMember = () => {
  const context = useContext(MemberContext);
  if(context === undefined){
    throw new Error('useMember must be used within a MemberProvider');
  }
  return context;
}

export const MemberProvider: React.FC<{children: React.ReactNode}> = ({ children }) => {
  const [memberInfo, setMemberInfo] = useState<MemberData | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchMemberInfo = useCallback(async (token: string | null) => {
    if (!token) {
        setMemberInfo(null);
        setIsLoading(false);
        return;
    }

    setIsLoading(true);
    try{
      const response = await axios.get('http://10.0.2.2:8090/member/info', {
        headers:{
          'Authorization' : `Bearer ${token}`
        }
      });

      if(response.status === 200){
        setMemberInfo(response.data);
        // const data = response.data;
        // console.log("member Info Loaded:", data);
      }
    } catch (error: any) {
      if (axios.isAxiosError(error) && error.response?.status === 401) {
             console.log("401 Unauthorized: Not authenticated. Clearing member info.");
             setMemberInfo(null);
             console.log("401 Response Data:", error.response?.data);
        } else {
             console.error("Network or Server error during member info fetch:", error);
             setMemberInfo(null);
        }
    } finally{
      setIsLoading(false);
    }
}, []);

  // useEffect(()=>{
  //   fetchMemberInfo();
  // }, []);

  const contextValue: MemberContextType = {
    memberInfo,
    setMemberInfo,
    isLoading,
    fetchMemberInfo,
  }

  return (
    <MemberContext.Provider value={contextValue}>
      {children}
    </MemberContext.Provider>
  );
}