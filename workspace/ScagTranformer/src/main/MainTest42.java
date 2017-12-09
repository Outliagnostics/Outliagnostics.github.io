package main;


import java.awt.Color;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("serial")
public class MainTest42 extends PApplet {
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
	  PApplet.main(new String[] { MainTest42.class.getName() });
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
		
		double[] t10 = {46619,34128,36171,47306,52267,51568,53193,45298};
		double[] t11 = {27983,44937,30047,32902,50559,28745,41113,49878};
		double[] t12 = {41672,36975,33763,44557,52777,45582,48762,52158};
		double[] t13 = {42107,34955,38138,37387,46478,40936,51689,54738};
		double[] t14 = {35241,33898,33182,33831,23116,8572,39627,35083};
		double[] t15 = {34364,21827,30527,30155,7824,3663,13895,18045};
		double[] t16 = {44665,39295,35692,49852,50617,17675,53274,48202};
		double[] t17 = {48216,48364,46979,51015,44676,24127,48803,53271};
		double[][] t1 = new double[8][]; 
		t1[0] = t10;
		t1[1] = t11;
		t1[2] = t12;
		t1[3] = t13;
		t1[4] = t14;
		t1[5] = t15;
		t1[6] = t16;
		t1[7] = t17;
		
		double[] t20 = {46661,42608,42562,45787,39423,30050,41562,38565};
		double[] t21 = {41281,34061,36665,42062,26480,18015,27937,29938};
		double[] t22 = {40869,36054,38035,42403,31187,23689,33397,33703};
		double[] t23 = {43547,41725,42554,45028,37777,27946,39222,36900};
		double[] t24 = {34076,24036,27670,33594,17155,12297,18602,24760};
		double[] t25 = {25324,16385,20592,24868,11738,8279,13279,19152};
		double[] t26 = {36452,26406,30682,37495,19302,13364,20160,27262};
		double[] t27 = {37024,31562,34657,36425,27366,21357,28931,31112};
		double[][] t2 = new double[8][]; 
		t2[0] = t20;
		t2[1] = t21;
		t2[2] = t22;
		t2[3] = t23;
		t2[4] = t24;
		t2[5] = t25;
		t2[6] = t26;
		t2[7] = t27;
		
		

		double[] t30 = {34944,25579,27113,28973,20180,19646,25432,12516};
		double[] t31 = {24002,16664,20456,23618,13995,13714,17058,7861};
		double[] t32 = {26835,20845,24304,26386,16345,16261,20281,11535};
		double[] t33 = {28719,25484,26584,28923,20381,20185,25345,13443};
		double[] t34 = {21452,14624,16942,20668,13621,13933,16617,7526};
		double[] t35 = {20593,15040,16941,20506,13859,13394,16898,8302};
		double[] t36 = {25950,17683,20825,25205,16780,16549,20013,8849};
		double[] t37 = {15085,9794,12834,16023,8670,8935,9673,6085};
		double[][] t3 = new double[8][]; 
		t3[0] = t30;
		t3[1] = t31;
		t3[2] = t32;
		t3[3] = t33;
		t3[4] = t34;
		t3[5] = t35;
		t3[6] = t36;
		t3[7] = t37;
		
		
		double[] t40 = {41711,37027,41996,38472,36595,34123,41243,34519};
		double[] t41 = {38798,37019,36196,33214,31740,24812,31549,28490};
		double[] t42 = {36974,34670,36274,36453,32867,26740,34534,28519};
		double[] t43 = {35600,36110,36848,34184,34867,32157,37246,31359};
		double[] t44 = {32440,31636,32234,31959,26794,16299,31272,23600};
		double[] t45 = {31142,25270,27897,32250,16431,9611,18739,17131};
		double[] t46 = {35661,33573,34696,35884,31904,19325,37026,28929};
		double[] t47 = {36594,30900,31177,33794,25806,18168,28132,21737};
		double[][] t4 = new double[8][]; 
		t4[0] = t40;
		t4[1] = t41;
		t4[2] = t42;
		t4[3] = t43;
		t4[4] = t44;
		t4[5] = t45;
		t4[6] = t46;
		t4[7] = t47;
		
