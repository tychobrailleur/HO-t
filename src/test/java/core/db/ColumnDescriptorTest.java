package core.db;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

class ColumnDescriptorTest {

    private DBInfo createDbInfo() {
        return new DBInfo(null);
    }

    @Test
    void testBuilderSetsValuesCorrectly() {
        class LocalStorer {
            int v;

            LocalStorer(int v) {
                this.v = v;
            }
        }
        LocalStorer storer = new LocalStorer(12);

        ColumnDescriptor descriptor = ColumnDescriptor.Builder.newInstance()
                .setColumnName("TEST")
                .setType(Types.VARCHAR)
                .setLength(42)
                .isNullable(true)
                .isPrimaryKey(true)
                .setGetter(o -> ((LocalStorer) o).v)
                .setSetter((o, v) -> ((LocalStorer) o).v = (int) v)
                .build();

        Assertions.assertEquals("TEST", descriptor.getColumnName());
        Assertions.assertEquals(Types.VARCHAR, descriptor.getType());
        Assertions.assertEquals(true, descriptor.isNullable());
        Assertions.assertEquals(true, descriptor.isPrimaryKey());
        Assertions.assertEquals(12, descriptor.getter.apply(storer));

        // Invoke setter
        descriptor.setter.accept(storer, 66);
        Assertions.assertEquals(66, storer.v);

        // Check length
        Assertions.assertEquals(" TEST VARCHAR(42) PRIMARY KEY",
                descriptor.getCreateString(createDbInfo()));
    }

    @Test
    void testColumnDescriptorNameTypeAndNullable() {
        Object[][] col = {
                { "NAME", Types.BOOLEAN, true, " NAME BOOLEAN" },
                { "NAME", Types.BOOLEAN, false, " NAME BOOLEAN NOT NULL" }
        };

        for (Object[] it : col) {
            ColumnDescriptor columnDescriptor = new ColumnDescriptor((String) it[0], (int) it[1], (boolean) it[2]);
            Assertions.assertEquals((String) it[3], columnDescriptor.getCreateString(createDbInfo()));
        }
    }

    @Test
    void testColumnDescriptorNameTypeNullableAndPrimaryKey() {
        Object[][] col = {
                { "NAME", Types.BOOLEAN, false, true, " NAME BOOLEAN NOT NULL PRIMARY KEY" },
                { "NAME", Types.BOOLEAN, false, false, " NAME BOOLEAN NOT NULL" },
                { "NAME", Types.BOOLEAN, true, true, " NAME BOOLEAN PRIMARY KEY" },
                { "NAME", Types.BOOLEAN, true, false, " NAME BOOLEAN" }
        };

        for (Object[] it : col) {
            ColumnDescriptor columnDescriptor = new ColumnDescriptor((String) it[0], (int) it[1], (boolean) it[2],
                    (boolean) it[3]);
            Assertions.assertEquals((String) it[4], columnDescriptor.getCreateString(createDbInfo()));
        }
    }
}
