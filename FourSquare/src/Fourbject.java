import java.util.ArrayList;

public class Fourbject {
	Point points[]; //all points in 4bject
	int edges[][]; //n×2, pairs of indices of points in points[]
	Threebject threebjects[]; //set of 3D "faces" of 4bject
	
	public Fourbject(Point points[], int edges[][], Threebject threebjects[]){
		this.points = points;
		this.edges = edges;
		this.threebjects = threebjects;
	}
	public Fourbject(){
		
	}
	public void setPoints(Point points[]){
		this.points = points;
	}
	public void setEdges(int edges[][]){
		this.edges = edges;
	}
	public void setThreebjects(Threebject threebjects[]){
		this.threebjects = threebjects;
	}
	
	public ArrayList<ArrayList<Point>> draw(double viewMatrix[][]){
		Point intercepts[] = new Point[edges.length];
		Point p0,p1;
		for(int i=0; i<edges.length; i++){ //calculate intercepts with all edges //TODO: optimize?
			p0=points[edges[i][0]];
			p1=points[edges[i][1]];
			double[] delta={p1.x-p0.x, p1.y-p0.y, p1.z-p0.z, p1.w-p0.w};
			int v=0; //index of {x,y,z,w} that you use to solve for t
			double dv = delta[0]<=1 ? delta[0] : 1/delta[0]; //temp var
			for(int d=1; d<4 /*delta.length*/; d++){ //make v the index of the item in delta[] closest to 1
				if( (delta[d]<=1 ? delta[d] : 1/delta[d])>dv ){
					dv = delta[d]<=1 ? delta[d] : 1/delta[d];
					v = d;
				}
			}
			double A[][] = new double[3][4];
			double b[][] = new double[3][1];
			//TODO: fill A,b with 0?
			int row=0;
			for(int j=0; j<4 /*delta.length*/; j++){
				if(j==v){
					continue;
				}
				A[row][j]=delta[v];
				A[row][v]=-delta[j];
				b[row][0]=p0.getCoord(j)*delta[v]-p0.getCoord(v)*delta[j];
				row++;
			}
			double mIntercept[][] = Matrix.rref(Matrix.augment(Matrix.multiply(A, viewMatrix),b)); //rref(A*V  b)
			if(mIntercept[0][0]==1 && mIntercept[1][1]==1 && mIntercept[2][2]==1){
				/*System.out.println("yay! normal matrix:");
				for(int a=0; a<mIntercept.length; a++){
					for(int c=0; c<mIntercept[0].length; c++){
						System.out.print(mIntercept[a][c]+" ");
					}
					System.out.println();
				}*/
				intercepts[i]=new Point(mIntercept[0][3],mIntercept[1][3],mIntercept[2][3]); //also need to make sure that it's in bounds
			} else {
				/*System.out.println("uh oh! weird matrix:");
				for(int a=0; a<mIntercept.length; a++){
					for(int c=0; c<mIntercept[0].length; c++){
						System.out.print(mIntercept[a][c]+" ");
					}
					System.out.println();
				}*/
			}
		}
		/*System.out.print("intercepts: {");
		for(int i=0; i<intercepts.length; i++){
			if(intercepts[i]!=null)
				System.out.print(intercepts[i]+",");
			else
				System.out.print("{},");
		}
		System.out.println("}");*/
		
		//System.out.println(threebjects);
		ArrayList <ArrayList<Point>> pgons=new ArrayList <ArrayList<Point>> (0);
		for(Threebject t : threebjects){
			//System.out.println("tbj:"+t);
			ArrayList<Point> tbjPolygon = t.draw(intercepts);
			//System.out.println("tbjPolygon:"+tbjPolygon);
			if(tbjPolygon!=null){
				pgons.add(tbjPolygon);
			}
		}
		//System.out.println("pgons:"+pgons);
		return pgons;
	}

}