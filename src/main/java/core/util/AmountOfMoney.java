package core.util;

import core.model.HOConfigurationIntParameter;
import core.model.WorldDetailLeague;
import core.model.WorldDetailsManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Amounts of money are stored in swedish krona and displayed in players' locale
 */
public class AmountOfMoney {

    private BigDecimal swedishKrona;

    /**
     * List of available currencies
     */
    private static Set<String> currencyInfo = new HashSet<>();

    /**
     * The currency setting (country id)
     * It is set either by the first download with the currency code of the premier
     * team
     * or by editing the currency settings in the options dialog.
     */
    private static final HOConfigurationIntParameter currencyCountryId = new HOConfigurationIntParameter(
            "CurrencyCountryId");

    /**
     * Currency formatter
     */
    private static NumberFormat currencyFormatter = null;

    /**
     * Exchange rate between swedish krona and selected currency
     */
    private static BigDecimal exchangeRate = null;

    public AmountOfMoney(BigDecimal swedishKrona) {
        this.swedishKrona = swedishKrona;
    }

    public AmountOfMoney(long swedishKrona) {
        this(BigDecimal.valueOf(swedishKrona));
    }

    public BigDecimal getSwedishKrona() {
        return swedishKrona;
    }

    public void setSwedishKrona(BigDecimal swedishKrona) {
        this.swedishKrona = swedishKrona;
    }

    /**
     * Get the list of currency info from the list of leagues in world detail file
     * Hattrick international is removed from the list.
     */
    public static Set<String> getCurrencyInfo() {
        if (currencyInfo.isEmpty()) {
            for (WorldDetailLeague worldDetails : WorldDetailsManager.instance().getLeagues()) {
                if (worldDetails.getCountryId() < 1000) {
                    String info = getCurrencyInfo(worldDetails);
                    if (info != null) {
                        currencyInfo.add(info);
                    }
                }
            }
        }
        return currencyInfo;
    }

    /**
     * Parse currency value from string.
     * If value could not be parsed with currency format a number format is tried.
     * 
     * @param v String to parse from
     * @return AmountOfMoney, null on parse error
     */
    public static AmountOfMoney parse(String v) {
        Number amount;
        try {
            NumberFormat formatter = getCurrencyFormatter();
            if (formatter != null) {
                amount = formatter.parse(v);
            } else {
                amount = null;
            }
        } catch (Exception e) {
            try {
                amount = Helper.getNumberFormat(0).parse(v);
            } catch (Exception ex) {
                HOLogger.instance().error(Helper.class, "error parsing currency " + ex);
                return null;
            }
        }

        if (amount == null)
            return new AmountOfMoney(BigDecimal.ZERO);

        return new AmountOfMoney(BigDecimal.valueOf(amount.doubleValue()));
    }

    /**
     * Get the currency formatter object
     */
    private static NumberFormat getCurrencyFormatter() {
        if (currencyFormatter == null) {
            Integer countryId = currencyCountryId.getIntValue();
            if (countryId != null) {
                WorldDetailLeague worldDetailLeague = WorldDetailsManager.instance()
                        .getWorldDetailLeagueByCountryId(countryId);
                for (Locale locale : NumberFormat.getAvailableLocales()) {
                    NumberFormat ret = NumberFormat.getCurrencyInstance(locale);
                    // Note: Check existing logic for getCurrency().getSymbol() vs currencyName
                    if (ret.getCurrency().getSymbol().equals(worldDetailLeague.getCurrencyName()) ||
                            locale.getCountry().equals(worldDetailLeague.getCountryCode())) {
                        currencyFormatter = ret;
                        return ret;
                    }
                }
            }
            currencyFormatter = NumberFormat.getCurrencyInstance();
        }
        return currencyFormatter;
    }

    /**
     * Get the exchange rate between internal swedish krona value and currency
     * setting
     */
    public static BigDecimal getExchangeRate() {
        if (exchangeRate == null) {
            Integer countryId = currencyCountryId.getIntValue();
            if (countryId == null) {
                WorldDetailLeague worldDetailLeague = WorldDetailLeague.getWorldDetailsLeagueOfPremierTeam();
                if (worldDetailLeague != null) {
                    countryId = worldDetailLeague.getCountryId();
                    currencyCountryId.setIntValue(countryId);
                }
            }
            if (countryId != null) {
                WorldDetailLeague worldDetailLeague = WorldDetailsManager.instance()
                        .getWorldDetailLeagueByCountryId(countryId);
                if (worldDetailLeague != null) {
                    exchangeRate = BigDecimal.valueOf(worldDetailLeague.getCurrencyRate());
                }
            }
            if (exchangeRate == null) {
                return BigDecimal.ONE;
            }
        }
        return exchangeRate;
    }

