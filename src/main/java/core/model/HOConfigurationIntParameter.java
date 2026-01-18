package core.model;

/**
 * Configuration parameter of integer type
 */
public class HOConfigurationIntParameter extends HOConfigurationParameter {

    /**
     * Parameter value as integer
     */
    private Integer intValue;

    public HOConfigurationIntParameter(String key, int defaultValue) {
        super(key, String.valueOf(defaultValue));
        init();
    }

    public HOConfigurationIntParameter(String key) {
        super(key, null);
        init();
    }

    /**
     * Common initialization logic
     */
    private void init() {
        String stringVal = this.getValue();
        if (stringVal != null && !stringVal.isEmpty()) {
            this.intValue = Integer.valueOf(stringVal);
        } else {
            this.intValue = null;
        }
    }

    /**
     * Return the parameter integer value
     * 
     * @return int
     */
    public Integer getIntValue() {
        return this.intValue;
    }

    /**
     * Set the new parameter integer value
     * ParameterChanged is set true if new value is different to previous parameter
     * value
     * 
     * @param newValue New integer value
     */
    public void setIntValue(Integer newValue) {
        if ((this.intValue == null && newValue != null) || (this.intValue != null && !this.intValue.equals(newValue))) {
            this.intValue = newValue;
            if (newValue != null) {
                setValue(newValue.toString());
            } else {
                setValue(null);
            }
        }
    }
}
