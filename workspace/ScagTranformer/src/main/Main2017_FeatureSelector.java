package main;

import java.awt.Color;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("serial")
public class Main2017_FeatureSelector extends PApplet {
	public static String[] scagNames = {"Outlying","Skewed","Clumpy","Sparse",
			"Striated","Convex","Skinny","Stringy","Monotonic"}; 
	
	public static int numSamp;
	public static int n;
	public static int numP;
	
	public static double[][][] scag;
	public static double[][][][] data;
	public PFont metaBold = loadFont("Times-Roman-48.vlw");
	
	public static Color[] scagColors = new Color[9];	
	
	public static int menu = 4;  // 0: Scagnostics examples
								 // 1: Evaluation 1 (Leader algorithm)	
	 							 // 2: Evaluation 2 (Forced-Directed Layout)	
	 							 // 3: Runtime test 
								 // 4: Draw Runtime test results
	public static void main(String args[]){
	  PApplet.main(new String[] { Main2017_FeatureSelector.class.getName() });
    }
	
	public void setup() {
		size(1440, 900);
		background(Color.WHITE.getRGB());
		stroke(255);
		frameRate(12);
		curveTightness(1.f); 
		
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
			}
		});
	}
	
	public static float computeDis(int p1, int p2) {
		float sum =0;
		int samp1 = p1/(numP);
		int samp2 = p2/(numP);
		int pair1 = p1%(numP);
		int pair2 = p2%(numP);
		for (int sc =0; sc<9;sc++){
			float dif = (float) (scag[samp1][pair1][sc]-scag[samp2][pair2][sc]);
			if (sc==0)
				sum += PApplet.abs(dif);
			else if (sc==4)
				sum += PApplet.abs(dif);
			else if (sc==8)
				sum += PApplet.abs(dif);
			else
				sum += PApplet.abs(dif);
			
		}
		return sum;
	}
	
	
	
	public void draw() {
		this.background(255,255,255);
		this.smooth();
		this.textFont(metaBold);
		// Draw button and Option Poupmenu
		textAlign(PApplet.LEFT);
		//BBP.xP.update();
		//BBP.x = BBP.xP.value;
		
			int numPPP = 500;
			double bin11 = 277;
			double bin21 = 150;
			double bin31 = 51;
			double[] bin1 = {145.591/2,145.591, bin11, bin11*2,bin11*3, bin11*5};
			double[] bin2 = {78.452/2,78.452, bin21, bin21*2,bin21*3, bin21*5};
			double[] bin3 = {26.141/2,26.141, bin31, bin31*2,bin31*3, bin31*5};
			double[][] bin = new double[3][]; 
			bin[0] = bin1;
			bin[1] = bin2;
			bin[2] = bin3;
			
			double scag11 = 3556;
			double scag21 = 3269;
			double scag31 = 3020;
			double[] scag1 = {scag11/5,scag11/2, scag11*1.2, scag11*2,scag11*3, scag11*5};
			double[] scag2 = {scag21/5,scag21/2, scag21*1.2, scag21*2,scag21*3, scag21*5};
			double[] scag3 = {scag31/5,scag31/2, scag31*1.2, scag31*2,scag31*3, scag31*5};
			double[][] scag = new double[3][]; 
			scag[0] = scag1;
			scag[1] = scag2;
			scag[2] = scag3;
			
			double[] leader = {3,4.52, 7.21, 12.55, 17, 23.10};
			
			
			double[] arrayX = {500,1000,1500,2000,2450,3000};
					
			float xMargin =80;
			float yMargin =10;
			float w =1456;
			float h =750;
			float x = xMargin;
			int numFram =3;
			for (int frame=0;frame<1; frame++){
				float y = yMargin+frame*(h+20);
				for (int t=0;t<5;t++){
					this.noStroke();
					float gapY = h/5;
					float yT = y+gapY*t;
					this.fill(221,221,221);
					this.rect(x,yT,w-110,gapY-1);
				}
			}
			for (int frame=0;frame<1; frame++){
				float y = yMargin+frame*(h+20);
				this.stroke(0,0,0);
				this.strokeWeight(1f);
				this.noFill();
				this.rect(x,y-1,w-110,h);
			}
			// Draw X axis
			float y = yMargin+numFram*(h+20);
			float numStep = 4.004f;
			float gapX = w/numStep;
			
			//PFont f = this.loadFont("Times-Roman-48.vlw");
			//this.textFont(f);
			this.fill(0,0,0);
			this.textSize(20);
			this.textAlign(PApplet.CENTER);
			for (int i=0;i<6; i++){
				float x2 = (float) (x+ i*270-130);
				float y2 = h+32;
				this.text(formatIntegerMillinon(i*numPPP),x2,y2);
			}
			this.textAlign(PApplet.CENTER);
			this.textSize(35);
			this.text("v (number of variables)",xMargin+w/2-60,yMargin+(h+20)*1+35);
			this.textSize(20);
			
			
			// Draw chart 1
			float w2 =55;
			float maxTime = 3f*3600; //4 hours
			this.noStroke();
			for (int frame=0;frame<3; frame++){
				float frameY = yMargin+ (1)*(h+20)-21;
				this.stroke(0,0,0);
				for (int i=0;i<numStep; i++){
					float x2 = (float) (x+ (1.5*arrayX[i]/1000f)*gapX)-frame*64-70 ;
					 
					float h2 = (float) (scag[frame][i]*h/maxTime);
					float y2 = frameY-h2;
					this.fill(120,100,225,200);
					this.rect(x2-w2/2,y2,w2,h2);
					
					//Binning
					float h3 = (float) (bin[frame][i]*1.5*h/maxTime);
					float y3 = frameY-h3;
					this.fill(0,150,0);
					this.rect(x2-w2/2-1,y3-1,w2+2,h3+1);
					
					//Leader 
					float h4 = (float) (leader[i]*6*h/maxTime);
					float y4 = y2;
					this.fill(220,0,0);
					this.rect(x2-w2/2-1,y4,w2+2,h4+1);
					
				}
				this.fill(0,0,0);
				this.textAlign(PApplet.LEFT);
				if (frame==0)
					this.text("10,000", xMargin+182, yMargin+ frameY-65);
				else if (frame==1)
					this.text("5,000", xMargin+120, yMargin+ frameY-60);
				else if (frame==2)
					this.text("n=1,000", xMargin+39, yMargin+ frameY-56);
				
				// Draw Y axis
				if (frame==0){ 
					this.textAlign(PApplet.RIGHT);
					for (int t=0;t<=5;t++){
						float gapY = h/5;
						float yT = frameY-gapY*t+2*t;
						this.fill(0,0,0);
						if (t==0){
							if (frame==2)
								this.text((t*30)+"", xMargin -5,yT);
						}	
						else	
							this.text((t*30), xMargin -5,yT);
					}
				}	
				
			}
			
			// Draw Y axis TEXT
			this.textSize(35);
			this.textAlign(PApplet.LEFT);
			float al = -PApplet.PI/2; 
			this.translate(xMargin-50,600);
			this.rotate(al);
			this.fill(0,0,0);
			this.text("Computation times (in minutes)",0,0);
			this.rotate(-al);
			this.translate(-(xMargin-50),-600);
			
			
			// Draw color legend
			this.textAlign(PApplet.LEFT);
			this.textSize(24);
			float x7 = xMargin+ 100;
			float y7 = yMargin;
			float size7 =13; 
			this.fill(220,0,0);
			this.rect(x7-5,y7+20,size7+5,size7);
			this.text("Clustering Variables", x7+20, y7+34);
			this.fill(70,70,200);
			this.rect(x7-5,y7+45,size7+5,size7);
			this.text("Computing Scagnostics", x7+20, y7+59);
			this.fill(0,150,0);
			this.rect(x7-5,y7+70,size7+5,size7);
			this.text("Binning Scatterplots", x7+20, y7+84);	
	}	
	public String formatIntegerMillinon(int num) {
		String nStr = ""+num;
		if (num<1000)
			return ""+num;
		int mi = num/1000;
		return mi+","+nStr.substring(nStr.length()-3,nStr.length());	
	}
	
	public String formatIntegerThousand(int num) {
		String nStr = ""+num;
		if (num<1000)
			return ""+num;
		int th = num/1000;
		return th+","+nStr.substring(nStr.length()-3,nStr.length());	
	}
}