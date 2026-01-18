package hattrickdata;

import core.util.HODateTime;

import java.util.Optional;

public class CurrentCapacity extends Capacity {

    private HODateTime rebuildDate;

    public CurrentCapacity() {
    }

    public CurrentCapacity(int terraces, int basic, int roof, int vip, int total, HODateTime rebuildDate) {
        super(terraces, basic, roof, vip, total);
        this.rebuildDate = rebuildDate;
    }

    public HODateTime getRebuildDate() {
        return rebuildDate;
    }

    public void setRebuildDate(HODateTime rebuildDate) {
        this.rebuildDate = rebuildDate;
    }

    public java.util.Optional<HODateTime> getRebuiltDate() {
        return java.util.Optional.ofNullable(rebuildDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        CurrentCapacity that = (CurrentCapacity) o;
        return java.util.Objects.equals(rebuildDate, that.rebuildDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), rebuildDate);
    }

    @Override
    public String toString() {
        return "CurrentCapacity{" +
                "rebuildDate=" + rebuildDate +
                ", " + super.toString() +
                '}';
    }

    public static class Builder {
        private int terraces;
        private int basic;
        private int roof;
        private int vip;
        private int total;
        private HODateTime rebuildDate;

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

        public Builder rebuildDate(HODateTime rebuildDate) {
            this.rebuildDate = rebuildDate;
            return this;
        }

        public CurrentCapacity build() {
            return new CurrentCapacity(terraces, basic, roof, vip, total, rebuildDate);
        }
    }
}
