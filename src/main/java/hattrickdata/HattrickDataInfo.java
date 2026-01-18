package hattrickdata;

import core.util.HODateTime;

public record HattrickDataInfo(String fileName, String version, int userId, HODateTime fetchedDate) {
}
