package main;


import java.awt.Color;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("serial")
public class MainTest41 extends PApplet {
	public static String[] scagNames = {"Outlying","Skewed","Clumpy","Sparse",
			"Striated","Convex","Skinny","Stringy","Monotonic"}; 
	
	public static int numSamp;
	public static int n;
	public static int numP;
	
	public static double[][][] scag;
	public static double[][][][] data;
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	
	public static Color[] scagColors = new Color[9];	
	public static float yMargin =10;
	public final static String[] dataNames = {"Isolet","Musk","Gas Sensor","Communities"};
	public final static String[] dataN = {"1,558","475","3,599","1,993"};
	public final static String[] dataP = {"13,861","13,861","8,128","8,128"};
	public final static String[] funcNames = {"None", "Half","Square","Sqrt","Log","Inverse","Logit","Sigmoid"};
	public final static int numFunc = 8;
	public static int menu = 0;  // 0: Scagnostics examples
								 // 1: Evaluation 1 (Leader algorithm)	
	 							 // 2: Evaluation 2 (Forced-Directed Layout)	
	 							 // 3: Runtime test 
								 // 4: Draw Runtime test results
	public static void main(String args[]){
	  PApplet.main(new String[] { MainTest41.class.getName() });
    }
	
	public void setup() {
		size(1280, 750);
		background(Color.WHITE.getRGB());
		stroke(255);
		frameRate(12);
		curveTightness(1.f); 
		
		if (menu==0){
			numSamp = 50;
			numP = 9;
			n = 200;
			data = new double[numSamp][numP][2][n];
			scag = new double[numSamp][numP][9];
		}	
		
		// Color brewer Set3
		scagColors[0] = new Color(141, 211, 199);
		scagColors[1] = new Color(255, 255, 179);
		scagColors[2] = new Color(190, 186, 218); 
		scagColors[3] = new Color(251, 128, 114); 
		scagColors[4] = new Color(128, 177, 211); 
		scagColors[5] = new Color(252, 205, 229); 
		scagColors[6] = new Color(253, 180, 98);
		scagColors[7] = new Color(179, 222, 105);
		scagColors[8] = new Color(188, 128, 189);
	
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent evt) {
				mouseWheel(evt);
			}
		});
	}
	
	
	
	public void draw() {
		this.background(255,255,255);
		this.smooth();
		this.textFont(metaBold);
		
		double[] read1 = {1038,1030,1051,1036,1039,1032,1043,1043};
		double[] read2 = {943,936,945,941,943,942,940,940};
		double[] read3 = {1178,1170,1199,1175,1187,1174,1193,1190};
		double[] read4 = {581,571,593,579,580,574,585,584};
		double[][] read = new double[4][]; 
		read[0] = read1;
		read[1] = read2;
		read[2] = read3;
		read[3] = read4;
		
		double[] bin1 = {3428,3195,3274,3214,2529,1970,2610,3048};
		double[] bin2 = {1494,1366,1490,1525,1205,1125,1179,1504};
		double[] bin3 = {3690,3339,3570,3685,2842,2509,3096,2956};
		double[] bin4 = {2595,2192,2372,2637,2103,1648,1964,2174};
		double[][] bin = new double[4][]; 
		bin[0] = bin1;
		bin[1] = bin2;
		bin[2] = bin3;
		bin[3] = bin4;
		
		double[] scag1 = {29776*2,29090*2,30313*2,25003*2,36400*2,57825*1.7,79998*1.5,30426*2};
		double[] scag2 = {88644,83262,88705,88360,81593,68513,84816,86145};
		double[] scag3 = {44643,34638,43774,43183,39801,42257,39239,57455};
		double[] scag4 = {43517,55647,47209,41035,43863,46383,47650,43547};
		double[][] scag = new double[4][]; 
		scag[0] = scag1;
		scag[1] = scag2;
		scag[2] = scag3;
		scag[3] = scag4;
		
		
		// Draw frames
		float xMargin =200;
		float w =767;
		float h =300;
		float x = xMargin;
		int numFram =4;
		for (int frame=0;frame<4; frame++){
			float y = yMargin+frame*h;
			for (int t=0;t<5;t++){
				this.noStroke();
				float gapY = h/5;
				float yT = y+gapY*t;
				this.fill(220,220,220);
				this.rect(x,yT+1,w,gapY-2);
			}
		}
		for (int frame=0;frame<dataNames.length; frame++){
			float y = yMargin+frame*h;
			this.stroke(0,0,0);
			this.strokeWeight(2f);
			this.noFill();
			this.rect(x,y,w,h);
		}
		// Draw X axis
		float y = yMargin+numFram*h;
		float gapX = 90f;
		
		
		// Draw function names
		this.fill(0,0,0);
		this.textSize(20);
		this.textAlign(PApplet.CENTER);
		float w2 =60;
		for (int i=0;i<numFunc; i++){
			float x2 = x+ i*gapX+40+w2/2;
			float y2 = y+20;
			this.text(funcNames[i],x2,y2);
		}
		this.textSize(26);
		this.textAlign(PApplet.CENTER);
		this.text("Transformation Functions",xMargin+w/2,yMargin+h*dataNames.length+50);
		
		
		// Draw chart 1
		float maxTime = 150*1000; //2 minutes
		this.noStroke();
		this.textSize(20);
		for (int frame=0;frame<dataNames.length; frame++){
			float frameY = yMargin+ (frame+1)*h-1;
			for (int i=0;i<numFunc; i++){
				float x2 = x+ (i)*gapX+40;
				//Read and transform
				float h1 = (float) (read[frame][i]*3*h/maxTime);
				float y1 = frameY-h1;
				this.fill(200,0,0);
				this.rect(x2,y1,w2,h1);
				
				//Binning
				float h2 = (float) (bin[frame][i]*2*h/maxTime);
				float y2 = frameY-h2-h1;
				this.fill(0,160,0);
				this.rect(x2,y2,w2,h2);
				
				//Scagnostic
				float h3 = (float) (scag[frame][i]*h/maxTime);
				float y3 = frameY-h1-h2-h3;
				this.fill(40,40,130);
				this.rect(x2+1f,y3,w2-2f,h3);
			}
			this.fill(0,0,0);
			this.textAlign(PApplet.LEFT);
			this.textSize(26);
			this.text(dataNames[frame], xMargin+40, yMargin+ frame*h+ 35);
			this.textAlign(PApplet.RIGHT);
			this.textSize(20);
			this.text("n="+dataN[frame], xMargin+350, yMargin+ frame*h+ 35);
			this.text("p="+dataP[frame], xMargin+500, yMargin+ frame*h+ 35);
			
			// Draw Y axis
			this.textSize(20);
			this.textAlign(PApplet.RIGHT);
			for (int t=0;t<=2;t++){
				float gapY = h/2.5f;
				float yT = frameY-gapY*t+2*t;
				this.fill(0,0,0);
						this.text(t+"", xMargin -5,yT);
				if (t>0)
					this.text(t+"", xMargin -5,yT);
			}	
			
		}
		
		// Draw Y axis TEXT
		this.textSize(26);
		this.textAlign(PApplet.CENTER);
		float al = -PApplet.PI/2; 
		this.translate(xMargin-35,yMargin+2*h);
		this.rotate(al);
		this.fill(0,0,0);
		this.text("Computation times (in minutes)",0,0);
		this.rotate(-al);
		this.translate(-(xMargin-35), -(yMargin+2*h));
		
		
		// Draw color legend
		this.textAlign(PApplet.LEFT);
		this.textSize(20);
		float x7 = xMargin+ 400;
		float y7 = yMargin;
		float size7 =15; 
		this.fill(40,40,130);
		this.rect(x7-w2,y7-75,w2-2,size7);
		this.text("Computing Scagnostics", x7+7, y7-60);
		
		this.fill(0,140,0);
		this.rect(x7-w2,y7-53,w2-2,size7);
		this.text("Binning", x7+7, y7-38);
		
		this.fill(190,0,0);
		this.rect(x7-w2,y7-31,w2-2,size7);
		this.text("Reading and transforming", x7+7, y7-16);
	}	
	public String formatIntegerMillinon(int num) {
		String nStr = ""+num;
		if (num<1000)
			return ""+num;
		int mi = num/1000000;
		return mi+","+"000"+","+nStr.substring(nStr.length()-3,nStr.length());	
	}
	
	public String formatIntegerThousand(int num) {
		String nStr = ""+num;
		if (num<1000)
			return ""+num;
		int th = num/1000;
		return th+","+nStr.substring(nStr.length()-3,nStr.length());	
	}
	
	public void keyPressed() {
	}
	
	
	public void mouseMoved() {
			
	}
	
	public void mousePressed() {
	}
	public void mouseReleased() {
	}
	
	public void mouseDragged() {
	}
	public void mouseClicked() {
	}
	
	public void mouseWheel(MouseWheelEvent e) {
		int delta = e.getWheelRotation();
			yMargin -=delta;
	}
}