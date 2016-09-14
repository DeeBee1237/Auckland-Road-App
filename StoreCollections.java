import java.io.*;
import java.util.*;

public class StoreCollections {


	public Set<Node> nodeSet ; // the set of Node Objects
	public List <Road> roadList; // the List of Road Objects
	public List <RoadSegment> roadSegmentList; // the List of RoadSegment Objects

	public Map <Integer,Set<RoadSegment>> idToSegments; 
	public Map <Integer,Node> nodeIDToNodes; 
	public Map <Integer,Road> roadIDToRoads; // I use this to get from segment to road in the graph

	//IMPORTANT Data collections:
	private Map <Road,Set<RoadSegment>> roadToSegments; // Maps roads to their respective segments
	private Map <RoadSegment,List<Node>> segmentToNodes; // Maps segments to their respective node pairs
	// THE GRAPH WHERE NODES ARE VERTICES AND SEGMENTS ARE EDGES:
	private Map <Node,Vertex> buildGraphMap; 
	private Set <Vertex> Graph;

	// constructor:
	public StoreCollections () {
		nodeSet = new HashSet <Node> ();
		roadSegmentList = new ArrayList <RoadSegment> ();
		roadList = new ArrayList <Road> ();
		idToSegments = new HashMap <Integer,Set<RoadSegment>> ();
		nodeIDToNodes = new HashMap <Integer,Node> ();
		roadIDToRoads  = new HashMap <Integer,Road> ();

		roadToSegments = new HashMap <Road,Set<RoadSegment>> ();
		segmentToNodes = new HashMap <RoadSegment,List<Node>> ();

		buildGraphMap = new HashMap <Node,Vertex> ();
		Graph = new HashSet <Vertex> ();

	}
	/*
	 * LOAD AND STORE (BUILDING OBJECT COLLECTIONS) METHODS ARE FOUND BELOW HERE:
	 * */

	// method to read and store the Node objects in the nodeSet:
	public void readAndStroreNodes (File nodeFile) {
		try {

			FileReader fileReader = new FileReader (nodeFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);


			String currentLine = bufferedReader.readLine();

			while(currentLine!=null) {

				// read the data from the current line, form and store the object. 
				String stringSpliter [] = currentLine.split("	",3);

				Integer nodeID = Integer.parseInt(stringSpliter[0]);
				Double latitude = Double.parseDouble(stringSpliter[1]);
				Double longitude = Double.parseDouble(stringSpliter[2]);

				Node nodeObject = new Node (nodeID,latitude,longitude);

				// add the node object to the nodeSet:
				nodeSet.add(nodeObject);

				// add the nodeID and the object to the map:
				nodeIDToNodes.put(nodeID,nodeObject);

				// change the reference of 'currentLine' to the next line:
				currentLine = bufferedReader.readLine();
			}
			// close the bufferedReader:
			bufferedReader.close();

		}

		catch (FileNotFoundException e) {
			System.out.println("The file could not be found!" + e);
		}

		catch (IOException e) {
			System.out.println("IO error has occured!" + e);
		}

	}

	// a method to read and store the Road objects into the road Set:
	public void readAndStoreRoads (File roadFile) {
		try  {

			FileReader fileReader = new FileReader (roadFile);
			BufferedReader bufferedReader = new BufferedReader (fileReader);

			bufferedReader.readLine();
			String currentLine = bufferedReader.readLine(); // skip the first line in the text file
			while (currentLine!=null) {
				// read the data from the current line, form and store the road object:
				String stringSpliter [] = currentLine.split("	",10);

				Integer roadID = Integer.parseInt(stringSpliter[0]);
				Integer type = Integer.parseInt(stringSpliter[1]);
				String name = stringSpliter[2];
				String city = stringSpliter[3];
				Integer oneWay = Integer.parseInt(stringSpliter[4]);
				Integer speed = Integer.parseInt(stringSpliter[5]);
				Integer roadClass = Integer.parseInt(stringSpliter[6]);
				Integer notCar = Integer.parseInt(stringSpliter[7]);
				Integer notPed = Integer.parseInt(stringSpliter[8]);
				Integer notByc = Integer.parseInt(stringSpliter[9]);


				Road roadObject = new Road(roadID,type,name,city,oneWay,speed,roadClass,notCar
						,notPed,notByc);

				// add the road object to the roadSet:
				roadList.add(roadObject);

				// add to the map of roadID -> Roads:
				//45798,45935,46072 and 46053.
				roadIDToRoads.put(roadID,roadObject);

				// change the reference of 'currentLine' to the next line:
				currentLine = bufferedReader.readLine();

			}
			// close the bufferedReader:
			bufferedReader.close();
		}

		catch (FileNotFoundException e) {
			System.out.println("The file could not be found!" + e);
		}

		catch (IOException e) {
			System.out.println("IO error has occured!" + e);
		}

	}

	// a method to read and store the RoadSegment objects in the RoadSegmentSet:

