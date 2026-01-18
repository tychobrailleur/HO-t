package core.model.arena;

import java.util.Objects;
import java.util.Optional;

public record Arena(int id, String name, TeamIdName teamIdName, LeagueIdName leagueIdName, RegionIdName regionIdName,
                    Capacity currentCapacity,
                    Optional<Capacity> expandedCapacity) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return id == arena.id && Objects.equals(name, arena.name)
                && Objects.equals(teamIdName, arena.teamIdName)
                && Objects.equals(leagueIdName, arena.leagueIdName)
                && Objects.equals(regionIdName, arena.regionIdName)
                && Objects.equals(currentCapacity, arena.currentCapacity)
                && Objects.equals(expandedCapacity, arena.expandedCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, teamIdName, leagueIdName, regionIdName, currentCapacity, expandedCapacity);
    }
}
