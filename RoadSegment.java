import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

// Class to create RoadSegment objects:

public class RoadSegment {
	// I will make the fields public to make testing easier:

	public int roadID; // the ID of the road object this segment belongs to
	public double length;
	public int NodeID1; // the node ID's that this segment is in between.
	public int NodeID2;
	public List <Double> coordinates;
	public Color color = new Color (0,0,0); // default color is black


	// constructor:

	public RoadSegment (int rdID,double Length,int ID1,int ID2,ArrayList<Double> C) {
		roadID = rdID;
		length = Length;
		NodeID1 = ID1;
		NodeID2 = ID2;
		coordinates = C;
	}

	// for changing the color that the road segments are drawn in:
	// when they need to be highlighted:
	public void changeColor (Color c) {
		color = c;
	}


	// code for drawing the segment object:
	public void drawSegment (Graphics g,double deltaX, double deltaY,double zoom) {
		// set the appropriate color:
		g.setColor(color);

		// the center from which all the pixels are drawn:
		Location center = new Location (-170 + deltaX, 170 + deltaY);
		// build a list of all the Point objects from the list of coordinates:
		List <Point> pointList = new ArrayList <Point> ();
		int i = 0;
		while (i < coordinates.size()) {
			// make a location object from the first two coordinates (in km):
			Location locObject = Location.newFromLatLon(coordinates.get(i),coordinates.get(i+1));
			// turn that locObject into a point Object (relative to the center):
			Point pointObject = locObject.asPoint(center, 1.5 + zoom);
			// add the point object to the list:
			pointList.add(pointObject);
			i = i + 2;
			if (i == coordinates.size()) {break;}

		}
		// now iterate over the point List and draw the Lines:
		for (int j = 0; j < pointList.size() - 1;j++) {
			// obtain the two points for which the line is between:
			Point p1 = pointList.get(j);
			Point p2 = pointList.get(j+1);

			// get the individual (x,y) coordinates for the points:
			Integer x1 = (int) p1.getX();
			Integer y1 = (int) p1.getY();
			Integer x2 = (int) p2.getX();
			Integer y2 = (int) p2.getY();

			g.drawLine(x1, y1, x2, y2);

		}

	}



}
