package core.model.arena;

import core.util.HODateTime;

public record Capacity(int terraces,int basic,int roof,int vip,int total,HODateTime rebuildDate,HODateTime expansionDate){

}
