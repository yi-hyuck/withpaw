interface Note {
  id: string;
  title: string;
  data: string;
}

export type RootStackParamList={
  SignUp:undefined;
  DogInfo: {userData:any};
  Home: undefined;
  Details: { id: number };
  Login: undefined;
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
  Foods: undefined;
  FoodDetail: {foodId: string};
};