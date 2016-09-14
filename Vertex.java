import java.util.HashSet;
import java.util.Set;

// for the graph data structure:
public class Vertex {

	public Node nodeObject;
	public Set <Edge> edges;

	public Vertex (Node n) {
		nodeObject = n;
		edges = new HashSet <Edge> ();
	}

	// add this road segment to the current list of edges for the vertex:
	public void addEdge (Edge thisEdge) {
		edges.add(thisEdge);
	}


}
