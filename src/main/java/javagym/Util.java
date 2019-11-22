package javagym;

import javax.annotation.Nonnull;

import noedit.Position;

public class Util {

	/**
	 * Manhattan distance, but can't go back in time, and time is weighted more heavily.
	 */
	public int dist(@Nonnull Position fromPoint, @Nonnull Position targetPoint) {
		if (targetPoint.t > fromPoint.t) {
			return Math.abs(targetPoint.x - fromPoint.x) +
					Math.abs(targetPoint.y - fromPoint.y) +
					50 * (targetPoint.t - fromPoint.t);
		} else {
			return Integer.MAX_VALUE;
		}
	}
}
