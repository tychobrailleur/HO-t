package core.db;

import java.time.Instant;

public record CachedStatementStats(Instant created, Instant lastAccessed, int count) {

}
