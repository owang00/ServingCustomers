import java.util.Iterator;
import java.util.NoSuchElementException;

public class Queue 
{
	private int size;
	private int serviceDuration;
	public Customer first, last;
	
	public Queue()
	{
		first = null;
		last = null;
		size = 0;
	}
	
	public void setServiceDuration(int duration)
	{
		serviceDuration = duration;
	}
	public int getServiceDuration()
	{
		return serviceDuration;
	}
	
	public void enqueue(int ID, int tm)
	{
		Customer current = last;
		last = new Customer(ID,tm);
		
		// If the list is empty, then first Customer is also the last one
		if(size++ == 0)
			first=last;
		else
			current.setNext(last);
	}
	
	public String dequeue()
	{
		if(size == 0)
			throw new NoSuchElementException();
		int ID = first.getID();
		int tm = first.getTime();
		
		if(--size==0)
			last = null;
		return "ID: "+ID+" Time: "+tm;
	}
	
	public int findPosition(int ID)
	{
		int position=1;
		
		Customer temp = first;
		while(temp !=null)
		{
			if(ID == temp.getID())
				return position;				
			else
				temp = temp.next();
			position++;
		}
		return -1;
	}
	
	public String toString()
	{
		String returnString="";
		
		Customer temp = first;
		
		while(temp !=null)
		{
			returnString+= "ID: "+temp.getID()+" Time: "+temp.getTime()+"\n";
			temp = temp.next();
		}
		return returnString;
	}
}
