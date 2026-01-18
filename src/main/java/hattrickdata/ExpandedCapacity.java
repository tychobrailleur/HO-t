package hattrickdata;

import core.util.HODateTime;

public class ExpandedCapacity extends Capacity {

    private HODateTime expansionDate;

    public ExpandedCapacity() {
    }

    public ExpandedCapacity(int terraces, int basic, int roof, int vip, int total, HODateTime expansionDate) {
        super(terraces, basic, roof, vip, total);
        this.expansionDate = expansionDate;
    }

    public HODateTime getExpansionDate() {
        return expansionDate;
    }

    public void setExpansionDate(HODateTime expansionDate) {
        this.expansionDate = expansionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        ExpandedCapacity that = (ExpandedCapacity) o;
        return java.util.Objects.equals(expansionDate, that.expansionDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), expansionDate);
    }

    @Override
    public String toString() {
        return "ExpandedCapacity{" +
                "expansionDate=" + expansionDate +
                ", " + super.toString() +
                '}';
    }

    public static class Builder {
        private int terraces;
        private int basic;
        private int roof;
        private int vip;
        private int total;
        private HODateTime expansionDate;

        public Builder terraces(int terraces) {
            this.terraces = terraces;
            return this;
        }

        public Builder basic(int basic) {
            this.basic = basic;
            return this;
        }

        public Builder roof(int roof) {
            this.roof = roof;
            return this;
        }

        public Builder vip(int vip) {
            this.vip = vip;
            return this;
        }

        public Builder total(int total) {
            this.total = total;
            return this;
        }

        public Builder expansionDate(HODateTime expansionDate) {
            this.expansionDate = expansionDate;
            return this;
        }

        public ExpandedCapacity build() {
            return new ExpandedCapacity(terraces, basic, roof, vip, total, expansionDate);
        }
    }
}
