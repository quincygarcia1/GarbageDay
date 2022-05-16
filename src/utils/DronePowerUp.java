package utils;

import java.util.ArrayList;
import java.util.List;

public class DronePowerUp extends PowerUp{
	
	private static DronePowerUp instance;
	private int usages = 0;
	private List<Integer> costList = List.of(100, 700, 4000, 14000); 

	private DronePowerUp() {
		super(100, "", 100, 30);
		// TODO Auto-generated constructor stub
		setTitle();
	}
	
	public static DronePowerUp getInstance() {
		if (instance == null) {
			synchronized(DronePowerUp.class) {
				if (instance == null) {
					instance = new DronePowerUp();
				}
			}
		}
		return instance;
	}
	
	private void setTitle() {
		this.setTitle("" + this.costList.get(usages));
	}
	
	public void changeCost() {
		if (usages != 3) {
			this.setCost(this.costList.get(usages + 1));
		}
		this.usages ++;
		if (usages == 4) {
			this.setDisable(true);
		}
		setTitle();
	}


}
