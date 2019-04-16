import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ListIterator;

public class Store 
{
	private static Queue storeLine = new Queue();
	private File customers;
	private File queries;
	
	public Store(String customerFile, String textFile) throws IOException
	{
		customers = new File(customerFile);
		readCustomerFile(customers);
		queries = new File(textFile);
		readQueryFile(queries);
	}
	
	// Gets wait time for a given person
	public int waitTime(int ID)
	{
		int position = storeLine.findPosition(ID);
		int time = 0;
		Customer current = storeLine.first;
		Customer firstInLongest = current;
		int duration = storeLine.getServiceDuration();
		for(int i=0; i<position; i++)
		{
			// If its after 5 PM
			if(current.getTime()>=61200)
				return 0;
			// If we reach our person
			if(i == position-1)
			{
				// If it is before 5 PM but we are in line
				if( current.getTime() <61200 && current.getTime()+time >=61200)
					time = 61200 - current.getTime();
				// If it is before 9 AM
				else if(current.getTime()<=32400)
				{
					time= 32400 - current.getTime()+time;
				}
				// If our person is in line after 9 AM with people before 9AM
				else if( 32400 + time >= current.getTime() )
				{
					time= current.getTime()-32400+time;
				}
				// If there is a line in front, then calculate time
				else if( time!=0 )
				{
					time= firstInLongest.getTime() + time - current.getTime();
				}
				else
					return 0;
			}
			// If its currently before 9 AM
			else if(current.getTime()<=32400 && 32400+time+duration>= current.next().getTime())
			{
				time+=duration;
			}
			// If the next person is in line or within service duration
			else if(current.getTime()+ duration>= current.next().getTime() || current.getTime()+ time>= current.next().getTime())
			{
				// If it is first in line
				if(time == 0)
				{
					firstInLongest = current;
				}
				time+=duration;
			}
			// Reset time to zero if there is no line
			else
			{
				time=0;
			}
			current = current.next();
			
		}
		return time;
	}
	
	public int customersServed()
	{
		int count=0;
		int duration = storeLine.getServiceDuration();
		Customer current = storeLine.first;
		
		while(current!=null)
		{
			// If it's after 5 PM or if they have to wait past 5 PM
			if(current.getTime() >= 61200 || current.getTime()+waitTime(current.getID()) >= 61200)
				return count;
			// If the current person is being served close to 5 PM
			else if( current.getTime()+duration >= 61200)
			{
				count++;
				return count;
			}
			else
				count++;
			current = current.next();
		}
		return count;
	}
	
	public int idleTime()
	{
		int idleTime = 28800;
		int duration = storeLine.getServiceDuration();
		Customer current = storeLine.first;
		
		// Go through the entire list of customers
		while(current!=null)
		{
			// If it's past 5 P.M.
			if(current.getTime() >= 61200)
				return idleTime;
			// The little bit of time before 5. Doesn't count after 5
			else if( current.getTime() <61200 && (current.getTime()+waitTime(current.getID())+duration >= 61200 ) )
			{
				idleTime-= 61200-current.getTime();
				return idleTime;
			}
			else
				idleTime -= duration;
			current = current.next();
		}
		
		if(idleTime<0)
			idleTime = 0;
		return idleTime;
	}
	
	public int longestBreak()
	{
		int longestBreak=0;
		int lineStart = 32400; // Start at 9AM
		int time = 0;
		int duration = storeLine.getServiceDuration();
		Customer current = storeLine.first;
		
		while(current!=null)
		{
			// Before 5 but no more customers
			if(current.next() == null && current.getTime()<61200)
			{
				if(longestBreak < 61200 - ( current.getTime() + waitTime(current.getID()) + duration ))
				{
					longestBreak = 61200 - ( current.getTime() + waitTime(current.getID()) + duration );
				}
				return longestBreak;
			}
			// If the person is before 9
			if(current.getTime() < 32400)
			{
				time+=duration;
			}
			// If the line is past 5, or if they are serviced until 5, return.
			else if( current.getTime() > 61200 || current.getTime()+duration >= 61200 || current.getTime()+waitTime(current.getID()) >=61200 )
			{
				return longestBreak;
			}
			// If there is a break
			else if( lineStart + time < current.getTime() )
			{
				// If it's the longest line
				if(longestBreak < (current.getTime() - (lineStart + time)))
				{
					longestBreak = current.getTime() - (lineStart + time);
				}
				time = duration; // Reset the time
				lineStart= current.getTime(); // Reset the start of the line
			}
			// If there is a line
			else
			{
				time += duration;
			}
			current = current.next();
		}
		
		return longestBreak;
	}
	
