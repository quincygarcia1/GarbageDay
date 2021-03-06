package mainGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sprites.Collectable;
import sprites.Drone;
import sprites.GarbagePlayer;
import sprites.cleanUpPlayers;
import utils.TrashList;

public class Model {

	int hashSize = 40;
	int score = 0;
	int trashReciprocal = 6;
	int multiplier = 1;
	protected ArrayList<cleanUpPlayers> autonomousPlayers = new ArrayList<cleanUpPlayers>();
	protected ArrayList<TrashList> trashHash = new ArrayList<TrashList>(hashSize);
	protected ArrayList<Integer> occupiedBuckets = new ArrayList<Integer>(hashSize);
	protected int trashCount = 0;
	private Collectable oldestCollectable = null;
	private Collectable newestCollectable = null;
	private Collectable closestCollectable = null;
	private Collectable furthestCollectable = null;
	Random rand;
	
	public GarbagePlayer movePlayer = new GarbagePlayer();
	
	public void fillHash() {
		for (int i = 0; i < hashSize; i++) {
			trashHash.add(null);
		}
	}
	
	private Collectable closestInList(TrashList list, cleanUpPlayers player) {
		Collectable max = null;
		TrashList temp = list;
		while (temp != null) {
			if (max == null || (Math.abs(temp.getItem().x - player.x) < Math.abs(player.x - max.x)) && !(temp.getItem().isAssigned())) {
				max = temp.getItem();
			}
			temp = temp.next;
		}
		return max;
	}
	
	protected void findDistRange() {
		for (int i = 0; i < occupiedBuckets.size(); i ++) {
			TrashList currentBucket = trashHash.get(occupiedBuckets.get(i));
			TrashList temp = currentBucket;
			while (temp != null) {
				if (furthestCollectable == null) {
					furthestCollectable = temp.getItem();
				} else if (((Math.abs(temp.getItem().x - movePlayer.x) > Math.abs(furthestCollectable.x - movePlayer.x))) && !(temp.getItem().isAssigned())) {
					furthestCollectable = temp.getItem();
				}
				temp = temp.next;
			}
		}
		closestCollectable = closestInHash(this.movePlayer);
	}
	
	private Collectable closestInHash(cleanUpPlayers player) {
		Collectable max = null;
		for (int i = 0; i < occupiedBuckets.size(); i ++) {
			Collectable maxInBucket = closestInList(trashHash.get(occupiedBuckets.get(i)), player);
			if ((max == null || (Math.abs(maxInBucket.x - player.x) < Math.abs(max.x - player.x))) && !(maxInBucket.isAssigned())) {
				max = maxInBucket;
			}
		}
		return max;
	}
	
	protected void startDroneThread(cleanUpPlayers drone) {
		Collectable newTarget = closestInHash(drone);
		drone.setTarget(newTarget);
		newTarget.setAssigned();
		
	}

	public Drone initializeDrone() {
		Drone newDrone = new Drone();
		if (this.autonomousPlayers.size() == 4) {
			return null;
		}
		int index = this.autonomousPlayers.size();
		this.autonomousPlayers.set(index, newDrone);
		return (Drone)autonomousPlayers.get(index);
	}

	public void startThread() {
		Thread thread = new Thread(movePlayer);
		thread.start();
	}
	
	public Thread startGarbageThread(Collectable target) {
		if (target == null) {
			return null;
		}
		Thread thread = new Thread(target);
		thread.start();
		
		return thread;
	}
	
	public ArrayList<Integer> getBuckets() {
		return occupiedBuckets;
	}
	
	protected void registerTrash(TrashList newElement) {
		
		int hashVal = hash(newElement.getItem().getXCenter()) % hashSize;
		newElement.next = trashHash.get(hashVal);
		System.out.println(hashVal);
		trashHash.set(hashVal, newElement);
		if (!(occupiedBuckets.contains(hashVal))) {
			occupiedBuckets.add(hashVal);
		}
		trashCount ++;
		if (oldestCollectable == null && newestCollectable == null) {
			targetAges();
		} else if (closestCollectable == null && furthestCollectable == null) {
			findDistRange();
		} else if (Math.abs(newElement.getItem().x - movePlayer.x) < Math.abs(closestCollectable.x - movePlayer.x)) {
			closestCollectable = newElement.getItem();
		} else if (Math.abs(newElement.getItem().x - movePlayer.x) > Math.abs(furthestCollectable.x - movePlayer.x)) {
			furthestCollectable = newElement.getItem();
		}
	}
	
	private void targetAges() {
		//To do: revise this method to work with the hashtable
		Collectable max = null;
		Collectable min = null;
		for (int i = 0; i < this.occupiedBuckets.size(); i ++) {
			TrashList linkedList = trashHash.get(occupiedBuckets.get(i));
			TrashList temp = linkedList;
			while (temp != null) {
				if (max == null) {
					max = temp.getItem();
				}
				if (min == null) {
					min = temp.getItem();
				}
				if (temp.getItem().getTime() < min.getTime()) {
					min = temp.getItem();
				} else if (temp.getItem().getTime() > max.getTime()) {
					max = temp.getItem();
				}
				temp = temp.next;
			}
		}
		oldestCollectable = max;
		newestCollectable = min;
	}
	
