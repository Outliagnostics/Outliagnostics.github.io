package main;


import static main.MainTestCompareRatio.funcNames;
import static main.MainTestCompareRatio.numFunc;
import static main.MainTestCompareRatio.ratios;
import static main.MainTestCompareRatio.sF;
import static main.MainTestCompareRatio.scagNames;
import static main.MainTestCompareRatio.scagRatio;

import java.awt.Color;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;
import scagOriginal.ScagnosticsRatior;

@SuppressWarnings("serial")
public class MainTestCompareRatio extends PApplet {
	public static String[] scagNames = {"Outlying","Skewed","Clumpy","Sparse",
			"Striated","Convex","Skinny","Stringy","Monotonic"}; 
	
	public static int nPoints;//Subway 423;///Libras 360;//1994;     // Economy 144;
	public static int nVars;//Subway  104;// Libras  91;  //128;//Economy 44;
	public static int nPairs;
	
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	public PopupOption option= new PopupOption(this);
	public CheckBoxImposed c1 = new CheckBoxImposed(this, 5, 10,"Best Ratio",1,0);
	public CheckBoxImposed c2 = new CheckBoxImposed(this, 5, 32,"SPLOM",-1,1);
	public static Color[] scagColors = new Color[9];
		
	public static float dissMax = 0;
	public static SPLOM splom = null;
	
	// Automatic scales
	public static float x = 0;
	public static float y = 41;
	public static double[][][] data;
	public static double[][][] dataRatio;
	public static double[][][] dataMax;
	public static double[][][][] scagRatio;
	public static int[][][] maxRatio;
	//public static int[][] maxFunc;
	public static int numDifPair=42;
	public static int[][] difPair = new int[numDifPair][9];
	public final static int SQUARE = 1;
	public final static int SQRT = 2;
	public final static int LOG = 3;
	public final static int LOGIT = 4;
	public final static int INVERSE = 5;
	public final static int numFunc = 6;
	public final static String[] funcNames = {"None","SQUARE","SQRT","LOG","LOGIT","INVERSE",};
	public static int numRatio = 101;
	public static float[] ratios = new float[numRatio];
	
	public static Slider slider; 
	public static PopupClustering popup;
	public static int sF =0;
	public ThreadLoader1 loader1=new ThreadLoader1();
	public Thread thread1=new Thread(loader1);
	public int count =0;
	public static void main(String args[]){
	  PApplet.main(new String[] { MainTestCompareRatio.class.getName() });
    }
	