		double[][][] t = new double[4][][]; 
		t[0] = t1;
		t[1] = t2;
		t[2] = t3;
		t[3] = t4;
		
		
		
		// Find max value
		double maxx =0;
		double minn =Double.POSITIVE_INFINITY;
		for (int d=0; d<t.length; d++){
			for (int fx=0; fx<t[0].length; fx++){
				for (int fy=0; fy<t[0][0].length; fy++){
					if (maxx<t[d][fx][fy])
						maxx = t[d][fx][fy];
					if (minn>t[d][fx][fy])
						minn = t[d][fx][fy];
				}
			}	
		}
		//System.out.println("MAX: "+maxx);
		//System.out.println("MIN: "+minn);
		
		double max = 60000;
		double min = 0;
		
		
		float h =480;
		float s =50;
		float dX =700;
		for (int d=0; d<t.length; d++){
			float dY = yMargin + d*h;
			this.textSize(32);
			this.fill(0,0,0);
			this.textAlign(PApplet.RIGHT);
			this.text(dataNames[d], dX-100,dY);
	
			for (int fx=0; fx<t[0].length; fx++){
				float y = dY + fx*s;
				for (int fy=0; fy<t[0][0].length; fy++){
					float x = dX + fy*s;
					this.stroke(0,0,0);
					this.strokeWeight(1f);
					
					double value =  (t[d][fx][fy]-min)/(max-min);
					//System.out.println(value);
					Color c = ColorScales.getColor(value, "rainbow", 1);
					//this.fill(c.getRGB());
					this.noFill();
					this.rect(x,y,s,s);
					
					this.fill(40,40,130);
					this.noStroke();
					float hh = (float) (s*value);
					this.rect(x+2, y-hh+s, s-3, hh);
					
					// Draw function names
					if (fx==0){
						this.textSize(22);
						this.textAlign(PApplet.LEFT);
						float al = -PApplet.PI/6; 
						this.translate(x+8,y);
						this.rotate(al);
						this.fill(0,0,0);
						this.text(funcNames[fy],0,0);
						this.rotate(-al);
						this.translate(-(x+8), -(y));
					}
				}
				this.textSize(22);
				this.fill(0,0,0);
				this.textAlign(PApplet.RIGHT);
				this.text(funcNames[fx], dX-4, y+30);
			}	
		}
		
		
		
		
		// Draw Color legend
		/*for (int i=0; i<=120; i++){
			double value =  (i)/(double) 120;
			Color c = ColorScales.getColor(value, "rainbow", 1);
			this.fill(c.getRGB());
			float ww = (s*8)/120;
			float xx = dX+ww*i-1;
			this.noStroke();
			this.rect(xx,yy,ww+1,hh);
			int time = i/2;
			if ((time==0 || time==10 || time==20 || time==30 || time==40 || time==50 || time==60) && i%2==0){
				this.textAlign(PApplet.CENTER);
				this.textSize(22);
				this.text(time, xx, yy-5);
			}
		}*/
		
		float hh = 30;
		float yy = yMargin-hh-100;
		int step = 10000;
		for (double i=1; i<=max/step; i++){
			this.fill(40,40,130);
			float xx = (float) (dX+s*i-1);
			float h2 = (float) (s*i*step/max);
			this.noStroke();
			this.rect(xx,yy+s-h2,s-3,h2);
			
			this.fill(0,0,0);
			this.textAlign(PApplet.CENTER);
			this.textSize(22);
			this.text(""+(int)i*10, xx+s/2, yy+s-h2-5);
		
			//int time = i/2;
			/*if ((time==0 || time==10 || time==20 || time==30 || time==40 || time==50 || time==60) && i%2==0){
				this.textAlign(PApplet.CENTER);
				this.textSize(22);
				this.text(time, xx, yy-5);
			}*/
		}
		
		this.textSize(22);
		this.fill(0,0,0);
		this.textAlign(PApplet.RIGHT);
		this.text("Computation time (in seconds)", dX+25, yMargin-80);
		
		
		
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