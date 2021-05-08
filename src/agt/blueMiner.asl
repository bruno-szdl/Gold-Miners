// miner agent

team("blue").

!start.
+!start : true
    <- lookupArtifact("blueTeamMap", MapId).

{ include("miner.asl") }


