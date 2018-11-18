public class Edge {
    Node from;
    Node to;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public boolean adjecentToRed(){
        return from.isRed || to.isRed;
    }

    public boolean adjecentToSource(){
        return from.isSource || to.isSource;
    }

    public boolean adjecentToSink(){
        return from.isSink || to.isSink;
    }
}
