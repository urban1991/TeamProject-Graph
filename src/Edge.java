public class Edge {

    private Vertex target;
    private double distance;

    public Edge(Vertex target, double distance) {
        this.target = target;
        this.distance = distance;
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        this.target = target;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void print(){
        System.out.println("Target: "+ target.getName()+" distance: "+ distance);
    }
}
