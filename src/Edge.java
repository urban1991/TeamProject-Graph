public class Edge {

    private Vertex cel;
    private double waga;

    public Edge(Vertex cel, double waga) {
        this.cel = cel;
        this.waga = waga;
    }

    public Vertex getCel() {
        return cel;
    }

    public void setCel(Vertex cel) {
        this.cel = cel;
    }

    public double getWaga() {
        return waga;
    }

    public void setWaga(double waga) {
        this.waga = waga;
    }

    public void wypisz(){
        System.out.println("Cel: "+cel.getName()+" waga: "+waga);
    }
}