	protected Collectable checkProximity() {
		double divisor = this.movePlayer.x/(double)this.movePlayer.getDestructionRange();
		
		if ((divisor - 0.5) == (int)divisor) {
			Collectable bottomBound = hashDivisor((int)((divisor - 0.5) * this.movePlayer.getDestructionRange()) % hashSize);
			Collectable upperBound = hashDivisor((int)((divisor + 0.5) * this.movePlayer.getDestructionRange()) % hashSize);
			if (bottomBound == null && upperBound == null) {
				return null;
			}
			if (bottomBound == null && upperBound != null) {
				if ((oldestCollectable != null && upperBound.getXCenter() == oldestCollectable.getXCenter()) ||
						(newestCollectable != null && upperBound.getXCenter() == newestCollectable.getXCenter())) {
					targetAges();
				}
				return upperBound;
			} else if (bottomBound != null && upperBound == null) {
				if ((oldestCollectable != null && bottomBound.getXCenter() == oldestCollectable.getXCenter()) ||
						(newestCollectable != null && bottomBound.getXCenter() == newestCollectable.getXCenter())) {
					targetAges();
				}
				return bottomBound;
			}
			if (Math.abs(this.movePlayer.x - bottomBound.x) > Math.abs(this.movePlayer.x - upperBound.x)) {
				System.out.println(bottomBound.x);
				TrashList reinsertedElement = new TrashList(upperBound);
				registerTrash(reinsertedElement);
				if ((oldestCollectable != null && bottomBound.getXCenter() == oldestCollectable.getXCenter()) ||
						(newestCollectable != null && bottomBound.getXCenter() == newestCollectable.getXCenter())) {
					targetAges();
				}
				return bottomBound;
			}
			System.out.println(upperBound.x);
			TrashList reinsertedElement = new TrashList(bottomBound);
			registerTrash(reinsertedElement);
			if ((oldestCollectable != null && upperBound.getXCenter() == oldestCollectable.getXCenter()) ||
					(newestCollectable != null && upperBound.getXCenter() == newestCollectable.getXCenter())) {
				targetAges();
			}
			return upperBound;
		} else {
			Collectable res = hashDivisor((int)(Math.round(divisor) * this.movePlayer.getDestructionRange()) % hashSize);
			if (res == null) {
				return null;
			}
			if ((oldestCollectable != null && res.getXCenter() == oldestCollectable.getXCenter()) ||
					(newestCollectable != null && res.getXCenter() == newestCollectable.getXCenter())) {
				targetAges();
			}
			return res;
		}
	}
	
	
	
	private Collectable hashDivisor(int divisor) {
		System.out.println(divisor);
		TrashList hash = trashHash.get(divisor);
		TrashList temp = hash;
		if ((temp != null) && !(temp.getItem().isTaken()) && temp.getItem().getXCenter() <= (this.movePlayer.x + this.movePlayer.getDestructionRange()) && temp.getItem().x >= (this.movePlayer.x - this.movePlayer.getDestructionRange())) {
			TrashList returnValue = temp;
			trashHash.set(divisor, temp.next);
			return returnValue.getItem();
		}
		while (temp != null && temp.next != null) {
			if (!(temp.getItem().isTaken()) &&temp.next.getItem().getXCenter() <= (this.movePlayer.x + this.movePlayer.getDestructionRange()) && temp.next.getItem().x >= (this.movePlayer.x - this.movePlayer.getDestructionRange())) {
				Collectable target = temp.next.getItem();
				temp.next = temp.next.next;
				return target;
			}
			temp = temp.next;
		}
		return null;
	}
	
	protected void removeFromHash(Collectable item) {
		int hashVal = hash(item.getXCenter()) % hashSize;
		TrashList bucket = this.trashHash.get(hashVal);
		TrashList temp = bucket;
		if (temp != null && temp.getItem().getXCenter() == item.getXCenter()) {
			trashHash.set(hashVal, temp.next);
			addPoints(temp.getItem());
			return;
		} else {
			while (temp != null && temp.next != null) {
				if (temp.next.getItem().getXCenter() == item.getXCenter()) {
					addPoints(item);
					temp.next = temp.next.next;
					return;
				}
				temp = temp.next;
			}
		}
		
		if (item.x == closestCollectable.x) {
			closestCollectable = null;
			findDistRange();
		} else if (item.x == furthestCollectable.x) {
			furthestCollectable = null;
			findDistRange();
		}
	}
	
	protected void addPoints(Collectable item) {
		if (!(item.getCollectedMethod())) {
			this.score += multiplier;
			return;
		}
		double multiplicationFactor = 1 + additionalPoints(item);
		this.score += multiplier * multiplicationFactor;
		System.out.println("adding score " + multiplier * multiplicationFactor);
		
	}
	
	private double additionalPoints(Collectable item) {
		double numerator = getReciprocalRepresentation(item);
		double denominator = Math.abs(getReciprocalRepresentation(closestCollectable) - getReciprocalRepresentation(furthestCollectable));
		if (denominator == 0) {
			return 2;
		}
		return (double)(numerator / denominator);
	}
	
	private double getReciprocalRepresentation(Collectable item) {
		return ((double)(10/3) * (double)(1/item.getTime())) *
				((double)(50/3) * (double)(1/Math.abs(item.x - movePlayer.x)));
	}
			
	private int hash(double val) {
		return (int)(Math.round(val/movePlayer.getDestructionRange()) * movePlayer.getDestructionRange());
	}
	
	public void increaseScore(int increment) {
		score += increment;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public void decreaseReciprocal() {
		this.trashReciprocal --;
	}
	
	public int getReciprocal() {
		return this.trashReciprocal;
	}
	
	public void setMultiplier(int newMultiplier) {
		this.multiplier = newMultiplier;
	}
	
}
