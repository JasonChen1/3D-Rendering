/* Code for COMP261 Assignment
 * Name:difu chen
 * Usercode:chendifu
 * ID:300252166

 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;


public class ImageGUI {

    public static int screenWidth = 800;
    public static int screenHeight = 800;
    private JComponent drawing;
    private static JFrame frame;
    private BufferedImage image;
	private Render render ; 
	private static JSlider ambientLightSlider = new JSlider(JSlider.HORIZONTAL,-10,20,5);
	private static JSlider intensity = new JSlider(JSlider.HORIZONTAL,-10,20,0);
	private static JFileChooser chooser;
	private int pressedX;
	private int pressedY;
	private int releasedX ;
	private int releasedY ;
	public static float dx;
	public static float dy;
	
	public ImageGUI(){	
		ambientLight();	
		setUpInterface();
		render = new Render();
		drawing.repaint();
	}
	
	
	@SuppressWarnings("serial")
	private void setUpInterface(){
    /** Creates a frame with a JComponent in it.
     *  Clicking in the frame will close it. */
    
	frame = new JFrame("3D Rendering");
	frame.setSize(screenWidth, screenHeight);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	drawing = new JComponent(){
		protected void paintComponent(Graphics g){
			if(render!=null){
			image = render.draw();
			if(image !=null){
			render.drawImage(g, image);	
			}
			}
			
		}
	 };
	
	 drawing.addMouseMotionListener(new MouseAdapter() {
		
		 /**
		  * Records where the mouse was pressed
		  * @param e MouseEvent
		  */
		 public void mousePressed(MouseEvent e) {
			 pressedX = e.getX();
			 pressedY = e.getY();
			 releasedX = e.getX();
			 releasedY = e.getY();
		 }

		 /**
		  * Records where the mouse was released
		  * @param e MouseEvent
		  */
		 public void mouseReleased(MouseEvent e) {
			 pressedX = e.getX();
			 pressedY = e.getY();
			 releasedX  = e.getX();
			 releasedY  = e.getY();
		 }

		 /**
		  * Rotates the image by the distance the mouse has been dragged
		  * @param e MouseEvent
		  */
		 public void mouseDragged(MouseEvent e) {
			releasedX  = e.getX();
			releasedY  = e.getY();
		  double diffX = pressedX -  releasedX ;
		  double diffY = pressedY -  releasedY ;
		 
		  dx=(float) (diffY / 10);
		  dy=(float) (diffX / 10);
		  
		  pressedX = e.getX();
		  pressedY = e.getY();
		  drawing.repaint();
		 }
		 
	});
	 
	 
	//menu bar
	JPanel menu = new JPanel();
	JPanel botMenu = new JPanel();
	frame.add(menu, BorderLayout.NORTH);
	frame.add(botMenu, BorderLayout.SOUTH);
	
	//load button
	menu.add(new JLabel("Click to load image: "));
	JButton load = new JButton("Load Image");
	load.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			render = new Render();
			drawing.repaint();
		}
	});
	menu.add(load);
	
	//save button
	menu.add(new JLabel("Click to save: "));
	JButton save = new JButton("Save");
	save.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {		
			saveImage("output Image.png");
		}
	});
	menu.add(save);
	
	//quit button
	JButton quitButton = new JButton("Quit");
	quitButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {System.exit(0);}});
	
	menu.add(quitButton);	
	
	//ambientLight slide bar
	menu.add(new JLabel("AmbientLight: "));
	//ambientLightSlider = new JSlider(JSlider.HORIZONTAL,-10,10,1);
	menu.add(ambientLightSlider);
	
	ambientLightSlider.addMouseListener(new MouseAdapter() {	
		@Override
		public void mouseReleased(MouseEvent e) {			
			ambientLight();
			intensityImage();
			render.setUpZbuffer();	//set up the z-buffer
			render.draw(); //convert the colour to an image
			drawing.repaint();
		}
	});
	
	//set up the paint tick 10 spacing each tick
	ambientLightSlider.setMajorTickSpacing(1);
	ambientLightSlider.setPaintTicks(true);
	//Create the label table
	Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
	labelTable.put( new Integer( -10 ), new JLabel("dim") );
	labelTable.put( new Integer( 20 ), new JLabel("bright") );
	ambientLightSlider.setLabelTable( labelTable );
	ambientLightSlider.setPaintLabels(true);
	
	//intensity slide bar
	botMenu.add(new JLabel("Intensity: "));
	botMenu.add(intensity);
	intensity.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {	
			ambientLight();
			intensityImage();
			render.setUpZbuffer();	//set up the z-buffer
			render.draw(); //convert the colour to an image
			drawing.repaint();
		}
	});
	
	//set up the paint tick 10 spacing each tick
		intensity.setMajorTickSpacing(1);
		intensity.setPaintTicks(true);
		//Create the label table
		Hashtable<Integer, JLabel> labelTable2 = new Hashtable<Integer, JLabel>();
		labelTable2.put( new Integer( -10 ), new JLabel("Low") );
		labelTable2.put( new Integer( 20 ), new JLabel("Heigh") );
		intensity.setLabelTable( labelTable2 );
		intensity.setPaintLabels(true);
	
		
		//image background
		JPanel bg = new JPanel(new BorderLayout());//background
		bg.setBackground(Color.WHITE);
		bg.add(drawing);
		frame.add(bg,BorderLayout.CENTER);
	frame.setVisible(true);
    
 }

	//returns the file name
	public static String fileName(){
		String fName = File.separator;		
		chooser = new JFileChooser(new File(fName));	
		//show the open dialog and return the files name
		chooser.showOpenDialog(frame);
		File selected = chooser.getSelectedFile();
		//System.out.println("here"+selected);
		if(selected!=null){
		return selected.getPath();
		}		
		return null;
	}
	
	
    //calculate the ambient light and update it
	private  void ambientLight(){
		int light = ambientLightSlider.getValue();	
		//System.out.println("light: "+light);
		if(light>0){
			Render.ambientLight  = (float) ((double) light/10);
		}
		else if(light<0){		
			Render.ambientLight = (float) ((double) (-light)/-10);
		}			
		//System.out.println("fields: "+Render.ambientLight);
	}
	
	private void intensityImage(){
		int intens = intensity.getValue();
		if(intens>0){
			Polygon.intensity =  (float) ((double) intens/10);
		}
		else if(intens<0){	
			Polygon.intensity =  (float) ((double) -intens/-10);
		}
	}
	
    /** writes the current image to a file of the specified name
     */ 
     private void saveImage(String fname){
 	try {ImageIO.write(image, "png", new File(fname));}
 	catch(IOException e){System.out.println("Image saving failed: "+e);}
     }
    
        
    
    public static void main(String[] arguments){
        new ImageGUI();
     }
    
}
