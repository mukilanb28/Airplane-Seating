import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Seat{
    int passengerId=0;
    String seatType;
    public Seat(int passengerId, String seatType){
        this.passengerId=passengerId;
        this.seatType=seatType;
    }
}

public class Main
{
	public static void main(String[] args) {
	    Scanner scanner = new Scanner(System.in);
	    //Inputs
	    int[][] seatOrder = {{2,3},{3,4},{3,2},{4,3}};
	    int passengerCount = 30;
	    
	    System.out.println("Enter 1 for custom inputs or Press any other key to take default input");
	    int inputState = scanner.nextInt();
	    if(inputState==1){
	        seatOrder = GetCustomInput();
	        System.out.println("Enter PassengerCount");
	        passengerCount = scanner.nextInt();
	    }
	    
	    //Declaration
	    LinkedHashMap<String,HashMap<String,Integer>> seatsCountByType = new LinkedHashMap<String,HashMap<String,Integer>>();
	    List<Seat[][]> seats = new ArrayList<Seat[][]>();
	    
	    //Function calls
	    Initilize(seatOrder,seats,seatsCountByType);
	    FillSeats(seatOrder,seats,passengerCount,seatsCountByType);
	    PrintStructure(seatOrder,seats);
	}
	
	/*
     * Function to get custom input from user
     */
	public static int[][] GetCustomInput(){
	    Scanner scanner = new Scanner(System.in);
	    System.out.println("Enter Matrix Length");
	    int length = scanner.nextInt();
	    int[][] seatOrder = new int[length][];
	    int index=0;
	    
	    try{
	        scanner = new Scanner(System.in);
	        System.out.println("Enter the seat size in a single line e.g 2,3 3,4 4,5");
	        String input = scanner.nextLine();
	        String[] tmpInput = input.split(" ");
	        for(String str : tmpInput){
	            String[] tmp = str.split(",");
	            seatOrder[index++] = new int[]{Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1])};
	            
	        }
	    }catch(Exception e){
	        System.out.println(e);
	        GetCustomInput();
	    }
	    return seatOrder;
	}
	
    /*
     * Function to initialize the seats list, seat types and its count
     */
    public static void Initilize(int[][] seatOrder, List<Seat[][]> seats,LinkedHashMap<String,HashMap<String,Integer>> seatsCountByType){
	    InitizeSeatTypes(seatsCountByType);
	    System.out.println("Initializing Seats...");
	    for(int index=0; index<seatOrder.length; index++){
	        int tempCount=0;
	        seats.add(new Seat[seatOrder[index][0]][seatOrder[index][1]]);
	        if(index==0||index==seatOrder.length-1){
	            tempCount = seatOrder[index][0];
	            if(seatOrder.length==1){
	                tempCount *= 2;
	            }else{
	               seatsCountByType.get("AISLE").put("Total",seatsCountByType.get("AISLE").get("Total")+tempCount);
	            }
	            seatsCountByType.get("WINDOW").put("Total",seatsCountByType.get("WINDOW").get("Total")+tempCount);
	        }else{
	            tempCount += seatOrder[index][1]>2 ? seatOrder[index][0]*2 : seatOrder[index][1]*seatOrder[index][0];
	            seatsCountByType.get("AISLE").put("Total",seatsCountByType.get("AISLE").get("Total")+tempCount);
	        }
	        int diff = seatOrder[index][1]-2;
            tempCount = diff>0 ? diff*seatOrder[index][0] : 0;
            seatsCountByType.get("MIDDLE").put("Total",seatsCountByType.get("MIDDLE").get("Total")+tempCount);   
	    }
	    UpdateStartIndex(seatsCountByType);
	}

    /*
     * Function to initialize the seat types and its count
     */
	public static void InitizeSeatTypes(LinkedHashMap<String,HashMap<String,Integer>> seatsCountByType){
	    System.out.println("Initializing SeatTypes...");
	    seatsCountByType.put("AISLE",new HashMap<String,Integer>(){{
	        put("Total",0);put("StartIndex",0);put("AssignedCount",0);
	    }});
	    seatsCountByType.put("WINDOW",new HashMap<String,Integer>(){{
	        put("Total",0);put("StartIndex",0);put("AssignedCount",0);
	    }});
	    seatsCountByType.put("MIDDLE",new HashMap<String,Integer>(){{
	        put("Total",0);put("StartIndex",0);put("AssignedCount",0);
	    }});
	}

     /*
     * Function to update the start index count based on the seat types
     */
	public static void UpdateStartIndex(LinkedHashMap<String,HashMap<String,Integer>> seatsCountByType){
        System.out.println("Updating Start Index...");
	    int index = 0;
	    String prevKey="";
        for ( Map.Entry<String,HashMap<String,Integer>> e : seatsCountByType.entrySet() ) {
            String key = e.getKey();
            if(prevKey!=""){
                HashMap<String,Integer> prevEntry = seatsCountByType.get(prevKey);
                e.getValue().put("StartIndex",prevEntry.get("Total")+prevEntry.get("StartIndex"));
            }
            prevKey=key;
        }
	}

    /*
     * Function to update the passengers id in seats list
     */
	public static void FillSeats(int[][] seatOrder, List<Seat[][]> seats, int passengerCount,LinkedHashMap<String,HashMap<String,Integer>> seatsCountByType){
	    boolean foundNextRow=false;
	    int rowIndex=0;
	    do{
	        foundNextRow=false;
	        for(int i=0; i<seats.size(); i++){ //Seat List Index
	            int rowSize=seatOrder[i][1];
	            boolean tmpFoundNextRow = seatOrder[i][0] > rowIndex;
                
	            foundNextRow = foundNextRow==false ? seatOrder[i][0] > rowIndex : foundNextRow;
	            for(int j=0; j<rowSize&&tmpFoundNextRow; j++){
	                String seatType; 
	                if( (i==0&&j==0) || (i==seats.size()-1&&j==rowSize-1)){
	                    seatType="WINDOW";
	                }else if(j==0||j==rowSize-1){
	                    seatType="AISLE";
	                }else{
	                    seatType="MIDDLE";
	                }
	                int passengerId = GetPassengerId(seatsCountByType.get(seatType),passengerCount);
	                if(passengerId>0){
	                    seats.get(i)[rowIndex][j] = new Seat(passengerId,seatType);
	                }
	            }
	        }
	        ++rowIndex;
	        if(rowIndex==10)
	            break;
	    }while(foundNextRow);
	}

     /*
     * Function to get next passengerId based on the seat types
     * @return passengerId
     */
	public static int GetPassengerId(HashMap<String,Integer> seatsCountByType, int maxId){
	   int passengerId = seatsCountByType.get("StartIndex")+seatsCountByType.get("AssignedCount");
	   if(passengerId+1 <= maxId){
	    seatsCountByType.put("AssignedCount",seatsCountByType.get("AssignedCount")+1);
	    return ++passengerId;
	   }
	   return 0;
	}

    /*
     * Function to print the seats with passengerId
     */
	public static void PrintStructure(int[][] seatOrder, List<Seat[][]> seats){
	    int rowIndex=0;
	    boolean foundNextRow=true;
	    do{
	        foundNextRow=false;
	        for(int i=0; i<seats.size(); i++){
	            int rowSize=seatOrder[i][1];
	            
	            boolean tmpFoundNextRow = seatOrder[i][0] > rowIndex;
	            foundNextRow = foundNextRow==false ? seatOrder[i][1] > rowIndex : foundNextRow;
	            for(int j=0; j<rowSize; j++){
	                String seatType, color=""; 
	                if( (i==0&&j==0) || (i==seats.size()-1&&j==rowSize-1)){
	                    seatType="WINDOW";
	                    color="\033[0;32m";
	                }else if(j==0||j==rowSize-1){
	                    seatType="AISLE";
	                    color="\033[0;34m";
	                }else{
	                    seatType="MIDDLE";
	                    color="\033[0;31m";
	                }
	                System.out.print(color);
	                Seat s = tmpFoundNextRow ? seats.get(i)[rowIndex][j] : null;
	                if(tmpFoundNextRow && s!= null ){
	                    
	                    String space = s.passengerId > 9 ? " " : "  ";
	                    System.out.print(s.passengerId+space);
	                }else if(tmpFoundNextRow){
	                    System.out.print("-  ");
	                }else{
	                    System.out.print("   ");
	                }
	                
	            }
	            System.out.print("  ");
	        }
	        System.out.println();
	        ++rowIndex;
	    }while(foundNextRow);
	    System.out.println("\033[0m");
	}
}