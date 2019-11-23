package javagym;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import noedit.Maze;
import noedit.Position;

import static javagym.Parameters.TIME_WEIGHT;

public class Util {

	/**
	 * Manhattan distance, but can't go back in time, and time is weighted more heavily.
	 */
	public static int dist(@Nonnull Position fromPoint, @Nonnull Position targetPoint) {
		if (targetPoint.t >= fromPoint.t) {
			return Math.abs(targetPoint.x - fromPoint.x) +
					Math.abs(targetPoint.y - fromPoint.y) +
					TIME_WEIGHT * (targetPoint.t - fromPoint.t);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public static int smallestDist(@Nonnull Position fromPoint, @Nonnull Position[] targetPoints) {
		assert targetPoints.length > 0;
		int smallest = Integer.MAX_VALUE;
		for (Position target : targetPoints) {
			int dist = dist(fromPoint, target);
			if (dist < smallest) {
				smallest = dist;
			}
		}
		return smallest;
	}


	@Nonnull
	@CheckReturnValue
	public static String costGridAsText(@Nonnull Maze maze, @Nonnull Position[] targetPoints) {
		StringBuilder text = new StringBuilder();
		for (int t = 0; t < maze.duration; t++) {
			text.append("Step ").append(t + 1).append(" of ").append(maze.duration).append(" (cost grid)\n");
			for (int y = 0; y < maze.height; y++) {
				for (int x = 0; x < maze.width; x++) {
					int dist = smallestDist(Position.at(t, x, y), targetPoints);
					if (dist < 1000) {
						text.append(String.format("%3d ", dist));
					} else {
						text.append("$$$ ");
					}
				}
				text.append("\n");
			}
		}
		return text.toString();
	}
}
