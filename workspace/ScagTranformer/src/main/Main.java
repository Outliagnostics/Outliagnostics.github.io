package main;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import processing.core.PApplet;
import scagnostics.ScagnosticsTranformer;

@SuppressWarnings("serial")
public class Main extends PApplet {
	
	public static String[] scagNames = {"Outlying","Skewed","Clumpy","Dense",
			"Striated","Convex","Skinny","Stringy","Monotonic"}; 
	
	public static int nPoints;
	public static int nVars;
	public static int nPairs;
	
	public PopupOption option= new PopupOption(this);
	public CheckBox c1 = new CheckBox(this, 100, 5,"SPLOM",1,0);
	public CheckBox c2 = new CheckBox(this, 100, 25,"Bigest differences",-1,1);
	public CheckBox c3 = new CheckBox(this, 100, 45,"Transformation Functions",-1,2);
	public CheckBox c4 = new CheckBox(this, 100, 65,"Transformation Example",-1,3);
	public static Color[] scagColors = new Color[9];
		
	public static float dissMax = 0;
	public static SPLOM splom= null;
	public static SPLOM_summary splomSummary = null;
	
	// Automatic scales
	public static float x = 0;
	public static float y = 41;
	public static double[][] data;
	public static double[][][] data2;
	public static double[][][] dataMax;	
	public static double[][][][] scagRatio;
	public static int[][] maxFx;
	public static int[][] maxFy;
	//public static int[][] maxFunc;
	public static int numDifPair=42;
	public final static int NONE = 0;
	public final static int HALF = 1;
	public final static int SQUARE = 2;
	public final static int SQRT = 3;
	public final static int LOG = 4;
	public final static int INVERSE = 5;
	public final static int LOGIT = 6;
	public final static int SIGMOID = 7;
	public final static int numFunc = 8;
	public final static String[] funcNames = {"None", "Half","Square","Sqrt","Log","Inverse","Logit","Sigmoid"};
	public static boolean showPoints = false;
	public static String message1 = "";
	public static String message2 = "";
	
	public static Slider slider1; 
	//public static Slider slider2; 
	public static PopupScagnostics popup;
	public static Button button;
	public static int sF =0;
	public ThreadLoader1 loader1=new ThreadLoader1();
	public Thread thread1=new Thread(loader1);
	public static int computingPair = 0;
	public static boolean stopDrawing = false;
	public static String colorScale = "gray";
	
	// Draw best plots
	public int[] scagInterest = {2,4,7,8};
	public ArrayList<Integer>[] indexes = new ArrayList[scagInterest.length];
	public ArrayList<Double>[] difs = new ArrayList[scagInterest.length];
	public static String[] varNames ={};
			
	// 618x44 -> 652x738
	public static int fileID = 304;
	
	// For runtime testing
	public boolean isTested = false;
	public long[] read = new long[numFunc];
	public static long[] bin = new long[numFunc];
	public static long[] computeScag = new long[numFunc];
	public static long[][] computeScag42 = new long[numFunc][numFunc];
	
	
	// For presentation
	public static float xx2 = 740;
	public static float yy2 = 120;
	public CheckBoxOption cOption1 = new CheckBoxOption(this, (int)xx2+380, 10,"Show ",-1,10);
	public CheckBoxOption cOption2 = new CheckBoxOption(this, (int)xx2+380, 28,"Color by the last attribute",-1,11);
	
