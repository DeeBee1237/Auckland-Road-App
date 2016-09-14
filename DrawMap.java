import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class DrawMap extends GUI {

	// I will need the maps (Roads -> RoadSegments, RoadSegments -> Nodes) from the StoreCollections class:
	private StoreCollections collectionsObject = new StoreCollections ();
	private Map <Road,Set<RoadSegment>> roadsToSegments;
	private Map <RoadSegment,List<Node>> segmentsToNodes;
	
	// the Map where I will store the highlighted segments
	// given add and remove methods are almost always O(1) with HashMap, it should be ok:
	Map <Road,Set<RoadSegment>> highlightedSegments = new HashMap <Road,Set<RoadSegment>> (); 
	Node highlightedNode = null;
	
    //possible fields to have for shifting the map in different directions:
	private double changeX;
	private double changeY;
	private double moveFactor; // when the map is zoomed in, there needs to be less movement (N,S etc)
	private double zoomFactor;

	// the graph that I will use to find intersections:
	private Set <Vertex> Graph;
	
	// constructor:
	public DrawMap () {
	changeX = 0;
	changeY = 0;
	moveFactor = 20;
	zoomFactor = 0.0;
	}
	
	
	// methods from GUI that I need to extend:
	
	// redraw method:
	public void redraw (Graphics g) {
	// initialize the collections object and the two maps that I need:
	if (roadsToSegments == null) return; // to avoid nullPointerException 
		 for (Road roadObject: roadsToSegments.keySet()) {
					Set <RoadSegment> roadSegments = roadsToSegments.get(roadObject);
					if (roadSegments == null) {continue;} // I am working with faulty data
					for (RoadSegment segmentObject: roadSegments) {
						// draw the road segment object:
						segmentObject.drawSegment(g,changeX,changeY,zoomFactor);
						List<Node> nodePair = segmentsToNodes.get(segmentObject);
						Node node1 = nodePair.get(0);
						Node node2 = nodePair.get(1);
						// now draw the two nodes:
						node1.drawNode(g,changeX,changeY,zoomFactor);
						node2.drawNode(g,changeX,changeY,zoomFactor);
						
						}
				}
		}
	
	 // on move method:
    public void onMove(Move m) {
		
    	switch (m) {
	    	
	    	case NORTH:
	    		if (moveFactor < 0) {changeY = changeY + 1;}
	    		else
	    	    changeY = changeY + moveFactor;
	    		break;
	    	
	    	case SOUTH:
	    		if (moveFactor < 0) {changeY = changeY - 1;}
	    		else
	    		changeY = changeY - moveFactor;
	    		break;
	    		
	    	case EAST:
	    		if (moveFactor < 0) {changeX = changeX  + 1;}
	    		else
	    		changeX = changeX  + moveFactor;
	    		break;
	    		
	    	case WEST:
	    		if (moveFactor < 0) {changeX = changeX - 1;}
	    		else
	    		changeX = changeX - moveFactor;
	    		break;
	    		
	    	case ZOOM_IN:
	    		zoomFactor = zoomFactor + 1.5; // can zoom in as much as you like
	    		if (zoomFactor>0 && zoomFactor < 15) 
	    		moveFactor = moveFactor - 2; // move less
	    		if (zoomFactor >= 15) 
	    			moveFactor = moveFactor - 3; // move even less
	    		break;
	    		
	    	case ZOOM_OUT:
	    		if (zoomFactor==0) break; // don't wanna zoom out the whole map!
	    		
	    		zoomFactor = zoomFactor - 1.5;
	    		
	    		if (zoomFactor>0 && zoomFactor < 15) 
		    		moveFactor = moveFactor + 2; // move more
	    		if (zoomFactor >= 15) 
	    			moveFactor = moveFactor + 3; // move even more
	    		break;
	    	}
	   }
	
	 
	 // the onSearch method:
	 public void onSearch() {
		 
		 // get the string representation of the users input:
		 JTextField textFieldObject = getSearchBox();
		 String userInput = textFieldObject.getText();
		 
		 // need to un-highlight the other segments first:
		 if (highlightedSegments!=null) {
		 for (Road road: highlightedSegments.keySet()) {
			Set <RoadSegment> segments = highlightedSegments.get(road);
			for (RoadSegment segment: segments) {
				segment.changeColor(new Color(0,0,0)); // change the color back to black
			}
		 }
		 highlightedSegments.clear(); // clear the map
		 }
		 
		 // for the text area, where the matching road info is given:
		 getTextOutputArea().setText("The following roads match your description: \n");
		 
		 // now find the road objects with the given name:
		 
		 for (Road roadObject: roadsToSegments.keySet()) {
			 
			 // if this roadObject is one of the roads that the user is looking for
			 // then get the set of segments and redraw them in a highlighted color
			 if (roadObject.name.equals(userInput)) {
				 // print the info of the relevant road object:
				 JTextArea roadInfo = getTextOutputArea();
				 roadInfo.append("RoadID: " + Integer.toString(roadObject.roadID) + " City: " + roadObject.city);
				 roadInfo.append("\n");
				 
				 Set <RoadSegment> segments = roadsToSegments.get(roadObject);
				 highlightedSegments.put(roadObject,segments); // mark the road and it's segments as highlighted
				 for (RoadSegment segmentObject: segments) {
					 segmentObject.changeColor(new Color(255,0,0));
				}
			}
		 }
}
	 
	
	// the onLoad method:
      public void onLoad(File nodes, File roads, File segments,
				File polygons) {
	  // read the appropriate road,node and segment files (Nodes and Roads before Segments):
	  collectionsObject.readAndStoreRoads(roads);
	  collectionsObject.readAndStroreNodes(nodes);  
	  collectionsObject.readAndStoreRoadSegments(segments);
	  // now that the files have been read, build the maps and initialize them here:
	  roadsToSegments = collectionsObject.buildRoadToSegmentsMap();
	  segmentsToNodes = collectionsObject.buildSegmentToNodesMap();
	  // create the graph:
	  Graph = collectionsObject.constructGraph();
      }
	 
	 
	public void onClick(MouseEvent e) {
		
		JTextArea roadInfo = getTextOutputArea();
		Integer xOnScreen = e.getX();
		Integer yOnScreen = e.getY();
	
		if (highlightedNode !=null)
			highlightedNode.changeColor(new Color (58,203,247));
		// search the vertices in the graph for the one that contains the right node
		// then get the edges (which contain the segments) for the matching vertex
		// from the edges, obtain the road ID of the segments and then the matching 
		// roads:
		
		// to avoid printing duplicate names I will use this set over here:
		Set <Integer> roadsAlreadyVisited = new HashSet <Integer> ();
		
		outerLoop:
		for (Vertex ver: Graph) {
			Node currentNode = ver.nodeObject;
			if (currentNode.nodeIsHere(xOnScreen,yOnScreen)) {
				currentNode.changeColor(new Color(0,255,0));
				highlightedNode = currentNode;
				getTextOutputArea().setText("The following roads are found at the intersection: \n");
				roadInfo.append("ID of intersection (node) : " + currentNode.ID + "\n");
				
			    Set <Edge> edges = ver.edges;
			    
			    
			    for (Edge edge: edges) {
			    	RoadSegment segment = edge.segmentObject;
			    	Integer roadID = segment.roadID;
			    	roadsAlreadyVisited.add(roadID);
			    	}
			    
			    for (Integer roadID: roadsAlreadyVisited){
					Road roadToPrint = collectionsObject.roadIDToRoads.get(roadID);
					roadInfo.append("Road ID : " + roadToPrint.roadID + " " + " Road Name: " + roadToPrint.name +
							" City: " + roadToPrint.city +"\n");
					}
			  
			   break outerLoop;
			}
			
		}
		
	}
	
	
    public static void main (String [] args) {
    	new DrawMap();
    }

}