	public int longestLine()
	{
		int longest=0; 
		int count=0;
		Customer current = storeLine.first;
		
		while(current!=null)
		{
			// If the person arrives after 5 PM
			if(current.getTime()>61200)
				return longest;
			// If the next person in line isn't null and the time is before 5 PM
			else if(current.next()==null&&current.getTime()<=61200)
			{
				// If there is a line
				if(waitTime(current.getID())>0)
				{
					count++;
					if(longest < count)
						longest=count;
				}
				return longest;
			}
			// If there is a line
			else if(waitTime(current.getID())>0)
			{
				count++;
				if(longest<count)
					longest=count;
			}
			else 
				count=0;
			current=current.next();
		}
		return longest;
	}
	
	private void readCustomerFile(File textFile) throws IOException
	{
        //System.out.println("Reading " + textFile);
        try
        {
        	BufferedReader reader = new BufferedReader(new FileReader(textFile));
        	int serviceTime;
        	String line;
            int id;
            String time;
            int convertedTime;
            
            String myTime[];
            // Get the duration on the 1st line
            line = reader.readLine();
            storeLine.setServiceDuration(Integer.parseInt(line));
            
            while((line = reader.readLine()) != null)
            {
                if(!(line.trim().equals("")))
                {
                    id = Integer.parseInt(line.substring(12).trim());
                    time = reader.readLine().substring(13).trim();
                    
                    myTime = time.split(":");
                    int hour = Integer.parseInt(myTime[0]);
                    if(hour < 7)
                        hour = hour + 12;
                    convertedTime = hour*3600 + Integer.parseInt(myTime[1])*60 + Integer.parseInt(myTime[2]);
                    //System.out.println(id + "\t" + convertedTime);
                    storeLine.enqueue(id, convertedTime);
                }
            }
            //System.out.println("Read successfully.");
            reader.close();

        }catch(FileNotFoundException e) 
        {
            e.printStackTrace();
        }
    }
	
	private void readQueryFile(File textFile) throws IOException
	{
        //System.out.println("Reading " + textFile);
        String line;
        int longestWait;
        int customersServed;
        int totalIdle;
        int longestBreak;
        int maxLine;
        int customerID;
        
        try
        {
        	//System.out.println("Read successfully.");
        	BufferedReader reader = new BufferedReader(new FileReader(textFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
			
            while((line = reader.readLine()) != null)
            {
            	if(line.contains("WAITING-TIME-OF"))
            	{
            		customerID = Integer.parseInt(line.substring(15).trim());
            		longestWait = waitTime(customerID);
            		System.out.println("WAITING-TIME-OF "+customerID+":"+longestWait);
            		writer.write("WAITING-TIME-OF "+customerID+":"+longestWait);
            		writer.newLine();
            	}
            	else if(line.equals("NUMBER-OF-CUSTOMERS-SERVED"))
            	{
            		customersServed = customersServed();
            		System.out.println("NUMBER-OF-CUSTOMERS-SERVED: "+ customersServed);
            		writer.write("NUMBER-OF-CUSTOMERS-SERVED: "+ customersServed+"\n");
            		writer.newLine();
            	}
            	else if(line.equals("TOTAL-IDLE-TIME"))
            	{
            		totalIdle = idleTime();
            		System.out.println("TOTAL-IDLE-TIME: "+totalIdle);
            		writer.write("TOTAL-IDLE-TIME: "+totalIdle+"\n");
            		writer.newLine();
            	}	
            	else if(line.equals("LONGEST-BREAK-LENGTH"))
            	{
            		longestBreak = longestBreak();
            		System.out.println("LONGEST-BREAK-LENGTH: "+longestBreak);
            		writer.write("LONGEST-BREAK-LENGTH: "+longestBreak+"\n");
            		writer.newLine();
            	}
            	else if(line.equals("MAXIMUM-NUMBER-OF-PEOPLE-IN-QUEUE-AT-ANY-TIME"))
            	{
            		maxLine = longestLine();
            		System.out.println("MAXIMUM-NUMBER-OF-PEOPLE-IN-QUEUE-AT-ANY-TIME:"+maxLine);
            		writer.write("MAXIMUM-NUMBER-OF-PEOPLE-IN-QUEUE-AT-ANY-TIME:"+maxLine+"\n");
            		writer.newLine();
            	}
            }
            reader.close();
            writer.close();

        }catch(FileNotFoundException e) 
        {
            e.printStackTrace();
        }
    }
	
}
