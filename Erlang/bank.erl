%% @author harshkour1
%% @doc @todo Add description to bank.


-module(bank).

%% ====================================================================
%% API functions
%% ====================================================================
-export([startbank/2]).
-import(timer,[sleep/1]).
-export([bankreq/2]).
-import(money,[requestreceive/0]).


%% ====================================================================
%% Internal functions
%% ====================================================================

startbank(Bname, Bamount) ->
	bankreq(Bname,Bamount).


bankreq(Bname,Bamount) ->
	receive
		{Name,Famount,Tamount, Amt,Bkeys} -> 
			Bal = Bamount - Amt,
			if 
				Bal >= 0 andalso Amt > 0 ->
					whereis(money) ! {approve,Name,Bname,Famount,Tamount, Amt,Bkeys},
					Bamount1 = Bal;
			true ->
					whereis(money) ! {reject,Name,Bname,Famount,Tamount, Amt,Bkeys},
					Bamount1 = Bamount
			end,
			bankreq(Bname,Bamount1);
		
		{givebal, Banknamem} ->
			whereis(money) ! {mybal, Banknamem, Bamount}, 
			bankreq(Bname,Bamount);
			
		Other ->
			bankreq(Bname,Bamount)
	end.



