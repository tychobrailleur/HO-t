package core.model.arena;

import java.util.Optional;

public record Arena(int id,String name,TeamIdName teamIdName,LeagueIdName leagueIdName,RegionIdName regionIdName,Capacity currentCapacity,Optional<Capacity>expandedCapacity){

}