	public void getData() {
		int file = 8;
		String[] filenames = {"Test3","Breast","MLB2008","Sonar",
				"Empl","Cancer1000","Communities","Gas","Musk","Isolet",
				"Libras", "Madelon","Subway","Usmoney",
				"Amazon", "Arcene"};
		//String[] filenames = {"NRC","Ecoli","wine","Parkisons","IndianLiver","Housing","Forestfires","Slump","Baseball","Cars","Iris"};
		
		String[] lines = loadStrings("../data/"+filenames[file]+"Data.txt");
		
		String token = ",";
        if (!lines[0].contains(token))  
        	token = " ";
        if (!lines[0].contains(token))
        	token = "\t";
            
		nPoints = lines.length;
		nVars = lines[0].split(token).length;
		if (filenames[file].contains("Gas"))
			nVars = nVars-1; //the last column is class attribute
		if (filenames[file].contains("Sonar"))
			nVars = nVars-1; //the last column is class attribute
		if (filenames[file].contains("Cancer"))
			nVars = 200; //the last column is class attribute
		if (filenames[file].contains("Madelon"))
			nVars = 100; //the last column is class attribute
		if (filenames[file].contains("Amazon"))
			nVars = 100; //the last column is class attribute
		if (filenames[file].contains("Arcene"))
			nVars = 100; //the last column is class attribute
		if (filenames[file].contains("Iris"))
			nVars = 4; //the last column is class attribute
		if (filenames[file].contains("Cars"))
			nVars = nVars-1; //the last column is class attribute
		//if (nVars>60)
		//	nVars =60;
		nPairs = nVars*(nVars-1)/2;
		
		System.out.println("Dataset: "+filenames[file]);
		System.out.println("num Instances: "+nPoints);
		System.out.println("num Variables: "+nVars);
		System.out.println("num Pairs: "+nPairs);
				
		scagRatio = new double[numFunc][numRatio][nPairs][9];
		maxRatio = new int[numFunc][nPairs][9];
		data = new double[numFunc][nVars][nPoints] ;
		dataRatio = new double[numRatio][nVars][nPoints] ;
			
		
        double[] dataMin = new double[nVars];
        double[] dataMax = new double[nVars];
        for (int i = 0; i < nVars; i++) {
            dataMin[i] = Double.POSITIVE_INFINITY;
            dataMax[i] = Double.NEGATIVE_INFINITY;
        }
        for (int row = 0; row < nPoints; row++) {
	    	String[] pieces = lines[row].split(token);
        	for (int i = 0; i < nVars; i++) {
				String tmp = pieces[i];
	    		try {
	        		if (!tmp.equals("?") && !tmp.equals("") && !tmp.equals(" ")){
	        			data[0][i][row] = Double.parseDouble(tmp);
	        		}
	        		else{
	        			data[0][i][row] =0;
	        		}	
	        		
	        		if (data[0][i][row] < dataMin[i])
	        			dataMin[i] = data[0][i][row];
	        	    if (data[0][i][row] > dataMax[i])
	        	    	dataMax[i] = data[0][i][row];
	        	} catch (Exception ie) {
	            	ie.printStackTrace();
	            }
	    	}
        }
        
        //Normalize Data
        for (int i = 0; i < nVars; i++) {
            for (int j = 0; j < nPoints; j++) {
            	data[0][i][j] = (data[0][i][j] - dataMin[i]) / (dataMax[i] - dataMin[i]);
            }
        }
	}
	
	public void getDataTest1(int numSamp) {
		double[][] data2 = new double[2][numSamp];
		Random rand = new Random(1122);
		double maxX =  Double.NEGATIVE_INFINITY;
		double maxY =  Double.NEGATIVE_INFINITY;
		double minX =  Double.POSITIVE_INFINITY;
		double minY =  Double.POSITIVE_INFINITY;
		
		for (int i=0; i<numSamp;i++){
			double z1 = .5 * rand.nextGaussian();
			double z2 = .5 * rand.nextGaussian();
			double p = rand.nextDouble();
			if (p < .33333) {
				data2[0][i] = z1 - 2.5;
				data2[1][i] = z2 ;
			} else if (p < .66666) {
				data2[0][i] = z1;
				data2[1][i] = z2;
			} else {
				data2[0][i] = z1 + 5.0;
				data2[1][i] = z2;
			}
			if (data2[0][i]>maxX)
				maxX=data2[0][i];
			if (data2[0][i]<minX)
				minX=data2[0][i];
			if (data2[1][i]>maxY)
				maxY=data2[1][i];
			if (data2[1][i]<minY)
				minY=data2[1][i];
		}	
		for (int i=0; i<numSamp;i++){
			data[0][0][i] = (data2[0][i]-minX)/(maxX-minX);
		}
		for (int i=0; i<numSamp;i++){
			data[0][1][i] = (data2[1][i]-minY)/(maxY-minY);
		}
	}
		
