package noedit;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

import static noedit.Cell.Wall;

/**
 * A time-dependent maze.
 */
public final class Maze implements Iterable<Pair<Position, Cell>> {

	@Positive public final int duration;
	@Positive public final int width;
	@Positive public final int height;
	@Nonnull private final Cell[][][] data;
	@Nullable private Pair<Position, Cell>[] flatCache = null;

	public Maze(@Positive int duration, @Positive int width, @Positive int height) {
		this.duration = duration;
		this.width = width;
		this.height = height;
		this.data = new Cell[duration][width][height];
		for (int t = 0; t < duration; t++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					data[t][x][y] = Wall;
				}
			}
		}
	}

	public Maze(@Nonnull Cell[][][] data) {
		Validate.isTrue(data.length > 0, "first dimension of maze is empty");
		Validate.isTrue(data[0].length > 0, "second dimension of maze is empty");
		Validate.isTrue(data[0][0].length > 0, "third dimension of maze is empty");

		this.duration = data.length;
		this.width = data[0].length;
		this.height = data[0][0].length;
		this.data = data;

		// Check that the maze is square.
		for (Cell[][] snapshot : data) {
			Validate.isTrue(data[0].length == snapshot.length,
					"Second dimensions of maze matrix aren't equal length (" + data[0].length + " vs " + snapshot.length + ")");
			for (Cell[] row : snapshot) {
				Validate.isTrue(data[0][0].length == row.length,
						"Second dimensions of maze matrix aren't equal length (" + data[0][0].length + " vs " + row.length + ")");
				for (Cell cell : row) {
					Validate.notNull(cell);
				}
			}
		}
	}

	@Nonnull
	@CheckReturnValue
	public Cell get(
			@NonNegative int t,
			@NonNegative int x,
			@NonNegative int y
	) {
		Validate.isTrue(t < duration, "'t' cannot exceed duration");
		Validate.isTrue(x < width, "'x' cannot exceed width");
		Validate.isTrue(y < height, "'y' cannot exceed height");

		return data[t][x][y];
	}

	@Nonnull
	@CheckReturnValue
	public Cell get(@Nonnull Position pos) {
		return get(pos.t, pos.x, pos.y);
	}

	/**
	 * Get the cell at a position, or a default value if the position is outside the board.
	 */
	@Nonnull
	@CheckReturnValue
	public Cell getOrElse(
			int t,
			int x,
			int y,
			@Nonnull Cell fallback
	) {
		if (t < 0 || x < 0 || y < 0 || t >= duration || x >= width || y >= height) {
			return fallback;
		}
		return data[t][x][y];
	}

	@Nonnull
	@CheckReturnValue
	public Cell getOrElse(@Nonnull Position pos, @Nonnull Cell fallback) {
		return getOrElse(pos.t, pos.x, pos.y, fallback);
	}

	@Nonnull
	@CheckReturnValue
	public String asStringAt(@NonNegative int t) {
		Validate.isTrue(t < duration, "'t' cannot exceed duration");

		StringBuilder text = new StringBuilder("step " + (t + 1) + " of " + duration + ":\n");
		text.append("+");
		for (int i = 0; i < width + 2; i++) {
			text.append("-");
		}
		text.append("+\n");
		for (int y = 0; y < height; y++) {
			text.append("|");
			for (int x = 0; x < width; x++) {
				text.append(data[t][x][y].toString());
			}
			text.append("|\n");
		}
		text.append("+");
		for (int i = 0; i < width + 2; i++) {
			text.append("-");
		}
		text.append("+\n");
		return text.toString();
	}

	@Nonnull
	@CheckReturnValue
	public String asStringAll() {
		return IntStream.range(0, duration)
		    .mapToObj(this::asStringAt)
		    .collect(Collectors.joining("\n"));
	}

	@Nonnull
	@CheckReturnValue
	public static Maze fromStrings(@Nonnull String[] textMazes) {
		Validate.isTrue(textMazes.length > 0);

		// Iterate over snapshots.
		Cell[][][] mazes = null;
		for (int stepNr = 0; stepNr < textMazes.length; stepNr++) {
			String snapshotText = textMazes[stepNr];

			// Determine the shape and allocate memory.
			String[] rows = snapshotText.split("\\r?\\n");
			Validate.isTrue(rows.length > 0);
			final int columnCount = rows[0].length();
			Cell[][] snapshotMaze = new Cell[columnCount][rows.length];
			if (mazes == null) {
				mazes = new Cell[textMazes.length][columnCount][rows.length];
			} else {
				Validate.isTrue(mazes[0].length == snapshotMaze.length && mazes[0][0].length == snapshotMaze[0].length,
						"Initial maze was " + mazes[0].length + " x " + mazes[0][0].length +
								", but later one is " +  + snapshotMaze.length + " x " + snapshotMaze[0].length);
			}
			mazes[stepNr] = snapshotMaze;

			// Parse the cells one by one.
			for (int rowNr = 0; rowNr < rows.length; rowNr++) {
				String row = rows[rowNr];
				Validate.isTrue(row.length() == columnCount, "Text maze is not square; " +
						"first row has " + columnCount + " cells, but a later row has " + row.length());
				for (int colNr = 0; colNr < columnCount; colNr++) {
					char letter = row.charAt(colNr);
					Cell cell = Cell.parse(letter);
					snapshotMaze[colNr][rowNr] = cell;
				}
			}
		}

		return new Maze(mazes);
	}

	@Nonnull
	@CheckReturnValue
	public static Maze fromString(@Nonnull String textMaze) {
		return fromStrings(new String[]{textMaze});
	}

	@Override
	public String toString() {
		return "Maze[" + width + "x" + height + "x" + duration + "]";
	}

	@Nonnull
	@CheckReturnValue
	public Pair<Position, Cell>[] flat() {
		if (flatCache == null) {
			//noinspection unchecked
			flatCache = new Pair[duration * width * height];
			for (int t = 0; t < duration; t++) {
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int index = t * width * height + x * height + y;
						Position pos = Position.at(t, x, y);
						Cell cell = data[t][x][y];
						flatCache[index] = Pair.of(pos, cell);
					}
				}
			}
		}
		return flatCache;
	}

	@Nonnull
	@CheckReturnValue
	public Stream<Pair<Position, Cell>> stream() {
		return Arrays.stream(flat());
	}

	@Nonnull
	@CheckReturnValue
	@Override
	public Iterator<Pair<Position, Cell>> iterator() {
		return stream().iterator();
	}
}
