package main;

import static main.Main.*;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import scagnostics.ScagnosticsTranformer;

@SuppressWarnings("serial")
public class MainVideo extends PApplet {
	public PopupOption option= new PopupOption(this);
	public CheckBoxOption cOption1 = new CheckBoxOption(this, 1070, 10,"Show ",-1,10);
	public CheckBoxOption cOption2 = new CheckBoxOption(this, 1070, 28,"Color by the last attribute",-1,11);
		
	public ThreadLoader1 loader1=new ThreadLoader1();
	public Thread thread1=new Thread(loader1);
	
	// Draw best plots
	public int[] scagInterest = {2,4,7,8};
	public ArrayList<Integer>[] indexes = new ArrayList[scagInterest.length];
	public ArrayList<Double>[] difs = new ArrayList[scagInterest.length];
	
	public Integrator[][] iS= new Integrator[2][scagNames.length];
	public Integrator[][] iP;
	public float x1 = 260;
	public Integrator iX2 = new Integrator(0,0.2f,0.5f);;
	public int step=-1;
	public Integrator[] iR = new Integrator[2];
	public Integrator[] iG = new Integrator[2];
	public Integrator[] iB = new Integrator[2];
	public int pairID = -1;
	public int xVar = -1;
	public int yVar = -1;
	public int sS = -1;
	public int fx1 = -1;
	public int fy1 = -1;
	public int fx2 = -1;
	public int fy2 = -1;
	
	// Transformation functions
	public Integrator iRfunctionX;
	public Integrator iGfunctionX;
	public Integrator iBfunctionX;
	public Integrator iRfunctionY;
	public Integrator iGfunctionY;
	public Integrator iBfunctionY;
	public boolean isTransformOnX;
	public boolean isTransformOnY;
	public String fileName ="";
	public String fileName2 ="";

	public static void main(String args[]){
	  PApplet.main(new String[] { MainVideo.class.getName() });
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
	        			data[i][row] =0;
	        		}	
	        		 
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
		
		// Example Outlying 
		/*	fileName = "../data/Subway50Data.txt";
			getData(fileName);
			
			if (fileName.contains("Subway"))
				fileName2 = "Subway dataset";
			pairID = 336;
			int[] ind = pairToIndex(pairID);
			xVar = ind[1];
			yVar = ind[0];
			isTransformOnX = true;
			isTransformOnY = true;
			sS = 0;
			fx1 = 0;
			fy1 = 0;
			fx2 = 4;
			fy2 = 4;*/
		
		/*
		// Example Clumpy 
			fileName = "../data2/IrisData.txt";
			getData(fileName);
			
			if (fileName.contains("Iris"))
				fileName2 = "Iris dataset";
			pairID = 4;
			int[] ind = pairToIndex(pairID);
			xVar = ind[1];
			yVar = ind[0];
			isTransformOnX = true;
			isTransformOnY = true;
			sS = 2;
			fx1 = 0;
			fy1 = 0;
			fx2 = 4;
			fy2 = 4;
		*/
		// Example Striated 
			fileName = "../data/BreastData.txt";
			getData(fileName);
			
			if (fileName.contains("Breast"))
				fileName2 = "Breast Cancer dataset";
			pairID = 378;
			int[] ind = pairToIndex(pairID);
			xVar = ind[1];
			yVar = ind[0];
			isTransformOnX = true;
			isTransformOnY = true;
			sS = 4;
			fx1 = 0;
			fy1 = 0;
			fx2 = 4;
			fy2 = 0;
		
		/*// Example Stringy 
			fileName = "../data/EmplData.txt";
			getData(fileName);
			
			if (fileName.contains("Empl"))
				fileName2 = "US Employment dataset";
			pairID = 275;
			int[] ind = pairToIndex(pairID);
			xVar = ind[1];
			yVar = ind[0];
			isTransformOnX = true;
			isTransformOnY = true;
			sS = 7;
			fx1 = 0;
			fy1 = 0;
			fx2 = 4;
			fy2 = 4;*/
			
