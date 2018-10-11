package scagnostics;

import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Random;

import main.ColorScales;
import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("serial")
public class ScagnosticsExamples extends PApplet {
	public static String[] scagNames = {"Outlying","Skewed","Clumpy","Dense",
			"Striated","Convex","Skinny","Stringy","Monotonic"}; 
	
	public static int n;
	public static int numP;
	
	public static double[][][] scag;
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	public static Color[] scagColors = new Color[9];	
	
	
	
	public static int menu = 0;  // 0: Scagnostics  examples
								 // 1: Evaluation 1 (Leader algorithm)	
	 							 // 2: Evaluation 2 (Forced-Directed Layout)	
	 							 // 3: Runtime test 
								 // 4: Draw Runtime test results
	
	private static int MAX_ROWS = 100;
	public static int numVars = 0;
	public static int numRows = 0;
	public static int numSamp = 0;
	private static int numScagnostics = Triangulation.numScagnostics;
	private static boolean[] isScagnosticOutlier = null;
	private static double[] dataMin, dataMax;
	public static double[][] data = null;
	public static double[][][][] data2 = null;
	public static double[][][][] scag2 = null;
	
	public static double[][] scagnostics = null;
	public static double[][] sscagnostics = null;
	public static String[] variableLabels = null;
	public static String[] scagnosticsLabels = Triangulation.scagnosticsLabels;
	public static int sScag = 8; // Selected scagnostics
	
	
	public void setup() {
		size(1440, 900);
		background(Color.WHITE.getRGB());
		stroke(255);
		frameRate(12);
		curveTightness(1.f); 
		
		if (menu==0){
			numSamp = 50;
			numP = 9;
			n = 200;
			scag = new double[numSamp][numP][9];
			generateLoMedHi(new Random(111));
		}	
		
		// Color brewer Set3
		//scagColors[0] = new Color(141, 211, 199);
		//scagColors[1] = new Color(255, 255, 179);
		//scagColors[2] = new Color(190, 186, 218); 
		//scagColors[3] = new Color(251, 128, 114); 
		//scagColors[4] = new Color(128, 177, 211); 
		//scagColors[5] = new Color(252, 205, 229); 
		//scagColors[6] = new Color(253, 180, 98);
		//scagColors[7] = new Color(179, 222, 105);
		//scagColors[8] = new Color(188, 128, 189);
		
		scagColors[0] = new Color(31, 119, 180);
		scagColors[1] = new Color(255, 127, 14);
		scagColors[2] = new Color(44, 160, 44); 
		scagColors[3] = new Color(214, 39, 40); 
		scagColors[4] = new Color(148, 103, 189); 
		scagColors[5] = new Color(140, 86, 75); 
		scagColors[6] = new Color(227, 119, 194);
		scagColors[7] = new Color(127, 127, 127);
		scagColors[8] = new Color(188, 189, 34);
	
		
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent evt) {
				mouseWheel(evt);
			}
		});
	}
	
	
	
	public void drawScagnosticsExamples() {
		// Adjust scagnostics to fit into screen
		this.strokeWeight(1);
		for (int s=0; s<9;s++){
			for (int t=0; t<1;t++){
				for (int v=0; v<numVars;v=v+2){
					if (v==0){
						if (scag2[s][t][v/2][s]<0.00)
							scag2[s][t][v/2][s]=0.00;
						if (scag2[s][t][v/2][s]>0.1)
							scag2[s][t][v/2][s]=0.1;
					}
					if (v==2){
						if (scag2[s][t][v/2][s]<0.38)
							scag2[s][t][v/2][s]=0.38;
						if (scag2[s][t][v/2][s]>0.52)
							scag2[s][t][v/2][s]=0.52;
					}
					if (v==4){
						if (scag2[s][t][v/2][s]<0.78)
							scag2[s][t][v/2][s]=0.78;
						if (scag2[s][t][v/2][s]>0.85)
							scag2[s][t][v/2][s]=0.85;
					}
				}		
			}
		}
		float size = 85;
		float gapX =55;
		float marginX = 110; 
		float marginY = 10;
		// Draw grid background
		for (int s=0; s<9;s++){
			float yy = marginY + (size+4)*s;
			//this.fill(scagColors[s].getRGB());
			
			this.fill(255,255,255);
			if (s%2==0)
				this.fill(0,0,0,20);
			this.noStroke();
			this.rect(marginX, yy-2, gapX*10, size+4);
		}
		// Draw horizontal lines
		for (int s=0; s<10;s++){
			float yy = marginY + (size+4)*s;
			//this.fill(scagColors[s].getRGB());
			
			this.stroke(0,0,0);
			this.strokeWeight(0.25f);
			//this.line(marginX, yy-2, gapX*10+110, yy-2);
		}
		
		// Draw grid
		this.strokeWeight(1f);
		this.stroke(0,0,0);
		this.fill(0,0,0);
		this.textSize(16);
		this.textAlign(PApplet.CENTER);
		for (float s=0; s<=10;s=s+10){
			float xx= marginX +s*gapX;
			this.line(xx, marginY-2, xx, 808);
			if (s==0)
				this.text("0",xx,827);
			else
				this.text("1",xx,827);
		}
		for (int s=0; s<9;s++){
			float yy = marginY + (size+4)*s+1;
			for (int t=0; t<1;t++){
				for (int v=0; v<numVars;v=v+2){
					float xx = (float) (marginX +gapX*10*scag2[s][t][v/2][s]);
					this.strokeWeight(1f);
					this.stroke(0,0,0);
					Color ccc =  ColorScales.getColor(scag2[s][t][v/2][s], "temperature", 1);
					
					this.fill(ccc.getRed(),ccc.getGreen(),ccc.getBlue());
					this.rect(xx,yy,size-2,size-2);
					for (int r = 0; r < numRows; r++) {
						float x3 = 7+(float) (xx + (size-15)*data2[s][t][v][r]);
						float y3 = 7+ (float)(yy + (size-15)*(1 - data2[s][t][v+1][r]));
						this.fill(0,0,0);
						this.stroke(255,255,255,150);
						this.strokeWeight(0.1f);
						this.ellipse(x3, y3, 4.5f, 4.5f);
					}
				}	
			}
			//this.fill(scagColors[s].getRGB());
			this.fill(0,0,0);
			this.textAlign(PApplet.RIGHT);
			this.textSize(16);
			this.text(scagNames[s], marginX-5,yy+size/2+8);
		}
		
		// Draw example for the video 
		float size2 =600;
		float yy = 20;
		float xx = (float) (marginX+700);
		this.stroke(0);
		this.strokeWeight(2);
		this.fill(220);
		this.rect(xx-20,yy+20,size2+40,size2+40);
		
		
		this.noStroke();
		for (int r = 0; r < numRows; r++) {
			float x3 = 20+(float) (xx + (size2-40)*data2[sScag][0][4][r]);
			float y3 = 60+ (float)(yy + (size2-40)*(1 - data2[sScag][0][5][r]));
			this.fill(0,0,0);
			this.ellipse(x3, y3, 16, 16);
		}
	}
		
	
	public void draw() {
		this.background(255,255,255);
		this.smooth();
		this.textFont(metaBold);
		textAlign(PApplet.LEFT);
		
		if (menu==0)
			drawScagnosticsExamples();
	}	
	
	
	public static void generateLoMedHi(Random rand) {
		numSamp = 1;
		numVars = 6;
		numRows = 250;
		variableLabels = new String[numVars];
		data = new double[numVars][numRows];
		data2 = new double[9][numSamp][numVars][numRows];
		scag2 = new double[9][numSamp][numVars/2][9];
		for (int k = 0; k < 9; k++) {
			for (int i = 0; i < numSamp; i++) {
				double[] lo, med, hi;
				initializeMinMax();
				for (int j = 0; j < numRows; j++) {
					if (k == 0) { // outlying
						//generateCircular(0, j, rand);
						//generateOutlyingSpherical(2, j, rand);
						//generateOutlyingLow(0, j, rand);
						generateSeriesHigStringy2(0, j, rand);
						generateOutlyingMed(2, j, rand);
						generateOutlying(4, j, rand);
					}
					if (k == 1) { // skewed
						generateSeriesLowSkew(0, j, rand, 1);
						generateSeriesMedSkew(2, j, rand, 147);
						generateHighSkewed(4, j, rand);
					}
					if (k == 2) { // clumpy
						generateLowClumpy(0, j, rand);
						generateDoughnut(2, j, rand);
						generateClustered(4, j, rand);
					}
					if (k == 3) { // Dense
						generateLowDense(0, j, rand);
						generateMedDense(2, j, rand);
						generateUniform(4, j, rand);
					}
					if (k == 4) { // striated
						generateLowStriated(0, j, rand);
						generateMedStriated(2, j, rand);
						generateStriated(4, j, rand);
					}
					if (k == 5) { // convex
						generateLowConvex(0, j, rand);
						generateQuadratic(2, j, rand);
						generateHighConvex(4, j, rand);
					}
					if (k == 6) { // skinny
						generateLowSkinny(0, j, rand);
						generateMedSkinny(2, j, rand);
						generateHigSkinny(4, j, rand);
					}
					if (k == 7) { // stringy
						generateSeriesLowStringy(0, j, rand);
						generateSeriesMedStringy(2, j, rand);
						generateSeriesHigStringy(4, j, rand);
					}
					if (k == 8) { // monotonic
						generateUniform(0, j, rand);
						generateTriangular(2, j, rand);
						generateMonotonic(4, j, rand);
					}
				}
				normalizeData();
				lo = computeAllScagnostics(data[0], data[1]);
				med = computeAllScagnostics(data[2], data[3]);
				hi = computeAllScagnostics(data[4], data[5]);
				if (k == 1) {
					int count =2;
					while (lo[1] > .2) {
						initializeMinMax();
						for (int j = 0; j < numRows; j++) {
							generateSeriesLowSkew(0, j, rand, count/5.);
						}
						normalizeData(0);
						normalizeData(1);
						lo = computeAllScagnostics(data[0], data[1]);
						count++;
					}
				}
				System.out.println((i + 1) + " " + lo[k] + " " + med[k] + " " + hi[k]);
				
				scag2[k][i][0] = lo;
				scag2[k][i][1] = med;
				scag2[k][i][2] = hi;
				for (int v=0;v<numVars;v++){	
					for (int r=0;r<numRows;r++){	
						data2[k][i][v][r] = data[v][r];
					}	
				}
			}
		}
	}

	private static double[] computeAllScagnostics(double[] x, double[] y) {
		Binner b = new Binner();
		BinnedData bdata = b.binHex(x, y, BinnedData.BINS);
		Triangulation dt = new Triangulation();
		double[] mt = dt.compute(bdata, false);
		return mt;
	}

	private static void generateUniform(int i, int j, Random rand) {
		data[i][j] = rand.nextDouble();
		data[i + 1][j] = rand.nextDouble();
		setMinMax(j, i);
	}

	private static void generateSpherical(int i, int j, Random rand) {
		data[i][j] = rand.nextGaussian();
		data[i + 1][j] = rand.nextGaussian();
		setMinMax(j, i);
	}

	// NOT used
	private static void generateBivariateNormal(int i, int j, Random rand) {
		double z1 = rand.nextGaussian();
		double z2 = rand.nextGaussian();
		data[i][j] = z1 + .5 * z2;
		data[i + 1][j] = .5 * z1 + z2;
		setMinMax(j, i);
	}

	
