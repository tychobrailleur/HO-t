package core.db;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DBManagerTest {

    @Test
    void testGetPlaceholders() {
        Assertions.assertEquals("", DBManager.getPlaceholders(0));
        Assertions.assertEquals("?", DBManager.getPlaceholders(1));
        Assertions.assertEquals("?,?", DBManager.getPlaceholders(2));
    }
}
