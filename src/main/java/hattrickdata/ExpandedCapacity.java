package hattrickdata;

import core.util.HODateTime;

public record ExpandedCapacity(int terraces, int basic, int roof, int vip, int total, HODateTime expansionDate) {
}
