// CArtAgO artifact code for project versionAE
package tools;

import cartago.*;
import jason.asSyntax.* ;
import java.lang.Object;
import java.util.*;

public class WorldMap extends Artifact {

	int map_rows = 45;
	int map_cols = 45;

	String team_color;

	HashMap<Integer, String> world_map = new HashMap<Integer, String>();

	void init(String team) {

		this.team_color = team;

		for (int i = 0; i < map_rows*map_cols; i++){
			world_map.put(i, "?");
			}
		
		defineObsProperty("team_color", team_color);
		defineObsProperty("world_map", world_map);
	}

	private static Set<Integer> getKeys(HashMap<Integer, String> map, String value) {

		Set<Integer> result = new HashSet<>();
		if (map.containsValue(value)) {
			for (Map.Entry<Integer, String> entry : map.entrySet()) {
				if (Objects.equals(entry.getValue(), value)) {
					result.add(entry.getKey());
				}
			}
		}
		return result;
	}

	@OPERATION
	void setFreeCell(int X, int Y) {
		int my_key = map_cols*X+Y;
		try{
			world_map.replace(my_key, "F");
		}
		catch (NullPointerException npe){}
	}

	@OPERATION
	void setObstacleCell(int X, int Y) {
		int my_key = map_cols*X+Y;
		world_map.replace(my_key, "O");
	}

	@OPERATION
	void setGoldCell(int X, int Y) {
		int my_key = map_cols*X+Y;
		world_map.replace(my_key, "G");
	}

	@OPERATION
	void setAgentGoldCell(int X, int Y) {
		int my_key = map_cols*X+Y;
		world_map.replace(my_key, "A");
	}

	@OPERATION
	void askCellValue(int X, int Y, OpFeedbackParam<String> V) {
		int my_key = map_cols*X+Y;
		String my_value = world_map.get(my_key);
		V.set(my_value);
	}
	

	@OPERATION
	void askCloserGoldCell(int X, int Y, OpFeedbackParam<Integer> XG, OpFeedbackParam<Integer> YG) {
		int min_dist = 100;
		int min_x_gold = 100;
		int min_y_gold = 100;
		int x_gold;
		int y_gold;
		for (Integer key : getKeys(world_map, "G")) {
			if (key < 45){
				x_gold = 0;
				y_gold = key;
			} else {
				y_gold = key % 45;
				x_gold = (key - y_gold)/45;
			}
			int dist = (X - x_gold) + (Y - y_gold);
			if (dist < min_dist){
				min_dist = dist;
				min_x_gold = x_gold;
				min_y_gold = y_gold;
			}
		}
		XG.set(min_x_gold);
		YG.set(min_y_gold);
	}

	@OPERATION
	void askUnknownCell(OpFeedbackParam<Integer> X, OpFeedbackParam<Integer> Y) {
		List<Integer> list_unknown = new ArrayList<>();
		for (Integer key : getKeys(world_map, "?")) {
			list_unknown.add(key);
		}
		Random rand = new Random();
        int cell = list_unknown.get(rand.nextInt(list_unknown.size()));
		int x;
		int y;
		if (cell < 45){
			x = 0;
			y = cell;
		} else {
			y = cell % 45;
			x = (cell - y)/45;
		}
		X.set(x);
		Y.set(y);
	}
/*
	@OPERATION
	void checkParticipants() {
		ObsProperty g = getObsProperty("name");
		ObsProperty b = getObsProperty("bidders");
		if (b.intValue() == 0){
			notSold();
			System.out.printf(" There is no bidder left, %s was not sold\n", g);
		}
		else if (b.intValue() == 1){
			sold();
			System.out.printf(" There is only one bidder left, selling %s\n", g);
		}
		else{
			raisePrice();
			System.out.printf(" There are %s bidders, raising price\n", b);

		}
	}	

	@OPERATION
	void addBidder() {
		ObsProperty b = getObsProperty("bidders");
		b.updateValue(b.intValue()+1);
		System.out.printf(" Adding one bidder. Total: %s\n", b);
	}

	@OPERATION
	void removeBidder() {
		ObsProperty b = getObsProperty("bidders");
		b.updateValue(b.intValue()-1);
		System.out.printf(" Removing one bidder. Total: %s\n", b);
	}

	@OPERATION
	void raisePrice() {
		ObsProperty g = getObsProperty("name");
		ObsProperty p = getObsProperty("price");
		ObsProperty b = getObsProperty("bidders");
		p.updateValue(p.intValue()+100);
		System.out.printf(" %s's value is now %s with %s bidder(s)!\n", g, p, b);
		signal("raisedPrice");
	}

	@OPERATION
	void sold() {
		signal("sold");
	}

	@OPERATION
	void sold2(String B) {
		ObsProperty w = getObsProperty("winner");
		w.updateValue(B);
	}

	@OPERATION
	void notSold() {
		ObsProperty w = getObsProperty("winner");
		w.updateValue("Not sold");
		signal("notSold");
	} */
}

