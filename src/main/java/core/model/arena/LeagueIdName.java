package core.model.arena;

import java.util.Objects;

public record LeagueIdName(int id, String name) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LeagueIdName that = (LeagueIdName) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
