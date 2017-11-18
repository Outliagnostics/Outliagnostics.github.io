package main;
import java.awt.Color;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PFont;
import static main.PlotSPLOM.*;
import static main.Plot.*;

public class Main extends PApplet {
	int count = 0;
	public static String[] scagNames = {"Outlying","Skewed","Clumpy","Sparse",
			"Striated","Convex","Skinny","Stringy","Monotonic"}; 
	public static String varlists = "Total Nonfarm"+ 
			"Manufacturing"+
			"Trade and Transportation"+ 
			"Retail Trade"+
			"Financial Activities"+
			"Professional and Business"+
			"Education and Health"+ 
			"Leisure and Hospitality"+ 
			"Accommodation and Food"+ 
			"Other Services"+ 
			"Government"+
			"State Government"+ 
			"Local Government"+ 
			"State Employment" ;
	public static String[] varlist;
	public static String[] statelist;
	
	public static int numS =49;
	public static int numV =14;
	public static int nM = 264;//22 * 12 - 2;
	public static int numP = numV*(numV-1)/2;
	public static int numBalls = (nM-1)*numP;// numBalls in BBP
	
	public static float[][][] scagVals = new float[nM][9][numP] ;
	public static float[][][] data = new float[nM][numV][numS] ;
	public static float[][][] data2 = new float[nM][numV][numS] ;
	public static double[][] meanYear = new double[9][numP] ;
	public static double[] meanYearMin = new double[9];
	public static double[] meanYearMax = new double[9];
	public static double[][] meanYearVar = new double[9][numV];
	public static int[][] orderVar = new int[9][numV];
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	
	public static int plotW =43;
	public static Integrator[] iW = new Integrator[numP];
	public static int plotH =38;
	public static Integrator[] iH = new Integrator[numP];
	public static float plotX =160;
	public static int plotY =200;
	public static int sS = 0;
	public PopupMenu keyFunction = new PopupMenu(this);
	public PopupOption option= new PopupOption(this);
	public static PlotSPLOM[] plots = new PlotSPLOM[numP];
	public static Plot[] plot2 = new Plot[numP];
	public PlotText[] plotText = new PlotText[numV];
	public static ArrayList<Integer> selectedPlots = new ArrayList<Integer>();
	
	public static int[] bIndex = new int[2];
	public static float dif1 =4;
	public static float dif2 =4;
	public static float dif3 =4f;
	public static float dif4 =4f;
	public static float[] pX = new float[numV+1];
	public static float[] pY = new float[numV+1];
	public static float[] pW = new float[numV+1];
	public static float[] pH = new float[numV+1];
	public static boolean[] isTextBr = new boolean[numV];
	public static boolean[] isTextSe = new boolean[numV];
	
	//public static boolean stop =false; //Stop Lensing for timeseries
	public static int plotW2 =135;
	public static int plotH2 =90;
	public static int plotH3 =40;  // Small time series
	public static float plotX2 =0.1f;
	public static int plotY2 =150;
	public static int gap2 =2;
	public static int gapS =2;
	public static float stepX2 =3;
	public static int nDLense =4;
	public ButtonMain button = new ButtonMain(1230-22,0,22,17, this);
	public CheckBoxImposed c1 = new CheckBoxImposed(this, 35, 10, "Superimposed",1,0);
	public CheckBoxImposed c2 = new CheckBoxImposed(this, 35, 40, "Search in the same time serie",-1,1);
	public CheckBoxImposed c3 = new CheckBoxImposed(this, 35, 60, "Search in selected time series",-1,2);
	public CheckBoxImposed c4 = new CheckBoxImposed(this, 35, 80,"Search in entire time series",-1,3);
	public CheckBoxImposed c5 = new CheckBoxImposed(this, 35, 100,"Search by scagnostics",-1,4);
	public CheckBoxImposed c6 = new CheckBoxImposed(this, 35, 130,"Request for Details",-1,5);
	
	public static  int c1V;
	public Slider1 slider1 = new Slider1(this,plotX2+plotW2,plotY2,plotH2,0);
	public static float sliderY1 =0;
	public static float sliderY2 =0;
	public Slider2 slider2 = new Slider2(this);
	public Slider4 slider4 = new Slider4(this);
	
	public static int screenW =1240;
	public static int l =12;
	public static Integrator[] iX2 = new Integrator[nM];
	public static int nLY =12;
	
	public static int bO =-1;
	public static int bD =-1;
	public static int bS =-1;
	public static boolean found =false;
	
	// Search
	public static int search=0;
	public static float x1 =20;
	public static float y1 =plotY2-2;
	public static float gx1 =50;
	public static float gy1 =230;
	
	public static float h1 =520;
	public static float w1 = 1240;
	public static Integrator iW1 = new Integrator(0);
	public static Integrator iW4 = new Integrator(0);
	public static int bO1 =-1;
	public static int bP1 =-1;
	public static int bM1 =-1;
	public static boolean found1 =false;
	public static int nP1 = 6;
	public static float sP1 = 153;
	public static Integrator[] iP1 = new Integrator[nP1];
	public static Integrator[][] iC1 = new Integrator[nP1][3];
	public static Integrator[][] iPX1 = new Integrator[nP1][numS];
	public static Integrator[][] iPY1 = new Integrator[nP1][numS];
	public static Integrator[][] iPS1 = new Integrator[nP1][9];
	public static int n = 300;
	public static int[][] disArray= new int[0][0];// For seach entire space 
	@SuppressWarnings("unchecked")
	public static ArrayList<Integer>[] disArray1 = new ArrayList[n];
	@SuppressWarnings("unchecked")
	public static ArrayList<Integer>[] disArray2 = new ArrayList[n];
	public static int sgap1 = 5;
	
	public static int bO2 =-1;
	public static int bP2 =-1;
	public static int bM2 =-1;
	
	//Used for Buble plot
	public static int bO12 =-1;
	public static int bP12 =-1;
	public static int bM12 =-1;
	
	public static boolean found2 =false;
	public static int bO3 =-1;
	public static int bP3 =-1;
	public static int bM3 =-1;
	public static boolean found3 =false;
	public static String[] months ={"Jan","Feb","Mar","Apr","May", "Jun", "Jul", "Aug","Sep","Oct","Nov","Dec"};
	public static boolean mClicked= false;
	
	public static Color color1= new Color(255,0,255);
	public static Color color2= new Color(0,255,255);
	public static Color[] color12= {new Color(255,0,255,80),new Color(0,255,255,80)};
	public static Integrator ySc = new Integrator(50);

	public static ArrayList<Integer> swapX = new ArrayList<Integer>();
	public static ArrayList<Integer> swapY = new ArrayList<Integer>();

	public static int MAX_PAIR= 10;
	
	public static Integrator[][] iPX3 = new Integrator[MAX_PAIR][nM];
	public static Integrator[][] iPY3 = new Integrator[MAX_PAIR][nM];
	public static Integrator iX3 = new Integrator(plotW2);
	public static Integrator iY3 = new Integrator(plotY2);
	
	public static Integrator[] iX4 = new Integrator[BBP.MAX_BALL_SHOWING];
	public static Integrator[] iY4 = new Integrator[BBP.MAX_BALL_SHOWING];
	
	public static BBP  bbp3;
	public static BBP2 bbp4;
	public HashMap<String,Integer> hm = new HashMap<String,Integer>(); 
	public static ArrayList<String> hData = new ArrayList<String>();
	public static Color[] scagColors = new Color[9];
	
	float[] scagLow = new float[9];
	float[] scagHigh = new float[9];
	
	public static Integrator[] iSL = new Integrator[9];
	public static Integrator[] iSH = new Integrator[9];
	
	public static int sPie = -1;
	public static int sSec = -1;
	public static int bPie = -1;
	public static int bSec = -1;
	boolean isActive = false;
	public static boolean change =true;
	public int kActive=0;
	public int kSatisfied=0;
	public boolean[][] isSatisfied= new boolean[numP][nM];
	public int numPoints = 101;
	public int[][] satCount= new int[9][numPoints];
	public int[] satCountByTime= new int[nM];

	public static boolean stopAnimation =false;

	
	public static void main(String args[]){
	  PApplet.main(new String[] { Main.class.getName() });
    }
	
