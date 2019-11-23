package javagym;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import noedit.PathBuilder;
import noedit.Position;

//TODO @mark: remove all syncrhonized if not parallel

/**
 * A FILO queue with a preference for later layers.
 *
 * This is used when there are too many exits for {@link ClosestQueue}.
 */
public final class LayerQueue implements PathQueue {

	private int itemCount = 0;
	@Nonnull private final Queue<PathBuilder>[] layerQueues;

	public LayerQueue(int layerCount) {
		//noinspection unchecked
		layerQueues = new Queue[layerCount];
		for (int t = 0; t < layerCount; t++) {
			layerQueues[t] = new LinkedList<>();
		}
	}

	@Override
	synchronized public void add(@Nonnull PathBuilder path) {
		Position pos = path.latest();
		layerQueues[pos.t].add(path);
		itemCount += 1;
	}

	@Override
	@Nullable
	@CheckReturnValue
	synchronized public PathBuilder head() {
		for (int t = layerQueues.length - 1; t >= 0; t--) {
			PathBuilder path = layerQueues[t].poll();
			if (path != null) {
				itemCount -= 1;
				return path;
			}
		}
		return null;
	}

	@Override
	synchronized public boolean isNotEmpty() {
		return itemCount > 0;
	}
}
