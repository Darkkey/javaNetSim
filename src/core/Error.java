package core;


public class Error{
	public static void Report(Exception e){
		System.out.println("\tError>>\t" + e.toString() + "\n");
                e.printStackTrace();
	}
}
