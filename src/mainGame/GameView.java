package mainGame;

import java.util.ArrayList;
import java.util.Random;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sprites.Collectable;
import sprites.Drone;
import sprites.PileItem;
import sprites.garbageItem;
import utils.Action;
import utils.Observer;
import utils.ObserverPickup;
import utils.TrashList;
import utils.TrashTimer;

public class GameView implements Observer, ObserverPickup {
	
	Stage stage;
	public Model model;
	BorderPane borderPane;
	ShopPane shop;
	GameController gamePane;
	Random rand = new Random();
	boolean gameStarted = false;
	private Action leftKey;
	private Action rightKey;
	private Action collectKey;
	private ArrayList<Action> commandQueue;
	public TrashTimer timer;
	
		
	public GameView(Stage stage, Model model) {
			this.stage = stage;
			this.model = model;
			this.commandQueue = new ArrayList<Action>();
			this.timer = new TrashTimer();
			
	}
	
	
	//To do: Create a method to update the game view when a change in sprites occurs.
	//There's a few ways to do this but the preferred method so far is to use the
	//observable/observer pattern
	
	public void initStart() {
		borderPane = new BorderPane();
		
		showMenu();
		
		var scene = new Scene(borderPane, 1000, 700);
		
		scene.setOnKeyPressed(e -> {
			if (gameStarted && e.getCode() == leftKey.getKey()) {
				this.commandQueue.add(leftKey);
				leftKey.execute();
				this.commandQueue.remove(leftKey);
			}
			if (gameStarted && e.getCode() == rightKey.getKey()) {
				this.commandQueue.add(rightKey);
				rightKey.execute();
				this.commandQueue.remove(rightKey);
			}
			if (gameStarted && e.getCode() == collectKey.getKey()) {
				this.commandQueue.add(collectKey);
				collectKey.execute();
				this.commandQueue.remove(collectKey);
			}
		});
		shop = new ShopPane(this);
		stage.setScene(scene);
		stage.show();
	}
	
	private void showMenu() {
		StartMenu menu = new StartMenu(this);
		VBox box = new VBox(10, menu);
		box.setAlignment(Pos.CENTER);
		borderPane.setCenter(box);
	}
	
	protected void showGameScreen() {
		gamePane = new GameController(this, model);
		shop.exitButton.setOnMouseClicked(e -> {
			VBox box = new VBox(gamePane);
			box.setAlignment(Pos.CENTER);
			borderPane.setCenter(box);
		});
		gamePane.shopButton.setOnMouseClicked(e -> {
			VBox box = new VBox(shop);
			box.setAlignment(Pos.CENTER);
			borderPane.setCenter(box);
		});
		VBox box = new VBox(gamePane);
		box.setAlignment(Pos.CENTER);
		borderPane.setCenter(box);
		timer.setObservingView(this);
		this.timer.notifyObserver();
	}
	
	public void newDrone() {
		Drone newDrone = this.model.initializeDrone();
		this.gamePane.addElement(newDrone);
	}
	
	protected void setLeftKey(Action action) {
		this.leftKey = action;
	}
	
	protected void setRightKey(Action action) {
		this.rightKey = action;
	}
	
	protected void setCollectKey(Action action) {
		this.collectKey = action;
	}
	
	private void spawnTrash() {
		if (this.model.trashCount > 40) {
			return;
		}
		TrashList newElement = null;
		int garbageType = rand.nextInt(this.model.getReciprocal());
		if (garbageType == 0) {
			newElement = new TrashList(new PileItem());
		} else if (garbageType > 0) {
			newElement = new TrashList(new garbageItem());
		}
		this.model.registerTrash(newElement);
		this.gamePane.addElement(newElement.getItem());
	}
	
	public Collectable collectNearest() {
		Collectable nearest = this.model.checkProximity();
		if (nearest == null) {
			return null;
		}
		// To do: run a collect method on the "nearest" variable and
		// delete it from the screen, update the model accordingly.
		this.model.addPoints(nearest);
		this.gamePane.updateScoreLabel();
		this.model.movePlayer.setTarget(nearest);
		System.out.println(nearest.x);
		return nearest;
	}

	@Override
	public void update(double observableState) {
		// TODO add code that updates a new time attribute on the collectables
		Platform.runLater(new Runnable() {
			public void run() {
				if (model.trashHash.size() == 0) {
					model.fillHash();
				}
				for (int i = 0; i < model.occupiedBuckets.size(); i ++) {
					TrashList occupiedBucket = model.trashHash.get(model.occupiedBuckets.get(i));
					TrashList temp = occupiedBucket;
					while (temp != null) {
						temp.getItem().addMillisecond();
						temp = temp.next;
					}
				}
				for (int j = 0; j < model.autonomousPlayers.size(); j ++) {
					if (model.autonomousPlayers.get(j).getTarget() == null) {
						model.startDroneThread(model.autonomousPlayers.get(j));
					}
				}
				if (observableState == 0.0) {
					timer.stopTimer();
					timer.resetTimer();
					
					int spawnQuantity = rand.nextInt(model.getReciprocal() * 3);
					if (spawnQuantity == 0) {
						spawnTrash();
						spawnTrash();
						spawnTrash();
					} else if (spawnQuantity == 1 || spawnQuantity == 2) {
						spawnTrash();
						spawnTrash();
					} else {
						spawnTrash();
					}

					timer.notifyObserver();
				}
			}
		});
	}


	@Override
	public void update(Collectable observableState) {
		// TODO Auto-generated method stub
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		        // Update UI here.
		    	if (!(observableState.getCollectedMethod())) {
		    		model.removeFromHash(observableState);
		    	}
		    	gamePane.removeElement(observableState);
		    	gamePane.updateScoreLabel();
				model.movePlayer.setTarget(null);
		    }
		});
	}
	
}
