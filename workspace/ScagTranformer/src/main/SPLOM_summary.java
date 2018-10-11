package main;

import static main.Main.*;

import java.awt.Color;
import java.text.DecimalFormat;
import processing.core.PApplet;

public class SPLOM_summary {
	public static Main parent;
	public static int step=0;;
	public static float plotSize; 
	public static int binSize; 
	public static int[][][][] bin;
	public static ThreadLoader2 loader2;
	public static Thread thread2;
	// Data points simulation
	public static Integrator[] iX;
	public static Integrator[] iY;
	public static int count =-1;
	private int bPlot =-1;
	private int bX = -1;
	private int bY = -1;
	private float bxx = 0;
	private float byy = 0;
	
	
	public SPLOM_summary(Main p) {
		parent = p;
		plotSize = 620/(nVars-1);
		binSize = (int) (10+PApplet.sqrt(plotSize));
		bin = new int[nVars][nVars][binSize+1][binSize+1];
		
		iX =  new Integrator[nPoints];
		iY =  new Integrator[nPoints];
		for (int i=0;i<nPoints;i++){
			iX[i] = new Integrator(0,0.5f,0.15f);
			iY[i] = new Integrator(0,0.5f,0.15f);
		}
		loader2 = new ThreadLoader2();
		thread2 = new Thread(loader2);
		thread2.start();
   }
	
	
	public class ThreadLoader2 implements Runnable {
		public ThreadLoader2() {
		}
		public void run() { 
			for (int v1 =0; v1<nVars;v1++){
				for (int v2 =0; v2<v1;v2++){
					for (int d = 0; d < nPoints; d++) {
						int binX = (int) (data[v2][d]*binSize);
						int binY = (int) (data[v1][d]*binSize);
						bin[v2][v1][binX][binY]++;
					}
					bin[v1][v2]=bin[v2][v1];
				}	
			}
		}
	}	
	
	// Draw SPLOM with best ratios
	public void draw2(){
		int k = 0;
		int sS = Main.popup.sS;
		float margin = 0.08f;
		
		parent.fill(255,255,255);
		parent.noStroke();
		parent.rect(0, 0, 700, 200);
		// Original SPLOM
		for (int i = 1; i < nVars; i++) {
            for (int j = 0; j < i; j++) {
            	//Skip the scatterplots don't satisfy filtering condition
            	double biggestDif =0;
            	double biggestDense =0;
            	for (int fx=0; fx<numFunc; fx++){
					for (int fy=0; fy<numFunc; fy++){
						if (sS==2 && (fy==Main.SIGMOID || fx==Main.SIGMOID))
							continue; // Clumpy, skip SIGMOID
						double dif = scagRatio[fx][fy][k][sS] -scagRatio[0][0][k][sS];
						if (dif>biggestDif)
				 			biggestDif = dif;
				 		
						double difD = scagRatio[fx][fy][k][3] -scagRatio[0][0][k][3];
				 		if (difD>biggestDense)
				 			biggestDense = difD;
				 	}
            	}	
            	
            	// Dense > a certain value
            	if (biggestDif<slider1.value){// || biggestDense<slider2.value) {
            	//if (biggestDif<slider1.value) {
            		k++;
            		continue;
            	}
            		
            	float xx = x+2+plotSize*j;
            	float yy = y+60+plotSize*(i-1);
            	float value = (float) scagRatio[0][0][k][sS];//PApplet.min((float) scagRatio[0][0][k][sS],0.7f);
            	Color color = ColorScales.getColor(value, Main.colorScale, 1);
            	parent.stroke(0,0,0);
            	parent.fill(color.getRGB());
            	/*
            	if (xx<=parent.mouseX && parent.mouseX<=xx+plotSize 
            			&& yy<=parent.mouseY && parent.mouseY <=yy+plotSize){
            		bX = j;
            		bY = i;
            		bPlot =k;
            		bxx = xx;
            		byy = yy;
            	}	
            	parent.rect(xx,yy,plotSize,plotSize);
            	parent.noStroke();
        		parent.fill(Color.BLACK.getRGB());
				for (int s = 0; s < nPoints; s++) {
					float x3 = (float) (xx + plotSize*(margin+data[j][s]*(1-2*margin)));
					float y3 = (float) (yy + plotSize*(1-margin - data[i][s]*(1-2*margin)));
					parent.ellipse(x3, y3, plotSize/16, plotSize/16);
				}
				*/
				float xx2 = x+2+plotSize*i;
            	float yy2 = y+60+plotSize*(j-1);
            	value = (float) scagRatio[4][4][k][sS];
            	color = ColorScales.getColor(value, Main.colorScale, 1);
            	parent.stroke(0,0,0);
            	parent.fill(color.getRGB());
            	parent.rect(xx,yy,plotSize,plotSize);
            	
            	parent.noStroke();
            	parent.fill(0,0,0);
            	for (int s = 0; s < nPoints; s++) {
					float x3 = (float) (xx + plotSize*(margin+data2[4][j][s]*(1-2*margin)));
					float y3 = (float) (yy + plotSize*(1-margin - data2[4][i][s]*(1-2*margin)));
					parent.ellipse(x3, y3, plotSize/16, plotSize/16);
				}
            	
				k++;
            }
		}    
		
		if (bPlot>=0 && bX>=0 && bY>=0){
			parent.strokeWeight(2);
			parent.stroke(255,0,0);
			parent.noFill();
			parent.rect(bxx,byy,plotSize,plotSize);
        	drawTransformation(bPlot, bX, bY);
		}
		count++;
		if (count>100000)
			count=100;
	 }
	
