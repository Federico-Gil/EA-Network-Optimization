package grafo;

public class Edge {
    //the two vertices that the edge connects and the weight of the edge
    private int v1, v2;//nodos
    private double weight;//probabilidad de fallar
    private Boolean exists;
    private double cost;//largo en km

    //constructor
    public Edge(int v1, int v2, double weight, double cost, Boolean exists) {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
        this.exists = exists;
        this.cost = cost;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    //toString method
    @Override
    public String toString() {
        return "Edge [v1=" + v1 + ", v2=" + v2 + ", weight=" + weight + ", exists=" + exists + ", cost=" + cost + "]";
    }

    //hashCode method, used to compare two edges, if they have the same vertices, then they are the same edge
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result += prime * result + v1;
        result += prime * result + v2;
        return result;
    }
    

    //equals method: only the vertices are compared the order of the vertices does not matter
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Edge other = (Edge) obj;
        if (this.v1 != other.v1 && this.v1 != other.v2) {
            return false;
        }
        if (this.v2 != other.v2 && this.v2 != other.v1) {
            return false;
        }
        return true;
    }

    public String toCsv(){
        return "" + v1 + ", " + v2 + ", " + weight + ", " + cost;
    }

	public Edge copy() {
        return new Edge(v1, v2, weight, cost,exists);
    }
}
