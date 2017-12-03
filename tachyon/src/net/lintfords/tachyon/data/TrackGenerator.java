package net.lintfords.tachyon.data;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Vector2f;
import net.lintfords.tachyon.GrahamScan;

public class TrackGenerator {

	public static Track generateTrack(long pSeed, float pSegmentLength, float pSegmentWidth) {
		Track lTrack = new Track();
		List<Vector2f> finalTrackPoints = new ArrayList<>();

		float maxWidth = 10000;
		float maxHeight = 10000;

		// Start by placing random Vector2fs within an area
		int Vector2fCount = RandomNumbers.random(10, 15);
		int[] trackVector2fsX = new int[Vector2fCount];
		int[] trackVector2fsy = new int[Vector2fCount];

		// Random fill the others
		for (int i = 0; i < Vector2fCount; i++) {
			trackVector2fsX[i] = (int) (RandomNumbers.random(0f, maxWidth));
			trackVector2fsy[i] = (int) (RandomNumbers.random(0f, maxHeight));

		}

		List<Vector2f> convexHull = GrahamScan.getConvexHull(trackVector2fsX, trackVector2fsy);

		if (convexHull.get(0) == convexHull.get(convexHull.size() - 1)) {
			convexHull.remove(convexHull.size() - 1);
		}

		for (int i = 0; i < 6; i++) {
			pushApart(convexHull, 600);

		}

		// iterate over the points of the track and interpolate more points every 64px
		final int origTrackPointSize = convexHull.size();

		for (int i = 0; i < origTrackPointSize; i++) {
			int v0 = i - 1;
			if (v0 < 0)
				v0 = origTrackPointSize - 2;
			int v1 = ((i) % (origTrackPointSize));
			int v2 = ((i + 1) % (origTrackPointSize));
			int v3 = ((i + 2) % (origTrackPointSize));

			System.out.println("interpolating track: " + v1 + " - " + v2);
			List<Vector2f> newPoints = interpolateTrack(convexHull.get(v0), convexHull.get(v1), convexHull.get(v2), convexHull.get(v3), pSegmentLength);
			System.out.println("  new points: " + newPoints.size());
			finalTrackPoints.addAll(newPoints);

		}

		// Fill track
		lTrack.fillTrackPoints(convexHull, finalTrackPoints);
		lTrack.segmentLength = pSegmentLength;
		lTrack.segmentWidth = pSegmentWidth;

		return lTrack;

	}

	private static List<Vector2f> interpolateTrack(Vector2f pV0, Vector2f pV1, Vector2f pV2, Vector2f pV3, float pSegmentLength) {

		List<Vector2f> lResult = new ArrayList<>();

		final int lDetail = 18;
		for (int i = 0; i < lDetail; i++) {
			Vector2f lNewPoint = catmulrom2D(pV0, pV1, pV2, pV3, (float) i / (float) lDetail);
			lResult.add(lNewPoint);
		}

		return lResult;
	}

	private static Vector2f catmulrom2D(Vector2f pV0, Vector2f pV1, Vector2f pV2, Vector2f pV3, float pAmt) {
		Vector2f lResult = new Vector2f();

		lResult.x = MathHelper.catmullRom(pV0.x, pV1.x, pV2.x, pV3.x, pAmt);
		lResult.y = MathHelper.catmullRom(pV0.y, pV1.y, pV2.y, pV3.y, pAmt);

		return lResult;

	}

	private static void pushApart(List<Vector2f> pDataSet, float pDist) {
		float dst2 = pDist * pDist;
		for (int i = 0; i < pDataSet.size(); i++) {
			for (int j = i + 1; j < pDataSet.size(); j++) {
				if (pDataSet.get(i).dst2(pDataSet.get(j)) < dst2) {
					float hx = pDataSet.get(j).x - pDataSet.get(i).x;
					float hy = pDataSet.get(j).y - pDataSet.get(i).y;
					float hl = (float) Math.sqrt(hx * hx + hy * hy);
					hx /= hl;
					hy /= hl;
					float dif = pDist - hl;
					hx *= dif;
					hy *= dif;
					pDataSet.get(i).x -= hx;
					pDataSet.get(i).y -= hy;
					pDataSet.get(j).x += hx;
					pDataSet.get(j).y += hy;

				}

			}

		}

	}

}
