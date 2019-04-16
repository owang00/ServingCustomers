import java.io.IOException;

public class Runner 
{
	public static void main(String[]args) throws IOException
	{
		//Store store = new Store("customersfile.txt", "queriesfile.txt");
		Store store = new Store(args[0], args[1]);
	}
}