// OUTLYING ****************************************************************
	private static void generateCircular(int i, int j, Random rand) {
		double z1 = rand.nextDouble();
		double z2 = rand.nextDouble();
		double theta = 6.28 * z1;
		double rho = 1;
		if (z2 > .5)
			rho = .97;
		data[i][j] = Math.cos(theta) * rho+rand.nextGaussian()/20;
		data[i + 1][j] = Math.sin(theta) * rho + rand.nextGaussian()/20;
		setMinMax(j, i);
	}
	
	private static void generateOutlyingSpherical(int i, int j, Random rand) {
		data[i][j] =  rand.nextGaussian();
		data[i + 1][j] = rand.nextGaussian();
		if (data[i][j]>0)
			data[i][j] = PApplet.pow((float) data[i][j],2f);
		else
			data[i][j] =  data[i][j]*2.9;
		if (data[i + 1][j]>0)
			data[i + 1][j] = PApplet.pow((float) data[i + 1][j],2f);
		else
			data[i + 1][j] = -PApplet.pow((float) data[i + 1][j],2f);
			
		setMinMax(j, i);
	}
	
	private static void generateOutlyingLow(int i, int j, Random rand) {
		double z1 = rand.nextDouble();
		double z2 = rand.nextDouble();
		data[i][j] = z1;
		data[i + 1][j] = z2;
		setMinMax(j, i);
	}
	
	private static void generateOutlyingMed(int i, int j, Random rand) {
		data[i][j] =  rand.nextGaussian();
		data[i + 1][j] = rand.nextGaussian();
		data[i][j] = PApplet.pow((float) data[i][j],2f);
		data[i+1][j] = PApplet.pow((float) data[i+1][j],2f);
		if (j==0){
			data[i][0] = 9;
			data[i+1][0] = 7;
		}
		if (j==1){
			data[i][j] = 8;
			data[i+1][j] = 5;
		}
				
		setMinMax(j, i);
	}
	
	
	
	private static void generateOutlying(int i, int j, Random rand) {
		data[i][j] = rand.nextGaussian();
		data[i + 1][j] = rand.nextGaussian();
		// TUAN
		if (j==0){
			data[i][j] = data[i][j]+10;
			data[i + 1][j] = data[i + 1][j]+10.3;
		}
		else if (j==1){
			data[i][j] = data[i][j]+0;
			data[i + 1][j] = data[i + 1][j]+10;
		}
		else if (j==2){
			data[i][j] = data[i][j]+9.7;
			data[i + 1][j] = data[i + 1][j]+0;
		}
		
		setMinMax(j, i);
	}
	
	
	
