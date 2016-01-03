
public class Rufus {
	Point location; //4-space
	double viewStep = 0.01;
	double step = 0.1;
	double viewMatrix[][]; //4×3, [ forward right up ] (those three are column unit vectors)
	
	public Rufus(){
		this.location=new Point(0,0,0,0);
		this.viewMatrix = new double[][]{{1,0,0},{0,1,0},{0,0,1},{0,0,0}};

	}
	public Rufus(Point location, double viewMatrix[][]){
		this.location=location;
		this.viewMatrix = viewMatrix;
	}
	public void moveForwards(double numSteps){
		location.changeCoord(0,numSteps*step*viewMatrix[0][0]); //change x
		location.changeCoord(1,numSteps*step*viewMatrix[1][0]); //change y
		location.changeCoord(2,numSteps*step*viewMatrix[2][0]); //change z
		location.changeCoord(3,numSteps*step*viewMatrix[3][0]); //change w
	}
	public void moveSideways(double numSteps){
		location.changeCoord(0,numSteps*step*viewMatrix[0][1]); //change x
		location.changeCoord(1,numSteps*step*viewMatrix[1][1]); //change y
		location.changeCoord(2,numSteps*step*viewMatrix[2][1]); //change z
		location.changeCoord(3,numSteps*step*viewMatrix[3][1]); //change w
	}
	public void moveUpwards(double numSteps){
		location.changeCoord(0,numSteps*step*viewMatrix[0][2]); //change x
		location.changeCoord(1,numSteps*step*viewMatrix[1][2]); //change y
		location.changeCoord(2,numSteps*step*viewMatrix[2][2]); //change z
		location.changeCoord(3,numSteps*step*viewMatrix[3][2]); //change w
	}
	public void moveForwardsXY(double numSteps){ //Note: this and sidewaysXY only move in the xy plane
		double magnitude=Math.sqrt(viewMatrix[0][0]*viewMatrix[0][0]+viewMatrix[1][0]*viewMatrix[1][0]); //magnitude of forwards vector in xy-plane
		location.changeCoord(0,numSteps*step*viewMatrix[0][0]/magnitude); //change x
		location.changeCoord(1,numSteps*step*viewMatrix[1][0]/magnitude); //change y
	}
	public void moveSidewaysXY(double numSteps){
		double magnitude=Math.sqrt(viewMatrix[0][1]*viewMatrix[0][1]+viewMatrix[1][1]*viewMatrix[1][1]); //magnitude of right vector in xy-plane
		location.changeCoord(0,numSteps*step*viewMatrix[0][1]/magnitude); //change x
		location.changeCoord(1,numSteps*step*viewMatrix[1][1]/magnitude); //change y
	}
	public void stepZ(double numSteps){ //not "moving up" because "up" changes
		location.changeCoord(2,numSteps*step);
	}
	public void stepW(double numSteps){ //also not "moving oot" because there is no "oot" vector, and if there were it would pry change
		location.changeCoord(3,numSteps*step);
	}
	public void rotateForwardUpViewPlane(double numSteps){ //TODO: put cap so you can't reach perfectly straight up or down //rotate forward and up vectors about right vector
		this.viewMatrix=Matrix.multiply(viewMatrix, new double[][]{ 
			{Math.cos(numSteps*viewStep), 0,-Math.sin(numSteps*viewStep)},
			{0,							  1, 0},
			{Math.sin(numSteps*viewStep), 0, Math.cos(numSteps*viewStep)} 
			});
	}	
	public void rotateXYViewPlane(double numSteps){ //Rotate all 3 view vectors' xy components about the zw plane (4 dimensions are weeeeeird)
		this.viewMatrix=Matrix.multiply(new double[][]{ 
			{Math.cos(numSteps*viewStep),-Math.sin(numSteps*viewStep), 0, 0},
			{Math.sin(numSteps*viewStep), Math.cos(numSteps*viewStep), 0, 0}, 
			{0,							  0,						   1, 0},
			{0,							  0,						   0, 1}
			}, viewMatrix);
		
		//TODO: put a check in so that if it's getting too far from a unit vector or too far from ⟂, it fixes that
		
	}
	public void rotateXWViewPlane(double numSteps){ //Rotate all 3 view vectors' xw components about the yz plane
		this.viewMatrix=Matrix.multiply(new double[][]{ 
			{Math.cos(numSteps*viewStep), 0, 0, -Math.sin(numSteps*viewStep)},
			{0,							  1, 0, 0},
			{0,							  0, 1, 0},
			{Math.sin(numSteps*viewStep), 0, 0, Math.cos(numSteps*viewStep)} 
			}, viewMatrix);
	}
	public void rotateZWViewPlane(double numSteps){ //Rotate all 3 view vectors' zw components about the xy plane
		this.viewMatrix=Matrix.multiply(new double[][]{
			{1, 0, 0,						   0},
			{0,	1, 0,						   0},
			{0, 0, Math.cos(numSteps*viewStep),-Math.sin(numSteps*viewStep)},
			{0, 0, Math.sin(numSteps*viewStep), Math.cos(numSteps*viewStep)} 

			}, viewMatrix);
	}

}
