package hattrickdata;

import core.util.HODateTime;

public record CurrentCapacity(int terraces, int basic, int roof, int vip, int total, HODateTime rebuildDate) {
}