// SKEW ****************************************************************
	private static void generateSeriesLowSkew(int i, int j,  Random rand,double step) {
		data[i][j] = Math.sin((double) (step * j) / numRows);
		data[i+1][j] = j;
		setMinMax(j, i);
	}
	private static void generateSeriesMedSkew(int i, int j,  Random rand,double step) {
		data[i][j] = j;
		data[i + 1][j] = Math.sin((double) (step * j) / numRows);
		setMinMax(j, i);
	}
	
	private static void generateHighSkewed(int i, int j, Random rand) {
		data[i][j] = j%5+rand.nextGaussian()*0.12;
		data[i+1][j] = j%6+rand.nextGaussian()*0.12;
		setMinMax(j, i);
	}
	private static void generateSeries(int i, int j, Random rand) {
		data[i][j] = j;
		data[i + 1][j] = Math.sin((double) (15 * j) / numRows) + rand.nextGaussian() / 10;
		setMinMax(j, i);
	}

	
// CLUMPY ****************************************************************
	private static void generateLowClumpy(int i, int j, Random rand) {
		double z1 = rand.nextDouble();
		double z2 = rand.nextDouble();
		double theta = 6.28 * z1;
		double rho = 1;
		if (z2 > .5)
			rho = .97;
		data[i][j] = Math.cos(theta) * rho+rand.nextGaussian()/10;
		data[i + 1][j] = Math.sin(theta) * rho + rand.nextGaussian()/10;
		setMinMax(j, i);
	}	
	
	private static void generateDoughnut(int i, int j, Random rand) {
		double z1 = rand.nextDouble();
		double z2 = .65 * rand.nextDouble();
		double theta = 6.28 * z1;
		double rho = z2;
		if (rand.nextDouble() > .4)
			rho = z2 + 1;
		data[i][j] = Math.cos(theta) * rho;
		data[i + 1][j] = Math.sin(theta) * rho;
		setMinMax(j, i);
	}	
	
	private static void generateClustered(int i, int j, Random rand) {
		double p = rand.nextDouble();
		double z1 = .1 * rand.nextGaussian();
		double z2 = .1 * rand.nextGaussian();
		if (p < .5) {
			data[i][j] = z1;
			data[i + 1][j] = z2;
		} 
		else {
			data[i][j] = z1 + 1;
			data[i + 1][j] = z2 +0.5 ;
		}
		setMinMax(j, i);
	}