	// Draw SPLOM with transformation
	public static void drawTransformation(int bPlot, int bX, int bY){
		parent.fill(255,0,0);
		parent.text(bPlot,500,200);
		
		int sS = Main.popup.sS;
		DecimalFormat df = new DecimalFormat("#.##");
		// Brushing plot
		parent.noFill();
		parent.noStroke();
		
		// Draw selected variable names
				parent.fill(0,0,0);
				parent.textSize(14);
				parent.textAlign(PApplet.LEFT);
				parent.text("Varibale X: "+varNames[bX].replace("\"", ""), 670,63);
				parent.text("Varibale Y: "+varNames[bY].replace("\"", ""), 670,79);
			
		float size = 78;
		float xx = x+640;
		float yy = 108;
		parent.fill(255,255,255);
		parent.rect(xx-120, yy-86, 800, 730);
		
		// compute summary scagnostics
		
		double[][] scag =  new double[numFunc][numFunc]; 
		for (int fx=0; fx<numFunc; fx++){
			for (int fy=0; fy<numFunc; fy++){
				double sum = 0;
				for (int p=0; p<nPairs; p++){
					sum += scagRatio[fx][fy][p][sS];
				//	System.out.println("fx="+fx+" fy="+fy+" "+scagRatio[fx][fy][p][sS]+" sum="+sum);
				}
				scag[fx][fy] = sum/(nPairs);
			}
		}
		
		
		for (int fx=0; fx<numFunc; fx++){
			for (int fy=0; fy<numFunc; fy++){
				if (fx==1 && fy==1) continue;
				double v = scag[fx][fy];
				Color c1 =  ColorScales.getColor(v, Main.colorScale, 1);
				float xx2 = xx+fx*size;
				float yy2 = yy+fy*size*2/3;
				parent.stroke(0,0,0);
	        	parent.strokeWeight(1);
	        	parent.fill(c1.getRGB());
	        		if (fx*numFunc+fy<=computingPair){
						if (fx*numFunc+fy==computingPair)
							parent.fill(255,255,255);
	        			parent.rect(xx2, yy2, size, size*2/3);
						parent.fill(0,0,255);
						parent.noStroke();
						for (int pp = 0; pp < nPoints; pp++) {
							// if color by the last column
	        				if (parent.cOption2.s>=0){
								double v2 = data[nVars-1][pp]*0.90;
								Color c2 =  ColorScales.getColor(v2, "circular", 1);
								parent.fill(c2.getRGB());
							}	
	        		}	
	        	}
        		if (fx*numFunc+fy<=computingPair){
			 		xx2 = xx+fx*size+size/2;
					yy2 = yy+fy*size*2/3;
					parent.textSize(22);
					parent.textAlign(PApplet.CENTER);
					parent.fill(0,0,0);
					parent.text(df.format(v),xx2 ,yy2+size/2-4);
        		}
					
			}
		}	
		
		// Draw text for brushing plot
		for (int fx=0; fx<numFunc; fx++){
			float xx2 = xx+fx*size;
			parent.fill(0,0,0);
			parent.textSize(22);
			parent.textAlign(PApplet.CENTER);
			parent.text(funcNames[fx], xx2+size/2,yy-8);
			
			float yy2 = yy+fx*size*2/3;
			parent.textAlign(PApplet.RIGHT);
			parent.text(funcNames[fx], xx-8,yy2+33);
		}
		}
}
