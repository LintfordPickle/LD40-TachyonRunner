package net.lintfords.tachyon.data;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.data.BaseData;

public class Track extends BaseData {

	private static final long serialVersionUID = -1983487698243868880L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float trackLength;
	public float trackSegments;
	public float segmentWidth;
	public float segmentLength;

	private List<Vector2f> controlPoints;
	private List<Vector2f> trackPoints;

	private List<StartGridElement> startGridElements;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Vector2f startPosition() {
		return controlPoints.get(0);
	}

	public List<Vector2f> controlPoints() {
		return controlPoints;
	}

	public List<Vector2f> trackPoints() {
		return trackPoints;
	}

	public int getNumTrackPoints() {
		return trackPoints.size();
	}

//	public Vector2f getControlPoint(int pControlPointID) {
//		if (pControlPointID < 0 || pControlPointID >= controlPoints.size())
//			return null;
//
//		return controlPoints.get(pControlPointID);
//	}

	public Vector2f getTrackPoint(int pTrackPointID) {
		if (pTrackPointID < 0 || pTrackPointID >= trackPoints.size())
			return null;

		return trackPoints.get(pTrackPointID);
	}

	public List<StartGridElement> startElements() {
		return startGridElements;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Track() {
		startGridElements = new ArrayList<>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void fillTrackPoints(List<Vector2f> pControlPoints, List<Vector2f> pPointList) {
		if (pPointList == null || pPointList.size() == 0)
			return;

		float lDist = 0;
		// pre-work
		for (int i = 0; i < pPointList.size(); i++) {
			// calc track length
			if (i + 1 < pPointList.size()) {
				lDist = Vector2f.len(pPointList.get(i).x, pPointList.get(i).y, pPointList.get(i + 1).x, pPointList.get(i + 1).y);

			}

		}

		trackLength = lDist;
		System.out.println("Track length: " + lDist);

		controlPoints = pControlPoints;
		trackPoints = pPointList;

	}

}
