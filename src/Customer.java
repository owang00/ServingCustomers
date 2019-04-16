public class Customer 
{
	private int id;
	private int time;
	private Customer next;
	
	public Customer(int id, int time)
	{
		this.id = id;
		this.time = time;
	}
	
	public Customer(int id, int time, Customer next)
	{
		this.id = id;
		this.time = time;
		this.next = next;
	}
	
	public int getID()
	{
		return id;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public Customer next()
	{
		return next;
	}
	
	public void setID(int ID)
	{
		id = ID;
	}
	
	public void setTime(int time)
	{
		this.time = time;
	}
	
	public void setNext(Customer next)
	{
		this.next = next;
	}
}
