package mainGame;

import java.util.ArrayList;
import java.util.Hashtable;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import sprites.Collectable;
import sprites.Sprite;

public class GameController extends Pane {

	private final GameView view;
	private final Model model;
	
	
	public GameController(GameView view, Model model) {
		this.view = view;
		this.model = model;
		this.setPrefSize(1200, 700);
		this.setMinSize(1200, 700);
		this.setMaxSize(1200, 700);
		createScreen();
	}
	
	private void createScreen() {
		this.getChildren().add(this.model.movePlayer);
	}
	
	protected void removeElement(Sprite sprite) {
		this.getChildren().remove(sprite);
	}
	
	protected void addElement(Sprite sprite) {
		this.getChildren().add(sprite);
		this.getChildren().get(this.getChildren().size() - 1).toBack();
	}
	
	public void update() {
		
	}
}
