/* Code for COMP261 Assignment3
 * Name:difu chen
 * Usercode:chendifu
 * ID:300252166

 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Render {

	private ArrayList<Polygon> polygon = new  ArrayList<Polygon>();
	private BufferedReader reader;
	private Vector3D lightSource ;
	public static float ambientLight ;
	private final int INFINITY = Integer.MAX_VALUE;
	private BufferedImage image;
	private int[][] zBuffer;
    private Color[][] screen;
    private float width;
	private static float height ;
	
	public Render(){		
		renderPipeLine();//load files
		draw(); //convert the colour to an image
		
	}
	
	private void renderPipeLine(){	
		String fName = ImageGUI.fileName();
		if(fName !=null){
		try {
			reader = new BufferedReader(new FileReader(new File(fName)));
			String line = reader.readLine();
			//String[] values = line.split("\t");			
			Scanner sc = new Scanner(line);
			if(sc.hasNextFloat()){
			//read the light source
			lightSource = new Vector3D(sc.nextFloat(), sc.nextFloat(),sc.nextFloat());	
			//System.out.println(lightSource);
			}
			sc.close();
			//read the polygon and store it in the array
			while((line = reader.readLine()) !=null){
				polygon.add(new Polygon(line));
			}
			
			reader.close();
		 } catch(IOException e){System.out.println("Failed to open file" + e);}	
		}
		//Declaring the two z-buffer
		zBuffer = new int[ImageGUI.screenWidth][ImageGUI.screenHeight];
		screen = new Color[ImageGUI.screenWidth][ImageGUI.screenHeight];
	}
	
	public void setUpZbuffer(){
		for (int i = 0; i < ImageGUI.screenWidth; i++) {
			for (int j = 0; j <ImageGUI.screenHeight; j++) {
				screen[i][j] = Color.gray;//set all the pixel of the window the color gray
				zBuffer[i][j] = INFINITY;//set all the pixel to Infinity
			}
		}
	}
	
	
	/*
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	public  BufferedImage convertToImage(Color[][] bitmap) {
		image = new BufferedImage(ImageGUI.screenWidth, ImageGUI.screenHeight,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < ImageGUI.screenWidth; x++) {
			for (int y = 0; y < ImageGUI.screenHeight; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}
	
	public void setBounds(){	
		
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;
		
		  for (Polygon p : polygon) {
		   for (Vector3D v : p.getVertx()) {	
			   if(v!=null){
				   minX = Math.min(minX, v.x);
				   minY = Math.min(minY, v.y);
				   maxX = Math.max(maxX, v.x);
				   maxY = Math.max(maxY, v.y);
			   }
		   }
		  }
		  
		width = maxX-minX;
		height = maxY-minY;
		//System.out.println("minX: "+minX+" minY: "+minY +" maxX  :"+ maxX+" maxY: "+maxY);
		//System.out.println(width+":" +height);
		float screenWidthCenter = ImageGUI.screenWidth/2;
		float screenHeightCenter = ImageGUI.screenHeight/2;
		
		float imageCenterX = (minX + maxX) / 2;
		float imageCenterY = (minY + maxY) / 2;	
		//System.out.println(imageCenterX+":"+imageCenterY);
		
		float diffX=0;
		float diffY=0;
		
		if(minX<0){
			diffX = -minX;
		}
		
		if(minY<0){
			diffY = -minY;			
		}
		//if minx or min y is samller than 0 translate them to positive
		for(Polygon poly:polygon){
			poly.newCenter(diffX,diffY,1.0f);
		}
		//make the image smaller if the width or the height are bigger than the frame
		if(width>ImageGUI.screenWidth || height>ImageGUI.screenHeight){
			float size ;
			if(width>height){			
				size = ImageGUI.screenWidth/width-0.1f;
			}
			else{
				size = ImageGUI.screenHeight/height-0.1f;
			}
			//System.out.println(size);
			for(Polygon p:polygon ){
				p.newSize(size,size,size);	
			}
		}else{

		float movedX = screenWidthCenter - imageCenterX;
		float movedY = screenHeightCenter - imageCenterY;
		//center the image
		for(Polygon p:polygon){
			p.newCenter(movedX,movedY,1.0f);
		}
		}
		
	}	
	
	public void rotate (){
		for(Polygon p: polygon){
			p.Xrotate();
			p.Yrotate();
		}
		setBounds();
	}	
	
	public static float getHeight() {
		return height;
	}
	
	public BufferedImage draw(){
		rotate();
		setBounds();
		setUpZbuffer();
		Iterator<Polygon> poly = polygon.iterator();
		
		while (poly.hasNext()) {
			Polygon p = poly.next();
			//only draws the polygons that are facing the user
			if (p.getDraw()) {
				EdgeList[] edgelist = p.getEdgeList();
				//lightSource = intensity();
				Color c = p.shading(lightSource, ambientLight);//compute the shading
				int minY = p.getMinY();	
				
				for (int i = 0; i < edgelist.length-1 && edgelist[i]!=null ; i++) {
					int y = minY+i;//go down each row
					//System.out.println(y);
					//System.out.println(edgelist[i].getLeftX());
					int x = Math.round(edgelist[i].getLeftX());
					int z = Math.round(edgelist[i].getLeftZ());

					int mz = Math.round((edgelist[i].getRightZ() - edgelist[i].getLeftZ())
							/ (edgelist[i].getRightX() - edgelist[i].getLeftX()));

					while (x <= Math.round(edgelist[i].getRightX())) {
						//System.out.println("left x: "+ x +"right x :"+Math.round(edgelist[i].getRightX()));
						if (z < zBuffer[x][y]) {
							zBuffer[x][y] = z;
							screen[x][y] = c;
						}
						x++;
						z += mz;
					}
				}
			}			
		}
		return convertToImage(screen);
	}
	

	public void drawImage(Graphics g, BufferedImage image){		
		if(image !=null){			
		g.drawImage(image,0, 0, null);
		}
	}
	
		
}
