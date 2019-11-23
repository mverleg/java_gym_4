package javagym;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import noedit.PathBuilder;

public interface PathQueue {

	void add(@Nonnull PathBuilder path);

	@Nullable
	@CheckReturnValue
	PathBuilder head();

	boolean isNotEmpty();
}