		/*
		// Transform X Sigmoid-> Stringy
			fileName = "../dataWeather/day305.txt";
			getData(fileName);
			
			if (fileName.contains("day"))
				fileName2 = "Weather dataset";
			pairID = 36; 
			int[] ind = pairToIndex(pairID);
			xVar = ind[1];
			yVar = ind[0];
			isTransformOnX = true;
			isTransformOnY = false;
			sS = 7;
			fx1 = 0;   
			fy1 = 0;   
			fx2 = 7;   
			fy2 = 0;   
		*/
		
		
		/*
		// Transform XY log logit -> Clumpy
			fileName = "../data/MLB2008Data.txt";
			getData(fileName);
			
			if (fileName.contains("MLB2008"))
				fileName2 = "2008 Major League Baseball dataset";
			pairID = 1223;
			int[] ind = pairToIndex(pairID);
			xVar = ind[1];
			yVar = ind[0];
			isTransformOnX = true;
			isTransformOnY = true;
			sS = 2;
			fx1 = 0;   
			fy1 = 0;   
			fx2 = 4;   
			fy2 = 6;   
		*/		
			
		
		
		/*
		// Transform X Inverse-> Striated
			fileName = "../dataUCI/WaterTreatment.txt";
			getData(fileName);
			
			if (fileName.contains("WaterTreatment"))
				fileName2 = "Water Treatment dataset";
			pairID = 462; // 357
			int[] ind = pairToIndex(pairID);
			xVar = ind[1];
			yVar = ind[0];
			isTransformOnX = true;
			isTransformOnY = false;
			sS = 4;
			fx1 = 0;   //2
			fy1 = 7;   //0
			fx2 = 5;   //2
			fy2 = 7;   //5
		*/
		
		/*
		// Transform Y log log -> Clumpy
		fileName = "../data/Subway50Data.txt";
		getData(fileName);
		
		if (fileName.contains("Subway"))
			fileName2 = "Subway dataset";
		pairID = 105;
		int[] ind = pairToIndex(pairID);
		xVar = ind[1];
		yVar = ind[0];
		isTransformOnX = false;
		isTransformOnY = true;
		sS = 2;
		fx1 = 0;   
		fy1 = 0;   
		fx2 = 0;   
		fy2 = 4;   */
		
		// Transform XY log log -> Clumpy
		/*fileName = "../data/Subway20Data.txt";
		getData(fileName);
		
		if (fileName.contains("Subway"))
			fileName2 = "Subway dataset";
		pairID = 102;
		int[] ind = pairToIndex(pairID);
		xVar = ind[1];
		yVar = ind[0];
		isTransformOnX = true;
		isTransformOnY = true;
		sS = 2;
		fx1 = 0;   
		fy1 = 0;   
		fx2 = 4;   
		fy2 = 4;   
		*/
		/*
		// Transform XY log/log-> Monotonic
		fileName = "../WorldData/Monotonic/year"+2000+".txt";
		getData(fileName);
		
		fileName2 = "The World Bank data";
		pairID = 954; 
		int[] ind = pairToIndex(pairID);
		xVar = ind[1];
		yVar = ind[0];
		isTransformOnX = true;
		isTransformOnY = true;
		sS = 8;
		fx1 = 0;   
		fy1 = 0;   
		fx2 = 4;   
		fy2 = 4;   
		*/
		
		// Transform data
		for (int f=0; f<numFunc; f++){
			ScagnosticsTranformer.computeFuncData(f);
		}
		
