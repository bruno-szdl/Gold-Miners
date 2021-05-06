// leader agent

{ include("$jacamoJar/templates/common-cartago.asl") }

/*
 * By Joao Leite
 * Based on implementation developed by Rafael Bordini, Jomi Hubner and Maicon Zatelli
 */


score("Blue",0).
score("Red",0).
score("Green",0).

winning(none,0).


//the start goal only works after execise j)
//!start.
//+!start <- tweet("a new mining is starting! (posted by jason agent)").

+dropped(T)[source(A)] : score(T,S) & winning(L,SL) & S+1>SL
   <- -score(T,S);
      +score(T,S+1);
      -dropped(T)[source(A)];
      -+winning(T,S+1);
      .print("Team ",T," is winning with ",S+1," pieces of gold");
      .broadcast(tell,winning(T,S+1)).

+dropped(T)[source(A)] : score(T,S) & winning(L,SL) & S+1=SL
   <- -score(T,S);
      +score(T,S+1);
      -dropped(T)[source(A)];
      -+winning(T,S+1);
      .print("Team ",T," and Team ", L, " are tied with ", S+1," pieces of gold");
      .broadcast(tell,winning(T,S+1)).

+dropped(T)[source(A)] : score(T,S)
   <- -score(T,S);
      +score(T,S+1);
      -dropped(T)[source(A)];
      .print("Team ",T," has dropped ",S+1," pieces of gold").
