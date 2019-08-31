//@author:harshkour
import java.util.*;
public class bank implements Runnable{
		
		 boolean flag = false;
		 money m;
		private String bankname= null;
		Thread t1;
		private int amount = 0;
		int sizelimit;
		List queue = new LinkedList(); 
		List queue1 = new LinkedList();
		 bank(String bankname, int amount, money m){
				this.bankname = bankname;
				this.amount  = amount;
				t1 = new Thread(this,bankname);
				this.m = m;
				queue1.add(amount);	
				this.queue = new LinkedList<>(queue1);
				this.sizelimit = 1;
			}
		
		public synchronized void giveloan(String name, int amount) throws InterruptedException {
			
			//while(true) {
				//System.out.println("Name is :"+ name + " and amount is:" + amount);
				//System.out.println("Queue size is " + this.queue.size());
				while (this.queue.size() == this.sizelimit) { 
		            wait(); 
		        } 
		        if (this.queue.size() == 0) { 
		            notifyAll(); 
		            
		        } 
		        
		
		}//giveloan 
		

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//System.out.println("In bank "+ this.bankname+ ":" +this.amount);
			try
            { 
                this.giveloan(this.bankname,this.amount); 
            } 
            catch(InterruptedException e) 
            { 
                e.printStackTrace(); 
            } 
			
		}//run

		public String getbname() {
			// TODO Auto-generated method stub
			return bankname;
		}
		
}//class
		