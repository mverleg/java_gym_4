package javagym;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import noedit.PathBuilder;
import noedit.Position;

import static javagym.Parameters.THREADING;

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
			if (THREADING) {
				layerQueues[t] = new ConcurrentLinkedQueue<>();
			} else {
				layerQueues[t] = new LinkedList<>();
			}
		}
	}

	@Override
	public void add(@Nonnull PathBuilder path) {
		Position pos = path.latest();
		layerQueues[pos.t].add(path);
		itemCount += 1;
	}

	@Override
	@Nullable
	@CheckReturnValue
	public PathBuilder head() {
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
	public boolean isNotEmpty() {
		return itemCount > 0;
	}
}
