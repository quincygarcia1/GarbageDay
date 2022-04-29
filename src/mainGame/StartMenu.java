package mainGame;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class StartMenu extends GridPane{
	
	private final GameView view;
	
	public StartMenu(GameView view) {
		this.view = view;
		this.setAlignment(Pos.CENTER);
		Text title = new Text("Community Service");
		title.setTextAlignment(TextAlignment.CENTER);
		Font titleFont = Font.loadFont("file:ZenKurenaido-Regular.ttf", 40);
		title.setFont(titleFont);
		HBox box = new HBox(title);
		box.setAlignment(Pos.CENTER);
		this.setVgap(10);
		this.add(box, 0, this.getRowCount());
		createButtons();
	}
	
	private void createButtons() {
		GameButton startButton = new GameButton("Start Game", 400, 100, 20);
		GameButton settingsButton = new GameButton("Settings", 400, 100, 20);
		GameButton quitButton = new GameButton("Quit Game", 400, 100, 20);
		
		quitButton.setOnAction(e -> System.exit(1));
		
		this.add(startButton, 0, this.getRowCount());
		this.add(settingsButton, 0, this.getRowCount());
		this.add(quitButton, 0, this.getRowCount());
	}
	

}