	public void getDataTest2(int numSamp) {
		double[][] data2 = new double[2][numSamp];
		Random rand = new Random(1122);
		double maxX =  Double.NEGATIVE_INFINITY;
		double maxY =  Double.NEGATIVE_INFINITY;
		double minX =  Double.POSITIVE_INFINITY;
		double minY =  Double.POSITIVE_INFINITY;
		
		for (int i=0; i<numSamp;i++){
			data2[0][i] = i;
			data2[1][i] = i/100.+Math.sin((double) (i) / 12) +rand.nextGaussian()/13 ;
			
			if (data2[0][i]>maxX)
				maxX=data2[0][i];
			if (data2[0][i]<minX)
				minX=data2[0][i];
			if (data2[1][i]>maxY)
				maxY=data2[1][i];
			if (data2[1][i]<minY)
				minY=data2[1][i];
		}	
		for (int i=0; i<numSamp;i++){
			data[0][0][i] = (data2[0][i]-minX)/(maxX-minX);
		}
		for (int i=0; i<numSamp;i++){
			data[0][1][i] = (data2[1][i]-minY)/(maxY-minY);
		}
	}
	
	public void getDataTest3(int numSamp) {
		double[][] data2 = new double[2][numSamp];
		Random rand = new Random(1122);
		double maxX =  Double.NEGATIVE_INFINITY;
		double maxY =  Double.NEGATIVE_INFINITY;
		double minX =  Double.POSITIVE_INFINITY;
		double minY =  Double.POSITIVE_INFINITY;
		
		for (int i=0; i<numSamp;i++){
			data2[0][i] = i;
			data2[1][i] = i*Math.sin((double) (i) / 12) +rand.nextGaussian()/10 ;
			
			if (data2[0][i]>maxX)
				maxX=data2[0][i];
			if (data2[0][i]<minX)
				minX=data2[0][i];
			if (data2[1][i]>maxY)
				maxY=data2[1][i];
			if (data2[1][i]<minY)
				minY=data2[1][i];
		}	
		for (int i=0; i<numSamp;i++){
			data[0][0][i] = (data2[0][i]-minX)/(maxX-minX);
		}
		for (int i=0; i<numSamp;i++){
			data[0][1][i] = (data2[1][i]-minY)/(maxY-minY);
		}
	}

	public void getDataTest4(int numSamp) {
		double[][] data2 = new double[2][numSamp];
		Random rand = new Random(1122);
		double maxX =  Double.NEGATIVE_INFINITY;
		double maxY =  Double.NEGATIVE_INFINITY;
		double minX =  Double.POSITIVE_INFINITY;
		double minY =  Double.POSITIVE_INFINITY;
		
		for (int i=0; i<numSamp;i++){
			data2[0][i] = i%4+ rand.nextGaussian()/10;
			data2[1][i] = rand.nextDouble() ;
			
			
			if (data2[0][i]>maxX)
				maxX=data2[0][i];
			if (data2[0][i]<minX)
				minX=data2[0][i];
			if (data2[1][i]>maxY)
				maxY=data2[1][i];
			if (data2[1][i]<minY)
				minY=data2[1][i];
		}	
		for (int i=0; i<numSamp;i++){
			data[0][0][i] = (data2[0][i]-minX)/(maxX-minX);
		}
		for (int i=0; i<numSamp;i++){
			data[0][1][i] = (data2[1][i]-minY)/(maxY-minY);
		}
	}	
	
