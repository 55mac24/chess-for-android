package edu.rutgers.recordmove;

import java.util.ArrayList;
import java.util.Collections;

public class recorder {
   /*private ArrayList<String[]> List = new ArrayList<>();
   private ArrayList<String> instruction;
   public recorder(ArrayList<String> name,ArrayList<String> time){
       for(int i=0;i<name.size();i++){
           String[] temp=new String[2];
           temp[0]=name.get(i);
           temp[1]=time.get(i);
           List.add(temp);
       }
    }
    public recorder(){
        instruction=new ArrayList<>();
    }*/
   public ArrayList<String> updateInstruction(ArrayList<String> instruction,String move){
       instruction.add(move);
       return instruction;
   }
   public ArrayList<String>  undoInstruction(ArrayList<String> instruction){
       instruction.remove(instruction.size()-1);
       return instruction;
   }
   public static String ToStringInstruction(ArrayList<String> instruction){
       if(instruction.size()==0){
           return "nah";
       }
       String temp=instruction.get(0);
       for(int i = 1;i<instruction.size();i++){
           temp=temp+";"+instruction.get(i);
       }
       return temp;
   }
   public static ArrayList<String[]>  ListTimeSort(ArrayList<String[]> List){
       ArrayList<String[]> temp=new ArrayList<>();
       temp=List;
       for(int i=0;i<temp.size();i++){
           int smallest=i;
           for(int k=i;k<temp.size();k++){
               String current=temp.get(smallest)[1].toLowerCase();
               String next=temp.get(k)[1].toLowerCase();
               if(current.compareTo(next)>0){
                   smallest=k;
               }
           }
           String[] a =temp.get(smallest);
           temp.set(smallest,temp.get(i));
           temp.set(i,a);
          // Collections.swap(temp,smallest,i);
       }
       return temp;
   }
    public static ArrayList<String[]>  ListNameSort(ArrayList<String[]> List){
        ArrayList<String[]> temp=List;

        for(int i=0;i<temp.size();i++){
            int smallest=i;
            for(int k=i;k<temp.size();k++){
                String current=temp.get(smallest)[0].toLowerCase();
                String next=temp.get(k)[0].toLowerCase();
                System.out.println(i+":"+current+" "+k+":"+next);
                if(current.compareTo(next)>0){
                    smallest=k;
                }
            }
            String[] a =temp.get(smallest);
            temp.set(smallest,temp.get(i));
            temp.set(i,a);
            //Collections.swap(temp,smallest,i);
        }
        return temp;
    }
    public static ArrayList<String> TimeList(ArrayList<String[]> List){
            ArrayList<String> temp= new ArrayList<>();
            for(int i=0;i<List.size();i++){
                temp.add(List.get(i)[1]);
            }
            return temp;
    }
    public static ArrayList<String> NameList(ArrayList<String[]> List) {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < List.size(); i++) {
            temp.add(List.get(i)[0]);
        }
        return temp;
    }
    public static String[] getinstruction(String instruction){
        String[] output=instruction.split(";");
        return output;
    }
}