	public static void main(String args[]){
	  PApplet.main(new String[] { Main.class.getName() });
    }
	
	
	public void getData(String fileName) {
		message1 = fileName;
	  	String[] lines = loadStrings(fileName);
		String token = "\t";
        if (!lines[0].contains(token))
        	token = ",";
        if (!lines[0].contains(token))  
        	token = " ";
            
		nPoints = lines.length-1; 
		nVars = lines[0].split(token).length;
		if (nVars>50)
			nVars =50;
		
		// For testing in Section 4.2
		if (isTested){
			nPoints = 400;
			nVars =100;
		}
		
		nPairs = nVars*(nVars-1)/2;
		varNames = lines[0].split(token);
	    System.out.println("Dataset: "+fileName);
		System.out.println("num Instances: "+nPoints);
		System.out.println("num Variables: "+nVars);
		System.out.println("num Pairs: "+nPairs);
				
		scagRatio = new double[numFunc][numFunc][nPairs][9];
		maxFx = new int[nPairs][9];
		maxFy = new int[nPairs][9];
		data = new double[nVars][nPoints] ;
		data2 = new double[numFunc][nVars][nPoints] ;
			 
		
        double[] dataMin = new double[nVars];
        double[] dataMax = new double[nVars];
        for (int i = 0; i < nVars; i++) {
            dataMin[i] = Double.POSITIVE_INFINITY;
            dataMax[i] = Double.NEGATIVE_INFINITY;
        }
        for (int row = 0; row < nPoints; row++) {
	    	String[] pieces = lines[row+1].split(token);
        	for (int i = 0; i < nVars; i++) {
				String tmp = pieces[i];
	    		try {
	        		if (!tmp.equals("?") && !tmp.equals("") && !tmp.equals(" ")){
	        			data[i][row] = Double.parseDouble(tmp);
	        		}
	        		else{
	        			data[i][row] = Double.NaN;
	        		}	
	        		
	        		//if (data[i][row]<=0)
	        		//	data[i][row] = Double.NaN;
	        		
	        		
	        		//if (tmp.equals("NaN"))
	        		//	System.out.println("********** nan"+data[i][row]);
	        		//if (Double.isNaN(data[i][row]))
	        		//	System.out.println("	-----"+data[i][row]);
	        		
	        		if (data[i][row] < dataMin[i])
	        			dataMin[i] = data[i][row];
	        		if (data[i][row] > dataMax[i])
	        	    	dataMax[i] = data[i][row];
	        	} catch (Exception ie) {
	            	ie.printStackTrace();
	            }
	    	}
        }
        
        //Normalize Data
        for (int i = 0; i < nVars; i++) {
            for (int j = 0; j < nPoints; j++) {
            	data[i][j] = (data[i][j] - dataMin[i]) / (dataMax[i] - dataMin[i]);
            }
        }
	}
	
	// Weather
	public void getDataWeather(int file) {
		String file1 = "../dataWeather/day"+file+".txt";
		//String file2 = "../dataWeather/day"+(file+1)+".txt";
		message1 = file1;
	  	String[] lines1 = loadStrings(file1);
		String token = ",";
        
		nPoints = lines1.length; 
		nVars = lines1[0].split(token).length;
		nPairs = nVars*(nVars-1)/2;
		String sss = "current_speed,current_direction,temperature,current_u,current_v,significant_wave_height,dominant_wave_period,air_temperature,wind_speed,wind_gust,wind_direction,visibility,barometric_pressure,water_temperature,salinity,sigma_t,conductivity";
		varNames = sss.split(token);
	    		
		scagRatio = new double[numFunc][numFunc][nPairs][9];
		maxFx = new int[nPairs][9];
		maxFy = new int[nPairs][9];
		data = new double[nVars][nPoints] ;
		data2 = new double[numFunc][nVars][nPoints] ;
			 
		
        double[] dataMin = new double[nVars];
        double[] dataMax = new double[nVars];
        for (int i = 0; i < nVars; i++) {
            dataMin[i] = Double.POSITIVE_INFINITY;
            dataMax[i] = Double.NEGATIVE_INFINITY;
        }
        for (int row = 0; row < lines1.length; row++) {
	    	String[] pieces1 = lines1[row].split(token);
	    	//String[] pieces2 = lines2[row].split(token);
        	for (int i = 0; i < nVars; i++) {
				String tmp1 = pieces1[i];
				//String tmp2 = pieces2[i];
	    		try {
	        		if (!tmp1.equals("?") && !tmp1.equals("") && !tmp1.equals(" ")){
	        			data[i][row] = Double.parseDouble(tmp1);
	        		}
	        		else{
	        			data[i][row] =0;
	        		}
	        		/*if (!tmp2.equals("?") && !tmp2.equals("") && !tmp2.equals(" ")){
	        			data[i][lines1.length+row] = Double.parseDouble(tmp2);
	        		}*/
	        			
	        		
	        		if (data[i][row] < dataMin[i])
	        			dataMin[i] = data[i][row];
	        	    if (data[i][row] > dataMax[i])
	        	    	dataMax[i] = data[i][row];
	        	} catch (Exception ie) {
	            	ie.printStackTrace();
	            }
	    	}
        }
        
        //Normalize Data
        for (int i = 0; i < nVars; i++) {
            for (int j = 0; j < nPoints; j++) {
            	data[i][j] = (data[i][j] - dataMin[i]) / (dataMax[i] - dataMin[i]);
            }
        }
	}
		
