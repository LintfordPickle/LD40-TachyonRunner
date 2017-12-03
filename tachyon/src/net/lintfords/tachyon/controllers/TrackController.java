package net.lintfords.tachyon.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.maths.Vector2f;
import net.lintfords.tachyon.data.Track;

public class TrackController extends BaseController {

	public static final String CONTROLLER_NAME = "TrackController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Track mTrack;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialised() {
		return mTrack != null;
	}

	public Track track() {
		return mTrack;
	}

	public int getClosestTrackPointID(float pX, float pY) {
		final int lNumPoints = mTrack.trackPoints().size();
		int foundID = 0;

		/*
		 * 
		 * float x_d = v.x - x; float y_d = v.y - y; return x_d * x_d + y_d * y_d;
		 * 
		 */

		double dist = Float.MAX_VALUE;
		for (int i = 0; i < lNumPoints; i++) {
			Vector2f iv = mTrack.trackPoints().get(i);
			float x_d = iv.x - pX;
			float y_d = iv.y - pY;
			double d = x_d * x_d + y_d * y_d;
			
			if( d < dist) {
				dist = d;
				foundID = i;
			}

		}
		
		return foundID;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TrackController(ControllerManager pControllerManager, int pGroupID, Track pTrack) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mTrack = pTrack;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