	public void getDataTest5(int numSamp) {
		double[][] data2 = new double[2][numSamp];
		Random rand = new Random(1122);
		double maxX =  Double.NEGATIVE_INFINITY;
		double maxY =  Double.NEGATIVE_INFINITY;
		double minX =  Double.POSITIVE_INFINITY;
		double minY =  Double.POSITIVE_INFINITY;
		
		for (int i=0; i<numSamp;i++){
			double z1 = rand.nextDouble();
			double z2 = rand.nextGaussian();
			data2[0][i] = 2 * z1 - 1;
			data2[1][i] = PApplet.pow((float) data2[0][i], 2) + .05 * z2;
			
			
			if (data2[0][i]>maxX)
				maxX=data2[0][i];
			if (data2[0][i]<minX)
				minX=data2[0][i];
			if (data2[1][i]>maxY)
				maxY=data2[1][i];
			if (data2[1][i]<minY)
				minY=data2[1][i];
		}	
		for (int i=0; i<numSamp;i++){
			data[0][0][i] = (data2[0][i]-minX)/(maxX-minX);
		}
		for (int i=0; i<numSamp;i++){
			data[0][1][i] = (data2[1][i]-minY)/(maxY-minY);
		}
	}	
	public void setup() {
		size(1280, 750);
		background(Color.WHITE.getRGB());
		stroke(255);
		frameRate(12);
		curveTightness(1.f); 
		
		// Automatic finding scales		
		for (int i=0; i<numRatio; i++){
			ratios[i] =1;
		}
		for (int i=0; i<numRatio/2; i++){
			ratios[i] = 1+(numRatio/2-i)/10f;
			ratios[numRatio-i-1] = 1/(1+(numRatio/2-i)/10f);
		}
		//for (int i=0; i<numRatio; i++){
		//	System.out.println(i+ " ratios = "+ratios[i]);
		//}
		int numSamp =  500;
		nPoints = numSamp;
		nVars = 2;
		nPairs = nVars*(nVars-1)/2;
		scagRatio = new double[numFunc][numRatio][nPairs][9];
		maxRatio = new int[numFunc][nPairs][9];
	//	maxFunc = new int[nPairs][9];
		data = new double[numFunc][nVars][nPoints] ;
		dataRatio = new double[numRatio][nVars][nPoints] ;
		
		// 1 = clumpy
		// 2 = Stringy
		// 3 = Stringy
		// 4 = Striated +Clumpy
		// 5 = Convex
		getDataTest4(numSamp);
		
		thread1.start();
		
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
		
		
		slider = new Slider(this);
		
		splom = new SPLOM(this);
		popup =  new PopupClustering(this);
    }
	class ThreadLoader1 implements Runnable {
		public ThreadLoader1() {}
		  public void run() {
			for (int f=0; f<numFunc; f++){
					ScagnosticsRatior.computeFuncData(f);
					ScagnosticsRatior.computeRatioData();
					ScagnosticsRatior.computeWithFunction(f);
					ScagnosticsRatior.computeMaxRatio(f);
				}	
				ScagnosticsRatior.computeFink();
		 }
	}
	
