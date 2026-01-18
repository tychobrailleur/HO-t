package hattrickdata;

import java.util.Optional;

public class Arena {

    private int id;
    private String name;

    private Team team;
    private League league;
    private Region region;

    private CurrentCapacity currentCapacity;
    private ExpandedCapacity expandedCapacity;

    public Arena() {
    }

    public Arena(int id, String name, Team team, League league, Region region, CurrentCapacity currentCapacity,
            ExpandedCapacity expandedCapacity) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.league = league;
        this.region = region;
        this.currentCapacity = currentCapacity;
        this.expandedCapacity = expandedCapacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public CurrentCapacity getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(CurrentCapacity currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public ExpandedCapacity getExpandedCapacity() {
        return expandedCapacity;
    }

    public void setExpandedCapacity(ExpandedCapacity expandedCapacity) {
        this.expandedCapacity = expandedCapacity;
    }

    public Optional<ExpandedCapacity> getExpandedCapacityOptional() {
        return Optional.ofNullable(expandedCapacity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Arena arena = (Arena) o;
        return id == arena.id &&
                java.util.Objects.equals(name, arena.name) &&
                java.util.Objects.equals(team, arena.team) &&
                java.util.Objects.equals(league, arena.league) &&
                java.util.Objects.equals(region, arena.region) &&
                java.util.Objects.equals(currentCapacity, arena.currentCapacity) &&
                java.util.Objects.equals(expandedCapacity, arena.expandedCapacity);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, team, league, region, currentCapacity, expandedCapacity);
    }

    @Override
    public String toString() {
        return "Arena{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", team=" + team +
                ", league=" + league +
                ", region=" + region +
                ", currentCapacity=" + currentCapacity +
                ", expandedCapacity=" + expandedCapacity +
                '}';
    }

    public static ArenaBuilder builder() {
        return new ArenaBuilder();
    }

    public static class ArenaBuilder {
        private int id;
        private String name;
        private Team team;
        private League league;
        private Region region;
        private CurrentCapacity currentCapacity;
        private ExpandedCapacity expandedCapacity;

        public ArenaBuilder id(int id) {
            this.id = id;
            return this;
        }

        public ArenaBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ArenaBuilder team(Team team) {
            this.team = team;
            return this;
        }

        public ArenaBuilder league(League league) {
            this.league = league;
            return this;
        }

        public ArenaBuilder region(Region region) {
            this.region = region;
            return this;
        }

        public ArenaBuilder currentCapacity(CurrentCapacity currentCapacity) {
            this.currentCapacity = currentCapacity;
            return this;
        }

        public ArenaBuilder expandedCapacity(ExpandedCapacity expandedCapacity) {
            this.expandedCapacity = expandedCapacity;
            return this;
        }

        public Arena build() {
            return new Arena(id, name, team, league, region, currentCapacity, expandedCapacity);
        }
    }
}