// Dense ****************************************************************
	private static void generateMedDense(int i, int j, Random rand) {
		double z1 = rand.nextGaussian();
		double z2 = rand.nextGaussian();
		data[i][j] = 	z1 ;
		data[i + 1][j] = z2 ;
		setMinMax(j, i);
	}
	private static void generateLowDense(int i, int j, Random rand) {
		double z1 = rand.nextGaussian();
		double z2 = rand.nextGaussian();
		data[i][j] = 	z1*z1*z1*z1*z1*z1*z2 ;
		data[i + 1][j] = z2*z2*z2*z2*z1 ;
		setMinMax(j, i);
	}
	

// STRIATED ****************************************************************
	private static void generateLowStriated(int i, int j, Random rand) {
		data[i][j] =  j;
		data[i + 1][j] = Math.cos((double) (j) / 6) + rand.nextGaussian() / 8;
		setMinMax(j, i);
	}
	private static void generateMedStriated(int i, int j, Random rand) {
		data[i][j] =  j;
		data[i + 1][j] = Math.cos((double) (j) / 13) + rand.nextGaussian() / 15;
		setMinMax(j, i);
	}
	private static void generateStriated(int i, int j, Random rand) {
		data[i][j] = Math.floor(4 * rand.nextDouble());
		data[i + 1][j] = rand.nextDouble();
		setMinMax(j, i);
	}

