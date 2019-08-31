//@author:harshkour
import java.util.*;
public class customer implements Runnable{

	private String custname;
	private int custamount;
	Thread t2;
	money m;
	List<String> banknames;
	static List<bank> banks1;
	List<bank> banks;
	static int count = 0;
	customer(String custname,int custamount, List<String> banknames,money m){
		this.custname = custname;
		this.custamount = custamount;
		this.m = m;
		this.banknames = banknames;
		t2 = new Thread(this, custname);
	}
	
	customer(){
		
	}
	public void bankobjects(List<bank> banklist) {
		this.banks = new ArrayList<>(banklist);
			banks1 = new ArrayList<>(banklist);
		//System.out.println(this.banks);
	}
	
	public void custobjects(List<customer> custlist) {
		count = custlist.size();
		//System.out.println("count is "+ count);
		//for(int i= 0 ;i < count; i++) {
		//	customer c = custlist.get(i);
		//	c.banks = banks1;
			
		//}
	}
	
	boolean flag = false;
	public synchronized void takeloan() throws InterruptedException{
		int famount = this.custamount;
		
		while(this.custamount != 0 && ((this.banks.size()) != 0)) {
			
			Random rand = new Random(); 
	        bank randombank = this.banks.get(rand.nextInt(this.banks.size()));
	        String bankcalled = randombank.getbname();
	       // System.out.println("Calling bank: " + bankcalled);
			
		//	System.out.println("Name is :"+ name + " and amount is:" + amount);
			
			
			
			while (randombank.queue.size() == 0) {
                try {
                	//System.out.println("waiting!\n");
                	this.t2.wait();
                	Thread.sleep(100);
                	}
                catch(Exception e){ 
                	}
                }//while
			
				try {
            	//Thread.sleep(1000);
            	}
				catch(Exception e){ 
				}

				if (randombank.queue.size() == randombank.sizelimit) {
				try {
	            notifyAll(); }
				catch(Exception e){ 
                }
				}
			
				double amtreq = Math.random();
				if(this.custamount >=50) {
				amtreq = amtreq * 50 + 1;}
				else {
					amtreq = amtreq * this.custamount + 1;	
				}
				int amtreqf = (int) amtreq;
				System.out.println(this.custname + " requested for a loan of " + amtreqf + " from bank "+ bankcalled);
				int amountreceived = 0;
				try {
				amountreceived = (int)randombank.queue.remove(0);}
				catch(Exception e){ 
                }
				//System.out.println("For customer "+ name + " Amount received is from bank:"+ bankcalled + "is : " +amountreceived);
				if (amountreceived >= amtreqf) {
				int amountbank = amountreceived - amtreqf;
				System.out.println(bankcalled + " approved a loan of " + amtreqf + " dollar(s) for "+ this.custname);
				this.custamount = this.custamount - amtreqf;
				//System.out.println("approve"+this.custamount+":"+this.custname);
				try {
				randombank.queue.add(amountbank);
				
				}
                catch(Exception e){
                }
				
				}else {
					try {
					System.out.println(bankcalled + " rejected a loan of " + amtreqf + " dollar(s) for "+ this.custname);
					randombank.queue.add(amountreceived);
					//System.out.println(bankcalled + " before removed from list for " + this.custname + "banks: " + this.banks);
					List<bank> bat = this.banks;
					bat.remove(randombank);
					this.banks = bat;
					//System.out.println("reject"+this.custamount+":"+this.custname);
					//System.out.println(bankcalled + " after removed from list for " + this.custname + "banks: " + this.banks);
					//System.out.println("bank list is" + bank);
					if (this.banks.size() == 0) {
						
						int damt = famount - this.custamount;
						
						count = count -1;
						//System.out.println("after reject count is "+ count);
						System.out.println("\n" + this.custname + " was only able to borrow " + damt + " dollar(s). Boo Hoo!\n");
						//System.out.println("\n Amount goal could not reached for " + name + " Boo hoo!");
					}
						}
					catch(Exception e){
					}
				}
				//System.out.println("\nAmount left for " + this.custname + " is: " + this.custamount);	
			
			//Thread.sleep(1000);	
			
		}//while
	
		//System.out.println("Out while loop :" + this.custamount + ":" +this.banks.size() + ":"+ this.custname);
		if (this.custamount == 0) {
		System.out.println("\n" + this.custname + " has reached the objective of " + famount + " dollar(s). Woo Hoo!\n");
		count = count-1;
		//System.out.println("after goal count is "+ count);
		}
		
		if (count == 0) {
			//System.out.println("in count = 0");
			for(int i= 0 ;i < banks1.size(); i++) {
				
				bank r = banks1.get(i);
				String s = r.getbname();
				int amt = 0;
				try {
				amt = (int)r.queue.remove(0);}
				catch(Exception e){
				}
				System.out.println("\n" +s + " has " + amt + " dollar(s) remaining.");
				
			}
		}
		
	}//takeloan
	

	@Override
	public void run() {
		//System.out.println("In customer "+ this.custname+ ":" +this.custamount);
		synchronized(this) {
		try
        { 
            this.takeloan(); 
        } 
        catch(InterruptedException e) 
        { 
            e.printStackTrace(); 
        }
		// TODO Auto-generated method stub
		}
	}
	
}//bankclass


