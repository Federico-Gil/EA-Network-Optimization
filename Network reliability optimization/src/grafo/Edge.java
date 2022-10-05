package grafo;

public class Edge {
    //the two vertices that the edge connects and the weight of the edge
    private int v1, v2;
    private float weight;

    //constructor
    public Edge(int v1, int v2, float weight) {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
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

    //toString method
    @Override
    public String toString() {
        return "Edge{" +
                "v1=" + v1 +
                ", v2=" + v2 +
                ", weight=" + weight +
                '}';
    }

    //equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (v1 != edge.v1) return false;
        if (v2 != edge.v2) return false;
        return Float.compare(edge.weight, weight) == 0;
    }
}
