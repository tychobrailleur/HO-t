package core.datatype;

/**
 * Combo item that associates an ID to a String.
 *
 * @author thomas.werth
 */
public record CBItem(String text, int id) implements ComboItem {

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CBItem temp) {
            return this.id == temp.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return text;
    }
}
