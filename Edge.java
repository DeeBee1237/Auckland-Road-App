// for the graph data Structure:
public class Edge {
	public Vertex vertexOnOtherSide; 
	public RoadSegment segmentObject; // the weight for this Edge

	public Edge (Vertex v,RoadSegment rs) {
		vertexOnOtherSide = v;
		segmentObject = rs;
	}

}