// CONVEX ****************************************************************
	private static void generateLowConvex(int i, int j, Random rand) {
		double z1 = rand.nextDouble();
		data[i][j] =  z1;
	//	data[i + 1][j] =  PApplet.sqrt((float) z2);
		if (j<numRows/2){
			data[i+1][j] =data[i][j];
		}
		else{
			data[i+1][j] =-data[i][j];
		}
		setMinMax(j, i);
	}
	private static void generateQuadratic(int i, int j, Random rand) {
		double z1 = rand.nextDouble();
		double z2 = rand.nextGaussian();
		data[i+1][j] = 2 * z1 - 1;
		data[i][j] = Math.pow(data[i+1][j], 2) + .22 * z2;
		setMinMax(j, i);
	}
	private static void generateHighConvex(int i, int j, Random rand) {
		double z1 = rand.nextDouble();
		double z2 = rand.nextDouble();
		double theta = 6.28 * z1;
		double rho = Math.pow(z2,0.35);
		data[i][j] = Math.cos(theta) * rho;
		data[i + 1][j] = Math.sin(theta) * rho;
		setMinMax(j, i);
	}

// SKINNY ****************************************************************
	private static void generateLowSkinny(int i, int j,  Random rand) {
		data[i][j] = j%15;
		data[i + 1][j] = j%16;
		setMinMax(j, i);
	}
	private static void generateMedSkinny(int i, int j,  Random rand) {
		
		double z1 = rand.nextDouble();
		double z2 = rand.nextGaussian();
		data[i][j] = 2 * z1 - 1;
		data[i + 1][j] = Math.pow(data[i][j], 2) + .15 * z2;
		setMinMax(j, i);
	}
	private static void generateHigSkinny(int i, int j,  Random rand) {
		double z1 = rand.nextDouble();
		double z2 = rand.nextGaussian();
		data[i][j] = 2 * z1 - 1;
		data[i + 1][j] = Math.pow(data[i][j], 2) + .01 * z2;
		setMinMax(j, i);
	}

// STRINGY ****************************************************************
	private static void generateSeriesLowStringy(int i, int j, Random rand) {
		data[i+1][j] = j;
		data[i][j] = j/100.+Math.sin((double) (j) / 12) +rand.nextGaussian()*4 ;
		setMinMax(j, i);
	}
	private static void generateSeriesMedStringy(int i, int j, Random rand) {
		data[i+1][j] = j;
		data[i][j] = j/100.+Math.sin((double) (j) / 14) +rand.nextGaussian()*0.1 ;
		setMinMax(j, i);
	}
	private static void generateSeriesHigStringy(int i, int j, Random rand) {
		data[i+1][j] = j;
		data[i][j] = j/100.+Math.sin((double) (j) / 16) +rand.nextGaussian()*0.02;
		setMinMax(j, i);
	}
	private static void generateSeriesHigStringy2(int i, int j, Random rand) {
		data[i][j] = j;
		data[i+1][j] = j/100.+Math.sin((double) (j) / 6) +rand.nextGaussian()*0.02;
		setMinMax(j, i);
	}
// MONOTONIC ****************************************************************
	private static void generateTriangular(int i, int j, Random rand) {
		double z1 = rand.nextDouble();
		double z2 = rand.nextGaussian();
		data[i+1][j] = z1;
		data[i][j] = z1 + .7 * z1 * z2;
		setMinMax(j, i);
	}
	private static void generateMonotonic(int i, int j, Random rand) {
		double z1 = rand.nextDouble();
		double z2 = rand.nextGaussian();
		data[i][j] = z1;
		data[i + 1][j] = Math.pow(z1, 1) + .01 * z2;
		setMinMax(j, i);
	}
// END **********************************************************************************
	
	private static void initializeMinMax() {
		dataMin = new double[numVars];
		dataMax = new double[numVars];
		for (int i = 0; i < numVars; i++) {
			dataMin[i] = Double.POSITIVE_INFINITY;
			dataMax[i] = Double.NEGATIVE_INFINITY;
		}
	}

	private static void updateMinMax(double d, int i) {
		if (d < dataMin[i])
			dataMin[i] = d;
		if (d > dataMax[i])
			dataMax[i] = d;
	}
	
	private static void setMinMax(int j, int loc) {
		updateMinMax(data[loc][j], loc);
		updateMinMax(data[loc + 1][j], loc + 1);
	}
	

	private static void normalizeData(int v) {
			for (int j = 0; j < numRows; j++) {
				data[v][j] = (data[v][j] - dataMin[v]) / (dataMax[v] - dataMin[v]);
			}
		
	}
	
	private static void normalizeData() {
		for (int i = 0; i < numVars; i++) {
			for (int j = 0; j < numRows; j++) {
				data[i][j] = (data[i][j] - dataMin[i]) / (dataMax[i] - dataMin[i]);
			}
		}
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
		if (key=='+')
			sScag ++;
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
	}
	
}