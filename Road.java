// a class to create road objects:

public class Road {

public int roadID;
public  int type;
public  String name;
public  String city;
public  int oneWay;
public  int speed;
public  int roadClass;
public  int notForCar;
public  int notForPede;
public  int notForBicy;

public Road(int id, int t, String n,String c,int o, int s,int r,int notC,int notP,int notB) {
	roadID = id;
	type = t;
	name = n;
	city = c;
	oneWay = o;
	speed = s;
	roadClass = r;
	notForCar = notC;
	notForPede = notP;
	notForBicy = notB;
}

}