	public void setup() {
		size(1440, 900);
		background(Color.BLACK.getRGB());
		stroke(255);
		frameRate(12);
		curveTightness(1.f); 
		
		System.out.println("--------- "+button.x);
		
		readData();
		readScags();
		computeMean();
		computeMeanVar();
		orderVar();
		
		varlist = loadStrings("IndustryFiltered.txt");
		for (int i=0;i<varlist.length;i++){
			varlist[i] = varlist[i].split("\t")[1];
		}
		statelist = loadStrings("state.txt");
	
		
		for (int p =0; p<numP;p++){
			iW[p] = new Integrator(plotW);
			iH[p] = new Integrator(plotH);
		}
		
		for (int p =0; p<nM;p++)
			iX2[p] = new Integrator(stepX2,0.5f,0.1f);
		for (int p =0; p<nP1;p++){
			iP1[p] = new Integrator(0f);
			iC1[p][0] = new Integrator(0f,0.5f,0.1f);
			iC1[p][1] = new Integrator(0f,0.5f,0.1f);
			iC1[p][2] = new Integrator(0f,0.5f,0.1f);
			for (int sc =0; sc<9;sc++){
				iPS1[p][sc] = new Integrator(0f,0.5f,0.1f);;
			}
				
		}	
		for (int p =0; p<nP1;p++)
			for (int r =0; r<numS;r++){
				iPX1[p][r] = new Integrator(x1+gx1,0.5f,0.1f);
				iPY1[p][r] = new Integrator(y1+gy1,0.5f,0.1f);	
			}	
			
		for (int p =0; p<numP;p++){
			plots[p] = new PlotSPLOM(p, this);
			plot2[p] = new Plot(p, this);
		}
		
		for (int v =0; v<numV; v++){
			plotText[v] = new PlotText(v, this);
		}
		
		for (int m =0; m<nM;m++){
			for (int p =0; p<MAX_PAIR;p++){
				iPX3[p][m] = new Integrator(plotX2+plotW2+stepX2*m*2f);
				iPY3[p][m] = new Integrator(200);
			}	
		}
		for (int i =0; i<BBP.MAX_BALL_SHOWING;i++){
			iX4[i] = new Integrator(slider2.x);
			iY4[i] = new Integrator(slider2.y);
		}	
		
		for (int i =0; i<9;i++){
			iSL[i] = new Integrator(0);
			iSH[i] = new Integrator(0);
		}			
		scagColors[0] = new Color(215,112,112); //red 
		scagColors[1] = new Color(127,163,192); // bluish
		scagColors[2] = new Color(230,230,125);//yellow 
		scagColors[3] = Color.pink;
		scagColors[4] = new Color(100,200,200); //cyan 
		scagColors[5] = Color.ORANGE;//orange 
		scagColors[6] = new Color(176,140,181);// purplish
		scagColors[7] = new Color(183,143,120);//brown 
		scagColors[8] = new Color(128,128,8);// flesh colored
		
		bbp3 = new BBP(this,hData, 200, 10, plotY2, 15);
		bbp3.setData(hm);	
		
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent evt) {
				mouseWheel(evt);
			}
		});
		
		selectedPlots = new ArrayList<Integer>();
		selectedPlots.add(67);
		selectedPlots.add(73);
		selectedPlots.add(57);
		selectedPlots.add(64);
		selectedPlots.add(13);
		
		for (int i =0; i<9;i++){
			scagLow[i] = 0f;
			scagHigh[i] =1f;
		}
		bbp4 = new BBP2(this,420,105,15,15);
		bbp4.createBalls();
	}
	
	public void draw() {
		button.pos.update();
		ySc.update();
		
		if (!button.s){
			textFont(metaBold,11);
			drawTimeSeries();
		}
		
		this.smooth();
		drawSearch5();
		drawDetails();
		drawSearchAllSelected();
		drawSearchAllSpace();
		drawSearchScag();
		if (button.x>=1){
			// RESET swap array of PAIRs
			swapX = new ArrayList<Integer>();
			swapY = new ArrayList<Integer>();
			
			smooth();
			drawSPLOM();
		}
		
		if (button.pos.value < plotW2 && (search==0 || search==2)){
			// If at least 1 pair is selected
			if (selectedPlots.size()>0){
				if (c1.s<0 && c6.s<0 && c3.s<0){
					slider1.draw();
				}	
				fill(255,255,255);
				textFont(metaBold,16);
				if (c6.s<0)
					text(scagNames[sS],plotX2+plotW2+2,plotY2-3);
			}
		}	
		// Draw button and Option Poupmenu
		textFont(metaBold,12);
		button.draw();
		if (!button.s){
			textAlign(PApplet.LEFT);
			textFont(metaBold,22);
			option.draw(c1,c2,c3,c4,c5,c6);
		}
		
		
		if (c4.s>=0 && (found1 || found2) && !button.s){
			bbp3.draw();
		}	
			
		else if (c5.s>=0 && !button.s){
			bbp4.draw();
		}	
		
		// Check check
		textAlign(PApplet.LEFT);
		if (c1.bMode >= 0) {
			if (c1.s>=0) {
				c2.s = -1;
				c3.s = -1;
				c4.s = -1;
				c5.s = -1;
				c6.s = -1;
			}
		} else if (c2.bMode >= 0) {
			if (c2.s>=0) {
				c1.s = -1;
				c3.s = -1;
				c4.s = -1;
				c5.s = -1;
				c6.s = -1;
			}
		} else if (c3.bMode >= 0) {
			if (c3.s>=0) {
				c1.s = -1;
				c2.s = -1;
				c4.s = -1;
				c5.s = -1;
				c6.s = -1;
			}
		}
		else if (c4.bMode >= 0) {
			if (c4.s>=0) {
				c1.s = -1;
				c2.s = -1;
				c3.s = -1;
				c5.s = -1;
				c6.s = -1;
			}
		}
		else if (c5.bMode >= 0) {
			if (c5.s>=0) {
				c1.s = -1;
				c2.s = -1;
				c3.s = -1;
				c4.s = -1;
				c6.s = -1;
			}
		}
		else if (c6.bMode >= 0) {
			if (c6.s>=0) {
				c1.s = -1;
				c2.s = -1;
				c3.s = -1;
				c4.s = -1;
				c5.s = -1;
			}
		}
		mClicked= false;
		count++;
		if (count%10==3)
			change=true;
		if (count>1000)
			count=0;
	}	
	
	public void drawSearchScag() {
		if (search!=4)  return;
		for (int sc = 0; sc < 9; sc++){
			iSL[sc].target(scagLow[sc]);			
			iSH[sc].target(scagHigh[sc]);			
			iSL[sc].update();
			iSH[sc].update();
		}
		
		if (change){
			int k=0;
			kActive =0;
			kSatisfied = 0;
			isSatisfied = new boolean[numP][nM];
			
			for (int p =0; p<numP;p++){
				for (int m=1;m<nM;m++){
					boolean ok = true;
					for (int sc =0; sc<9;sc++){
						if (scagVals[m][sc][p]<iSL[sc].value - 0.001f || scagVals[m][sc][p]>iSH[sc].value+0.001f)
							ok = false;
					}
					if (ok){
						isSatisfied[p][m] = ok;
						kSatisfied++;
					}	
					if (kActive<BBP2.maxBalls && ok) {
						BBP2.balls[k].active= true;
						kActive++;
					}
					else{
						BBP2.balls[k].active= false;
					}
					
					k++;
				}
			}
		}
		
		
		satCount= new int[9][numPoints];
		satCountByTime= new int[nM];
		for (int p =0; p<numP;p++){
			for (int m=1;m<nM;m++){
				boolean ok = true;
				for (int sc =0; sc<9;sc++){
					if (scagVals[m][sc][p]<iSL[sc].value - 0.001f || scagVals[m][sc][p]>iSH[sc].value+0.001f)
						ok = false;
				}
				if (ok){
					for (int sc =0; sc<9;sc++){
						int loc = (int) (scagVals[m][sc][p]*(numPoints-1));
						satCount[sc][loc]++; 
					}
					satCountByTime[m]++;
				}	
			}
		}
		
		
		
		drawPieChart();
		//drawBarChart();
		
		
		// Draw summary text
		fill(255,255,255);
		textFont(metaBold,15);
		
		int x41 = 200;
		int x42 = 245;
		int y41 = 680;
		int y42 = 702;
		int y43 = 724;
		this.textAlign(RIGHT);
		text("Showing plots: ",x41,y41);
		text("Satisfied plots: ",x41,y42);
		text("Plots in entire series: ",x41,y43);
		text(formatIntegerThousand(kActive),x42,y41);
		text(formatIntegerThousand(kSatisfied),x42,y42);
		text(formatIntegerThousand(BBP2.balls.length+17*18/2),x42,y43);
		this.textAlign(LEFT);
	}
	
	public String formatIntegerThousand(int num) {
		String nStr = ""+num;
		if (num<1000)
			return ""+num;
		int th = num/1000;
		return th+","+nStr.substring(nStr.length()-3,nStr.length());	
	}
		
	
	public void drawPieText(String message, float x, float y, float r, float a, Color color) {
		 // We must keep track of our position along the curve
		  float arclength = 0;
		  // For every box
		  for (int i = 0; i < message.length(); i ++ ) {
		    // The character and its width
		    char currentChar = message.charAt(i);
		    // Instead of a constant width, we check the width of each character.
		    float w = textWidth(currentChar); 
		    // Each box is centered so we move half the width
		    arclength += w/2;
		    
		    // Angle in radians is the arclength divided by the radius
		    // Starting on the left side of the circle by adding PI
		    float theta = a+arclength / r;
		    
		    pushMatrix();
		    
		    // Polar to Cartesian conversion allows us to find the point along the curve. See Chapter 13 for a review of this concept.
		    translate(x+r*cos(theta), y+r*sin(theta)); 
		    // Rotate the box (rotation is offset by 90 degrees)
		    rotate(theta + PI/2); 
		    
		    // Display the character
		    fill(color.getRGB());
		    text(currentChar,0,0);
		    //System.out.println(currentChar+" "+r*cos(theta));
		    popMatrix();
		    
		    // Move halfway again
		    arclength += w/2;
		  }
	}
		
	public void drawPieChart() {
		PFont f = createFont("Courier",40,true);
		textAlign(PApplet.LEFT);
		float lastAng = -PApplet.PI/2;
		strokeWeight(1);
		textFont(BBP.font, 10+100/20);
		
		float r = 250;
		float x = 175;
		float y = 300;
		if (dist(x,y,mouseX,mouseY)<=r/2)
			isActive = true;
		else
			isActive = false;
			
		float a = PApplet.atan((mouseY-y)/(mouseX-x));
		if (mouseX<x)
			a += PI;
		
		bPie = -1;
		bSec = -1;
		
		
		
		
		float maxH = 200;  
		float w = 400;  
		
		float maxCount = Float.MIN_VALUE;
		for (int m =0; m<nM; m++){
			if (satCountByTime[m]>maxCount){
				maxCount = satCountByTime[m];
			}
		}
		// Find the last point
		int lastPoint =0;
		for (int m =0; m<nM; m++){
			if (satCountByTime[m]>0){
				lastPoint = m;
			}
		}	
		
		fill(Color.WHITE.getRGB());
		noStroke();
		for (int m =0; m<=lastPoint;m++){
			float step = 1.4f;
			for (int i=0; i<satCountByTime[m];i++){
				float xG = 50 + m*step;
				float yG = 650 - i*step;
				this.ellipse(xG+2f, yG-1, step, step);
			}
		}
		
		for (int i = 0; i < 9; i++){
			float nextAng = lastAng+PApplet.PI*2/9;
			
			Color color =scagColors[i];
			boolean isOn = isActive && lastAng<=a && a<nextAng;
			if (isOn){
				//color = color.brighter();
				bPie =i;
				bSec = 2;
				if (dist(x,y,mouseX,mouseY)<=scagHigh[i]*r/2){
					bSec = 1;
				}
				if (dist(x,y,mouseX,mouseY)<=scagLow[i]*r/2){
					bSec = 0;
				}
				stroke(255,255,255);
				strokeWeight(2f);
			}
			else
				noStroke();
			
			// Outsider
			noStroke();
			fill(color.getRed(),color.getGreen(),color.getBlue(),80);
			arc(x,y, r, r, lastAng, nextAng);
						
			DecimalFormat df = new DecimalFormat("#.##");
			// High filter
			if (i==sPie && sSec>=1){
				stroke(255,0,255);
				strokeWeight(2f);
				
				textFont(f,12);
				drawPieText(df.format(iSH[i].value), x,y,iSH[i].value*r/2+3, lastAng, Color.MAGENTA);
			}
			else if (isOn && bSec>=1){
				stroke(255,255,255);
				strokeWeight(2f);
				textFont(f,12);
				drawPieText(df.format(iSH[i].value), x,y,iSH[i].value*r/2+3, lastAng, Color.WHITE);
			}
			else
				noStroke();
			fill(color.getRGB());
			arc(x,y, iSH[i].value*r, iSH[i].value*r, lastAng, nextAng);
			
			// Low Filter
			if (i==sPie && sSec<=1){
				stroke(255,0,255);
				strokeWeight(2f);
			}
			else if (isOn && bSec<=1){
				stroke(255,255,255);
				strokeWeight(2f);
			}
			else
				noStroke();
			fill(0,0,0,135);
			arc(x,y, iSL[i].value*r, iSL[i].value*r, lastAng, nextAng);
			
			
			if (i==sPie && sSec<=1){
				textFont(f,12);
				drawPieText(df.format(iSL[i].value), x,y,iSL[i].value*r/2-11, lastAng, Color.MAGENTA);
			}
			else if (isOn && bSec<=1){
				textFont(f,12);
				drawPieText(df.format(iSL[i].value), x,y,iSL[i].value*r/2-11, lastAng, Color.WHITE);
			}
				
			this.textFont(f,15);
			drawPieText(scagNames[i], x,y,r/2+5, lastAng, color.brighter());
			
		    lastAng = nextAng;  
		}
	}
	
	public void drawBarChart() {
		DecimalFormat df = new DecimalFormat("#.##");
		
		textAlign(PApplet.RIGHT);
		strokeWeight(1);
		textFont(this.metaBold, 12);
		
		float w = 250;
		float x = 105;
		float y = 80;
		float h = 68;
		float h2 = 20;
		
		float maxH = h*1.f;  
		float maxCount = Float.MIN_VALUE;
		for (int sc = 0; sc < 9; sc++){
			for (int l =0; l<numPoints;l++){
				if (satCount[sc][l]>maxCount){
					maxCount = satCount[sc][l];
				}
			}
		}	
		
		bPie = -1;
		bSec = -1;
		for (int sc = 0; sc < 9; sc++){
			Color color =scagColors[sc];
			float yy = y+sc*h ;
			
			// Draw scagnostics names
			textFont(this.metaBold, 14);
			this.fill(color.getRGB());
			this.textAlign(PApplet.RIGHT);
			text(scagNames[sc], x-6,yy+h2/2+5);
			strokeWeight(1f);
		
			// Find the last point
			int lastPoint =0;
			for (int l =0; l<numPoints;l++){
				if (satCount[sc][l]>0){
					lastPoint = l;
				}
			}	
			
			
			if (kSatisfied> BBP2.maxBalls){
				// Draw chart on top of bar
				beginShape();
				float xG = x;
				float yG = yy;
				curveVertex(xG, yG);
				curveVertex(xG, yG);
				for (int l =0; l<=lastPoint;l++){
					xG = x + l*w/numPoints;
					yG = yy - maxH*(satCount[sc][l]/maxCount);
					if (satCount[sc][l]>0){
						curveVertex(xG+3f, yG-2);
					}
					else{
						curveVertex(xG+3f, yG);
					}
				}
				xG = xG+3;
				yG = yy;
				curveVertex(xG, yG);
				curveVertex(xG, yG);
				stroke(new Color(100, 100, 100, 200).getRGB());
				strokeWeight(1);
				fill(Color.WHITE.getRGB());
				endShape();
			}
			else{
				fill(Color.WHITE.getRGB());
				noStroke();
				for (int l =0; l<=lastPoint;l++){
					float step = w/numPoints;
					for (int i=0; i<satCount[sc][l];i++){
						float xG = x + l*step;
						float yG = yy - i*step;
						this.ellipse(xG+2f, yG-1, step, step);
					}
				}
			}
			
					
			boolean isOn = false;
			if (x<mouseX && mouseX<x+w && yy <mouseY && mouseY<yy+h)
				isOn = true;
			
			if (isOn){
				bPie =sc;
				bSec = 2;
				if (x<mouseX && mouseX<x+scagHigh[sc]*w){
					bSec = 1;
				}
				if (x<mouseX && mouseX<x+scagLow[sc]*w){
					bSec = 0;
				}
				stroke(255,255,255);
				strokeWeight(2f);
			}
			else
				noStroke();
			
			// Outsider
			noStroke();
			strokeWeight(1f);
			fill(color.getRed(),color.getGreen(),color.getBlue(),80);
			rect(x,yy, w, h2);
			
			// High filter
			fill(color.getRGB());
			noStroke();
			rect(x,yy, iSH[sc].value*w, h2);
			float xxH = x+iSH[sc].value*w;
			if (sc==sPie && sSec>=1){
				stroke(255,0,255);
				strokeWeight(2f);
				this.fill(Color.MAGENTA.getRGB());
				textFont(this.metaBold, 12);
				this.textAlign(PApplet.LEFT);
				this.text(df.format(iSH[sc].value), xxH+3,yy+15);
			}
			else if (isOn && bSec>=1){
				stroke(255,255,255);
				strokeWeight(2f);
				this.fill(Color.WHITE.getRGB());
				textFont(this.metaBold, 12);
				this.textAlign(PApplet.LEFT);
				this.text(df.format(iSH[sc].value), xxH+3,yy+15);
			}
			else
				noStroke();
			line(x+iSH[sc].value*w,yy, xxH, yy+h2-1);
			
			
			// Low Filter
			fill(0,0,0,135);
			noStroke();
			rect(x,yy, iSL[sc].value*w, h2);
			float xxL = x+iSL[sc].value*w;
			if (sc==sPie && sSec<=1){
				stroke(255,0,255);
				strokeWeight(2f);
				this.fill(Color.MAGENTA.getRGB());
				textFont(this.metaBold, 12);
				this.textAlign(PApplet.RIGHT);
				this.text(df.format(iSL[sc].value), xxL-3,yy+15);
			}
			else if (isOn && bSec<=1){
				stroke(255,255,255);
				strokeWeight(2f);
				this.fill(Color.WHITE.getRGB());
				textFont(this.metaBold, 12);
				this.textAlign(PApplet.RIGHT);
				this.text(df.format(iSL[sc].value), xxL-3,yy+15);
			}
			else
				noStroke();
			line(x+iSL[sc].value*w,yy, xxL, yy+h2-1);
			
			
			
			if (sc==sPie && sSec<=1){
				//drawPieText(df.format(iSL[i].value), x,y,iSL[i].value*r/2-11, lastAng, Color.MAGENTA);
			}
			else if (isOn && bSec<=1){
				//drawPieText(df.format(iSL[i].value), x,y,iSL[i].value*r/2-11, lastAng, Color.WHITE);
			}
			
			
		}
	}
	
	public void drawSearchAllSpace() {
		if (search!=3)  return;
		
		textFont(metaBold,12);
		slider4.draw();
		
		int oo = bO2;
		int mm = bM2;
		int pp = bP2;
		if (found1){
			oo = bO1;
			mm = bM1;
			pp = bP1;
		}
			
		found3=false;
		bO3 = -1;
		bP3 = -1;
		bM3 = -1;
		
		if (found1 || found2){
			// Compute disimilarity
			for (int i =0; i<n;i++){
				disArray1[i] = new ArrayList<Integer>();
				disArray2[i] = new ArrayList<Integer>();
			}
			
			disArray = new int[numP][nM];
			
			float[][] sum = new float[numP][nM];
			for (int p =0; p<numP;p++){
				for (int m=0;m<nM;m++){
					for (int sc =0; sc<9;sc++){
						float dif = scagVals[m][sc][p]-scagVals[mm][sc][pp];
						sum[p][m] += dif*dif*dif*dif;
					}
					sum[p][m] = (float) Math.pow(sum[p][m],0.25f);
					disArray[p][m] =  (int) (sum[p][m]*100);
					disArray1[disArray[p][m]].add(p); 
					disArray2[disArray[p][m]].add(m); 
				}
			}
			
			// Draw BBP
			if (search==3 && (oo!=bO12 || mm!=bM12 || pp!=bP12)){
				bO12 =oo;
				bM12 = mm;
				bP12 = pp;
				hm = new HashMap<String,Integer>(); 
				hData = new ArrayList<String>();
				
				int count=0;
				for (int dis =0; dis<BBP.maxDisimilarity && count<BBP.MAX_BALL_SHOWING;dis++){
					for (int p =0; p<numP && count<BBP.MAX_BALL_SHOWING;p++){
						for (int m=0;m<nM && count<BBP.MAX_BALL_SHOWING;m++){
							if (disArray[p][m]==dis){
								String key = p+" "+p+" "+m;
								hData.add(new String(key));
								int occur = disArray[p][m];
								if (occur<0) occur=0;
								hm.put(key, new Integer(occur));
								count++;
							}
						}
					}
				}
				slider4.updateMaxBalls();
				bbp3 = new BBP(this,hData,200,0,plotY2,15);
				bbp3.setData(hm);
			}
		}
		
		
		// Draw text
		int x41 = 130;
		int x42 = 180;
		int y41 = 150;
		int y42 = 172;
		int y43 = 194;
		
		textAlign(PApplet.LEFT);
		textFont(metaBold,14);
		fill(255,255,255);
		this.textAlign(RIGHT);
		text("Showing plots: ",x41,y41);
		text("Satisfied plots: ",x41,y42);
		text("Search space: ",x41,y43);
		text(formatIntegerThousand(BBP.bubble_plots),x42,y41);
		text(formatIntegerThousand(BBP.sasBalls),x42,y42);
		text(formatIntegerThousand(numP*(nM)),x42,y43);
		this.textAlign(LEFT);
		
		
		// Draw Dissimilarity above slider
		fill(255,255,255);
		textFont(metaBold,15);
		this.text("Dissimilarity",26,slider4.y-10);
		
		float xx = slider4.x;
		float yy = slider4.y;
		float g1 = sgap1;
		for (int i = 0; i < BBP.maxDisimilarity; i++) {
			xx = slider4.x;
			yy = slider4.y + i*g1;
			float val = ((float) BBP.maxDisimilarity - i)/BBP.maxDisimilarity;
			Color color = ColorScales.getColor(val, "temperature", 1f);
			fill(color.getRGB());
			rect(xx,yy,g1*2,g1+1);
		}
		
		
		// Draw scatterplot above slider
		if (found1 || found2){
			float g2 = sgap1;
			int numDotInStack =0;
			int oldDis =-1;
			for (int i = 0; i < hData.size(); i++) {
				String key = hData.get(i);
				int dis =  hm.get(key);
				
				if (dis !=oldDis){
					numDotInStack =0;
					oldDis=dis;
				}
				else{
					numDotInStack++;
				}
				yy = slider4.y + dis*g2 + g2/2;
				xx = slider4.x+ g2*(numDotInStack+2) + 4; 
				
				float val = ((float) BBP.maxDisimilarity - dis)/BBP.maxDisimilarity;
				if (val<0) val=0;
				else if (val>1) val=1;
				Color color = ColorScales.getColor(val, "temperature", 1f);
				fill(color.getRGB());
					
					
					iX4[i].target(xx);
					iY4[i].target(yy);
					if (dis*(sgap1)>slider4.u){
						iX4[i].target(-20);
					}		
					iX4[i].update();
					iY4[i].update();
					float x33 = iX4[i].value;
					float y33 = iY4[i].value;
					if (i==0){
						stroke(255,255,0);
						strokeWeight(1f);
						this.ellipse(x33,y33-2,g2,g2);
						noStroke();
					}
					else
						this.ellipse(x33,y33-2,g2,g2);
					
				
		
				
			}	
		}
		
	}
	
	public void drawSearchAllSelected() {
		if (search!=2) return;
		this.noStroke();
		fill(0,0,0,220);
		rect(0,slider2.y-155,1440,200);
		fill(255,0,255);
		textFont(metaBold,15);
		this.text("Dissimilarity",35,slider2.y);
		textFont(metaBold,12);
		slider2.draw();
		
		int oo = bO2;
		int mm = bM2;
		int pp = bP2;
		if (found1){
			oo = bO1;
			mm = bM1;
			pp = bP1;
		}
		found3=false;
		bO3 = -1;
		bP3 = -1;
		bM3 = -1;
		
		if (found1 || found2){
			// Compute disimilarity
			for (int i =0; i<n;i++){
				disArray1[i] = new ArrayList<Integer>();
				disArray2[i] = new ArrayList<Integer>();
			}
			
			float[][] sum = new float[selectedPlots.size()][nM];
			for (int p1 =0; p1<selectedPlots.size();p1++){
				int p =p1;
				if (p1==0 && selectedPlots.size()>=3)
					p=2;
				else if (p1==2)
					p=0;
				int pair = selectedPlots.get(p);
				pair = plots[pair].pair;
				//System.out.println(p1+"  "+pair);
				for (int d=0;d<nM;d++){
					for (int sc =0; sc<9;sc++){
						float dif = scagVals[d][sc][pair]-scagVals[mm][sc][pp];
						if (sc==sS)
							sum[p][d] += 2*dif*dif;
						else
							sum[p][d] += 0.2f*dif*dif;			
					}
					sum[p][d] = sqrt(sum[p][d]);
					int index = (int) (sum[p][d]*100);
					disArray1[index].add(p); 
					disArray2[index].add(d); 
					
					if (sum[p][d]<slider2.uM){
						fill(getSearchColor(sum[p][d]).getRGB());
						float x3 = xxx1[d];
						float x4 = xxx1[d]+www1[d];
						float y3 = yyy1[p]-5;
						float y4 = yyy1[p];
						if (pair==pp && d==mm){
							stroke(255,255,0);
							strokeWeight(1f);
							rect(x3,y3-4,www1[d]-1,12);
							noStroke();
						}
						else
							rect(x3,y3,www1[d],5);
							
						if (x3<mouseX && mouseX<x4 && y3-5<mouseY && mouseY<y4+5){
							found3=true;
							bO3 = p;
							bP3 = pair;
							bM3 = d;
							stroke(180,180,180);
							rect(x3,y3-4,www1[d],12);
							noStroke();
						}
					}
				}	
			}
			// Draw Dissimilarity above slider
			float xx = slider2.x;
			float yy = slider2.y;
			float g1 = sgap1;
			for (int i = 0; i < 200; i++) {
				xx = slider2.x + i*g1;
				yy = slider2.y - g1;
				fill(getSearchColor((float) i/100).getRGB());
				if (xx>slider2.u+slider2.x)
					fill(25,25,0);
				rect(xx,yy,g1,g1);
			}
			
			// Draw scatterplot above slider
			float g2 = sgap1;
			for (int i = 0; i < n; i++) {
				xx = slider2.x + i*g2;
				for (int j=0;j<disArray1[i].size();j++){
					int o = disArray1[i].get(j);
					int m = disArray2[i].get(j);
					if (o<3)
						fill(colors[o][m].getRGB());
					else{
						int p = plots[o].pair;
						float sat = 40+scagVals[m][sS][p]*100;
						fill(sat,sat,sat);
					}	
					yy = slider2.y - j*(g2-1f)-10;
					
					iPX3[o][m].target(xx);
					iPY3[o][m].target(yy);
					if (i*(sgap1)>slider2.u){
						iPY3[o][m].target(760);
					}		
					iPX3[o][m].update();
					iPY3[o][m].update();
					float x33 = iPX3[o][m].value;
					float y33 = iPY3[o][m].value;
					if (m==mm && o==oo){
						stroke(255,255,0);
						strokeWeight(1f);
						rect(x33,y33-2,g2+0.5f,g2+0.5f);
						noStroke();
					}
					else{
						rect(x33,y33,g2-0.4f,g2-1f);
					}
					
					if (x33<=mouseX && mouseX<=x33+g2 && y33<=mouseY && mouseY<=y33+g2-1){
						found3=true;
						bO3 = o;
						int pair = selectedPlots.get(o);
						pair = plots[pair].pair;
						bP3 = pair;
						bM3 = m;
						stroke(180,180,180);
						rect(xxx1[m]-1,yyy1[o]-9,www1[m]+2,12);
						noStroke();
					}
				}
			}
		}
		drawSearchAllPlot();
	}
	
	public void drawSearchAllPlot() {
		if (found3){
			float w3 = 340;
			float h3 = 260;
			stroke(200,200,200);
			fill(30,30,30,250);
			iX3.target(xxx1[bM3]-w3/2-75);
			iY3.target(yyy1[bO3]+5);
			iX3.update();
			iY3.update();
			float x3 = iX3.value;
			float y3 = iY3.value;
			rect(x3,y3,w3,h3);
			
			for (int p =0; p<nP1;p++){
				iP1[p].target(sP1);
				iP1[p].update();
			}
			float hP1 = iP1[0].value*2/3;
			iC1[0][0].target((int) colors[bO2][bM2].getRed());
			iC1[0][1].target((int) colors[bO2][bM2].getGreen());
			iC1[0][2].target((int) colors[bO2][bM2].getBlue());
			iC1[0][0].update();
			iC1[0][1].update();
			iC1[0][2].update();
			float xRect = x3+40;
			float yRect = y3+h3/2+5 ;
			stroke(255,255,0);
			strokeWeight(2.0f);
			fill(iC1[0][0].value,iC1[0][1].value,iC1[0][2].value);
			rect(xRect,yRect,hP1,hP1);
			
			stroke(125,125,125,225);
			this.strokeWeight(.9f);
			int[] index = PlotSPLOM.pairToIndex(bP2);
			
			for (int s = 0; s < numS; s++) {
				iPX1[0][s].target(hP1*data[bM2][index[0]][s]*0.85f);
				iPY1[0][s].target(hP1*(1-data[bM2][index[1]][s])*0.85f);
				iPX1[0][s].update();
				iPY1[0][s].update();
				fill(Color.BLACK.getRGB());
				ellipse(8 + xRect+iPX1[0][s].value, 8 + yRect+iPY1[0][s].value, 6, 6);
			}
			
			// Draw Labels
			fill(200,200,200);
			textFont(metaBold,11);
			int y = bM2/12 + 1990;
			int m = bM2%12;
			text(months[m]+" "+y,xRect+28,yRect-5);
			text(varlist[index[0]],xRect,yRect+hP1+13);
			translate(xRect-5,yRect+hP1);
			rotate((float) (-PI/2.));
			text(varlist[index[1]],0,0);
			rotate((float) (PI/2.));
			translate(-(xRect-5),-(yRect+hP1));
			
			// Draw Scagnostics
			float w1 = hP1/9;
			float gap1 = 23;
			noStroke();
			for (int sc =0; sc<9;sc++){
				iPS1[0][sc].target(hP1*scagVals[bM2][sc][bP2]);;
				iPS1[0][sc].update();
			}
			for (int sc = 0; sc < 9; sc++) {
				if (sc==sS)
					fill(iC1[0][0].value,iC1[0][1].value,iC1[0][2].value);
				else
					fill(125,125,125);
				this.rect(xRect+w1*sc, yRect-iPS1[0][sc].value-gap1, w1-1, iPS1[0][sc].value+4);
			}	
			
			
			// Draw the SECOND scatterplot
			float xRect2 = x3+w3/2+30;
			if (bO3<3){
				iC1[1][0].target((int) colors[bO3][bM3].getRed());
				iC1[1][1].target((int) colors[bO3][bM3].getGreen());
				iC1[1][2].target((int) colors[bO3][bM3].getBlue());
				iC1[1][0].update();
				iC1[1][1].update();
				iC1[1][2].update();
				fill(iC1[1][0].value,iC1[1][1].value,iC1[1][2].value);
			}
			else{
				float sat = 40+scagVals[bM3][sS][bP3]*100;
				fill(sat,sat,sat);
			}
			stroke(125,125,125,225);
			this.strokeWeight(.9f);
			rect(xRect2,yRect,hP1,hP1);
			
			stroke(125,125,125,225);
			this.strokeWeight(.9f);
			int[] index2 = PlotSPLOM.pairToIndex(bP3);
			
			for (int s = 0; s < numS; s++) {
				iPX1[1][s].target(hP1*data[bM3][index2[0]][s]*0.85f);
				iPY1[1][s].target(hP1*(1-data[bM3][index2[1]][s])*0.85f);
				iPX1[1][s].update();
				iPY1[1][s].update();
				fill(Color.BLACK.getRGB());
				ellipse(8 + xRect2+iPX1[1][s].value, 8 + yRect+iPY1[1][s].value, 6, 6);
			}
			
			// Draw Labels
			fill(200,200,200);
			textFont(metaBold,11);
			y = bM3/12 + 1990;
			m = bM3%12;
			text(months[m]+" "+y,xRect2+28,yRect-5);
			text(varlist[index2[0]],xRect2,yRect+hP1+13);
			translate(xRect2-5,yRect+hP1);
			rotate((float) (-PI/2.));
			text(varlist[index2[1]],0,0);
			rotate((float) (PI/2.));
			translate(-(xRect2-5),-(yRect+hP1));
			
			// Draw Scagnostics
			noStroke();
			for (int sc =0; sc<9;sc++){
				iPS1[1][sc].target(hP1*scagVals[bM3][sc][bP3]);;
				iPS1[1][sc].update();
			}
			float min =10000;
			for (int sc = 0; sc < 9; sc++) {
				if (sc==sS){
					if (bO3<3)
						fill(iC1[1][0].value,iC1[1][1].value,iC1[1][2].value);
					else{
						fill(100,100,100);
						float sat = 40+scagVals[bM3][sS][bP3]*100;
						fill(sat,sat,sat);
					}
				}
				
				else
					fill(125,125,125);
				rect(xRect2+w1*sc, yRect-iPS1[1][sc].value-gap1, w1-1, iPS1[1][sc].value+4);
				if (yRect-iPS1[1][sc].value-gap1<min)
					min=yRect-iPS1[1][sc].value-gap1;
			}	
			
			float dis = 0;
			for (int sc =0; sc<9;sc++){
				float dif = scagVals[bM2][sc][bP2]-scagVals[bM3][sc][bP3];
				if (sc == sS)
					dis += 2*dif*dif;
				else
					dis += 0.2f*dif*dif;		
			}
			dis = sqrt(dis);
			
			fill(255,0,255);
			DecimalFormat df = new DecimalFormat("#.##");
			if (min<y3+20)
				min=y3+20;
			text("Dissimilarity = " + df.format(dis),xRect2,min-4);
			
		}
	}	
	
	public Color getSearchColor(float val) {
		float v = ((1.3f-val))*255;
		if (v<30) v=30;
		else if (v>255) v=255;
		 return new Color((int) v,0,(int) v);
		
	}
		
	
	public void drawDetails() {
		if (c6.s>0){
			iW4.target(w1+40);
			iW4.update();
		}
		else{
			iW4.target(0);
			iW4.update();
			return;
		}
		int bO_ = bO2;
		int bP_ = bP2;
		if (found1){
			bO_ = bO1;
			bP_ = bP1;
		}
		
		
		if (bP_>=0){
			//Remove inActive series
			float max = stepX2*l;
			noStroke();
			int[] index = PlotSPLOM.pairToIndex(bP_);
			for (int i=0; i<3;i++){
				if (i!=bO_){
					fill(0,0,0,200);
					rect(plotW2,max*i+2,1155,max);
					float www = this.textWidth(tx[i]);
					if (i+1==bO_ || i==2){
						rect(x4[i], y4[i],www+5,12);
					}
				}
			}
			textFont(metaBold,10);
			double al = PI/16.;
			translate(x4[bO_],y4[bO_]);
			rotate((float) (-al));
			fill(color2.getRGB());
			text(ty[bO_], 0, 0);
			rotate((float) (al));
			translate(-(x4[bO_]),-(y4[bO_]));
			fill(color1.getRGB());
			text(tx[bO_], x4[bO_], y4[bO_]+10);
			
			float[] yyy = new float[2];
			yyy[0] = plotY2+160;
			yyy[1] = plotY2+400;
				
			textFont(metaBold,12);
			al = PI/8.;
			translate(10,yyy[0]+10);
			rotate((float) (-al));
			fill(color2.getRGB());
			text(ty[bO_], 0, 0);
			rotate((float) (al));
			translate(-(10),-(yyy[0]+10));
			fill(color1.getRGB());
			text(tx[bO_], 10, yyy[1]+3);
			
			DecimalFormat df = new DecimalFormat("#.##");
			textFont(metaBold,11);
			for (int i=0;i<2;i++){
					float pX = Plot.x[0];
					float[] pY = new float[numS];
					for (int s = 0; s < numS; s++) {
						pY[s] = yyy[i]-40*data2[0][index[1-i]][s];
					}
					
					for (int m=1; m<nM;m++){
						float pX2 = Plot.x[m];
						if (pX2 > iW4.value+50) break;
						stroke(color12[1-i].getRGB());
						this.strokeWeight(1f);
						float xb = 0;
						float yb = 0;
						
						for (int s = 0; s < numS; s++) {
								float pY2 = yyy[i] -ySc.value*data2[m][index[1-i]][s];
								line(pX, pY[s],pX2,pY2);
								if (s==bS){
									xb = pX;
									yb = pY[s];
								}
								pY[s] =pY2;
							
						}
						if (found){
							this.strokeWeight(2f);
							stroke(255,255,0);
							float val = data2[m-1][index[1-i]][bS];
							line(xb, yb,pX2,pY[bS]);
							
							if (pX2-pX>15){
								String tex = df.format(val);
								this.fill(255,255,255);
								if(val>=0)
									this.text(tex, xb-5,yb-2);
								else
									this.text(tex, xb-5,yb+11);
							}
						}
						pX = pX2;
					}
				}
		}	
		
	}	
	public void drawSearch5() {
		if (search==1){
			iW1.target(w1);
			iW1.update();
		}
		else{
			iW1.target(0);
			iW1.update();
			return;
		}
		stroke(125,125,125);
		fill(0,0,0,220);
		rect(x1,y1,iW1.value,h1);
		if (found1 || found2){
			for (int p =0; p<nP1;p++){
				iP1[p].target(sP1);
				iP1[p].update();
			}
			int bO_ = bO2;
			int bD_ = bM2;
			int bP_ = bP2;
			if (found1){
				bO_ = bO1;
				bD_ = bM1;
				bP_ = bP1;
			}
			float hP1 = iP1[0].value;
			iC1[0][0].target((int) colors[bO_][bD_].getRed());
			iC1[0][1].target((int) colors[bO_][bD_].getGreen());
			iC1[0][2].target((int) colors[bO_][bD_].getBlue());
			iC1[0][0].update();
			iC1[0][1].update();
			iC1[0][2].update();
			fill(iC1[0][0].value,iC1[0][1].value,iC1[0][2].value);
			float xRect = x1+gx1;
			float yRect = y1+gy1;
			stroke(255,255,0);
			strokeWeight(2.8f);
			rect(xRect,yRect,hP1,hP1);
			// Draw scagnostics Name
			textFont(metaBold,24);
			text(scagNames[sS],x1+25,y1+30);
			
			stroke(125,125,125,225);
			this.strokeWeight(.9f);
			int[] index = PlotSPLOM.pairToIndex(bP_);
			
			for (int s = 0; s < numS; s++) {
				iPX1[0][s].target(11 + xRect+hP1*data[bD_][index[0]][s]*0.87f);
				iPY1[0][s].target(11 + yRect+hP1*(1-data[bD_][index[1]][s])*0.87f);
				iPX1[0][s].update();
				iPY1[0][s].update();
				fill(Color.BLACK.getRGB());
				ellipse(iPX1[0][s].value, iPY1[0][s].value, 7, 7);
				
				if (dist(mouseX, mouseY, iPX1[0][s].value, iPY1[0][s].value) < 8) {
					bO = bO_;
					bD = bD_ ;
					bS =s ;
					found = true;
				}
			}
			
			// Draw Labels
			fill(200,200,200);
			textFont(metaBold,12);
			int y = bD_/12 + 1990;
			int m = bD_%12;
			text(months[m]+" "+y,xRect+55,yRect-5);
			text(varlist[index[0]],xRect+1,yRect+hP1+13);
			translate(xRect-7,yRect+hP1-1);
			rotate((float) (-PI/2.));
			text(varlist[index[1]],0,0);
			rotate((float) (PI/2.));
			translate(-(xRect-7),-(yRect+hP1-1));
					
			// Draw Scagnostics
			float w1 = hP1/9;
			float gap1 = 23;
			noStroke();
			for (int sc =0; sc<9;sc++){
				iPS1[0][sc].target(hP1*scagVals[bD_][sc][bP_]);;
				iPS1[0][sc].update();
			}
			for (int sc = 0; sc < 9; sc++) {
				if (sc==sS)
					fill(iC1[0][0].value,iC1[0][1].value,iC1[0][2].value);
				else
					fill(125,125,125);
				this.rect(xRect+w1*sc, yRect-iPS1[0][sc].value-gap1, w1-1, iPS1[0][sc].value+4);
			}	
			
			// Compute disimilarity
			/*float[] sum = new float[nM];
			for (int d=0;d<nM;d++){
				for (int sc =0; sc<9;sc++){
					float dif = scagVals[d][sc][bP_]-scagVals[bD_][sc][bP_];
					if (sc==0)
					sum[d] += dif*dif;
				}
				sum[d] = sqrt(sum[d]);	
			}*/
			
			
			float[][] sum = new float[selectedPlots.size()][nM];
			int bOrder = -1;
			for (int p =0; p<selectedPlots.size();p++){
				int pair = selectedPlots.get(p);
				pair = plots[pair].pair;
				if (pair==bP_)
					bOrder = p;
				for (int d=0;d<nM;d++){
					for (int sc =0; sc<9;sc++){
						float dif = scagVals[d][sc][pair]-scagVals[bD_][sc][bP_];
						if (sc==sS)
							sum[p][d] += 2*dif*dif;
						else
							sum[p][d] += 0.2f*dif*dif;			
					}
					sum[p][d] = sqrt(sum[p][d]);
				}
				
			}	
			//Search for top 5
			int[] top = search2(sum,selectedPlots.size(), bD_,bOrder);
			this.noStroke();
			DecimalFormat df = new DecimalFormat("#.##");
			float min =400;
			for (int i=1;i<nP1;i++){
				int order = top[nP1-1+i-1];
				if (order>=selectedPlots.size()|| order<0) continue;
				int pair = selectedPlots.get(order);
				pair = plots[pair].pair;
				index = PlotSPLOM.pairToIndex(pair);
				
				hP1 = iP1[i].value;
				if (order<3){
					iC1[i][0].target((int) colors[order][top[i-1]].getRed());
					iC1[i][1].target((int) colors[order][top[i-1]].getGreen());
					iC1[i][2].target((int) colors[order][top[i-1]].getBlue());
				}
				else{
					float val = scagVals[top[i-1]][sS][pair] *100+100;
					iC1[i][0].target((int) val);
					iC1[i][1].target((int) val);
					iC1[i][2].target((int) val);
				}
				iC1[i][0].update();
				iC1[i][1].update();
				iC1[i][2].update();
				fill(iC1[i][0].value,iC1[i][1].value,iC1[i][2].value);
				xRect = x1+gx1 + (hP1+37)*i+45;
				yRect = y1+gy1;
				rect(xRect,yRect,hP1,hP1);
				
				stroke(125,125,125,225);
				this.strokeWeight(.9f);
				fill(Color.BLACK.getRGB());
				
				for (int s = 0; s < numS; s++) {
					iPX1[i][s].target(11 + xRect+hP1*data[top[i-1]][index[0]][s]*0.87f);
					iPY1[i][s].target(11 + yRect+hP1*(1-data[top[i-1]][index[1]][s])*0.87f);
					iPX1[i][s].update();
					iPY1[i][s].update();
					this.ellipse(iPX1[i][s].value, iPY1[i][s].value, 7, 7);
					
					if (dist(mouseX, mouseY, iPX1[i][s].value, iPY1[i][s].value) < 8) {
						bO = bO_;
						bD = bD_ ;
						bS = s ;
						found = true;
					}
					
				}			
				
				// Draw Labels
				fill(200,200,200);
				textFont(metaBold,12);
				y = top[i-1]/12 + 1990;
				m = top[i-1]%12;
				text(months[m]+" "+y,xRect+55,yRect-5);
				text(varlist[index[0]],xRect+1,yRect+hP1+13);
				translate(xRect-5,yRect+hP1-1);
				rotate((float) (-PI/2.));
				text(varlist[index[1]],0,0);
				rotate((float) (PI/2.));
				translate(-(xRect-5),-(yRect+hP1-1));
				
				// Draw Scagnostics
				noStroke();
				for (int sc =0; sc<9;sc++){
					iPS1[i][sc].target(hP1*scagVals[top[i-1]][sc][pair]);;
					iPS1[i][sc].update();
				}
				for (int sc = 0; sc < 9; sc++) {
					if (sc==sS)
						fill(iC1[i][0].value,iC1[i][1].value,iC1[i][2].value);
					else
						fill(125,125,125);
					float yy = yRect-iPS1[i][sc].value-gap1;
					this.rect(xRect+w1*sc, yy, w1-1, iPS1[i][sc].value+4);
					if (yy<min)
						min=yy;
				}	
			}
			// Draw brushing point in selected scatterplot and 5 scatterplots
			if (found){
				stroke(0,0,0);
				int ccc = (count*25)%255+50;
				if (ccc>255) ccc=255;
				fill(ccc,ccc,0);
				ellipse(iPX1[0][bS].value, iPY1[0][bS].value, 10, 10);

				noStroke();
				fill(new Color(0,0,0,200).getRGB());
				textFont(metaBold,13);
				float wText = textWidth(statelist[bS]);
				rect(iPX1[0][bS].value+7, iPY1[0][bS].value-10, wText+10, 17);		
				fill(Color.YELLOW.getRGB());
				text(statelist[bS],iPX1[0][bS].value+13, iPY1[0][bS].value+2);
				
				for (int i=1;i<nP1;i++){
					if (found){
						stroke(0,0,0);
						strokeWeight(1.f);
						fill(ccc,ccc,0);
						ellipse(iPX1[i][bS].value, iPY1[i][bS].value, 10, 10);
					}
					
				}	
			}
			
			
			// For PICTURE in paper
		/*	int st1 = 16;
			noStroke();
			fill(new Color(0,0,0,200).getRGB());
			textFont(metaBold,13);
			float wText2 = textWidth(statelist[st1]);
			rect(iPX1[0][st1].value+8, iPY1[0][st1].value-10, wText2+10, 17);		
			fill(255,255,0);
			text(statelist[st1],iPX1[0][st1].value+14, iPY1[0][st1].value+2);
			for (int i=0;i<nP1;i++){
				stroke(0,0,0);
				strokeWeight(1.f);
				ellipse(iPX1[i][st1].value, iPY1[i][st1].value, 10, 10);
			}
			
			int st2 = 22;
			noStroke();
			fill(new Color(0,0,0,200).getRGB());
			textFont(metaBold,13);
			float wText = textWidth(statelist[st2]);
			rect(iPX1[0][st2].value+8, iPY1[0][st2].value, wText+10, 17);		
			fill(0,255,255);
			text(statelist[st2],iPX1[0][st2].value+14, iPY1[0][st2].value+12);
			for (int i=0;i<nP1;i++){
				stroke(0,0,0);
				strokeWeight(1.f);
				fill(0,255,255);
				ellipse(iPX1[i][st2].value, iPY1[i][st2].value, 10, 10);
			}
			*/
			
			
			// Draw disimilarity
			for (int i=1;i<nP1;i++){
				xRect = x1+gx1 + (hP1+37)*i+45;
				fill(255,0,255);
				if (top[i-1]>=0)
					text("Dissimilarity = "+ df.format(sum[top[nP1-1+i-1]][top[i-1]]),xRect+16,min-4);
			}
				
			
		}
		else{
			for (int p =0; p<nP1;p++){
				iP1[p].target(0);
				iP1[p].update();
			}
		}
	}
	
	public boolean isIn(int index, int[] a) {
		for (int i=0;i<a.length;i++){
			if (a[i]==index)
				return true;
		}
		return false;
	}
	public boolean isIn2(int index, int order, int[] a) {
		for (int i=0;i<nP1-1;i++){
			if (a[i]==index && a[nP1-1+i]==order)
				return true;
		}
		return false;
	}
	
	public int[] search( float[] sum, int bD_) {
		// Order sum scagnostics
		int[] t = new int[(nP1-1)];
		for (int i=0;i<(nP1-1);i++){
			t[i] =-1;
		}
			
		for (int i=0;i<nP1-1;i++){
			float min = Float.MAX_VALUE;
			int minIndex = -1;
			for (int d=0;d<nM;d++){
				if (sum[d]<min && !isIn(d,t) && d!=bD_){
					min = sum[d];
					minIndex =d;
				}	
			}
			t[i] = minIndex;
		}
		return t;	
	}
	
	public int[] search2( float[][] sum,int numSelectedPair, int bD_, int bOrder) {
		// Order sum scagnostics
		int[] t = new int[(nP1-1)*2];
		for (int i=0;i<(nP1-1)*2;i++){
			t[i] =-1;
		}
			
		for (int i=0;i<nP1-1;i++){
			float min = Float.MAX_VALUE;
			int minIndex = -1;
			int minOrder = -1;
			for (int p=0;p<numSelectedPair;p++){
				for (int d=0;d<nM;d++){
					if (this.key == '1') { // Search in same serie}
						if (sum[p][d]<min && !isIn2(d,p,t) && (d!=bD_ && p==bOrder)){
							min = sum[p][d];
							minIndex =d;
							minOrder =p;
						}
					}
					else{	
						if (sum[p][d]<min && !isIn2(d,p,t) && (d!=bD_ || p!=bOrder)){
							min = sum[p][d];
							minIndex =d;
							minOrder =p;
						}
					}
				}	
			}
			t[i] = minIndex;
			t[nP1-1+i] = minOrder;
		}
		return t;	
	}
		
	
	public void drawTimeSeries() {
		sliderY1 = slider1.l;
		sliderY2 = slider1.u;
		c1V =c1.s;
		
		noStroke();
		fill(Color.BLACK.getRGB());
		this.textFont(metaBold, 12);
		rect(0, 0, 1440, 900);
		if (c5.s>=0){
			return;
		}
			
		
		float xx = plotX2+plotW2+gapS;
		
		float bI = (mouseX-xx)/stepX2-30;
		//if (!found2){
		if (!stopAnimation){
			for (int d=0; d<nM;d++){
				if (bI-nDLense<=d && d<=bI+nDLense)
					iX2[d].target(stepX2*l);
				else if (bI-nDLense-l+1<d && d<bI-nDLense)
					iX2[d].target(stepX2*(d-bI+nDLense+l));
				else if (bI+nDLense<d && d<bI+nDLense+l-1)
					iX2[d].target(stepX2*(bI+nDLense+l-d));
				else 
					iX2[d].target(stepX2);
				
				iX2[d].update();
			}
		}
		for (int d=0; d<nM;d++){
			float val = iX2[d].value;
			xx += val;
			Plot.x[d] =  xx-(val)/2;
		}
		
		// Draw grid
		strokeWeight(1);
		
		if (c4.s<0){
			if (bI>=0){
				for (int d=0; d<scagVals.length;d++){
					fill(190,190,190);
					stroke(200, 200, 200, 30+iX2[d].value*2f);
					line(Plot.x[d], 0, Plot.x[d], 750);
					if (iX2[d].value>10){
						int y = d/12 + 1990;
						int m = d%12;
						float size = 4 + 8*iX2[d].value/(stepX2*l);
						textFont(metaBold,size);
						text(months[m],Plot.x[d],  plotY2-30 +size);
						if (m==0)
							text(y,Plot.x[d],  plotY2-18 +size);
					}	
				}
			}
			else{ //Overview
				for (int d=0; d<scagVals.length;d++){
					if (d%12==0){
						fill(new Color(190,190,190,255).getRGB());
						stroke(255, 255, 255,50);
						line(Plot.x[d], 0, Plot.x[d], 750);
						int y = d/12 + 1990;
						textFont(metaBold,11);
						text(y,Plot.x[d],  plotY2-18);
					}	
				}
			}
			
		}
		
		// Draw Time series
		if (c6.s<0 && c4.s<0){
			noSmooth();
			this.textFont(metaBold, 12);
			for (int p =0; p<selectedPlots.size();p++){
				int pair = selectedPlots.get(p);
				pair = plots[pair].pair;
				if (p<3){
					textFont(metaBold,12);
					if (c1.s>=0)
						plot2[pair].drawTime12(p); 
					else
						plot2[pair].drawTime11(p); 
				}	
				else{
					textFont(metaBold,11);
					if (c1.s>=0)
						plot2[pair].drawTime22(p); 
					else
						plot2[pair].drawTime21(p); 
				}	
			}
		}
		// Draw Scatterplot
		noStroke();
		smooth();
		found = false;
		
		
		
		boolean found1Temp =found1;
		bO1 = -1;
		bP1 = -1;
		bM1 =-1 ;
		found1 = false;
		for (int p =0; p<selectedPlots.size();p++){
			int pair = selectedPlots.get(p);
			pair = plots[pair].pair;
			if (p<3){
				if (c1.s>=0)
					plot2[pair].drawScatterplotSuperimposed(p);
				else{
					textFont(metaBold,10);
					plot2[pair].drawScatterplot(p, bI, found1Temp, c4);
					textFont(metaBold,12);
				}	
			}	
		}
		
		//Draw selected point
		textFont(metaBold,12);
		for (int p =0; p<selectedPlots.size();p++){
			int pair = selectedPlots.get(p);
			pair = plots[pair].pair;
			if (p<3){
				if (c1.s<0)
					plot2[pair].drawSelectedPointSuperimposed(p);
				else
					plot2[pair].drawSelectedPoint(p);
			}	
		}
	 }
	
	public void drawSPLOM() {
		if (button.x>0)
			plotX  = button.pos.value-screenW+button.w+50;
		
		for (int p =0; p<numP;p++){
			iW[p].update();
			iH[p].update();
		}
		noStroke();
		fill(Color.WHITE.getRGB());
		rect(plotX-100, 0, screenW+28, 725);
	
		
		keyFunction.draw(button.pos.value-screenW);
		this.textFont(metaBold, 12);
		// Check brushing
		for (int p =0; p<numP;p++){
			plots[p].checkBrushing();
		}
		if (!isBrushing()){
			for (int p =0; p<numP;p++){
				iW[p].target(plotW);
				iH[p].target(plotH);
			}
			for (int p =0; p<numP;p++){
				plots[p].draw();
			}
		}
		else{
			for (int p =0; p<numP;p++){
				plots[p].draw3();
			}
		}
		// Draw selected plots on the right
		selectedPlots = new ArrayList<Integer>();
		for (int p =0; p<numP;p++){
			if (plots[p].s && selectedPlots.size()<MAX_PAIR)
				selectedPlots.add(p);
		}
		orderSelectedPlots();
		// Draw selected plots
		for (int i =0; i<9;i++){
			textFont(metaBold,12);
			fill(Color.GRAY.getRGB());
			if (i==sS){
				textFont(metaBold,12);
				fill(Color.BLACK.getRGB());
			}	
			translate(PlotSPLOM.xS+3+PlotSPLOM.wS*i,PlotSPLOM.yS-2);
			rotate((float) (-PI/6.));
			text(scagNames[i],0,0);
			rotate((float) (PI/6.));
			translate(-(PlotSPLOM.xS+3+PlotSPLOM.wS*i),-(PlotSPLOM.yS-2));
		}
		textFont(metaBold,11);
		for (int p =0; p<selectedPlots.size();p++){
			int pair = selectedPlots.get(p);
			plots[pair].drawSelectedScag(p); 
		}
		
		strokeWeight(1);
		textFont(metaBold,12);
		if (!isBrushing()){
			for (int v =0; v<varlist.length; v++){
				plotText[v].checkBrushing();
				plotText[v].checkPressed();
				plotText[v].draw();
			}
		}
		else{
			for (int v =0; v<varlist.length; v++){
				plotText[v].draw3();
			}
		}
		textFont(metaBold,12);
		
		
		
		// Draw color legend
		if (button.x>=1000){
			float xL = 660;
			float yL = 0;
			float wL = 300;
			float hL = 25;
			this.stroke(0,0,0);
			this.strokeWeight(1);
			this.noFill();
			this.rect(xL, yL, wL, hL);
			boolean is1 = true;
			boolean is2 = false;
			this.textAlign(CENTER);
			DecimalFormat df = new DecimalFormat("#.##");
			
			for (float i=xL;i<=xL+wL;i++){
				float val = (i-xL)/wL;
				double scag = (val-meanYearMin[sS])/(meanYearMax[sS]-meanYearMin[sS]); 
				if (scag>=0 && scag<=1){
					Color color = ColorScales.getColor(scag,"temperature", 1f);
					this.noStroke();
					this.fill(color.getRGB());
					this.rect(i-3, yL+1, 6, hL-1);
					if (is1){
						textFont(metaBold,12);
						this.text(df.format(val),i,yL+hL+13);
						is1 = false;
					}
					is2=true;
				}
				else{
					if (is2){
						textFont(metaBold,12);
						this.text(df.format(val),i,yL+hL+13);
						is2 = false;
					}
				}
			}
			this.fill(0,0,0);
			this.textAlign(LEFT);
			this.text(0,xL,yL+hL+13);
			this.textAlign(RIGHT);
			this.text(1,xL+wL,yL+hL+13);
			this.textAlign(LEFT);
			this.textSize(12);
			this.text("Mean " + scagNames[sS]+" over 262 months",xL-195,yL+20);
			this.strokeWeight(1f);
			this.textAlign(LEFT);
		}
	 }
	
	public void orderSelectedPlots() {
		ArrayList<Integer> a = new ArrayList<Integer>();
		double maxLimit = Double.MAX_VALUE;
		for (int i =0; i<selectedPlots.size();i++){
			double max = Double.MIN_VALUE;
			int maxPair = -1;
			for (int p =0; p<selectedPlots.size();p++){
					int pair = selectedPlots.get(p);
					int pair2 = plots[pair].pair;
					if (meanYear[sS][pair2]>max && meanYear[sS][pair2]<maxLimit){
						max = meanYear[sS][pair2];
						maxPair =pair;
					}	
			}		
			a.add(maxPair);
			maxLimit = max;
		}
		selectedPlots =a;
	}
		
	
	public void readScags() {
		for (int m=0; m<nM;m++){
			String[] lines = loadStrings("Output/scagnostics"+(m)+".txt");
			for (int p = 1; p < lines.length; p++) {
				String[] pieces = lines[p].split(" ");
				for (int s = 0; s < pieces.length; s++) {
					scagVals[m][s][p-1] = Float.parseFloat(pieces[s]);
					if (scagVals[m][s][p-1]>1) 
						scagVals[m][s][p-1]=1;
				}
			}	
		}
	}
	
	public void readData() {	
		String[] lines = this.loadStrings("employ2.txt");
		for (int k = 1; k < lines.length; k++) {
			int s = (k - 1) / numV;
			int ins = (k - 1) % numV;
			String[] p = lines[k].split("\t");
			for (int m =0; m < p.length; m++) {
				data[m][ins][s] = Float.parseFloat(p[m]);
			}
		}
		lines = this.loadStrings("employRate.txt");
		for (int k = 1; k < lines.length; k++) {
			int s = (k - 1) / numV;
			int ins = (k - 1) % numV;
			String[] p = lines[k].split("\t");
			for (int m =0; m < p.length; m++) {
				data2[m][ins][s] = Float.parseFloat(p[m]);
			}
		}	
	}
	
	
	public boolean isBrushing(){
		for (int p =0; p<numP;p++){
			if (plots[p].b>=0)
				return true;
		}
		return false;
	}
	
	public void keyPressed() {
		if (key == 's' || key == 'S'){
			stopAnimation = !stopAnimation;
		}
	}
		
		
	public void mousePressed() {
		slider1.checkSelectedSlider1();
		slider2.checkSelectedSlider1();
		slider4.checkSelectedSlider1();
	}
	public void mouseReleased() {
		slider1.checkSelectedSlider2();	
		slider2.checkSelectedSlider2();
		slider4.checkSelectedSlider2();
	}
	
	public void mouseDragged() {
		if (button.s){
			plotX += (mouseX - pmouseX);
			plotY += (mouseY - pmouseY);
		}
	}
	public void mouseClicked() {
		mClicked= true;
		button.mousePressed();
		if (option.b>=0){
			c1.checkSelected();
			c2.checkSelected();
			c3.checkSelected();
			c4.checkSelected();
			c5.checkSelected();
			c6.checkSelected();
		}
		// Check brushing
		if (button.s)
			for (int p =0; p<numP;p++){
				plots[p].checkSelected();
			}
		if (c4.s>=0){
			bbp3.mouseClicked();
		}
			
		// Pie chart is active ...... SEARCH 4
		sPie = bPie;
		sSec = bSec;
	}
	
	public void mouseWheel(MouseWheelEvent e) {
		int delta = e.getWheelRotation();
		if (c5.s>=0){
			//System.out.println(c5.s);
			if (sPie>=0 && sSec<=1){
				float delta2 = delta/100.f;
				scagLow[sPie]+=delta2;
				if (scagLow[sPie]<0)
					scagLow[sPie] =0;
				else if (scagLow[sPie]>scagHigh[sPie])
					scagLow[sPie] =scagHigh[sPie];
				change =true;
			}
			if (sPie>=0 && sSec>=1){
				float delta2 = delta/100.f;
				scagHigh[sPie]+=delta2;
				if (scagHigh[sPie]>1)
					scagHigh[sPie] =1;
				else if (scagHigh[sPie]<scagLow[sPie])
					scagHigh[sPie] =scagLow[sPie];
				change =true;
			}
				
		}
		else if (c6.s>=0){
			float del = delta;
			if (ySc.value + del > 10)
				ySc.target(ySc.value +del*2);
			
		}
		else{
			if (keyPressed){
				double del = delta;
				if (plotH - del > 10 && plotH - del < 245){
					plotH -= del;
					for (int p =0; p<numP;p++){
						iH[p].target(plotH);
					}
				}	
			}
			else{
				if (button.s){
					if (plotW - delta > 10 && plotW - delta < 245){
						plotW -= delta;
						for (int p =0; p<numP;p++){
							iW[p].target(plotW);
						}	
					}	
				}
				else{
					float delta2 = delta/100.f;
					if (stepX2 - delta2 > 0.1){
						stepX2-=delta2;
					}
				}	
			}
		}
	}
}