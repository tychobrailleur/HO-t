package hattrickdata;

import core.util.HODateTime;

import java.util.Objects;

public record HattrickDataInfo(String fileName, String version, int userId, HODateTime fetchedDate) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HattrickDataInfo that = (HattrickDataInfo) o;
        return userId == that.userId
                && Objects.equals(version, that.version)
                && Objects.equals(fileName, that.fileName)
                && Objects.equals(fetchedDate, that.fetchedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, version, userId, fetchedDate);
    }
}
