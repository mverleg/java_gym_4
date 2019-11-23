package javagym;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import noedit.Maze;
import noedit.Position;

/**
 * Tracks cells that are already visited or are in the {@link Queue} (which counts as visited).
 */
public class VisitGrid {

	private final int duration;
	private final int width;
	private final int height;
	private final boolean[][][] isVisited;

	public VisitGrid(@Nonnull Maze maze) {
		duration = maze.duration;
		width = maze.width;
		height = maze.height;
		isVisited = new boolean[duration][width][height];
	}

	public void mark(@Nonnull Position position) {
		//Validate.isTrue(!visited(position));
		isVisited[position.t][position.x][position.y] = true;
	}

	public boolean visited(@Nonnull Position position) {
		return isVisited[position.t][position.x][position.y];
	}

	@Nonnull
	@CheckReturnValue
	public String asText() {
		StringBuilder text = new StringBuilder();
		for (int t = 0; t < duration; t++) {
			text.append("Step ").append(t + 1).append(" of ").append(duration).append(" (visited grid)\n");
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (isVisited[t][x][y]) {
						text.append("#");
					} else {
						text.append(".");
					}
				}
				text.append("\n");
			}
		}
		return text.toString();
	}
}
