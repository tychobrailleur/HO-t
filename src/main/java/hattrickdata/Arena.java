package hattrickdata;

import java.util.Optional;

public record Arena(int id, String name, Team team, League league, Region region, CurrentCapacity currentCapacity,
                    Optional<ExpandedCapacity> expandedCapacity) {
}
