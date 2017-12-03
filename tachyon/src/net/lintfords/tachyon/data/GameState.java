package net.lintfords.tachyon.data;

import net.lintford.library.data.BaseData;

public class GameState extends BaseData {

	private static final long serialVersionUID = -648643481662877958L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public int playerScore;
	public int playerHealth;
	public int playerHealthMax;
	
	public int totalRacers;
	public int playerPosition;
	
	public int totalLaps;
	public int currentTrackPoint;
	public int currentLap;
	
	public int totalTime;
	public int lapTime;
	
	public boolean playerAlive;
	
	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameState() {
		playerAlive = true;
		
	}
	
	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

}
