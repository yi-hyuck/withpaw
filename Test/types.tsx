interface Note {
  id: string;
  title: string;
  data: string;
}

interface Schedule {
  id: number;
  //일정 이름
  name: string;
  //시작일
  startDate: string;
  //종료일
  endDate: string;
  //반복 여부
  repeats: boolean;
  //몇 주마다 반복?
  repeatWeeks: number;
  originalId: string;
}

export type NewDogData = {
  name: string;
  dogName: string,
  dogGender: string,
  dogBirth: string,
  dogBreed: string,
  dogWeight: string,
  neutering: string,
};

interface userType {
    loginId: string;
    email: string;
    password: string;
}

export type UpdatedDogData = NewDogData & {id: string};

export type RootStackParamList={
  SignUp:undefined;
  DogInfo: {userData:any};
  Home: undefined;
  Details: { id: number };
  Login: undefined;
  NaviBar: undefined;
  StackNaviBar: undefined;
  Maps: undefined;
  Disease: undefined;
  DogAi: undefined;
  DogNoteScreen: undefined;
  DogNote: undefined;
  DogNotePlus: {
    saveNote: (title: string, data: string, id?: string) => void;
    initialNote?: Note;
    onSaveAndGoBackToDogNote?: () => void;
  };
  NoteDetail: { 
    note: Note;
    deleteNote: (id: string) => void;
    navigateToEdit: (note: Note) => void;
  };
  Calender: undefined;
  CalendarScreen: undefined;
  CalendarMainScreen: undefined;
  ScheduleAdd: {
    addSchedule: (data: any) => void;
    isSaving: boolean;
  } | undefined;
  Foods: undefined;
  FoodDetail: {foodId: string};
  FoodsScreen: undefined;
  UserInfo: undefined;
  UserInfoScreen: undefined;
  DogMgmtScreen: undefined;
  DogManagement: {newDog?: any; updatedDog?: any, refresh?: boolean} | undefined;
  DogAdd: undefined;
  DogEdit: {petId: number};
  Logout: undefined;
  App: undefined;
  UserEdit: {currentData: {loginId: string; email:string; password:string;}|null; onSave:()=>void};
};