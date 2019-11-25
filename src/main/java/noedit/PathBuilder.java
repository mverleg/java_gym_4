package noedit;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

/**
 * Create {@link Path} using {@link #build()}.
 */
public class PathBuilder implements Cloneable {

	private static class Node {
		@Nullable
		private final Node previous;
		@Nonnull
		private final Position position;

		public Node(@Nullable Node previous, @Nonnull Position position) {
			this.previous = previous;
			this.position = position;
		}
	}

	private int length = 0;
	private Node latest = null;

	public PathBuilder(@Nonnull Position initialPosition) {
		addChain(initialPosition);
	}

	public void add(@Nonnull Position position) {
		addChain(position);
	}

	private void addChain(@Nonnull Position position) {
		length += 1;
		latest = new Node(latest, position);
	}

	@Nonnull
	@CanIgnoreReturnValue
	public Position left() {
		Position next = latest().left();
		addChain(next);
		return next;
	}

	@Nonnull
	@CanIgnoreReturnValue
	public Position right() {
		Position next = latest().right();
		addChain(next);
		return next;
	}

	@Nonnull
	@CanIgnoreReturnValue
	public Position up() {
		Position next = latest().up();
		addChain(next);
		return next;
	}

	@Nonnull
	@CanIgnoreReturnValue
	public Position down() {
		Position next = latest().down();
		addChain(next);
		return next;
	}

	@Nonnull
	@CanIgnoreReturnValue
	public Position waitStep() {
		Position next = latest().nextStep();
		addChain(next);
		return next;
	}

	@Nonnull
	@CheckReturnValue
	public Position latest() {
		return latest.position;
	}

	@Nonnull
	@CheckReturnValue
	public Path build() {
		List<Position> steps = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			steps.add(null);
		}
		Node current = latest;
		for (int i = length - 1; i >= 0; i--) {
			assert current != null;
			steps.set(i, current.position);
			current = current.previous;
		}
		assert current == null;
		return new Path(steps);
	}

	@CheckReturnValue
	public int size() {
		return length;
	}

	/**
	 * This clone method uses structural sharing and as such is constant time and memory, so very efficient.
	 */
	@Nonnull
	@CheckReturnValue
	@Override
	public PathBuilder clone() {
		PathBuilder doppel;
		try {
			doppel = (PathBuilder)super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new IllegalStateException(ex);
		}
		doppel.length = this.length;
		doppel.latest = this.latest;
		return doppel;
	}
}
