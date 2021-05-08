// miner agent

team("green").

!start.
+!start : true
    <- lookupArtifact("blueTeamMap", MapId).

{ include("miner.asl") }


