import java.awt.Color;
import java.util.ArrayList;

public class Polygon {
	ArrayList<Point> points;
	Color color;
	public Polygon(Color color){
		this.points = new ArrayList<Point>(0);
		this.color=color;
	}
	public Polygon(ArrayList<Point> points){
		this.points = points;
		this.color=new Color((int)Math.random()*255,(int)Math.random()*255,(int)Math.random()*255);
	}
	public void clipPoints(double fov, int windowWidth, int windowHeight){
		for(int i=0; i<points.size(); i++){
			Point oldPt = points.get(i);
			if(oldPt.x<=0 || Math.abs(fov*oldPt.y/oldPt.x)>windowWidth/2 || Math.abs(fov*oldPt.z/oldPt.x)>windowHeight/2){ //if point will be off screen, replace it with 2 points on screen edge, or delete if neighbors are off-screen too
				Point prevPt = points.get((i-1+points.size())%points.size());
				Point nextPt = points.get((i+1)%points.size());
				boolean prevOff = Math.abs(fov*prevPt.y/prevPt.x)>windowWidth/2 || Math.abs(fov*prevPt.z/prevPt.x)>windowHeight/2;
				boolean nextOff = Math.abs(fov*nextPt.y/nextPt.x)>windowWidth/2 || Math.abs(fov*nextPt.z/nextPt.x)>windowHeight/2;
				if(prevOff && nextOff){ //if this point and both neighbors are off the screen, kill this point
					points.remove(i);
					i--;
				} else {
					if(!prevOff){
						
					}
					if(!nextOff){
						
					}
				}
				
			} else {
				points.set(i, new Point((fov*oldPt.y/Math.abs(oldPt.x)), (fov*oldPt.z/Math.abs(oldPt.x)), oldPt.x)); //TODO: the abs(x) works but only doing the end points isn't quite right, maybe only get screencoords later?
			}
		}
	}
	public void convertToScreenCoords(double fov){ //make the vertices of the polygon (which have normal coords) into screen coords (fov*y/x, fov*z/x) with depth (x) data 
		for(int i=0; i<points.size(); i++){
			Point oldPt = points.get(i);
			//points.set(i, new Point((fov*oldPt.y/Math.abs(oldPt.x)), (fov*oldPt.z/Math.abs(oldPt.x)), oldPt.x)); //TODO: the abs(x) works but only doing the end points isn't quite right, maybe only get screencoords later?
			points.set(i, new Point(fov*Math.atan(oldPt.y/oldPt.x), fov*Math.atan(oldPt.z/oldPt.x), oldPt.x)); //TODO: the abs(x) works but only doing the end points isn't quite right, maybe only get screencoords later?
		}
	}
	public void orderPoints(){
		Point center = this.getXYCenter();
		for(int sortCount=1; sortCount<this.points.size(); sortCount++){ //insertion sort points by clockwise order //TODO: optimize?
			Point hold=this.points.get(sortCount);						 //so that they make a nice convex polygon
			int backCount=sortCount;
			while(backCount>0 && this.points.get(backCount-1).isClockwiseOf(hold, center)){
				this.points.set(backCount, this.points.get(backCount-1));
				backCount--;
			}
			this.points.set(backCount, hold);
		}
	}
	public Point getXYCenter(){
		double xTotal=0;
		double yTotal=0;
		for(Point point:this.points){
			xTotal+=point.x;
			yTotal+=point.y;
		}
		return new Point(xTotal/this.points.size(), yTotal/this.points.size());
	}
	public void orderPointsYZ(){
		Point center = this.getYZCenter();
		for(int sortCount=1; sortCount<this.points.size(); sortCount++){ //insertion sort points by clockwise order //TODO: optimize?
			Point hold=this.points.get(sortCount);						 //so that they make a nice convex polygon
			int backCount=sortCount;
			while(backCount>0 && this.points.get(backCount-1).isYZClockwiseOf(hold, center)){
				this.points.set(backCount, this.points.get(backCount-1));
				backCount--;
			}
			this.points.set(backCount, hold);
		}
	}
	public Point getYZCenter(){
		double yTotal=0;
		double zTotal=0;
		for(Point point:this.points){
			yTotal+=point.y;
			zTotal+=point.z;
		}
		return new Point(0, yTotal/this.points.size(), zTotal/this.points.size());
	}
	public double[][] getStripes(){
		double[][] listOfStripes = new double[(int)getYRange()][4]; //the first index + minY = yCoord, the list at each index is {x0, z0, x1, z1} where (x0, y, z0) and (x1, y, z1) are the endpoints of the stripe
		boolean[] isInitialized=new boolean[listOfStripes.length]; //used to determine if first point has been added yet
		double minY = getMinY();
		for(int pointIndex=0; pointIndex<points.size(); pointIndex++){
			Point p0=points.get(pointIndex);
			Point p1=points.get((pointIndex+1)%points.size());
			if(p0.y>p1.y){
				p0=p1;
				p1=points.get(pointIndex);
			}
			double x=p0.x;
			double z=p0.z;
			double dx=(p1.x-p0.x)/(p1.y-p0.y);
			double dz=(p1.z-p0.z)/(p1.y-p0.y);
			if(p0.y<minY){
				x+=(minY-p0.y)*dx;
				z+=(minY-p0.y)*dz;
				p0=new Point(p0.x, minY, p0.z);
			}
			for(int y=(int)(p0.y-minY); y<listOfStripes.length && y<(int)(p1.y-minY); y++){
				if(!isInitialized[y]){
					listOfStripes[y][0]=x;
					listOfStripes[y][1]=z;
					isInitialized[y]=true;
				} else {
					listOfStripes[y][2]=x;
					listOfStripes[y][3]=z;
				}
				x+=dx;
				z+=dz;
			}
		}
		if(Lumberjack.checkStripeInitialization){
			for(int i=0; i<isInitialized.length; i++){
				if(!isInitialized[i]){
					System.out.println("item "+i+" not initialized, minY="+minY);
				}
				if(listOfStripes[i][3]==0 && listOfStripes[i][4]==0 && listOfStripes[i][5]==0){ 
					System.out.println("item "+i+" suspicious (0,0)");
				}
			}
		}
		return listOfStripes;
	}
	
