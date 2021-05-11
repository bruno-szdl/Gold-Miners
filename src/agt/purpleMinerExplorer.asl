// miner agent

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }

/* beliefs */
last_dir(null). // the last movement I did
score(0).
count(0).
team("purple").

!start.
+!start : true
    <-  ?team(T);
        .concat(T, "TeamMap", MapName);
        lookupArtifact(MapName, MapId);
        ?pos(AgX, AgY);
        !setFreeCellsAround(AgX, AgY);
        .

+!explore
   <- +free.

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


+near(X,Y) : free <- -+free.


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


+cell(X,Y,gold) 
    <- setGoldCell(X, Y) [artifact_id(MapId)];
       setGoldFound(X, Y) [artifact_id(MapId)];
    .

+cell(X,Y,obstacle) 
    <- setObstacleCell(X, Y) [artifact_id(MapId)];
.

