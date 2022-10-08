package grafo;

public class Edge {
    //the two vertices that the edge connects and the weight of the edge
    private int v1, v2;
    private float weight;
    private Boolean exists;

    //constructor
    public Edge(int v1, int v2, float weight, Boolean exists) {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
        this.exists = exists;
    }

    //getters and setters
    public int getV1() {
        return v1;
    }

    public void setV1(int v1) {
        this.v1 = v1;
    }

    public int getV2() {
        return v2;
    }

    public void setV2(int v2) {
        this.v2 = v2;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    //toString method
    @Override
    public String toString() {
        return "Edge [v1=" + v1 + ", v2=" + v2 + ", weight=" + weight + ", exists=" + exists + "]";
    }

    //equals method
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Edge other = (Edge) obj;
        if (exists == null) {
            if (other.exists != null)
                return false;
        } else if (!exists.equals(other.exists))
            return false;
        if (v1 != other.v1)
            return false;
        if (v2 != other.v2)
            return false;
        if (Float.floatToIntBits(weight) != Float.floatToIntBits(other.weight))
            return false;
        return true;
    }

	public Edge copy() {
        return new Edge(v1, v2, weight, exists);
    }
}