	public void setup() {
		size(1440, 900);
		background(Color.WHITE.getRGB());
		stroke(255);
		frameRate(12);
		curveTightness(1.f); 
		
		// Read data
		long t1 = System.currentTimeMillis();
		//getDataTest();
		//getData("../dataUCI/sleep.txt");
		
		fileID = 2000;
		//getData("/Users/nhontuan/Desktop/WorldBankData_Clumpy/year"+fileID+".txt");
		//getData("/Users/nhontuan/Desktop/Monotonic/year"+fileID+".txt");
		//getData("../data/Subway50Data.txt");
		getData("../dataChirp/pageBlocks.txt");

		long t2 = System.currentTimeMillis();
			
		// Transform data
		for (int f=0; f<numFunc; f++){
			long t3 = System.currentTimeMillis();
			ScagnosticsTranformer.computeFuncData(f);
			long t4 = System.currentTimeMillis();
			read[f] = (t2-t1)+(t4-t3);
		}
		if (isTested){
			System.out.print("Time to read + tranform:	");
			for (int f=0; f<numFunc; f++){
				System.out.print(read[f]+ ",");
			}
			System.out.println("");	
		}
	
		/*
		// For Testing Section 4.1
		if (isTested){
			for (int f1=0; f1<numFunc; f1++){
				ScagnosticsTranformer.computeScagnosticsForTesing41(f1,0);
			}	
			System.out.print("Time to bin:		");
			for (int f=0; f<numFunc; f++){
				System.out.print(bin[f]+ ",");
			}
			System.out.println("");
			System.out.print("Time to compute scagnostics:	");
			for (int f=0; f<numFunc; f++){
				System.out.print(computeScag[f]+ ",");
			}
			System.out.println("");
		}*/
		
		// For Testing Section 4.2
		if (isTested){
			for (int fx=0; fx<1; fx++){
				for (int fy=0; fy<numFunc; fy++){
					ScagnosticsTranformer.computeScagnosticsForTesing42(fx,fy);
				}	
			}	
			System.out.print("Time to compute scagnostics:	");
			System.out.println("");
			//computeScag42[0][0] = computeScag42[1][1];
			for (int fx=0; fx<numFunc; fx++){
				for (int fy=0; fy<numFunc; fy++){
					System.out.print(computeScag42[fx][fy]+ ",");
				}	
				System.out.println("");	
			}
			System.out.println("");
		}	
		
		thread1.start();
		splom = new SPLOM(this);
		splomSummary = new SPLOM_summary(this);
		
		scagColors[0] = new Color(215,112,112); //red 
		scagColors[1] = new Color(127,163,192); // bluish
		scagColors[2] = new Color(230,230,125);//yellow 
		scagColors[3] = Color.pink;
		scagColors[4] = new Color(100,200,200); //cyan 
		scagColors[5] = Color.ORANGE;//orange 
		scagColors[6] = new Color(176,140,181);// purplish
		scagColors[7] = new Color(183,143,120);//brown 
		scagColors[8] = new Color(128,128,8);// flesh colored
	
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent evt) {
				mouseWheel(evt);
			}
		});
		
		slider1 = new Slider(this,"difference is greater than",450,70,0);
	//	slider2 = new Slider(this,"difference is greater than",450,90,1);
		popup =  new PopupScagnostics(this);
		button = new Button(this);
	}
	
	class ThreadLoader1 implements Runnable {
		public ThreadLoader1() {}
		public void run() {
			 // Initialize best plots
			 for (int i=0; i<scagInterest.length; i++){
				indexes[i] = new ArrayList<Integer>();
				difs[i] = new ArrayList<Double>();
				for (int p=0; p<numDifPair; p++){
					indexes[i].add(-1);
					difs[i].add(-1.);
				}
			}
		 
		 	for (int f1=0; f1<numFunc; f1++){
				for (int f2=0; f2<numFunc; f2++){
					if (f1==1 && f2==1) {
						computingPair++;
						continue;
					}
					message2= "Computing Scagnostics for "+funcNames[f1]+ " vs "+funcNames[f2];
					ScagnosticsTranformer.computeScagnostics(f1,f2);
					ScagnosticsTranformer.computeMaxRatio();
					computeBestPlots();
					SPLOM.count=0;
					computingPair++;
				}	
			}	
			message2 = ""; 
		 }
	}
	public void computeBestPlots() {
		 // Initialize best plots
		 for (int i=0; i<scagInterest.length; i++){
			indexes[i] = new ArrayList<Integer>();
			difs[i] = new ArrayList<Double>();
			for (int p=0; p<numDifPair; p++){
				indexes[i].add(-1);
				difs[i].add(-1.);
			}
		}
		 
		for (int s = 0; s < scagInterest.length; s++) {
			int m = scagInterest[s];
            for (int p=0; p<nPairs; p++){
				int maxX = maxFx[p][m];
		        int maxY = maxFy[p][m];
		        double dif =  (scagRatio[maxX][maxY][p][m]-scagRatio[0][0][p][m]);
                if ( dif > difs[s].get(numDifPair-1)){
                	for (int p2=0; p2<numDifPair; p2++){
                		if (dif>difs[s].get(p2)){
                			indexes[s].add(p2, p);
                			difs[s].add(p2, dif);
                			break;
                		}
                	}
                }
			}	
		}
	}
		
	
	public void draw() {
		if (stopDrawing) return;
		this.background(255,255,255);
		this.smooth();
		if (c1.s>=0){
			splom.draw2();
			slider1.draw();
			//slider2.draw();
			//splomSummary.draw2();
			popup.draw2();
			cOption1.draw();
			cOption2.draw();
		}
		else if (c2.s>=0){
			float gapX = 1285/4;
			float gapY = 50;
			//float size = 68;
			float size = 48;
			int numCol = 3;
			int numPlotsEachCol = numDifPair/numCol;
			
			int bPlot = -1;
			int bX = -1;
			int bY = -1;
			// Draw background
			for (int i=0; i<scagInterest.length; i++){
				this.fill(50,50,50);
				this.noStroke();
				this.rect(x+i*gapX, y-4, gapX-3, 710);
			}
			
			// Draw scagNames
			this.textAlign(PApplet.CENTER);
			this.textSize(16);
			this.fill(0,0,0);
			for (int s = 0; s < scagInterest.length; s++) {
				int m = scagInterest[s];
	        	this.text(scagNames[m],x+ (s+0.5f)*gapX , y-6);
			}
			for (int s = 0; s < scagInterest.length; s++) {
				int m = scagInterest[s];
	        	for (int count=0; count<indexes[s].size() && count<numDifPair; count++){
					float yy2 = y+(count%numPlotsEachCol)*gapY;
					float xx2 = x+6+s*gapX+ (gapX/numCol-4)*(count/numPlotsEachCol);
					int p = indexes[s].get(count);
					if (p<0) continue;
					
					int[] index = pairToIndex(p);
					int vX = index[1];
					int vY = index[0];
					
					 int maxX = maxFx[p][m];
				     int maxY = maxFy[p][m];
				        
					Color c1 =  ColorScales.getColor(scagRatio[0][0][p][m], "temperature", 1);
					Color c2 =  ColorScales.getColor(scagRatio[maxX][maxY][p][m], "temperature", 1);
					if (xx2<mouseX && mouseX<xx2+size &&
							yy2<mouseY && mouseY<yy2+size && popup.b<0){
						bPlot = p;
						bX = vX;
						bY = vY;
						this.stroke(0,255,0);
					}
					
					this.fill(c1.getRGB());
					this.rect(xx2, yy2, size, size);
					this.fill(c2.getRGB());
					if (maxX==1)
						this.rect(xx2+size+2, yy2, size/2+3, size);
					else if (maxY==1)
						this.rect(xx2+size+2, yy2+size/2-3, size, size/2+3);
					else	
						this.rect(xx2+size+2, yy2, size, size);
					
					
					this.noStroke();
					this.fill(Color.BLACK.getRGB());
					for (int pp = 0; pp < nPoints; pp++) {
						float x3 = (float) (xx2 + size*(0.05f+data[vX][pp]*0.9f));
						float y3 = (float) (yy2 + size*(0.95f-data[vY][pp]*0.9f));
						this.ellipse(x3, y3, size/15, size/15);
					}
					
					for (int pp = 0; pp < nPoints; pp++) {
						float x4 = (float) (xx2 + size*(0.05f+data2[maxX][vX][pp]*0.9f))+size+2;
						float y4 = (float) (yy2 + size*(0.95f-data2[maxY][vY][pp]*0.9f));
						this.ellipse(x4, y4, size/15, size/15);
					}
	        	}	
			}	
				
			
			if (bPlot>=0){
				SPLOM.drawTransformation(bPlot, bX, bY);
				
			}
		}
		
		else if (c3.s>=0){
			drawFunctions();
		}
		else if (c4.s>=0){
			this.fill(0,0,0);
			this.textSize(30);
			this.text("This is for the Subway dataset. Teaser image", 100,100);
			// Draw teaser image for the paper
			float xx = 1;
			float yy = 200;
			float size = 182;//157;
			
			
			this.background(0);
			for (int fx=0; fx<numFunc; fx++){
				float xx2 = xx+fx*size;
				this.fill(255);
				// skip sqrt
				if (fx==2) continue;
				else if (fx>2)
					 xx2 = xx+(fx-1)*size;
				
				//change color
				if (fx==0)
					this.fill(255);
				else if (fx==1)
					this.fill(180);
				else if (fx==3)	
					this.fill(Color.PINK.getRGB());
				else if (fx==4)	
					this.fill(255,150,100);
				else if (fx==4)	
					this.fill(255,150,150);
				else if (fx==5)	
					this.fill(255,255,150);
				else if (fx==6)	
					this.fill(150,255,150);
				else if (fx==7)	
					this.fill(150,255,255);
	
				this.textSize(25);
				this.textAlign(PApplet.CENTER);
				this.text(funcNames[fx], xx2+size/2,yy-10);
			}
			
			
			for (int fx=0; fx<numFunc; fx++){
				int fy = 5;
				float xx2 = xx+fx*size;
				float yy2 = yy;
				this.strokeWeight(1.5f);
				this.stroke(0,0,0);
				this.fill(200,200,200);
				
				//skip sqrt
				if (fx==2) continue;
				else if (fx>2)
					 xx2 = xx+(fx-1)*size;
				
				//change color
				if (fx==0)
					this.fill(255);
				else if (fx==1)
					this.fill(180);
				else if (fx==3)	
					this.fill(Color.PINK.getRGB());
				else if (fx==4)	
					this.fill(255,150,100);
				else if (fx==4)	
					this.fill(255,150,150);
				else if (fx==5)	
					this.fill(255,255,150);
				else if (fx==6)	
					this.fill(150,255,150);
				else if (fx==7)	
					this.fill(150,255,255);
	
				if (fx==1){
					xx2 +=size/4-4;
					this.rect(xx2,yy2,size/2+8,size);
				}	
				else
					this.rect(xx2,yy2,size,size);
				
				this.fill(0,0,0);
				this.noStroke();
				for (int pp = 0; pp < nPoints; pp++) {
					float x4 = (float) (xx2 + size*(0.08f+data2[fx][11][pp]*0.85f));
					float y4 = (float) (yy2 + size*(0.92f-data2[fy][14][pp]*0.84f));
					this.ellipse(x4, y4, size/17, size/17);
				}	
			}	
		}
		
		this.textAlign(PApplet.LEFT);
		this.textSize(14);
		this.fill(0,0,0);
		String[] str = message1.split("/");
		this.text(str[str.length-1],220,16);
		
		long milis = (System.currentTimeMillis()/10)%256;
		double vvv = (double) milis/255;
		Color ccc =  ColorScales.getColor(vvv, "rainbow", 1);
		this.fill(ccc.getRGB());
		this.textSize(16);
		if (message2.contains("Done"))
			this.fill(0,0,0);
		this.text(message2,220,34);
		
		// Draw button and Option Poupmenu
		button.draw();
		option.draw(c1,c2,c3,c4);
					
		// Check check
		if (c1.bMode >= 0) {
			if (c1.s>=0) {
				c2.s = -1;
				c3.s = -1;
				c4.s = -1;
			}
		}
		else if (c2.bMode >= 0) {
			if (c2.s>=0) {
				c1.s = -1;
				c3.s = -1;
				c4.s = -1;
			}
		}
		else if (c3.bMode >= 0) {
			if (c3.s>=0) {
				c1.s = -1;
				c2.s = -1;
				c4.s = -1;
			}
		}
		else if (c4.bMode >= 0) {
			if (c4.s>=0) {
				c1.s = -1;
				c2.s = -1;
				c3.s = -1;
			}
		}
	}	
	
	// Draw functions using for tranformations.
	public void drawFunctions(){
		float xx = 200;
		float yy = 700;
		float ss = 650;
		
		// Colors for different functions
		Color[] colors = new Color[numFunc]; 
		colors[NONE] = Color.BLACK;  
		colors[HALF] = Color.GRAY; 
		colors[SQUARE] = new Color(200,0,180); 
		colors[SQRT] = new Color(255,0,0); 
		colors[LOG] = new Color(240,240,0);   
		colors[INVERSE] = new Color(0,240,0); 
		colors[LOGIT] = new Color(0,220,240); 
		colors[SIGMOID] =  new Color(0,0,255);
		
		// Draw frame
		this.fill(200,200,200);
		this.stroke(0,0,0);
		this.strokeWeight(3);
		this.rect(xx, yy-ss, ss, ss);
		
		this.stroke(0,0,0);
		this.strokeWeight(0.6f);
		for (int f=0; f<numFunc; f++){
			//if (f!=NONE && f!=SQUARE && f!=SQRT) continue;
			//if (f!=LOG && f!=INVERSE) continue;
			if (f!=NONE && f!=LOGIT  &&  f!=SIGMOID ) continue;
			//if (f!=NONE && f!=LOGIT && f!=LOGIT_INVERSE  &&  f!=SIGMOID ) continue;
			// All function
			//if (f==LOGIT_INVERSE ) continue;
			
			this.fill(colors[f].getRGB());
			for (int i=0;i<=100;i++){
				double x = (double) i/100;
				double y = ScagnosticsTranformer.computeFuncData(f, x);
				float r=8;
				this.ellipse(xx + (float) x*ss, yy - (float) y*ss, r,r);
			}
			this.ellipse(xx + ss+40, yy-ss +f*28+100, 8,8);
			this.fill(0,0,0);
			this.textSize(20);
			this.text(funcNames[f], xx + ss+50+1, yy-ss +f*28+106+1);
			this.fill(colors[f].getRGB());
			this.text(funcNames[f], xx + ss+50, yy-ss +f*28+106);
		}
		// Draw lables
		this.textSize(30);
		this.fill(0,0,0);
		this.textAlign(PApplet.LEFT);
		this.text("0", xx-22,yy+30);
		this.text("1", xx+ss-8,yy+30);
		this.text("1", xx-24,yy-ss+18);
		this.textSize(30);
		this.text("x*", xx-33,yy-ss/2+12);
		this.text("x", xx+ss/2-10,yy+28);
	}
			
	public int[] pairToIndex(int pair){
		int[] index = new int[2];
		int p=0;
		for (int v1 =0; v1<nVars;v1++){
			for (int v2 =0; v2<v1;v2++){
				if (p==pair){
					index[0] = v1;
					index[1] = v2;
					return index;
				}
				p++;
			}
		}	
		return index;
	}
	
	
	public void keyPressed() {
		//Weather
		if (this.key == 'o' || this.key == 'O') {
			fileID--;
			
			stopDrawing = true;
			thread1.stop();
			getDataWeather(fileID);
			
			System.out.println("getData: "+fileID);
			for (int f=0; f<numFunc; f++){
			  	ScagnosticsTranformer.computeFuncData(f);
			}
			computingPair = 0;
			thread1=new Thread(loader1);
			thread1.start();
			splom = new SPLOM(this);
			splomSummary = new SPLOM_summary(this);
			stopDrawing = false;
		}
		else if (this.key == 'p' || this.key == 'P') {
			fileID++;
			
			stopDrawing = true;
			thread1.stop();
			getDataWeather(fileID);
			
			System.out.println("getData: "+fileID);
			for (int f=0; f<numFunc; f++){
			  	ScagnosticsTranformer.computeFuncData(f);
			}
			computingPair = 0;
			thread1=new Thread(loader1);
			thread1.start();
			splom = new SPLOM(this);
			stopDrawing = false;
			
		}
		
		// World data
		if (this.key == '-') {
			fileID--;
			
			stopDrawing = true;
			thread1.stop();
			getData("/Users/nhontuan/Desktop/WorldBankData2/year"+fileID+".txt");

			System.out.println("getData: "+fileID);
			for (int f=0; f<numFunc; f++){
			  	ScagnosticsTranformer.computeFuncData(f);
			}
			computingPair = 0;
			thread1=new Thread(loader1);
			thread1.start();
			splom = new SPLOM(this);
			stopDrawing = false;
		}
		else if (this.key == '+' || this.key == '=') {
			fileID++;
			
			stopDrawing = true;
			thread1.stop();
			getData("/Users/nhontuan/Desktop/WorldBankData2/year"+fileID+".txt");

			System.out.println("getData: "+fileID);
			for (int f=0; f<numFunc; f++){
			  	ScagnosticsTranformer.computeFuncData(f);
			}
			computingPair = 0;
			thread1=new Thread(loader1);
			thread1.start();
			splom = new SPLOM(this);
			stopDrawing = false;
			
		}
			
		

		
		
		if (this.key == 'i' || this.key == 'I') { 
			if (cOption1.s<0)
				cOption1.s = 1;
			else
				cOption1.s =-1;
		}
		if (this.key == 'p' || this.key == 'P') 
			showPoints = !showPoints;
		if (this.key == '0') { 
			sF =0;
		}
		else if (this.key == '1') { 
			sF =1;
		}
		if (this.key == '2') { 
			sF =2;
		}
		if (this.key == '3') { 
			sF =3;
		}
		if (this.key == '4') { 
			sF =4;
		}
		if (this.key == '5') { 
			sF =5;
		}	
	}
	
	
	
	public void mouseMoved() {
	//	if (c1.s>=0)
	//		bbp.mouseMoved1();
	//	else if (c2.s>=0)
	//		bbp.mouseMoved2();
	//	else if (c3.s>=0)
	//		bbp.mouseMoved3();
	}
	
	public void mousePressed() {
	//	bbp.mousePressed();
		slider1.mousePressed();
		//slider2.mousePressed();
	}
	public void mouseReleased() {
	//	bbp.mouseReleased();
		slider1.mouseRelease();
		//slider2.mousePressed();
	}
	
	public void mouseDragged() {
	//	bbp.mouseDragged();
		
	}
	public void mouseClicked() {
		popup.mouseClicked();
		if (option.b>=0){
			c1.checkSelected();
			c2.checkSelected();
			c3.checkSelected();
			c4.checkSelected();
			return;
		}
		cOption1.checkSelected();
		cOption2.checkSelected();
		
		if (button.b>=0){
			stopDrawing = true;
			thread1.stop();
			String fileName =  loadFile(new Frame(), "Open your file", "..", ".txt");
			System.out.println("getData: "+fileName);
			if (fileName.equals("..null"))
				return;
			getData(fileName);
			System.out.println("Transform Data");
			for (int f=0; f<numFunc; f++){
			  	ScagnosticsTranformer.computeFuncData(f);
			}
			computingPair = 0;
			thread1=new Thread(loader1);
			thread1.start();
			splom = new SPLOM(this);
			splomSummary = new SPLOM_summary(this);
			stopDrawing = false;
		}
	}
	
	public String loadFile (Frame f, String title, String defDir, String fileType) {
		  FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
		  fd.setFile(fileType);
		  fd.setDirectory(defDir);
		  fd.setLocation(50, 50);
		  fd.show();
		  String path = fd.getDirectory()+fd.getFile();
	      return path;
	}

	public void mouseWheel(MouseWheelEvent e) {
		int delta = e.getWheelRotation();
		if (this.keyPressed){
			y -=delta;
		}
		else {
			x -=delta;
	}	
		
	}
}