	public void draw() {
		this.background(255,255,255);
		this.smooth();
		slider.draw();
		this.textFont(metaBold,12);
		
		int[] scagInterest = {0,2,4,7,8,1,3,5,6};
		float gapX = 1285/5;
		float gapY = 50;
		//float size = 68;
		float size = 120;
		int numCol = 3;
		int numPlotsEachCol = numDifPair/numCol;
		
		int bPlot = -1;
		int bX = -1;
		int bY = -1;
		int bS = -1;
		float bScaleX2 = -1;
		float bScaleY2 = -1;
		// Draw background
		for (int i=0; i<scagInterest.length; i++){
			this.fill(50,50,50);
			this.noStroke();
			this.rect(x+i*gapX, y-4, gapX-3, 710);
		}
		
		for (int count=0; count<numDifPair; count++){
			for (int m = 0; m < 9; m++) {
				difPair[count][m] =-1;
			}
		}
		int mid = numRatio/2;
		for (int s = 0; s < scagInterest.length; s++) {
			int m = scagInterest[s];
            int count=0;
			for (int p=0; p<nPairs; p++){
				if (count==numDifPair)
					break;
		        int maxR = maxRatio[sF][p][m];
		        float limit = slider.value;
             	//System.out.println("\t ScagnosticsRatior = "+maxRatio+" "+maxFunc);
                if (((float) (scagRatio[sF][maxR][p][m]-scagRatio[0][mid][p][m]))>limit){
                	difPair[count][m] = p;
                	count++;
                }
			}	
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
        	for (int count=0; count<numDifPair; count++){
				if (difPair[count][m]<0) continue;
				float yy2 = y+(count%numPlotsEachCol)*gapY;
				float xx2 = x+6+s*gapX+ (gapX/numCol-4)*(count/numPlotsEachCol);
				int p = difPair[count][m];
				int[] index = pairToIndex(p);
				int vX = index[1];
				int vY = index[0];
				
				float scaleX2 = 1;
				float scaleY2 = 1;
				if (ratios[maxRatio[sF][p][m]]<1)
					scaleX2 = ratios[maxRatio[sF][p][m]];
				else
					scaleY2 = 1/ratios[maxRatio[sF][p][m]];
				
				Color c1 =  ColorScales.getColor(scagRatio[0][mid][p][m], "temperature", 1);
				Color c2 =  ColorScales.getColor(scagRatio[sF][maxRatio[sF][p][m]][p][m], "temperature", 1);
				
				this.fill(c1.getRGB());
				this.rect(xx2, yy2, size, size);
				this.fill(c2.getRGB());
				this.rect(xx2+size+2, yy2+size*(1-scaleY2), size*scaleX2, size*scaleY2);
				if (xx2<mouseX && mouseX<xx2+size &&
						yy2<mouseY && mouseY<yy2+size){
					bPlot = p;
					bX = vX;
					bY = vY;
					bS = m;
					bScaleX2 = scaleX2;
					bScaleY2 = scaleY2;
				}
				
				this.fill(Color.BLACK.getRGB());
				for (int pp = 0; pp < nPoints; pp++) {
					float x3 = (float) (xx2 + size*(0.05f+data[0][vX][pp]*0.9f));
					float y3 = (float) (yy2 + size*(0.95f-data[0][vY][pp]*0.9f));
					this.ellipse(x3, y3, size/20, size/20);
				}
				
				int maxR = maxRatio[sF][p][m];
				if (ratios[maxR]<1)
					for (int pp = 0; pp < nPoints; pp++) {
						float x4 = (float) (xx2 + size*(0.05f+dataRatio[maxR][vX][pp]*0.9f)) + size+2;
						float y4 = (float) (yy2 + size*(0.95f-data[sF][vY][pp]*0.9f));
						this.ellipse(x4, y4, size/15, size/20);
					}
				else
					for (int pp = 0; pp < nPoints; pp++) {
						float x4 = (float) (xx2 + size*(0.05f+data[sF][vX][pp]*0.9f)) + size+2;
						float y4 = (float) (yy2 + size*(0.95f-dataRatio[maxR][vY][pp]*0.9f));
						this.ellipse(x4, y4, size/15, size/20);
					}
			}
		}	
			
		
		if (bPlot>=0){
			int maxR = maxRatio[sF][bPlot][bS] ;
			Color c1 =  ColorScales.getColor(scagRatio[0][mid][bPlot][bS], "temperature", 1);
			Color c2 =  ColorScales.getColor(scagRatio[sF][maxR][bPlot][bS], "temperature", 1);
			float size2 = 100;
			float xx3 = 10;
			float yy3 = 55;
			if (bScaleX2<1){
				bScaleY2 = bScaleY2/bScaleX2;
				bScaleX2 =1;
			}
			else if (bScaleY2<1){
				bScaleX2 = bScaleX2/bScaleY2;
				bScaleY2 =1;
			}	
			
			// FINK
			float scaleX3 = 1;
			float scaleY3 = 1;
			float scaleX4 = 1;
			float scaleY4 = 1;
			if (ScagnosticsRatior.FinkMinEdge==null) {
				count++;
				this.fill((count*25)%255,100,100);
				this.textSize(40);
				this.text("Please wait ...",200,200);
				return;
			
			}
			int maxRF = ScagnosticsRatior.FinkMinEdge[bPlot];
			int maxRF2 = ScagnosticsRatior.FinkMinUncompactness[bPlot];
			//System.out.println("	Our solution = "+maxR);
			//System.out.println("	Fink min Edge = "+maxRF);
			if (ratios[maxRF]<1){
				scaleY3 = 1/ratios[maxRF];
				scaleX3 =1;
			}
			else{
				scaleY3 = 1;
				scaleX3 =ratios[maxRF];
			}
			if (ratios[maxRF2]<1){
				scaleY4 = 1/ratios[maxRF2];
				scaleX4 =1;
			}
			else{
				scaleY4 = 1;
				scaleX4 =ratios[maxRF2];
			}
			//********
			
			float shiftY = 0;
			if (bScaleY2>1){
				shiftY = size2*(bScaleY2-1);
			}
			float ww = size2*2+50;
			float hh = size2+50;
			if (bScaleX2>1)
				ww = size2*(bScaleX2 +1)+50;
			if (bScaleY2>1)
				hh = size2*(bScaleY2)+50;
			//this.fill(0,0,0);
			this.fill(255,255,255);
			this.rect(0,0,1280,740);
			
			strokeWeight(1);
			this.stroke(0,0,0);
			float size4 = size2*1.6f;
			// size2 = size2*1.9f;
			this.fill(c1.getRGB());
			this.rect(xx3, yy3, size4, size4);
			this.fill(c2.getRGB());
			this.rect(xx3+size4+20, yy3+size2*(1-bScaleY2)+shiftY, size2*bScaleX2, size2*bScaleY2);
			this.fill(Color.BLACK.getRGB());
			for (int pp = 0; pp < nPoints; pp++) {
				float x3 = (float) (xx3 + size4*(0.05f+data[0][bX][pp]*0.9f));
				float y3 = (float) (yy3 + size4*(0.95f-data[0][bY][pp]*0.9f));
				this.ellipse(x3, y3, size2/20, size2/20);
			//	System.out.println(data[0][bX][pp]+","+data[0][bY][pp]);
			}
			//System.out.println(bScaleX2+" "+bScaleY2);
			if (ratios[maxR]<1){
				for (int pp = 0; pp < nPoints; pp++) {
					float x4 = (float) (xx3 + size2*(0.05f+bScaleY2*dataRatio[maxR][bX][pp]*0.9f))+ size4+20;
					float y4 = (float) (yy3 + size2*bScaleY2*(0.95f-data[sF][bY][pp]*0.9f) );
					this.ellipse(x4, y4, size2/20, size2/20);
				}
			}
			else{
				for (int pp = 0; pp < nPoints; pp++) {
					float x4 = (float) (xx3 + size2*bScaleX2*(0.05f+data[sF][bX][pp]*0.9f))+ size4+20;
					float y4 = (float) (yy3 + size2*(0.95f-bScaleX2*dataRatio[maxR][bY][pp]*0.9f));
					this.ellipse(x4, y4, size2/20, size2/20);
				}
			}
			
			
			//FINK
			float gapX1 =140;
			float gapY1 =160;
			float gapX2 =280;
			float gapY2 =300;
			
			this.fill(200,150,200);
			this.rect(xx3+size4+gapX1+20, yy3+gapY1, size2*scaleX3, size2*scaleY3);
			this.fill(200,150,200);
			this.rect(xx3+size4+gapX2+20, yy3+gapY2, size2*scaleX4, size2*scaleY4);
			this.fill(200,150,200);
			this.textSize(26);
			this.textAlign(PApplet.LEFT);
			this.text("FINK's best ratios", xx3+size4+gapX1+220,yy3+gapY1-5);
			this.fill(Color.BLACK.getRGB());
			if (ratios[maxRF]<1){
				for (int pp = 0; pp < nPoints; pp++) {
					float x4 = (float) (xx3 + gapX1 + size2*(0.05f+scaleY3*dataRatio[maxRF][bX][pp]*0.9f))+ size4+20;
					float y4 = (float) (yy3 + gapY1 + size2*scaleY3*(0.95f-data[0][bY][pp]*0.9f) );
					this.ellipse(x4, y4, size2/20, size2/20);
				}
			}
			else{
				for (int pp = 0; pp < nPoints; pp++) {
					float x4 = (float) (xx3 + gapX1  + size2*scaleX3*(0.05f+data[0][bX][pp]*0.9f))+ size4+20;
					float y4 = (float) (yy3 + gapY1  + size2*(0.95f-scaleX3*dataRatio[maxRF][bY][pp]*0.9f));
					this.ellipse(x4, y4, size2/20, size2/20);
				}
			}
			if (ratios[maxRF2]<1){
				for (int pp = 0; pp < nPoints; pp++) {
					float x4 = (float) (xx3 + gapX2 + size2*(0.05f+scaleY4*dataRatio[maxRF2][bX][pp]*0.9f))+ size4+20;
					float y4 = (float) (yy3 + gapY2 + size2*scaleY4*(0.95f-data[0][bY][pp]*0.9f) );
					this.ellipse(x4, y4, size2/20, size2/20);
				}
			}
			else{
				for (int pp = 0; pp < nPoints; pp++) {
					float x4 = (float) (xx3 + gapX2  + size2*scaleX4*(0.05f+data[0][bX][pp]*0.9f))+ size4+20;
					float y4 = (float) (yy3 + gapY2  + size2*(0.95f-scaleX4*dataRatio[maxRF2][bY][pp]*0.9f));
					this.ellipse(x4, y4, size2/20, size2/20);
				}
			}	
			// Draw text
			//this.fill(255,100,0);
			//this.textAlign(PApplet.LEFT);
			//this.text("Func="+funcNames[sF],xx3,yy3-10);
			//this.text("MaxRation="+ratios[maxR],xx3+size2+5,yy3-10);
			//this.text("bScaleY2="+bScaleY2+" "+bScaleX2,xx3+size2,yy3+10);
			
			// Draw text for brushing plot
			DecimalFormat df = new DecimalFormat("#.##");
			this.fill(0,0,0);
			this.textSize(20);
			this.textAlign(PApplet.LEFT);
			this.text("Ratio of 1:1", xx3,yy3-7);
			this.text("Ratio of "+ df.format(ratios[maxR]), xx3+size4+20,yy3-7);
			this.text("Ratio of "+ df.format(ratios[maxRF]), xx3+size4+180,yy3-4+gapY1);
			this.text("Ratio of "+ df.format(ratios[maxRF2]), xx3+size4+320,yy3-4+gapY2);
			this.text(scagNames[bS]+"="+df.format(scagRatio[0][mid][bPlot][bS]), xx3,yy3+size4+22);
			float y5 = yy3+size2+23;
			if (ratios[maxR]<1)
				y5 = yy3+size2/ratios[maxR]+23;
			this.text(scagNames[bS]+"="+df.format(scagRatio[sF][maxR][bPlot][bS]), xx3+size4+20,y5);
			this.text("Function="+funcNames[sF], 400,yy3-7);
		}
			
		// Draw button and Option Poupmenu
		textAlign(PApplet.LEFT);
		option.draw(c1,c2);
	
		// Check check
		if (c1.bMode >= 0) {
			if (c1.s>=0) {
				c2.s = -1;
			}
		}
		else if (c2.bMode >= 0) {
			if (c2.s>=0) {
				c1.s = -1;
			}
		}
	
		
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
		slider.mousePressed();
	}
	public void mouseReleased() {
	//	bbp.mouseReleased();
		slider.mouseRelease();
	}
	
	public void mouseDragged() {
	//	bbp.mouseDragged();
		
	}
	public void mouseClicked() {
		popup.mouseClicked();
		
		if (option.b>=0){
			c1.checkSelected();
			c2.checkSelected();
		}
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