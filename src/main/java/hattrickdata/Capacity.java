package hattrickdata;

public class Capacity {

    private int terraces;
    private int basic;
    private int roof;
    private int vip;
    private int total;

    public Capacity() {
    }

    public Capacity(int terraces, int basic, int roof, int vip, int total) {
        this.terraces = terraces;
        this.basic = basic;
        this.roof = roof;
        this.vip = vip;
        this.total = total;
    }

    public int getTerraces() {
        return terraces;
    }

    public void setTerraces(int terraces) {
        this.terraces = terraces;
    }

    public int getBasic() {
        return basic;
    }

    public void setBasic(int basic) {
        this.basic = basic;
    }

    public int getRoof() {
        return roof;
    }

    public void setRoof(int roof) {
        this.roof = roof;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Capacity capacity = (Capacity) o;
        return terraces == capacity.terraces &&
                basic == capacity.basic &&
                roof == capacity.roof &&
                vip == capacity.vip &&
                total == capacity.total;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(terraces, basic, roof, vip, total);
    }

    @Override
    public String toString() {
        return "Capacity{" +
                "terraces=" + terraces +
                ", basic=" + basic +
                ", roof=" + roof +
                ", vip=" + vip +
                ", total=" + total +
                '}';
    }

    public static CapacityBuilder builder() {
        return new CapacityBuilder();
    }

    public static class CapacityBuilder {
        private int terraces;
        private int basic;
        private int roof;
        private int vip;
        private int total;

        public CapacityBuilder terraces(int terraces) {
            this.terraces = terraces;
            return this;
        }

        public CapacityBuilder basic(int basic) {
            this.basic = basic;
            return this;
        }

        public CapacityBuilder roof(int roof) {
            this.roof = roof;
            return this;
        }

        public CapacityBuilder vip(int vip) {
            this.vip = vip;
            return this;
        }

        public CapacityBuilder total(int total) {
            this.total = total;
            return this;
        }

        public Capacity build() {
            return new Capacity(terraces, basic, roof, vip, total);
        }
    }
}
