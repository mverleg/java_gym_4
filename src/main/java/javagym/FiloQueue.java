package javagym;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import noedit.PathBuilder;
import noedit.Position;

/**
 * Simple FILO queue.
 */
public final class FiloQueue implements PathQueue {

	@Nonnull private final Queue<PathBuilder> layerQueues;

	public FiloQueue() {
		layerQueues = new LinkedList<>();
	}

	@Override
	public void add(@Nonnull PathBuilder path) {
		Position pos = path.latest();
		layerQueues.add(path);
	}

	@Override
	@Nullable
	@CheckReturnValue
	public PathBuilder head() {
		return layerQueues.poll();
	}

	@Override
	public boolean isNotEmpty() {
		return !layerQueues.isEmpty();
	}
}
