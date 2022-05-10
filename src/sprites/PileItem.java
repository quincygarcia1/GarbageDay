package sprites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javafx.scene.image.Image;

public class PileItem extends Collectable{
	
	static Random rand = new Random();

	public PileItem() {
		super(new ArrayList<Image>(Arrays.asList(new Image("file:Images/BigGarbage-Undamaged.png"),
				new Image("file:Images/BigGarbage-Destroyed.png"))), rand.nextInt(5) + 3, 380);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	

}
