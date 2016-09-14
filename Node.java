import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

// Nodes will have an ID,latitude and longitude (in degrees not KM):

public class Node {

	public int ID;
	public double latitude;
	public double longitude;
	// fields for drawing the Node object;
	public Color color = new Color (58,203,247);
	public final int SIZE = 5;
	// the x and y locations (Point location) for this node:
	public double xPoint;
	public double yPoint;

	// constructor:
	public Node (int id,double lat,double lng) {
		ID = id;
		latitude = lat;
		longitude = lng;
	}

	public void changeColor (Color c) {
		color = c;
	}

	// code for drawing the Node object:
	// Note:
	// to move the map further north/south I need to change the y parameter of the center object
	// for east and west I need to change the x parameter
	public void drawNode (Graphics g,double deltaX,double deltaY, double zoom) {
		g.setColor(color);
		// center Location object:
		Location center = new Location (-170 + deltaX,170 + deltaY);

		// the location object with latitude/longitude values in km!
		Location newLocObject = Location.newFromLatLon(latitude,longitude);
		// this will be the point Object (pixel representation) for the Node:
		Point pointObject = newLocObject.asPoint(center,1.5 + zoom);

		xPoint = pointObject.getX();
		yPoint = pointObject.getY();	
		Integer xCoordinate = (int) (pointObject.getX());
		Integer yCoordinate = (int) (pointObject.getY());

		// with the x and y pixel coordinates of the Node, I can draw the node:
		g.fillOval(xCoordinate, yCoordinate,SIZE,SIZE); // I will need to see if width and height are ok later
	}

	// return true if: these x and y coordinates match the point location of this Node:
	public boolean nodeIsHere (int x, int y) {
		if ((x > xPoint && x < xPoint + 2*SIZE) && (y > yPoint && y < yPoint + 2*SIZE)) 
			return true;
		else 
			return false;
	}

}
