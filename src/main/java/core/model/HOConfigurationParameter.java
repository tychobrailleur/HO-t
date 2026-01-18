package core.model;

import core.db.AbstractTable.Storable;
import core.db.DBManager;
import java.util.Map;

/**
 * Configuration parameters are registered in a static property register
 * which is saved in the user configuration table at application termination
 * Remark: This class should replace usage of UserParameter an HOParameter
 * classes
 */
public class HOConfigurationParameter extends Storable {

    /**
     * The parameters' registry
     */
    protected static final HOProperties parameters = new HOProperties();

    /**
     * Remember if parameters were changed
     */
    private static boolean parametersChanged = false;

    /**
     * Parameter key
     */
    private final String key;

    /**
     * Parameter value
     */
    private String value;

    /**
     * Create configuration parameter
     * If key is found in registry, the value is fetched from registry otherwise it
     * is loaded from the database.
     * If this isn't found either the given default value is used.
     * 
     * @param key          Parameter key
     * @param defaultValue Default value
     */
    public HOConfigurationParameter(String key, String defaultValue) {
        this.key = key;
        this.value = parameters.getProperty(key);

        if (this.value == null) {
            this.value = DBManager.instance().loadHOConfigurationParameter(key);
            if (this.value == null) {
                this.value = defaultValue;
            }
            if (this.value != null) {
                parameters.setProperty(key, this.value);
            }
        }
    }

    /**
     * Return the value
     * 
     * @return String
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value.
     * ParameterChanged is set to true, if new value different to previous value
     * 
     * @param value New value
     */
    public void setValue(String value) {
        if ((value == null && this.value != null) || (value != null && !value.equals(this.value))) {
            this.value = value;
            if (value != null) {
                parameters.setProperty(key, value);
            } else {
                parameters.remove(key); // Assuming remove exists or handle nulls appropriately
            }
            parametersChanged = true;
        }
    }

    /**
     * Store the current parameters of the registry in the database
     */
    public static void storeParameters() {
        if (parametersChanged) {
            for (Map.Entry<Object, Object> entry : parameters.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                DBManager.instance().saveUserParameter(key, value);
            }
        }
    }
}
