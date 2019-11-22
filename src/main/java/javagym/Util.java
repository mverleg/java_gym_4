package javagym;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import noedit.Position;

public class Util {

	/**
	 * Manhattan distance, but can't go back in time, and time is weighted more heavily.
	 */
	public static int dist(@Nonnull Position fromPoint, @Nonnull Position targetPoint) {
		if (targetPoint.t > fromPoint.t) {
			return Math.abs(targetPoint.x - fromPoint.x) +
					Math.abs(targetPoint.y - fromPoint.y) +
					80 * (targetPoint.t - fromPoint.t);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public static int smallestDist(@Nonnull Position fromPoint, @Nonnull Position[] targetPoints) {
		Validate.isTrue(targetPoints.length > 0);
		int smallest = Integer.MAX_VALUE;
		for (Position target : targetPoints) {
			int dist = dist(fromPoint, target);
			if (dist < smallest) {
				smallest = dist;
			}
		}
		return smallest;
	}
}
