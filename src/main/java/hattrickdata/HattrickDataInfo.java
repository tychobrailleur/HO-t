package hattrickdata;

import core.util.HODateTime;

import java.util.Objects;

public class HattrickDataInfo {
    private String fileName;
    private String version;
    private int userId;
    private HODateTime fetchedDate;

    public HattrickDataInfo() {
    }

    public HattrickDataInfo(String fileName, String version, int userId, HODateTime fetchedDate) {
        this.fileName = fileName;
        this.version = version;
        this.userId = userId;
        this.fetchedDate = fetchedDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public HODateTime getFetchedDate() {
        return fetchedDate;
    }

    public void setFetchedDate(HODateTime fetchedDate) {
        this.fetchedDate = fetchedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HattrickDataInfo that = (HattrickDataInfo) o;
        return userId == that.userId &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(version, that.version) &&
                Objects.equals(fetchedDate, that.fetchedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, version, userId, fetchedDate);
    }

    @Override
    public String toString() {
        return "HattrickDataInfo{" +
                "fileName='" + fileName + '\'' +
                ", version='" + version + '\'' +
                ", userId=" + userId +
                ", fetchedDate=" + fetchedDate +
                '}';
    }

    public static HattrickDataInfoBuilder builder() {
        return new HattrickDataInfoBuilder();
    }

    public static class HattrickDataInfoBuilder {
        private String fileName;
        private String version;
        private int userId;
        private HODateTime fetchedDate;

        public HattrickDataInfoBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public HattrickDataInfoBuilder version(String version) {
            this.version = version;
            return this;
        }

        public HattrickDataInfoBuilder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public HattrickDataInfoBuilder fetchedDate(HODateTime fetchedDate) {
            this.fetchedDate = fetchedDate;
            return this;
        }

        public HattrickDataInfo build() {
            return new HattrickDataInfo(fileName, version, userId, fetchedDate);
        }
    }
}
