package org.inasayaflanderin.abyssine.memory.cache;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MRU<K, D> extends AbstractRecentlyCache<K, D> {
    @Serial
    private static final long serialVersionUID = 1983383161364329324L;

    public MRU(int size) {
        super(size);
    }

    public void findRemove(int position) {
        var removePosition = position;

        while(this.data.get(removePosition) == null) removePosition = (removePosition + 1) % this.data.size();

        for (int i = removePosition; i != -1; i = this.data.get(i).second()) if(this.data.get(i).second() == -1) remove(i);
    }
}