	public void readAndStoreRoadSegments (File segmentFile) {
		try {

			FileReader fileReader = new FileReader (segmentFile);
			BufferedReader bufferedReader = new BufferedReader (fileReader);

			bufferedReader.readLine(); // skip the first line of the text file
			String currentLine = bufferedReader.readLine();
			while (currentLine!=null) {
				// read the data from the text file, then form and store the object:
				String [] stringSpliter = currentLine.split("	",5);

				Integer roadID = Integer.parseInt(stringSpliter[0]);
				Double length = Double.parseDouble(stringSpliter[1]);
				Integer nodeID1 = Integer.parseInt(stringSpliter[2]);
				Integer nodeID2 = Integer.parseInt(stringSpliter[3]);
				String coordinates = stringSpliter[4]; // long ass line of coordinates!
				// create a string splitter to split the coordinates string:
				String [] stringSpliter2 = coordinates.split("	");
				ArrayList <Double> coordinateList  = new ArrayList <Double> ();
				for (int i = 0; i < stringSpliter2.length; i ++)
					coordinateList.add(Double.parseDouble(stringSpliter2[i]));

				// create the RoadSegmentObject:
				RoadSegment segmentObject = new RoadSegment(roadID,length,nodeID1,nodeID2,coordinateList);
				// add the RoadSegment Object to the roadSegment set:
				roadSegmentList.add(segmentObject);

				// put it in the idToSegments map:
				if (idToSegments.containsKey(roadID)) {
					Set<RoadSegment> thisSet = idToSegments.get(roadID);
					thisSet.add(segmentObject);
					idToSegments.put(roadID,thisSet); // update the map 
				}

				else {
					Set <RoadSegment> toAdd = new HashSet <RoadSegment> ();
					toAdd.add(segmentObject);
					idToSegments.put(roadID,toAdd);
				}

				// change the reference of 'currentLine':
				currentLine = bufferedReader.readLine();
			}
			bufferedReader.close();
		}

		catch (FileNotFoundException e) {
			System.out.println("The file could not be found!" + e);
		}

		catch (IOException e) {
			System.out.println("IO error has occured!" + e);
		}
	}

	/*
	 * METHODS TO CONSTRUCT THE TWO IMPORTANT MAPS (AND GRAPH) AND RETURN THEM ARE HERE:
	 */

	// method to build the road to set of segments Map:
	public Map <Road,Set<RoadSegment>> buildRoadToSegmentsMap () {


		for (int i = 0; i < roadList.size();i++) {
			Integer roadID = roadList.get(i).roadID;

			Set<RoadSegment> segSet = idToSegments.get(roadID);
			roadToSegments.put(roadList.get(i),segSet);
		}
		return roadToSegments;
	}


	// method to build the segmentToNodes Map:
	public Map <RoadSegment,List<Node>> buildSegmentToNodesMap () {

		for (int i = 0; i < roadSegmentList.size();i++) {
			RoadSegment segObject = roadSegmentList.get(i);
			Integer id1 = segObject.NodeID1;
			Integer id2 = segObject.NodeID2;
			List <Node> currentList = new ArrayList <Node> ();

			currentList.add(nodeIDToNodes.get(id1));
			currentList.add(nodeIDToNodes.get(id2));

			segmentToNodes.put(segObject,currentList);
		}
		return segmentToNodes;
	}

	// methods to construct the graph:

	public Set <Vertex> constructGraph () {
		for (Road roadObject: roadToSegments.keySet()) {
			Set <RoadSegment> roadSegments = roadToSegments.get(roadObject);
			if (roadSegments == null) {continue;} // I am working with faulty data
			for (RoadSegment segmentObject: roadSegments) {

				List<Node> nodePair = segmentToNodes.get(segmentObject);
				Node node1 = nodePair.get(0);
				Node node2 = nodePair.get(1);

				// create the vertex objects for those nodes:
				Vertex v1 = new Vertex (node1); 
				Vertex v2 = new Vertex (node2);
				//if any of these two nodes are already in the 'buildGraphMap'
				// get their vertex objects and modify those ones:

				if (buildGraphMap.containsKey(node1)) {
					Vertex vertexForNode1 = buildGraphMap.get(node1);
					vertexForNode1.addEdge(new Edge(v2,segmentObject));
					buildGraphMap.put(node1,vertexForNode1); 
				}
				if (buildGraphMap.containsKey(node2)) {
					Vertex vertexForNode2 = buildGraphMap.get(node2);
					vertexForNode2.addEdge(new Edge(v1,segmentObject));
					buildGraphMap.put(node2,vertexForNode2);
				}


				// otherwise create new vertex objects for the two nodes:

				// add the two nodes as vertices:
				if (!buildGraphMap.containsKey(node1))	{
					v1.addEdge(new Edge(v2,segmentObject)); // connect 1 to 2
					buildGraphMap.put(node1, v1);
				}

				if (!buildGraphMap.containsKey(node2)) {
					v2.addEdge(new Edge(v1,segmentObject)); // connect 2 to 1
					buildGraphMap.put(node2,v2);
				}
			}
		}

		// now put all the values from the buildGraphMap into the set of vertices
		// the actual graph:
		for (Node nodeObject: buildGraphMap.keySet()) {
			Vertex vertexObject = buildGraphMap.get(nodeObject);
			Graph.add(vertexObject);
		}

		return Graph;
	}


	// main method
	public static void main (String [] args) {
		new StoreCollections();
	}
}
