package org.inasayaflanderin.abyssine.memory.cache;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.inasayaflanderin.abyssine.exception.ImpossibleException;

import java.io.Serial;
import java.util.stream.IntStream;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LRU<K, D> extends AbstractRecentlyCache<K, D> {
    @Serial
    private static final long serialVersionUID = -6671718468291433541L;

    public LRU(int size) {
        super(size);
    }

    public void findRemove(int position) {
        var removePosition = position;

        if(this.data.get(removePosition) == null) removePosition = (removePosition + 1) % this.data.size();

        IntStream.iterate(removePosition, i -> i != -1, i -> this.data.get(i).first())
                .filter(i -> this.data.get(i).first() == -1).findFirst()
                .ifPresentOrElse(this::remove,
                        () -> {
                            log.error("There was a head in the cache, but it was not removed.");

                            throw new ImpossibleException();
                        });
    }
}