/* Code for COMP261 Assignment3
 * Name:difu chen
 * Usercode:chendifu
 * ID:300252166

 */

import java.awt.Color;
import java.util.Scanner;


public class Polygon {

	private Vector3D[] vertx = new Vector3D[3];
	private Color reflection;
	private Vector3D normal;
	private boolean toDraw;//boolean for only drawing the polygons that you can see
	private Transform t;	
	public static float intensity;
	
	//loading file and compute normal
	public Polygon(String line){		
		if(line != null){
		Scanner scan = new Scanner(line);
		//scan the x,y,z of the polygon and put it into the vertx
		if(scan.hasNextFloat()){
		for(int i =0; i < 3;  i++){
			vertx[i] = new Vector3D(scan.nextFloat(),scan.nextFloat(),scan.nextFloat()); 
		//	System.out.print(vertx[i]+"\n");
		}
		//scan the color of the reflection
		reflection = new Color(scan.nextInt(),scan.nextInt(),scan.nextInt());
		//System.out.println("colour:"+reflection);
		}
		scan.close();
		}	
		calNormal();
	}
	
	private void calNormal(){
		if(vertx[0]!=null){
		normal  =  ((vertx[1].minus(vertx[0])).crossProduct(vertx[2].minus(vertx[1]))).unitVector();
		
		/*only true if the shape is facing the user
		Mark all the polygons that are facing away from the viewer (ie, the z component of their 
		normal vector is +ve) as hidden.*/
		
		if(normal.z > 0){
			toDraw = false;			
		}else {
			toDraw = true;			
		}	
		}
	}
	
	//compute the color shading
	public Color shading(Vector3D lightSource, float ambientLight) {
		float reflect = ambientLight;
		if (normal.cosTheta(lightSource) > 0){
			reflect = ambientLight + normal.cosTheta(lightSource);
		}
		float intensityDepth =((normal.unitVector()).dotProduct(lightSource.unitVector()));
		
		//compute the new colour and store it to the colour variable
		int r = checkRange((int) (reflection.getRed() * (reflect+intensity*intensityDepth)));
		int g = checkRange((int) (reflection.getGreen() * (reflect+intensity*intensityDepth)));
		int b = checkRange((int) (reflection.getBlue() * (reflect+intensity*intensityDepth)));
		Color colour = new Color(r, g, b);
		return colour;	
	}		
	
	//check the range of the colour make sure its not over 255 or under 0
	private int checkRange(int x) {
		//System.out.println("Colour range being checked: " + x);
		if (x <= 0){
			x = 0;
		}
		else if (x >= 255){
			x = 255;
		}
		return x;
	}
	
	public void Xrotate(){
		for(int i =0; i<vertx.length  && vertx[i]!=null;i++){
			 t = Transform.newXRotation(ImageGUI.dx);
			vertx[i]  = t.multiply(vertx[i]);		
		}	
		calNormal();
	}
	
	public void Yrotate(){
		for(int i =0; i<vertx.length  && vertx[i]!=null;i++){
			 t = Transform.newYRotation(ImageGUI.dy);
			vertx[i] = t.multiply(vertx[i]);
		}
		calNormal();
	}
	
	
	public boolean getDraw(){
		return toDraw;		
	}
		
	public int getMinY(){
		return Math.round(Math.min(Math.min(vertx[0].y, vertx[1].y),vertx[2].y));
	}		

	public Vector3D[] getVertx(){
		return vertx;	
	}
	
	
	public void newCenter(float x, float y, float z){	
		Transform t = Transform.newTranslation(x,y,z);
		for(int i =0; i<vertx.length && vertx[i]!=null;i++){
			vertx[i] = t.multiply(vertx[i]);
		}
	}
	
	
	public void newSize(float x,float y, float z){
		Transform t = Transform.newScale(x, y, z);
		for(int i =0; i<vertx.length  && vertx[i]!=null ;i++){
			vertx[i] = t.multiply(vertx[i]);
		}
		calNormal();
	}	
		
	public EdgeList[] getEdgeList() {		
		EdgeList[] edge = new EdgeList[(int) Render.getHeight()+1];

		//go through each edge polygon 
		for(int i =0; i<3; i++){
			Vector3D va = vertx[i];//first vertex
			Vector3D vb = vertx[(i+1)%3];//other vertices
			//set the smallest y value 'va' always has the smallest value
			if(va.y > vb.y){
				va = vb;
				vb = vertx[i];	
			}
					
			float mx = (vb.x - va.x)/(vb.y - va.y);
			float mz = (vb.z - va.z)/(vb.y - va.y);
			float x = va.x;
			float z = va.z;
						
			int miny = Math.round(va.y- this.getMinY());
			int maxy = Math.round(vb.y - this.getMinY());			
			
			while(miny < maxy){
				if(edge[miny] == null){
					edge[miny] = new EdgeList(x, z);
				} 
				else{
					/*call the addRightEdges method from the edge list class which will compute and add the edges 
					(left edges or right edges)*/
					edge[miny].addRightEdges(x, z);
				}
				miny++;
				x += mx;
				z += mz;	
			}
		}			
		return edge;
	}


		
}
