%% @author harshkour1
%% @doc @todo Add description to customer.


-module(customer).

%% ====================================================================
%% API functions
%% ====================================================================
-export([startcust/3]).
-import(lists,[nth/2]).
-import(lists,[delete/2]).
-import(timer,[sleep/1]).
-export([for/6]).
-export([custreceive/1]).
-import(money,[requestreceive/0]).
-import(bank,[bankreq/2]).


%% ====================================================================
%% Internal functions
%% ====================================================================


startcust(Name, Amount, Bkeys) ->
	Length = length(Bkeys),
	for(Length,Name,Amount,Amount,Amount,Bkeys),
	custreceive(Name).

custreceive(Name) ->
				receive
					{approve, Name, Bname, Famt,Tamt, Amount,Keysb} -> 
							cust_update(1,Name,Bname,Famt,Tamt, Amount,Keysb),
							custreceive(Name);
					{reject, Name, Bname,Famt, Tamt, Amount,Keysb} -> 
							cust_update(2,Name,Bname,Famt,Tamt, Amount,Keysb),
							custreceive(Name)
				end.
	
cust_update(Flag,Name,Bname,Famt,Tamt,Amount,Keysb) ->
	if
		Flag == 1 ->
			Amount1 = Tamt - Amount,
			Keysbb= Keysb,
			Length = length(Keysbb);	
		true ->
			Amount1 = Tamt,
			Keysbb = delete(Bname,Keysb),
			Length = length(Keysbb),
			io:fwrite("")
	end,
	if Length == 0 andalso Flag == 2->
		 Amtleft = Famt - Tamt,
		whereis(money) ! {borrowleft,Name,Amtleft};   
	true ->
		io:fwrite("")
	end,
	if Amount1 == 0 ->
		 whereis(money) ! {reached,Name,Famt};
	true ->
		io:fwrite("")
	end,
	if
		Amount1 > 0 andalso Length /= 0 ->
			F =  rand:uniform(Length),
			for(F,Name,Famt,Amount1,Amount1,Keysbb);
		true ->
			io:fwrite("")
	end.


for(Max,Name,Famount,Tamount, Amount,Bkeys) ->
	Amount1 = Amount,
	random:seed(now()),
	D = rand:uniform(100),
	if D > 10 ->
		timer:sleep(rand:uniform(D));
	true ->
		timer:sleep(10)
	end,
	if 
		Amount1 > 50 ->
			A =  rand:uniform(50);
		true ->
			A =  rand:uniform(Amount1)
	end,
	
	Bankcalled = nth(rand:uniform(Max),Bkeys),
	Bankpid = whereis(Bankcalled),
	whereis(money) ! {request,Name,A,Bankcalled},
	Bankpid ! {Name, Famount,Tamount, A, Bkeys}.
		
	