	public void addPoint(Point p){
		points.add(p);
	}
	public int numPoints(){
		return points.size();
	}
	private double getYRange(){
		double minY=points.get(0).y;
		double maxY=minY;
		for(int i=1; i<points.size(); i++){
			double pY=points.get(i).y;
			if(pY<minY){ 
				minY = pY;
			} else if(pY>maxY){ 
				maxY = pY;
			}
		}
		if(minY<=-TheLittleEngineThatCould.windowHeight/2){
			minY=-TheLittleEngineThatCould.windowHeight/2+1;
		} else if(minY>TheLittleEngineThatCould.windowHeight/2){
			minY=TheLittleEngineThatCould.windowHeight/2;
		}
		if(maxY<=-TheLittleEngineThatCould.windowHeight/2){
			maxY=-TheLittleEngineThatCould.windowHeight/2+1;
		} else if(maxY>TheLittleEngineThatCould.windowHeight/2){
			maxY=TheLittleEngineThatCould.windowHeight/2;
		}
		if((int)(maxY-minY)<0){
			System.err.println("Negative Y Range: minY:"+minY+", maxY:"+maxY);
		}
		return maxY-minY;
	}
	public double getMinY(){
		double minY=points.get(0).y;
		for(int i=1; i<points.size(); i++){
			double pY=points.get(i).y;
			if(pY<minY){
				minY = pY;
			}
		}
		if(minY<=-TheLittleEngineThatCould.windowHeight/2){
			return -TheLittleEngineThatCould.windowHeight/2+1;
		}
		return minY;
	}
	/*public double[][] getStripes(){
		double[][] listOfStripes = new double[(int)getYRange()][4]; //the first index + minY = yCoord, the list at each index is {x0, z0, x1, z1} where (x0, y, z0) and (x1, y, z1) are the endpoints of the stripe
		boolean[] isInitialized=new boolean[listOfStripes.length]; //used to determine if first point has been added yet
		double minY = getMinY();
		for(int pointIndex=0; pointIndex<points.size(); pointIndex++){
			Point p0=points.get(pointIndex);
			Point p1=points.get((pointIndex+1)%points.size());
			if(p0.y>p1.y){
				p0=p1;
				p1=points.get(pointIndex);
			}
			double x=p0.x;
			double z=p0.z;
			double dx=(p1.x-p0.x)/(p1.y-p0.y);
			double dz=(p1.z-p0.z)/(p1.y-p0.y);
			for(int y=(int)(p0.y-minY); y<(int)(p1.y-minY); y++){
				if(!isInitialized[y]){
					listOfStripes[y][0]=x;
					listOfStripes[y][1]=z;
					isInitialized[y]=true;
				} else {
					listOfStripes[y][2]=x;
					listOfStripes[y][3]=z;
				}
				x+=dx;
				z+=dz;
			}
		}
		return listOfStripes;
		
	}
	
	
	
	public void addPoint(Point p){
		points.add(p);
	}
	public int numPoints(){
		return points.size();
	}
	private double getYRange(){
		double minY=points.get(0).y;
		double maxY=minY;
		for(int i=1; i<points.size(); i++){
			double pY=points.get(i).y;
			if(pY<minY){ 
				minY = pY;
			} else if(pY>maxY){ 
				maxY = pY;
			}
		}
		return maxY-minY;
	}
	public double getMinY(){
		double minY=points.get(0).y;
		for(int i=1; i<points.size(); i++){
			double pY=points.get(i).y;
			if(pY<minY){ 
				minY = pY;
			}
		}
		return minY;
	}*/
	
	public Color getColor(){
		return this.color;
	}
	public void setColor(Color c){
		this.color=c;
	}
	public String toString(){
		String ret="";
		for(Point p:this.points){
			ret+=p.toString()+",";
		}
		return ret;
	}
}