		float iSpeed = 0.15f;
		for (int i = 0; i < scagNames.length; i++) {
			iS[0][i] = new Integrator(0,0.2f,iSpeed*3);
			iS[1][i] = new Integrator(0,0.2f,iSpeed);
		}
		iP= new Integrator[4][nPoints];
		for (int i = 0; i < nPoints; i++) {
			iP[0][i] = new Integrator(0,0.2f,iSpeed*3);
			iP[1][i] = new Integrator(0,0.2f,iSpeed*3);
			iP[2][i] = new Integrator(0,0.2f,iSpeed);
			iP[3][i] = new Integrator(0,0.2f,iSpeed);
		}
		iR[0] = new Integrator(80,0.2f,iSpeed*3);
		iG[0] = new Integrator(80,0.2f,iSpeed*3);
		iB[0] = new Integrator(80,0.2f,iSpeed*3);
		iR[1] = new Integrator(80,0.2f,iSpeed);
		iG[1] = new Integrator(80,0.2f,iSpeed);
		iB[1] = new Integrator(80,0.2f,iSpeed);
		iRfunctionX = new Integrator(80,0.2f,iSpeed);
		iGfunctionX = new Integrator(80,0.2f,iSpeed);
		iBfunctionX = new Integrator(80,0.2f,iSpeed);
		iRfunctionY = new Integrator(80,0.2f,iSpeed);
		iGfunctionY = new Integrator(80,0.2f,iSpeed);
		iBfunctionY = new Integrator(80,0.2f,iSpeed);
				
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
		
