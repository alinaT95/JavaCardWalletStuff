package tonwallet ;

//import com.ftsafe.javacard.debug.DebugHelper;

public class Utils  
{

	public static void printArray(byte[] arr, short offset, short len){
	    for(short i = offset; i < (short)(offset + len); i++){
		   com.ftsafe.javacard.debug.DebugHelper. print(arr[i]); 
	    }
	    com.ftsafe.javacard.debug.DebugHelper. println();
    }

}
