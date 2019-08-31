%% @author harshkour1
%% @doc @todo Add description to money.


-module(money).

%% ====================================================================
%% API functions
%% ====================================================================

-import(lists,[nth/2]).
-import(maps, [remove/2]).  
-import(maps, [put/3]).
-import(maps, [get/2]).
-import(customer,[startcust/3]).
-import(bank,[startbank/2]).
-export([start/0]).

%% ====================================================================
%% Internal functions
%% ====================================================================


start() ->
	register(money, self()),
	Str2 = "** Banks and financial resources **~n",
	io:fwrite(Str2),
	{ok,Bank} = file:consult("banks.txt"),
	lists:foreach(
	 	fun({C,D}) ->
			io:format("~p : ~p~n",[C,D]) end
	  	,Bank),
	io:fwrite("~n"),
	M2 = maps:from_list(Bank),
	M2keys = maps:keys(M2),
	Length2 = length(M2keys),
	for(Length2,M2keys, M2,2,M2keys),
	Str1 = "** Customers and loan objectives **~n",
	io:fwrite(Str1),
	{ok,Cust} = file:consult("customers.txt"),
	lists:foreach(
	  	fun({A,B}) ->
			io:format("~p : ~p~n",[A,B]) end
	  	,Cust),
	io:fwrite("~n"),
	M1 = maps:from_list(Cust),
	M1keys = maps:keys(M1),
	Length1 = length(M1keys),
	for(Length1, M1keys,M1,1,M2keys),
	requestreceive(M2keys,Length1, M1,M1).


for(0,_,_,_,_) ->
	ok;

for(Max, MKeys , Map, Call,Bkeys) when Max > 0 ->
	Name = nth(Max,MKeys),
	Amt = get(Name, Map),
	if 
		Call == 1 ->
			Pid = spawn(customer, startcust, [Name, Amt,Bkeys]),
			register(Name,Pid);
		true ->
			Pid = spawn(bank, startbank, [Name, Amt]), 
			register(Name,Pid)
	end,
	for(Max-1, MKeys,Map,Call,Bkeys).


for1(0,_) ->
	ok;

for1(Len,M2keys) when Len > 0 ->
	Bname = nth(Len,M2keys),
	whereis(Bname) ! {givebal, Bname},
	for1(Len-1,M2keys).
	
getbankbal(M2keys) ->
	Length = length(M2keys),
	for1(Length,M2keys).
	

for2(_,_,0,_) ->
	ok;
for2(S,Keys,Lent,Map) when Lent >0 ->
	Name = nth(Lent, Keys),
	Tamt = get(Name,Map),
	if S == 1 ->
		io:fwrite("~p has reached the objective of ~p dollar(s). Woo Hoo!~n",[Name,Tamt]);
	   true ->
		io:fwrite("~p was only able to borrow ~p dollar(s). Boo Hoo!~n",[Name,Tamt])
	 end,
	for2(S,Keys,Lent-1,Map).

displaycust(Map1,Map2) ->
	Keys1 = maps:keys(Map1),
	Lent1 = length(Keys1),
	io:fwrite("~n~n"),
	for2(1,Keys1,Lent1,Map1),
	Keys2 = maps:keys(Map2),
	Lent2 = length(Keys2),
	for2(2,Keys2,Lent2,Map2).
	

requestreceive(M2keys,Length1,Mwohoo,Mbohoo) ->
			receive
				{approve,Name,Bname,Famount,Tamount, Amt,Bkeys} ->
					io:fwrite("~p's request approved from ~p for amount ~p~n", [Name,Bname,Amt]),
					whereis(Name) ! {approve,Name,Bname,Famount,Tamount, Amt,Bkeys},
					requestreceive(M2keys,Length1,Mwohoo,Mbohoo);
				{reject,Name,Bname,Famount,Tamount, Amt,Bkeys} ->
					io:fwrite("~p's request rejected from ~p for amount ~p~n", [Name,Bname,Amt]),
					whereis(Name) ! {reject,Name,Bname,Famount,Tamount, Amt,Bkeys},
					requestreceive(M2keys,Length1,Mwohoo,Mbohoo);
				{reached,Name,Tamt} ->
					M2 = Mwohoo,
					M3 = Mbohoo,
					M4 = remove(Name,M2),
					M5 = remove(Name,M3),
					M6 = put(Name,Tamt,M4),
					unregister(Name),
					Len =  Length1 - 1,
					if Len == 0 ->
						   displaycust(M6,M5),
						   getbankbal(M2keys);
					   true ->
						   io:fwrite("")
					end,
					requestreceive(M2keys,Len,M6,M5);
				{borrowleft,Name,Amount} ->
					M2 = Mwohoo,
					M3 = Mbohoo,
					M4 = remove(Name,M2),
					M5 = remove(Name,M3),
					M6 = put(Name,Amount,M5),
					unregister(Name),
					Len =  Length1 - 1,
					if Len == 0 ->
						   displaycust(M4,M6),
						   getbankbal(M2keys);
					   true ->
						   io:fwrite("")
					end,
					requestreceive(M2keys,Len,M4,M6);
				{request,Name,A,Bank} ->
					io:fwrite("~p requests a loan of ~p dollar(s) from ~p~n",[Name,A,Bank]),
					requestreceive(M2keys,Length1,Mwohoo,Mbohoo);
				{mybal, Name, Amount} ->
					io:fwrite("~p has ~p dollar(s) remaining.~n",[Name,Amount]),
					requestreceive(M2keys,Length1,Mwohoo,Mbohoo)
				after 10000 -> 
					io:fwrite("")					
			end.


