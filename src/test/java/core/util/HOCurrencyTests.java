package core.util;

import core.model.HOModel;
import core.model.HOModelManager;
import core.model.UserParameter;
import core.model.XtraData;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;

class HOCurrencyTests {
    //  @Test
    void test() {
        // Prepare model
        HOModelManager hov = HOModelManager.instance();
        HOModel model = new HOModel(1);
        hov.setModel(model);
        model.setXtraDaten(new XtraData());
        model.getXtraDaten().setCountryId(3);
        UserParameter.instance().currencyRate = 10f;

        AmountOfMoney c = new AmountOfMoney(10);
        Assertions.assertEquals(new BigDecimal("10.00"), c.toLocale());

        AmountOfMoney e = new AmountOfMoney(50);
        Assertions.assertEquals(10, e.toLocale());

        AmountOfMoney d = new AmountOfMoney(c.getSwedishKrona().longValueExact() + 90);
        String nbsp = "\u00A0";
        Assertions.assertEquals("10" + nbsp + "€", d.toLocaleString());
    }
}
