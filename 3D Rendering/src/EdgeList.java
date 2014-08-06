/* Code for COMP261 Assignment3
 * Name:difu chen
 * Usercode:chendifu
 * ID:300252166

 */

public class EdgeList {

	
	//contains 4 values 0 is left x , 1 is left z, 2 and 3 are right x and z
	private float[] edges = new float[4];

	
	
	public EdgeList(float x, float z){	
		edges[0] = x;
		edges[1] = z;
	}	
	
	/*if x is smaller than the first values in edges that means this is the edges in the right hand side
	then add them into the right hand side and add the parameter x,z to the left hand side*/
	public void addRightEdges(float x, float z){
		if(x < edges[0]){
			edges[2] = edges[0];
			edges[3] = edges[1];
			edges[0] = x;
			edges[1] = z;
		}
		else{
			edges[2] = x;
			edges[3] = z;
		}	
	}
	
	public float getLeftX() {
		return edges[0];
	}

	public float getLeftZ() {
		return edges[1];
	}

	public float getRightX() {
		return edges[2];
	}

	public float getRightZ() {
		return edges[3];
	}
	
	
	
}
