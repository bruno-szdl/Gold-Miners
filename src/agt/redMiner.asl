// miner agent

{ include("$jacamoJar/templates/common-cartago.asl") }

/* beliefs */
last_dir(null). // the last movement I did
free.
score(0).
count(0).
team("red").

!start.
+!start : true
    <-  ?team(T);
        .concat(T, "TeamMap", MapName);
        lookupArtifact(MapName, MapId);
        ?pos(AgX, AgY);
        !setFreeCellsAround(AgX, AgY);
        .

+free
   <-   askUnknownCell(RX, RY) [artifact_id(MapId)];
        if(RX == 100){
         .print("There is no free cell");
         -free;
        } else{
         !go_near(RX,RY);
        }
   .

+free
   <- .wait(100); -+free.


+near(X,Y) : free <- !ask_gold_cell.


+!setFreeCellsAround(X, Y)
    <- !setFreeCells(X, Y);
       !setFreeCells(X, Y+1);
       !setFreeCells(X, Y-1);
       !setFreeCells(X+1, Y);
       !setFreeCells(X+1, Y+1);
       !setFreeCells(X+1, Y-1);
       !setFreeCells(X-1, Y);
       !setFreeCells(X-1, Y+1);
       !setFreeCells(X-1, Y-1);
       .


+!setFreeCells(X, Y)
    <-  askCellValue(X, Y, V);
        if(V == "?") {
            setFreeCell(X, Y) [artifact_id(MapId)];
        }
        .

+!go_near(X,Y) : free
  <- -near(_,_);
     -last_dir(_);
     !near(X,Y).

+!near(X,Y) : (pos(AgX,AgY) & jia.neighbour(AgX,AgY,X,Y))
   <- .print("I am at ", "(",AgX,",", AgY,")", " which is near (",X,",", Y,")");
      +near(X,Y).

+!near(X,Y) : pos(AgX,AgY) & last_dir(skip)
   <- .print("I am at ", "(",AgX,",", AgY,")", " and I can't get to' (",X,",", Y,")");
      +near(X,Y).

+!near(X,Y) : not near(X,Y)
   <- !next_step(X,Y);
      !near(X,Y).
+!near(X,Y) : true
   <- !near(X,Y).


+!next_step(X,Y) : pos(AgX,AgY) // I already know my position
   <- !setFreeCellsAround(AgX, AgY);
      jia.get_direction(AgX, AgY, X, Y, D);
      -+last_dir(D);
      D.
+!next_step(X,Y) : not pos(_,_) // I still do not know my position
   <- !next_step(X,Y).
-!next_step(X,Y) : true  // failure handling -> start again!
   <- -+last_dir(null);
      !next_step(X,Y).

+!pos(X,Y) : pos(X,Y)
   <- .print("I've reached ",X,"x",Y).
+!pos(X,Y) : not pos(X,Y)
   <- !next_step(X,Y);
      !pos(X,Y).

+cell(X,Y,gold) :  not carrying_gold
    <- setGoldCell(X, Y) [artifact_id(MapId)];
       +gold(X,Y);
    .

+cell(X,Y,gold) 
    <- setGoldCell(X, Y) [artifact_id(MapId)];
       setGoldFound(X, Y) [artifact_id(MapId)];
    .

+cell(X,Y,obstacle) 
    <- setObstacleCell(X, Y) [artifact_id(MapId)];
.

+gold_found(X, Y) 
   : not gold(X,Y)
   <- +gold(X, Y);
   .

+gold_picked(X, Y) 
   <- -gold(X, Y);
   .

@pgold[atomic]           // atomic: so as not to handle another event until handle gold is initialised
+gold(X,Y)
  :  not carrying_gold & free
  <- -free;
     .print("Gold perceived: ",gold(X,Y));
     !init_handle(gold(X,Y)).

@pgold2[atomic]
+gold(X,Y)
  :  not carrying_gold & not free &
     .desire(handle(gold(OldX,OldY))) &   // I desire to handle another gold which
     pos(AgX,AgY) &
     jia.dist(X,   Y,   AgX,AgY,DNewG) &
     jia.dist(OldX,OldY,AgX,AgY,DOldG) &
     DNewG < DOldG                        // is farther than the one just perceived
  <- .drop_desire(handle(gold(OldX,OldY)));
     .print("Giving up current gold ",gold(OldX,OldY)," to handle ",gold(X,Y)," which I am seeing!");
     !init_handle(gold(X,Y)).

+!ensure(pick,_) : pos(X,Y) & gold(X,Y)
  <- pick;
     setFreeCell(X, Y) [artifact_id(MapId)];
     ?carrying_gold;
     -gold(X,Y).

@pih1[atomic]
+!init_handle(Gold)
  :  .desire(near(_,_))
  <- .print("Dropping near(_,_) desires and intentions to handle ",Gold);
     .drop_desire(near(_,_));
     !init_handle(Gold).
@pih2[atomic]
+!init_handle(Gold)
  :  pos(X,Y)
  <- .print("Going for ",Gold);
     !!handle(Gold). // must use !! to perform "handle" as not atomic

+!handle(gold(X,Y))
  :  not free & team(T)
  <- .print("Handling ",gold(X,Y)," now.");
     !pos(X,Y);
     !ensure(pick,gold(X,Y));
     ?depot(_,DX,DY);
     !pos(DX,DY);
     !ensure(drop, 0);
     .print("Finish handling ",gold(X,Y));
     ?score(S);
     -+score(S+1);
     .send(leader,tell,dropped(T));
     !!ask_gold_cell.

// if ensure(pick/drop) failed, pursue another gold
-!handle(G) : G
  <- .print("failed to catch gold ",G);
     .abolish(G); // ignore source
     !!ask_gold_cell.
-!handle(G) : true
  <- .print("failed to handle ",G,", it isn't in the BB anyway");
     !!ask_gold_cell.

+!ensure(pick,_) : pos(X,Y) & gold(X,Y)
  <- pick;
     ?carrying_gold;
     -gold(X,Y).
// fail if no gold there or not carrying_gold after pick!
// handle(G) will "catch" this failure.

+!ensure(drop, _) : carrying_gold & depot(_,DX,DY)
  <- drop.

+!ask_gold_cell : pos(AgX, AgY)
    <- askCloserGoldCell(AgX, AgY, XG, YG);
       .print("--------(", XG, ", ", YG,")--------------");
       if (XG \== 100){
         setAgentGoldCell(XG, YG) [artifact_id(MapId)];
         !!handle(gold(XG, YG));
       } else {
         -+free;
       }
       . 

