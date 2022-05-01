package mainGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Drone extends cleanUpPlayers {

	
	static Random rand = new Random();
	private Collectable target;
	
	public Drone() {
		
		super(new ArrayList<Image>(Arrays.asList(new Image("file:Images/drone_r2.png"), new Image("file:Images/drone_r3.png"),
				new Image("file:Images/drone_r1.png"), new Image("file:Images/drone_l2.png"),
				new Image("file:Images/drone_l3.png"), new Image("file:Images/drone_l1.png"))), rand.nextInt(1000), 200.00, 25);
		this.spriteImages = new ArrayList<Image>(Arrays.asList(new Image("file:Images/drone_r2.png"), new Image("file:Images/drone_r3.png"),
				new Image("file:Images/drone_r1.png"), new Image("file:Images/drone_l2.png"),
				new Image("file:Images/drone_l3.png"), new Image("file:Images/drone_l1.png")));
		this.target = null;
	}
	
	public void moveTo(double location) throws InterruptedException {
		if (location < this.x) {
			this.currentAnimationNum = 3;
			changeAnimation();
		}
		while (Math.abs(this.x - location) > this.getDestructionRange()) {
			if (location < this.x) {
				this.x -= 5;
				if (currentAnimationNum == 5) {
					currentAnimationNum = 3;
				} else {
					currentAnimationNum ++;
				}
			} else {
				this.x += 5;
				if (currentAnimationNum == 2) {
					currentAnimationNum = 0;
				} else {
					currentAnimationNum ++;
				}
			}
			changeAnimation();
			Thread.sleep(10);
		}
		currentAnimationNum = 0;
		changeAnimation();
	}
	
	private void changeAnimation() {
		this.setImage(this.spriteImages.get(currentAnimationNum));
	}
	
	public void setTarget(Collectable target) {
		this.target = target;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (this.target == null) {
			return;
		}
		try {
			moveTo(this.target.x);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			return;
		}
		
	}
}
