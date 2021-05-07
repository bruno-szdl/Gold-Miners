package mining;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import mining.MiningPlanet.Move;

public class WorldModel extends GridWorldModel {

    public static final int   GOLD  = 16;
    public static final int   DEPOT = 32;
    public static final int   ENEMY = 64;

    Location                  depot;
    Set<Integer>              agWithGold;  // which agent is carrying gold
    int                       goldsInDepot   = 0;
    int                       initialNbGolds = 0;

    private Logger            logger   = Logger.getLogger("jasonTeamSimLocal.mas2j." + WorldModel.class.getName());

    private String            id = "WorldModel";

    int agsByTeam = 3;

    // singleton pattern
    protected static WorldModel model = null;

    synchronized public static WorldModel create(int w, int h, int nbAgs) {
        if (model == null) {
            model = new WorldModel(w, h, nbAgs);
        }
        return model;
    }

    public static WorldModel get() {
        return model;
    }

    public static void destroy() {
        model = null;
    }

    private WorldModel(int w, int h, int nbAgs) {
        super(w, h, nbAgs);
        agWithGold = new HashSet<Integer>();
    }

    public int getAgsByTeam() {
        return agsByTeam;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String toString() {
        return id;
    }

    public Location getDepot() {
        return depot;
    }

    public int getGoldsInDepot() {
        return goldsInDepot;
    }

    public boolean isAllGoldsCollected() {
        return goldsInDepot == initialNbGolds;
    }

    public void setInitialNbGolds(int i) {
        initialNbGolds = i;
    }

    public int getInitialNbGolds() {
        return initialNbGolds;
    }

    public boolean isCarryingGold(int ag) {
        return agWithGold.contains(ag);
    }

    public void setDepot(int x, int y) {
        depot = new Location(x, y);
        data[x][y] = DEPOT;
    }

    public void setAgCarryingGold(int ag) {
        agWithGold.add(ag);
    }
    public void setAgNotCarryingGold(int ag) {
        agWithGold.remove(ag);
    }

    /** Actions **/

    boolean move(Move dir, int ag) throws Exception {
        Location l = getAgPos(ag);
        switch (dir) {
        case UP:
            if (isFree(l.x, l.y - 1)) {
                setAgPos(ag, l.x, l.y - 1);
            }
            break;
        case DOWN:
            if (isFree(l.x, l.y + 1)) {
                setAgPos(ag, l.x, l.y + 1);
            }
            break;
        case RIGHT:
            if (isFree(l.x + 1, l.y)) {
                setAgPos(ag, l.x + 1, l.y);
            }
            break;
        case LEFT:
            if (isFree(l.x - 1, l.y)) {
                setAgPos(ag, l.x - 1, l.y);
            }
            break;
        }
        return true;
    }

    boolean pick(int ag) {
        Location l = getAgPos(ag);
        if (hasObject(WorldModel.GOLD, l.x, l.y)) {
            if (!isCarryingGold(ag)) {
                remove(WorldModel.GOLD, l.x, l.y);
                setAgCarryingGold(ag);
                return true;
            } else {
                logger.warning("Agent " + (ag + 1) + " is trying the pick gold, but it is already carrying gold!");
            }
        } else {
            logger.warning("Agent " + (ag + 1) + " is trying the pick gold, but there is no gold at " + l.x + "x" + l.y + "!");
        }
        return false;
    }

    boolean drop(int ag) {
        Location l = getAgPos(ag);
        if (isCarryingGold(ag)) {
            if (l.equals(getDepot())) {
                goldsInDepot++;
                logger.info("Agent " + (ag + 1) + " carried a gold to depot!");
            } else {
                add(WorldModel.GOLD, l.x, l.y);
            }
            setAgNotCarryingGold(ag);
            return true;
        }
        return false;
    }

    /*
    public void clearAgView(int agId) {
        clearAgView(getAgPos(agId).x, getAgPos(agId).y);
    }

    public void clearAgView(int x, int y) {
        int e1 = ~(ENEMY + ALLY + GOLD);
        if (x > 0 && y > 0) {
            data[x - 1][y - 1] &= e1;
        } // nw
        if (y > 0) {
            data[x][y - 1] &= e1;
        } // n
        if (x < (width - 1) && y > 0) {
            data[x + 1][y - 1] &= e1;
        } // ne

        if (x > 0) {
            data[x - 1][y] &= e1;
        } // w
        data[x][y] &= e1; // cur
        if (x < (width - 1)) {
            data[x + 1][y] &= e1;
        } // e

        if (x > 0 && y < (height - 1)) {
            data[x - 1][y + 1] &= e1;
        } // sw
        if (y < (height - 1)) {
            data[x][y + 1] &= e1;
        } // s
        if (x < (width - 1) && y < (height - 1)) {
            data[x + 1][y + 1] &= e1;
        } // se
    }
    */


    /** no gold/no obstacle world */
    static WorldModel world1() throws Exception {
        WorldModel model = WorldModel.create(21, 21, 4);
        model.setId("Scenario 1");
        model.setDepot(0, 0);
        model.setAgPos(0, 1, 0);
        model.setAgPos(1, 20, 0);
        model.setAgPos(2, 3, 20);
        model.setAgPos(3, 20, 20);
        model.setInitialNbGolds(model.countObjects(WorldModel.GOLD));
        return model;
    }

    /** world with gold, no obstacle */
    static WorldModel world2() throws Exception {
        WorldModel model = WorldModel.create(35, 35, 4);
        model.setId("Scenario 2");
        model.setDepot(0, 0);
        model.setAgPos(0, 1, 0);
        model.setAgPos(1, 22, 0);
        model.setAgPos(2, 3, 22);
        model.setAgPos(3, 22, 22);
        model.add(WorldModel.OBSTACLE, 20, 0);
        model.add(WorldModel.OBSTACLE, 20, 1);
        model.add(WorldModel.OBSTACLE, 20, 2);
        model.add(WorldModel.OBSTACLE, 20, 3);
        model.add(WorldModel.OBSTACLE, 20, 4);
        model.add(WorldModel.OBSTACLE, 20, 5);
        model.add(WorldModel.OBSTACLE, 20, 6);
        model.add(WorldModel.OBSTACLE, 20, 7);
        model.add(WorldModel.OBSTACLE, 20, 8);
        model.add(WorldModel.OBSTACLE, 20, 9);
        model.add(WorldModel.OBSTACLE, 20, 10);
        model.add(WorldModel.OBSTACLE, 20, 11);
        model.add(WorldModel.OBSTACLE, 20, 12);
        model.add(WorldModel.OBSTACLE, 20, 13);
        model.add(WorldModel.OBSTACLE, 20, 14);
        model.add(WorldModel.OBSTACLE, 20, 15);
        model.add(WorldModel.OBSTACLE, 20, 16);
        model.add(WorldModel.OBSTACLE, 20, 17);
        model.add(WorldModel.OBSTACLE, 20, 18);
        model.add(WorldModel.OBSTACLE, 20, 19);
        model.add(WorldModel.OBSTACLE, 20, 20);
        model.add(WorldModel.OBSTACLE, 21, 20);
        model.add(WorldModel.OBSTACLE, 22, 20);
        model.add(WorldModel.OBSTACLE, 23, 20);
        model.add(WorldModel.OBSTACLE, 24, 20);
        model.add(WorldModel.OBSTACLE, 25, 20);
        model.add(WorldModel.OBSTACLE, 26, 20);
        model.add(WorldModel.OBSTACLE, 27, 20);
        model.add(WorldModel.OBSTACLE, 28, 20);
        model.add(WorldModel.OBSTACLE, 29, 20);
        model.add(WorldModel.OBSTACLE, 30, 20);
        model.add(WorldModel.OBSTACLE, 31, 20);
        model.add(WorldModel.OBSTACLE, 32, 20);
        model.add(WorldModel.OBSTACLE, 33, 20);
        model.add(WorldModel.OBSTACLE, 34, 20);
        model.setInitialNbGolds(model.countObjects(WorldModel.GOLD));
        return model;
    }

    /** world with gold, no obstacle */
    static WorldModel world3() throws Exception {
        WorldModel model = WorldModel.create(35, 35, 4);
        model.setId("Scenario 3");
        model.setDepot(0, 0);
        model.setAgPos(0, 1, 0);
        model.setAgPos(1, 20, 0);
        model.setAgPos(2, 3, 20);
        model.setAgPos(3, 20, 20);
        model.add(WorldModel.GOLD, 20, 13);
        model.add(WorldModel.GOLD, 15, 20);
        model.add(WorldModel.GOLD, 1, 1);
        model.add(WorldModel.GOLD, 3, 5);
        model.add(WorldModel.GOLD, 24, 24);
        model.add(WorldModel.GOLD, 20, 20);
        model.add(WorldModel.GOLD, 26, 21);
        model.add(WorldModel.GOLD, 12, 22);
        model.add(WorldModel.GOLD, 20, 23);
        model.add(WorldModel.GOLD, 33, 24);
        model.add(WorldModel.GOLD, 19, 20);
        model.add(WorldModel.GOLD, 19, 21);
        model.add(WorldModel.GOLD, 34, 34);
        model.setInitialNbGolds(model.countObjects(WorldModel.GOLD));
        return model;
    }



    /** world with gold, no obstacle */
    static WorldModel world4() throws Exception {
        WorldModel model = WorldModel.create(35, 35, 4);
        model.setId("Scenario 4");
        model.setDepot(5, 27);
        model.setAgPos(0, 1, 0);
        model.setAgPos(1, 20, 0);
        model.setAgPos(2, 3, 20);
        model.setAgPos(3, 20, 20);
        model.add(WorldModel.GOLD, 20, 13);
        model.add(WorldModel.GOLD, 15, 20);
        model.add(WorldModel.GOLD, 1, 1);
        model.add(WorldModel.GOLD, 3, 5);
        model.add(WorldModel.GOLD, 24, 24);
        model.add(WorldModel.GOLD, 20, 20);
        model.add(WorldModel.GOLD, 20, 21);
        model.add(WorldModel.GOLD, 20, 22);
        model.add(WorldModel.GOLD, 20, 23);
        model.add(WorldModel.GOLD, 20, 24);
        model.add(WorldModel.GOLD, 19, 20);
        model.add(WorldModel.GOLD, 19, 21);
        model.add(WorldModel.GOLD, 34, 34);
        model.setInitialNbGolds(model.countObjects(WorldModel.GOLD));
        return model;
    }



    /** world with gold, some obstacle */
    static WorldModel world5() throws Exception {
        WorldModel model = WorldModel.create(35, 35, 4);
        model.setId("Scenario 5");
        model.setDepot(5, 27);
        model.setAgPos(0, 1, 0);
        model.setAgPos(1, 20, 0);
        model.setAgPos(2, 3, 20);
        model.setAgPos(3, 20, 20);
        model.add(WorldModel.GOLD, 20, 13);
        model.add(WorldModel.GOLD, 15, 20);
        model.add(WorldModel.GOLD, 1, 1);
        model.add(WorldModel.GOLD, 3, 5);
        model.add(WorldModel.GOLD, 24, 24);
        model.add(WorldModel.GOLD, 20, 20);
        model.add(WorldModel.GOLD, 20, 21);
        model.add(WorldModel.GOLD, 20, 22);
        model.add(WorldModel.GOLD, 20, 23);
        model.add(WorldModel.GOLD, 20, 24);
        model.add(WorldModel.GOLD, 19, 20);
        model.add(WorldModel.GOLD, 19, 21);
        model.add(WorldModel.GOLD, 34, 34);

        model.add(WorldModel.OBSTACLE, 12, 3);
        model.add(WorldModel.OBSTACLE, 13, 3);
        model.add(WorldModel.OBSTACLE, 14, 3);
        model.add(WorldModel.OBSTACLE, 15, 3);
        model.add(WorldModel.OBSTACLE, 18, 3);
        model.add(WorldModel.OBSTACLE, 19, 3);
        model.add(WorldModel.OBSTACLE, 20, 3);
        model.add(WorldModel.OBSTACLE, 14, 8);
        model.add(WorldModel.OBSTACLE, 15, 8);
        model.add(WorldModel.OBSTACLE, 16, 8);
        model.add(WorldModel.OBSTACLE, 17, 8);
        model.add(WorldModel.OBSTACLE, 19, 8);
        model.add(WorldModel.OBSTACLE, 20, 8);
        model.setInitialNbGolds(model.countObjects(WorldModel.GOLD));
        return model;
    }


    /** world with gold and obstacles */
    static WorldModel world6() throws Exception {
        WorldModel model = WorldModel.create(35, 35, 4);
        model.setId("Scenario 6");
        model.setDepot(16, 16);
        model.setAgPos(0, 1, 0);
        model.setAgPos(1, 20, 0);
        model.setAgPos(2, 6, 26);
        model.setAgPos(3, 20, 20);
        model.add(WorldModel.GOLD, 20, 13);
        model.add(WorldModel.GOLD, 15, 20);
        model.add(WorldModel.GOLD, 1, 1);
        model.add(WorldModel.GOLD, 3, 5);
        model.add(WorldModel.GOLD, 24, 24);
        model.add(WorldModel.GOLD, 20, 20);
        model.add(WorldModel.GOLD, 20, 21);
        model.add(WorldModel.GOLD, 2, 22);
        model.add(WorldModel.GOLD, 2, 12);
        model.add(WorldModel.GOLD, 19, 2);
        model.add(WorldModel.GOLD, 14, 4);
        model.add(WorldModel.GOLD, 34, 34);

        model.add(WorldModel.OBSTACLE, 12, 3);
        model.add(WorldModel.OBSTACLE, 13, 3);
        model.add(WorldModel.OBSTACLE, 14, 3);
        model.add(WorldModel.OBSTACLE, 15, 3);
        model.add(WorldModel.OBSTACLE, 18, 3);
        model.add(WorldModel.OBSTACLE, 19, 3);
        model.add(WorldModel.OBSTACLE, 20, 3);
        model.add(WorldModel.OBSTACLE, 14, 8);
        model.add(WorldModel.OBSTACLE, 15, 8);
        model.add(WorldModel.OBSTACLE, 16, 8);
        model.add(WorldModel.OBSTACLE, 17, 8);
        model.add(WorldModel.OBSTACLE, 19, 8);
        model.add(WorldModel.OBSTACLE, 20, 8);

        model.add(WorldModel.OBSTACLE, 12, 32);
        model.add(WorldModel.OBSTACLE, 13, 32);
        model.add(WorldModel.OBSTACLE, 14, 32);
        model.add(WorldModel.OBSTACLE, 15, 32);
        model.add(WorldModel.OBSTACLE, 18, 32);
        model.add(WorldModel.OBSTACLE, 19, 32);
        model.add(WorldModel.OBSTACLE, 20, 32);
        model.add(WorldModel.OBSTACLE, 14, 28);
        model.add(WorldModel.OBSTACLE, 15, 28);
        model.add(WorldModel.OBSTACLE, 16, 28);
        model.add(WorldModel.OBSTACLE, 17, 28);
        model.add(WorldModel.OBSTACLE, 19, 28);
        model.add(WorldModel.OBSTACLE, 20, 28);

        model.add(WorldModel.OBSTACLE, 3, 12);
        model.add(WorldModel.OBSTACLE, 3, 13);
        model.add(WorldModel.OBSTACLE, 3, 14);
        model.add(WorldModel.OBSTACLE, 3, 15);
        model.add(WorldModel.OBSTACLE, 3, 18);
        model.add(WorldModel.OBSTACLE, 3, 19);
        model.add(WorldModel.OBSTACLE, 3, 20);
        model.add(WorldModel.OBSTACLE, 8, 14);
        model.add(WorldModel.OBSTACLE, 8, 15);
        model.add(WorldModel.OBSTACLE, 8, 16);
        model.add(WorldModel.OBSTACLE, 8, 17);
        model.add(WorldModel.OBSTACLE, 8, 19);
        model.add(WorldModel.OBSTACLE, 8, 20);

        model.add(WorldModel.OBSTACLE, 32, 12);
        model.add(WorldModel.OBSTACLE, 32, 13);
        model.add(WorldModel.OBSTACLE, 32, 14);
        model.add(WorldModel.OBSTACLE, 32, 15);
        model.add(WorldModel.OBSTACLE, 32, 18);
        model.add(WorldModel.OBSTACLE, 32, 19);
        model.add(WorldModel.OBSTACLE, 32, 20);
        model.add(WorldModel.OBSTACLE, 28, 14);
        model.add(WorldModel.OBSTACLE, 28, 15);
        model.add(WorldModel.OBSTACLE, 28, 16);
        model.add(WorldModel.OBSTACLE, 28, 17);
        model.add(WorldModel.OBSTACLE, 28, 19);
        model.add(WorldModel.OBSTACLE, 28, 20);

        model.add(WorldModel.OBSTACLE, 13, 13);
        model.add(WorldModel.OBSTACLE, 13, 14);

        model.add(WorldModel.OBSTACLE, 13, 16);
        model.add(WorldModel.OBSTACLE, 13, 17);

        model.add(WorldModel.OBSTACLE, 13, 19);
        model.add(WorldModel.OBSTACLE, 14, 19);

        model.add(WorldModel.OBSTACLE, 16, 19);
        model.add(WorldModel.OBSTACLE, 17, 19);

        model.add(WorldModel.OBSTACLE, 19, 19);
        model.add(WorldModel.OBSTACLE, 19, 18);

        model.add(WorldModel.OBSTACLE, 19, 16);
        model.add(WorldModel.OBSTACLE, 19, 15);

        model.add(WorldModel.OBSTACLE, 19, 13);
        model.add(WorldModel.OBSTACLE, 18, 13);

        model.add(WorldModel.OBSTACLE, 16, 13);
        model.add(WorldModel.OBSTACLE, 15, 13);

        // labirinto
        model.add(WorldModel.OBSTACLE, 2, 32);
        model.add(WorldModel.OBSTACLE, 3, 32);
        model.add(WorldModel.OBSTACLE, 4, 32);
        model.add(WorldModel.OBSTACLE, 5, 32);
        model.add(WorldModel.OBSTACLE, 6, 32);
        model.add(WorldModel.OBSTACLE, 7, 32);
        model.add(WorldModel.OBSTACLE, 8, 32);
        model.add(WorldModel.OBSTACLE, 9, 32);
        model.add(WorldModel.OBSTACLE, 10, 32);
        model.add(WorldModel.OBSTACLE, 10, 31);
        model.add(WorldModel.OBSTACLE, 10, 30);
        model.add(WorldModel.OBSTACLE, 10, 29);
        model.add(WorldModel.OBSTACLE, 10, 28);
        model.add(WorldModel.OBSTACLE, 10, 27);
        model.add(WorldModel.OBSTACLE, 10, 26);
        model.add(WorldModel.OBSTACLE, 10, 25);
        model.add(WorldModel.OBSTACLE, 10, 24);
        model.add(WorldModel.OBSTACLE, 10, 23);
        model.add(WorldModel.OBSTACLE, 2, 23);
        model.add(WorldModel.OBSTACLE, 3, 23);
        model.add(WorldModel.OBSTACLE, 4, 23);
        model.add(WorldModel.OBSTACLE, 5, 23);
        model.add(WorldModel.OBSTACLE, 6, 23);
        model.add(WorldModel.OBSTACLE, 7, 23);
        model.add(WorldModel.OBSTACLE, 8, 23);
        model.add(WorldModel.OBSTACLE, 9, 23);
        model.add(WorldModel.OBSTACLE, 2, 29);
        model.add(WorldModel.OBSTACLE, 2, 28);
        model.add(WorldModel.OBSTACLE, 2, 27);
        model.add(WorldModel.OBSTACLE, 2, 26);
        model.add(WorldModel.OBSTACLE, 2, 25);
        model.add(WorldModel.OBSTACLE, 2, 24);
        model.add(WorldModel.OBSTACLE, 2, 23);
        model.add(WorldModel.OBSTACLE, 2, 29);
        model.add(WorldModel.OBSTACLE, 3, 29);
        model.add(WorldModel.OBSTACLE, 4, 29);
        model.add(WorldModel.OBSTACLE, 5, 29);
        model.add(WorldModel.OBSTACLE, 6, 29);
        model.add(WorldModel.OBSTACLE, 7, 29);
        model.add(WorldModel.OBSTACLE, 7, 28);
        model.add(WorldModel.OBSTACLE, 7, 27);
        model.add(WorldModel.OBSTACLE, 7, 26);
        model.add(WorldModel.OBSTACLE, 7, 25);
        model.add(WorldModel.OBSTACLE, 6, 25);
        model.add(WorldModel.OBSTACLE, 5, 25);
        model.add(WorldModel.OBSTACLE, 4, 25);
        model.add(WorldModel.OBSTACLE, 4, 26);
        model.add(WorldModel.OBSTACLE, 4, 27);
        model.setInitialNbGolds(model.countObjects(WorldModel.GOLD));
        return model;
    }

    static WorldModel world7() throws Exception {
        WorldModel model = WorldModel.create(45, 45, 9);
        model.setId("Scenario 7");
        model.setDepot(21, 21);
        model.setAgPos(0, 0, 0);
        model.setAgPos(1, 1, 0);
        model.setAgPos(2, 0, 1);
        model.setAgPos(3, 44, 43);
        model.setAgPos(4, 43, 44);
        model.setAgPos(5, 44, 43);
        model.setAgPos(6, 43, 0);
        model.setAgPos(7, 44, 0);
        model.setAgPos(8, 43, 1);
        model.add(WorldModel.GOLD, 25, 18);
        model.add(WorldModel.GOLD, 20, 25);
        model.add(WorldModel.GOLD, 4, 4);
        model.add(WorldModel.GOLD, 8, 10);
        model.add(WorldModel.GOLD, 31, 29);
        model.add(WorldModel.GOLD, 25, 25);
        model.add(WorldModel.GOLD, 25, 26);
        model.add(WorldModel.GOLD, 7, 27);
        model.add(WorldModel.GOLD, 7, 17);
        model.add(WorldModel.GOLD, 24, 7);
        model.add(WorldModel.GOLD, 19, 9);
        model.add(WorldModel.GOLD, 34, 39);
        model.add(WorldModel.GOLD, 11, 32);
        model.add(WorldModel.GOLD, 32, 11);
        model.add(WorldModel.GOLD, 35, 10);
        model.add(WorldModel.GOLD, 35, 20);
        model.add(WorldModel.GOLD, 20, 35);
        model.add(WorldModel.GOLD, 20, 12);
        model.add(WorldModel.GOLD, 12, 20);
        model.add(WorldModel.GOLD, 10, 35);
        model.add(WorldModel.GOLD, 33, 33);
        model.add(WorldModel.GOLD, 15, 16);
        model.add(WorldModel.GOLD, 18, 3);
        model.add(WorldModel.GOLD, 25, 2);
        model.add(WorldModel.GOLD, 17, 43);
        model.add(WorldModel.GOLD, 25, 41);
        model.add(WorldModel.GOLD, 3, 26);
        model.add(WorldModel.GOLD, 36, 21);
        model.add(WorldModel.GOLD, 25, 41);
        model.add(WorldModel.GOLD, 40, 40);
        model.add(WorldModel.GOLD, 41, 28);
        model.add(WorldModel.GOLD, 1, 39);
        model.add(WorldModel.GOLD, 4, 43);
        model.add(WorldModel.GOLD, 16, 28);
        model.add(WorldModel.GOLD, 41, 16);
        model.add(WorldModel.GOLD, 14, 29);
        model.add(WorldModel.GOLD, 2, 16);
        model.add(WorldModel.GOLD, 40, 4);
        model.add(WorldModel.GOLD, 21, 0);
        model.add(WorldModel.GOLD, 26, 32);
        model.add(WorldModel.GOLD, 4, 40);


        model.add(WorldModel.OBSTACLE, 17, 8);
        model.add(WorldModel.OBSTACLE, 18, 8);
        model.add(WorldModel.OBSTACLE, 19, 8);
        model.add(WorldModel.OBSTACLE, 20, 8);
        model.add(WorldModel.OBSTACLE, 23, 8);
        model.add(WorldModel.OBSTACLE, 24, 8);
        model.add(WorldModel.OBSTACLE, 25, 8);
        model.add(WorldModel.OBSTACLE, 19, 13);
        model.add(WorldModel.OBSTACLE, 20, 13);
        model.add(WorldModel.OBSTACLE, 21, 13);
        model.add(WorldModel.OBSTACLE, 22, 13);
        model.add(WorldModel.OBSTACLE, 24, 13);
        model.add(WorldModel.OBSTACLE, 25, 13);

        model.add(WorldModel.OBSTACLE, 17, 37);
        model.add(WorldModel.OBSTACLE, 18, 37);
        model.add(WorldModel.OBSTACLE, 19, 37);
        model.add(WorldModel.OBSTACLE, 20, 37);
        model.add(WorldModel.OBSTACLE, 23, 37);
        model.add(WorldModel.OBSTACLE, 24, 37);
        model.add(WorldModel.OBSTACLE, 25, 37);
        model.add(WorldModel.OBSTACLE, 19, 33);
        model.add(WorldModel.OBSTACLE, 20, 33);
        model.add(WorldModel.OBSTACLE, 21, 33);
        model.add(WorldModel.OBSTACLE, 22, 33);
        model.add(WorldModel.OBSTACLE, 24, 33);
        model.add(WorldModel.OBSTACLE, 25, 33);

        model.add(WorldModel.OBSTACLE, 8, 17);
        model.add(WorldModel.OBSTACLE, 8, 18);
        model.add(WorldModel.OBSTACLE, 8, 19);
        model.add(WorldModel.OBSTACLE, 8, 20);
        model.add(WorldModel.OBSTACLE, 8, 23);
        model.add(WorldModel.OBSTACLE, 8, 24);
        model.add(WorldModel.OBSTACLE, 8, 25);
        model.add(WorldModel.OBSTACLE, 13, 19);
        model.add(WorldModel.OBSTACLE, 13, 20);
        model.add(WorldModel.OBSTACLE, 13, 21);
        model.add(WorldModel.OBSTACLE, 13, 22);
        model.add(WorldModel.OBSTACLE, 13, 24);
        model.add(WorldModel.OBSTACLE, 13, 25);

        model.add(WorldModel.OBSTACLE, 37, 17);
        model.add(WorldModel.OBSTACLE, 37, 18);
        model.add(WorldModel.OBSTACLE, 37, 19);
        model.add(WorldModel.OBSTACLE, 37, 20);
        model.add(WorldModel.OBSTACLE, 37, 23);
        model.add(WorldModel.OBSTACLE, 37, 24);
        model.add(WorldModel.OBSTACLE, 37, 25);
        model.add(WorldModel.OBSTACLE, 33, 19);
        model.add(WorldModel.OBSTACLE, 33, 20);
        model.add(WorldModel.OBSTACLE, 33, 21);
        model.add(WorldModel.OBSTACLE, 33, 22);
        model.add(WorldModel.OBSTACLE, 33, 24);
        model.add(WorldModel.OBSTACLE, 33, 25);

        model.add(WorldModel.OBSTACLE, 18, 18);
        model.add(WorldModel.OBSTACLE, 18, 19);

        model.add(WorldModel.OBSTACLE, 18, 21);
        model.add(WorldModel.OBSTACLE, 18, 21);

        model.add(WorldModel.OBSTACLE, 18, 24);
        model.add(WorldModel.OBSTACLE, 19, 24);

        model.add(WorldModel.OBSTACLE, 21, 24);
        model.add(WorldModel.OBSTACLE, 22, 24);

        model.add(WorldModel.OBSTACLE, 24, 24);
        model.add(WorldModel.OBSTACLE, 24, 23);

        model.add(WorldModel.OBSTACLE, 24, 21);
        model.add(WorldModel.OBSTACLE, 24, 20);

        model.add(WorldModel.OBSTACLE, 24, 18);
        model.add(WorldModel.OBSTACLE, 23, 18);

        model.add(WorldModel.OBSTACLE, 21, 18);
        model.add(WorldModel.OBSTACLE, 20, 18);

        model.add(WorldModel.OBSTACLE, 3, 10);
        model.add(WorldModel.OBSTACLE, 3, 9);
        model.add(WorldModel.OBSTACLE, 3, 8);
        model.add(WorldModel.OBSTACLE, 3, 7);
        model.add(WorldModel.OBSTACLE, 3, 6);
        model.add(WorldModel.OBSTACLE, 3, 5);
        model.add(WorldModel.OBSTACLE, 3, 4);
        model.add(WorldModel.OBSTACLE, 3, 3);
        model.add(WorldModel.OBSTACLE, 4, 3);
        model.add(WorldModel.OBSTACLE, 5, 3);
        model.add(WorldModel.OBSTACLE, 6, 3);
        model.add(WorldModel.OBSTACLE, 7, 3);
        model.add(WorldModel.OBSTACLE, 8, 3);
        model.add(WorldModel.OBSTACLE, 9, 3);
        model.add(WorldModel.OBSTACLE, 10, 3);

        model.add(WorldModel.OBSTACLE, 3, 34);
        model.add(WorldModel.OBSTACLE, 3, 35);
        model.add(WorldModel.OBSTACLE, 3, 36);
        model.add(WorldModel.OBSTACLE, 3, 37);
        model.add(WorldModel.OBSTACLE, 3, 38);
        model.add(WorldModel.OBSTACLE, 3, 39);
        model.add(WorldModel.OBSTACLE, 3, 40);
        model.add(WorldModel.OBSTACLE, 3, 41);
        model.add(WorldModel.OBSTACLE, 4, 41);
        model.add(WorldModel.OBSTACLE, 5, 41);
        model.add(WorldModel.OBSTACLE, 6, 41);
        model.add(WorldModel.OBSTACLE, 7, 41);
        model.add(WorldModel.OBSTACLE, 8, 41);
        model.add(WorldModel.OBSTACLE, 9, 41);
        model.add(WorldModel.OBSTACLE, 10, 41);

        model.add(WorldModel.OBSTACLE, 41, 34);
        model.add(WorldModel.OBSTACLE, 41, 35);
        model.add(WorldModel.OBSTACLE, 41, 36);
        model.add(WorldModel.OBSTACLE, 41, 37);
        model.add(WorldModel.OBSTACLE, 41, 38);
        model.add(WorldModel.OBSTACLE, 41, 39);
        model.add(WorldModel.OBSTACLE, 41, 40);
        model.add(WorldModel.OBSTACLE, 41, 41);
        model.add(WorldModel.OBSTACLE, 40, 41);
        model.add(WorldModel.OBSTACLE, 39, 41);
        model.add(WorldModel.OBSTACLE, 38, 41);
        model.add(WorldModel.OBSTACLE, 37, 41);
        model.add(WorldModel.OBSTACLE, 36, 41);
        model.add(WorldModel.OBSTACLE, 35, 41);
        model.add(WorldModel.OBSTACLE, 34, 41);

        model.add(WorldModel.OBSTACLE, 41, 10);
        model.add(WorldModel.OBSTACLE, 41, 9);
        model.add(WorldModel.OBSTACLE, 41, 8);
        model.add(WorldModel.OBSTACLE, 41, 7);
        model.add(WorldModel.OBSTACLE, 41, 6);
        model.add(WorldModel.OBSTACLE, 41, 5);
        model.add(WorldModel.OBSTACLE, 41, 4);
        model.add(WorldModel.OBSTACLE, 41, 3);
        model.add(WorldModel.OBSTACLE, 40, 3);
        model.add(WorldModel.OBSTACLE, 39, 3);
        model.add(WorldModel.OBSTACLE, 38, 3);
        model.add(WorldModel.OBSTACLE, 37, 3);
        model.add(WorldModel.OBSTACLE, 36, 3);
        model.add(WorldModel.OBSTACLE, 35, 3);
        model.add(WorldModel.OBSTACLE, 34, 3);

        // labirinto
        model.add(WorldModel.OBSTACLE, 7, 37);
        model.add(WorldModel.OBSTACLE, 8, 37);
        model.add(WorldModel.OBSTACLE, 9, 37);
        model.add(WorldModel.OBSTACLE, 10, 37);
        model.add(WorldModel.OBSTACLE, 11, 37);
        model.add(WorldModel.OBSTACLE, 12, 37);
        model.add(WorldModel.OBSTACLE, 13, 37);
        model.add(WorldModel.OBSTACLE, 14, 37);
        model.add(WorldModel.OBSTACLE, 15, 37);
        model.add(WorldModel.OBSTACLE, 15, 36);
        model.add(WorldModel.OBSTACLE, 15, 35);
        model.add(WorldModel.OBSTACLE, 15, 34);
        model.add(WorldModel.OBSTACLE, 15, 33);
        model.add(WorldModel.OBSTACLE, 15, 32);
        model.add(WorldModel.OBSTACLE, 15, 31);
        model.add(WorldModel.OBSTACLE, 15, 30);
        model.add(WorldModel.OBSTACLE, 15, 29);
        model.add(WorldModel.OBSTACLE, 15, 28);
        model.add(WorldModel.OBSTACLE, 7, 28);
        model.add(WorldModel.OBSTACLE, 8, 28);
        model.add(WorldModel.OBSTACLE, 9, 28);
        model.add(WorldModel.OBSTACLE, 10, 28);
        model.add(WorldModel.OBSTACLE, 11, 28);
        model.add(WorldModel.OBSTACLE, 12, 28);
        model.add(WorldModel.OBSTACLE, 13, 28);
        model.add(WorldModel.OBSTACLE, 14, 28);
        model.add(WorldModel.OBSTACLE, 7, 34);
        model.add(WorldModel.OBSTACLE, 7, 33);
        model.add(WorldModel.OBSTACLE, 7, 32);
        model.add(WorldModel.OBSTACLE, 7, 31);
        model.add(WorldModel.OBSTACLE, 7, 30);
        model.add(WorldModel.OBSTACLE, 7, 29);
        model.add(WorldModel.OBSTACLE, 7, 28);
        model.add(WorldModel.OBSTACLE, 7, 34);
        model.add(WorldModel.OBSTACLE, 8, 34);
        model.add(WorldModel.OBSTACLE, 9, 34);
        model.add(WorldModel.OBSTACLE, 10, 34);
        model.add(WorldModel.OBSTACLE, 11, 34);
        model.add(WorldModel.OBSTACLE, 12, 34);
        model.add(WorldModel.OBSTACLE, 12, 33);
        model.add(WorldModel.OBSTACLE, 12, 32);
        model.add(WorldModel.OBSTACLE, 12, 31);
        model.add(WorldModel.OBSTACLE, 12, 30);
        model.add(WorldModel.OBSTACLE, 11, 30);
        model.add(WorldModel.OBSTACLE, 10, 30);
        model.add(WorldModel.OBSTACLE, 9, 30);
        model.add(WorldModel.OBSTACLE, 9, 31);
        model.add(WorldModel.OBSTACLE, 9, 32);
        model.setInitialNbGolds(model.countObjects(WorldModel.GOLD));
        return model;
    }

}
