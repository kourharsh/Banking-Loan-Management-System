//@author:harshkour
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class money implements Runnable{
	//static Thread master;
	static List<bank> banklist = new ArrayList<bank>();
	static List<customer> custlist = new ArrayList<customer>();
	static List<String> banknames = new ArrayList<String>();
	Queue<String> que;
	Thread t;
	public static void main(String[] args) {
		new money();
	}
	
	public money() {
		this.que = new ConcurrentLinkedQueue<String> ();
		t = new Thread(this,"money");
		t.start();
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
		try {
			BufferedReader in = new BufferedReader(new FileReader("banks.txt")); 
			int count= 0;
			HashMap<String,Integer> bank = new HashMap<String,Integer>();
			for (String x = in.readLine(); x != null; x = in.readLine())
            {
				count++;
				//System.out.println(x);
				x =x.replaceAll("[{\\.\\}]","");
				x = x.trim();
				//System.out.println(x);
				String[] local = x.split(",");
				int amt = Integer.parseInt(local[1]);
				bank.put(local[0], amt);
            }//for
			
			//System.out.println(bank);
			System.out.println("** Banks and financial resources **");
			
			//final bank b = new bank();
			
			for(Map.Entry<String,Integer> it: bank.entrySet()) {
				String name = it.getKey();
				int amount = it.getValue();
				System.out.println(name+":"+amount);
				banknames.add(name);
				bank b = new bank(name,amount,this);
				banklist.add(b);
			}
			
			
			
			
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		try {
			BufferedReader in1 = new BufferedReader(new FileReader("customers.txt")); 
			int count1= 0;
			HashMap<String,Integer> customer = new HashMap<String,Integer>();
			for (String x = in1.readLine(); x != null; x = in1.readLine())
            {
				count1++;
				//System.out.println(x);
				x =x.replaceAll("[{\\.\\}]","");
				x = x.trim();
				//System.out.println(x);
				String[] local = x.split(",");
				int amt = Integer.parseInt(local[1]);
				customer.put(local[0], amt);
            }//for
			
			//System.out.println(customer);
			System.out.println("** Customers and loan objectives **");
			for(Map.Entry<String,Integer> it: customer.entrySet()) {
				String name = it.getKey();
				int amount = it.getValue();
				System.out.println(name+":"+amount);
				banknames.add(name);
				customer c = new customer(name,amount, banknames,this);
				custlist.add(c);	
			}
			
			customer tempobj = new customer();
			//tempobj.bankobjects(banklist);
			tempobj.custobjects(custlist);
			for(customer c : custlist) {
				c.bankobjects(banklist);
			}
			
			
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//starting all the threads.
		for(bank bankit: banklist) {
			bankit.t1.start();
		}
		
		try {
			t.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(customer custit: custlist) {
			custit.t2.start();
		}
		
		//System.out.println("Back in money!! \n \n");
		
	}//run
	
	
	
	/* public static class bankclass {
		
		public static synchronized void giveloan(String name, int amount) throws InterruptedException {
			
			while(true) {
				System.out.println("Name is :"+ name + " and amount is:" + amount);
				//while (amount!=0) 
                //    wait();
				Thread.sleep(1000);
			}//while
			
		}//giveloan
		
		public synchronized void takeloan() {
			
			while(true) {
				
			}//while
			
		}//takeloan
		
	}//bankclass 
	
*/
}//bankingclass