    /**
     * Set currency.
     * All other currency settings are reset if the new value differs from the
     * current value.
     */
    public static boolean setCurrencyCountry(String inCurrencyInfo) {
        if (inCurrencyInfo.contains("(")) {
            String countryCode = inCurrencyInfo.substring(inCurrencyInfo.indexOf("(") + 1, inCurrencyInfo.indexOf(")"));
            for (WorldDetailLeague country : WorldDetailsManager.instance().getLeagues()) {
                if (country.getCountryCode().equals(countryCode)) {
                    currencyCountryId.setIntValue(country.getCountryId());
                    currencyFormatter = null;
                    exchangeRate = null;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get a display string of the current currency setting.
     */
    public static String getSelectedCurrencyCode() {
        Integer val = currencyCountryId.getIntValue();
        if (val != null) {
            WorldDetailLeague worldDetailLeague = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(val);
            if (worldDetailLeague != null) {
                return getCurrencyInfo(worldDetailLeague);
            }
        }
        return null;
    }

    /**
     * Format the currency display string, containing the country name, currency
     * name and the country code
     */
    private static String getCurrencyInfo(WorldDetailLeague worldDetails) {
        if (worldDetails != null) {
            return worldDetails.getCountryName() + " - " + worldDetails.getCurrencyName() + " ("
                    + worldDetails.getCountryCode() + ")";
        }
        return null;
    }

    /**
     * Get the currency name (symbol) of current setting
     */
    public static String getCurrencyName() {
        Integer val = currencyCountryId.getIntValue();
        if (val != null) {
            WorldDetailLeague worldDetailLeague = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(val);
            if (worldDetailLeague != null) {
                return worldDetailLeague.getCurrencyName();
            }
        }
        return "";
    }

    /**
     * Convert to the local currency
     */
    public BigDecimal toLocale() {
        return this.swedishKrona.divide(getExchangeRate(), 2, RoundingMode.HALF_UP);
    }

    /**
     * Format the amount to a locale display string
     */
    public String toLocaleString() {
        return toLocaleString(0);
    }

    /**
     * Format the amount to a locale display string
     */
    public String toLocaleString(int decimals) {
        NumberFormat formatter = getCurrencyFormatter();
        formatter.setMaximumFractionDigits(decimals);
        formatter.setMinimumFractionDigits(decimals);
        return formatter.format(this.toLocale());
    }

    /**
     * Add an amount to the current value
     */
    public void add(AmountOfMoney amountOfMoney) {
        this.swedishKrona = this.swedishKrona.add(amountOfMoney.swedishKrona);
    }

    /**
     * Subtract an amount from the current value
     */
    public void subtract(AmountOfMoney amountOfMoney) {
        this.swedishKrona = this.swedishKrona.subtract(amountOfMoney.swedishKrona);
    }

    /**
     * Return the sum of 2 amounts.
     */
    public AmountOfMoney plus(AmountOfMoney amount) {
        return new AmountOfMoney(this.swedishKrona.add(amount.swedishKrona));
    }

    /**
     * Return the difference of 2 amounts.
     */
    public AmountOfMoney minus(AmountOfMoney amount) {
        return new AmountOfMoney(this.swedishKrona.subtract(amount.swedishKrona));
    }

    /**
     * Return the product of 2 amounts.
     */
    public AmountOfMoney times(BigDecimal factor) {
        return new AmountOfMoney(this.swedishKrona.multiply(factor));
    }

    /**
     * Return the division of 2 amounts
     */
    public AmountOfMoney divide(BigDecimal divisor) {
        try {
            BigDecimal amount = this.swedishKrona.divide(divisor, RoundingMode.HALF_UP);
            return new AmountOfMoney(amount);
        } catch (Exception e) {
            double d = this.swedishKrona.doubleValue() / divisor.doubleValue();
            return new AmountOfMoney(BigDecimal.valueOf(d));
        }
    }

    public BigDecimal divide(AmountOfMoney divisor) {
        return this.swedishKrona.divide(divisor.swedishKrona, 2, RoundingMode.HALF_UP);
    }

    /**
     * Returns true if the given amounts are equal
     */
    public boolean equals(AmountOfMoney other) {
        return (other != null) && this.swedishKrona.equals(other.swedishKrona);
    }

    /**
     * Returns true if the current amount is greater than the given one.
     */
    public boolean isGreaterThan(AmountOfMoney i) {
        return this.swedishKrona.compareTo(i.swedishKrona) > 0;
    }

    /**
     * Returns true if the current amount is less than the given one
     */
    public boolean isLessThan(AmountOfMoney i) {
        return this.swedishKrona.compareTo(i.swedishKrona) < 0;
    }
}
