package org.inasayaflanderin.abyssine.memory.cache;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

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

        for (int i = removePosition; i != -1; i = this.data.get(i).first()) if (this.data.get(i).first() == -1) remove(i);
    }
}