		slider1 = new Slider(this,"difference is greater than",300,52,0);
	//	slider2 = new Slider(this,"regular plot",300,75,1);
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
					ScagnosticsTranformer.computeScagnosticsMainVideo(f1,f2,pairID);
					ScagnosticsTranformer.computeMaxRatio();
					SPLOM.count=0;
					computingPair++;
					//begin the simulation program
				}	
			}	
			//step=0;
			message2 = ""; 
		 }
	}
	
	
	public void draw() {
		this.background(0,0,0);
		this.smooth();
		
		this.fill(255,255,255);
		this.textSize(40);
		this.textAlign(PApplet.CENTER);
		this.text(fileName2, this.width/2,90);
		
		this.textSize(16);
		this.textAlign(PApplet.LEFT);
		
		float y = 180;
		
		
		// Set data 
		if (step==0){
			iX2.set(x1);
			for (int i = 0; i < nPoints; i++) {
				iP[0][i].set((float) data2[fx1][xVar][i]);
				iP[1][i].set((float) data2[fy1][yVar][i]);
				iP[2][i].set((float) data2[fx1][xVar][i]);
				iP[3][i].set((float) data2[fy1][yVar][i]);
			}
			
			// Color
			for (int i = 0; i < scagNames.length; i++){
				float v1 = (float) scagRatio[fx1][fy1][pairID][i];
				float v2 = v1*200;
				iS[0][i].target(v2);
		    	iS[1][i].target(v2);
		    }
			float v = (float) scagRatio[fx1][fy1][pairID][sS];
			Color c1 = ColorScales.getColor(v, "temperature", 1);
	    	iR[0].target(c1.getRed());
	    	iG[0].target(c1.getGreen());
	    	iB[0].target(c1.getBlue());
	    	iR[1].target(c1.getRed());
	    	iG[1].target(c1.getGreen());
	    	iB[1].target(c1.getBlue());
	   
			
			step++;
		}
		else if (step==2){
			iX2.target(800);
			step++;
		}
		else if (step==4){
			for (int i = 0; i < nPoints; i++) {
				iP[0][i].target((float) data2[fx1][xVar][i]);
				iP[1][i].target((float) data2[fy1][yVar][i]);
				iP[2][i].target((float) data2[fx2][xVar][i]);
				iP[3][i].target((float) data2[fy2][yVar][i]);
			}
			// Color
			for (int i = 0; i < scagNames.length; i++){
				float v1 = (float) scagRatio[fx1][fy1][pairID][i];
				float v3 = (float) scagRatio[fx2][fy2][pairID][i];
				if (i==sS){
					System.out.println("v1="+v1);
					System.out.println("v3="+v3);
				}
				float v2 = v1*200;
				float v4 = v3*200;
		    	iS[0][i].target(v2);
		    	iS[1][i].target(v4);
		     }
			float v1 = (float) scagRatio[fx1][fy1][pairID][sS];
			float v3 = (float) scagRatio[fx2][fy2][pairID][sS];
			Color c1 = ColorScales.getColor(v1, "temperature", 1);
	    	Color c2 = ColorScales.getColor(v3, "temperature", 1);
	    	iR[0].target(c1.getRed());
	    	iG[0].target(c1.getGreen());
	    	iB[0].target(c1.getBlue());
	    	iR[1].target(c2.getRed());
	    	iG[1].target(c2.getGreen());
	    	iB[1].target(c2.getBlue());
	    	iRfunctionX.target(255);
	    	iGfunctionX.target(255);
	    	iBfunctionX.target(255);
	    	iRfunctionY.target(255);
	    	iGfunctionY.target(255);
	    	iBfunctionY.target(255);
			step++;
		}
		else{
			iX2.update();
			for (int i = 0; i < nPoints; i++) {
				iP[0][i].update();
				iP[1][i].update();
				iP[2][i].update();
				iP[3][i].update();
			}
			for (int i = 0; i < scagNames.length; i++){
				iS[0][i].update();
				iS[1][i].update();
			}
			iR[0].update();
			iG[0].update();
			iB[0].update();
			iR[1].update();
			iG[1].update();
			iB[1].update();
			iRfunctionX.update();
			iGfunctionX.update();
			iBfunctionX.update();
			iRfunctionY.update();
			iGfunctionY.update();
			iBfunctionY.update();
		}
		
		this.textSize(23);
		drawScag(0,fx1,fy1,6,x1,y);
		if (step>1)
			drawScag(1,fx2,fy2,6,iX2.value,y);
		
		// Draw function names
		if (step>=5){
			if (isTransformOnX){
				this.textAlign(PApplet.CENTER);
				this.textSize(30);
				this.fill(iRfunctionX.value, iGfunctionX.value, iBfunctionX.value);
				this.text(funcNames[fx2], iX2.value + 140, y-10);
			}
			if (isTransformOnY){
				this.textAlign(PApplet.CENTER);
				this.textSize(30);
				this.fill(iRfunctionY.value, iGfunctionY.value, iBfunctionY.value);
				
				float xx = iX2.value-10;
				float yy = y+140;
				float al = -PApplet.PI/2; 
				this.translate(xx,yy);
				this.rotate(al);
				this.text(funcNames[fy2],0,0);
				this.rotate(-al);
				this.translate(-(xx), -(yy));
				
			}
		}
	}	
	
	public void drawScag(int g, int fx, int fy, int k, float x, float y) {
		float size = 350;
		//this.noStroke();
		this.stroke(0,0,0);
		this.strokeWeight(2);
		this.fill(iR[g].value, iG[g].value, iB[g].value);
		this.rect(x,y,size,size);
			
		this.fill(0,0,0);
		this.noStroke();
		for (int pp = 0; pp < nPoints; pp++) {
			float x4 = (float) (x + size*(0.08f+iP[g*2][pp].value*0.84f));
			float y4 = (float) (y + size*(0.92f-iP[g*2+1][pp].value*0.84f));
			this.ellipse(x4, y4, size/20, size/20);
		}	
		this.textSize(20);
		
		
		this.textAlign(PApplet.RIGHT);
		this.strokeWeight(1);
	    for (int i = 0; i < scagNames.length; i++){
	    	float y2 = y+380+i*24;
			float x2 = x+120;
			this.fill(180,180,180);
			if (i==sS){
				this.fill(0,0,0);
				this.text(scagNames[i], x2-5+1, y2+20+1);
				this.fill(iR[g].value, iG[g].value, iB[g].value);
			}
			rect(x2, y2, iS[g][i].value, 22);
			this.text(scagNames[i], x2-5, y2+18);
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
		step++;
		
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
	//	slider2.mousePressed();
	}
	public void mouseReleased() {
	//	bbp.mouseReleased();
		slider1.mouseRelease();
	//	slider2.mousePressed();
	}
	
	public void mouseDragged() {
	//	bbp.mouseDragged();
		
	}
	public void mouseClicked() {
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