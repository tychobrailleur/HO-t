package core.model.arena;

import core.util.HODateTime;

import java.util.Objects;

public record Capacity(int terraces, int basic, int roof, int vip, int total, HODateTime rebuildDate,
                       HODateTime expansionDate) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Capacity capacity = (Capacity) o;
        return vip == capacity.vip
                && roof == capacity.roof
                && basic == capacity.basic
                && total == capacity.total
                && terraces == capacity.terraces
                && Objects.equals(rebuildDate, capacity.rebuildDate)
                && Objects.equals(expansionDate, capacity.expansionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terraces, basic, roof, vip, total, rebuildDate, expansionDate);
    }
}
