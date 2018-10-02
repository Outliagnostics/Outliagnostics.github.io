package main;

import static main.Main.*;

import java.awt.Color;
import java.text.DecimalFormat;
import processing.core.PApplet;

public class SPLOM {
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
	
	
	public SPLOM(Main p) {
		parent = p;
		plotSize = 650/(nVars-1);
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
		float margin = .0f;
		
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
            		
            	float xx = x+1+plotSize*j;
            	float yy = y+20+plotSize*(i-1);
            	float value = (float) scagRatio[0][0][k][sS];//PApplet.min((float) scagRatio[0][0][k][sS],0.7f);
            	Color color = ColorScales.getColor(value, Main.colorScale, 1);
            	parent.stroke(0,0,0);
            	parent.fill(color.getRGB());
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
            	if (showPoints){
	            	parent.noStroke();
	            	parent.fill(Color.BLACK.getRGB());
					for (int s = 0; s < nPoints; s++) {  
						float x3 = (float) (xx + plotSize*(margin+data[j][s]*(1-2*margin)));
						float y3 = (float) (yy + plotSize*(1-margin - data[i][s]*(1-2*margin)));
						parent.ellipse(x3, y3, plotSize/16, plotSize/16);
					}
            	}
            	else{
	            	float cellW = plotSize/(binSize+1);
	            	float cellH = plotSize/(binSize+1);
	            	for (int b1 = 0; b1 <= binSize; b1++) {
	        			for (int b2 = 0; b2 <= binSize; b2++) {
	        				if (bin[j][i][b2][b1]==0) continue;
	        				int count = bin[j][i][b2][b1]*100;
	        				if (count>255) count=255;
	        				parent.fill(0,0,0,count);
	        				float x3 = xx + cellW*b2;
	    					float y3 = yy + plotSize - cellH*b1 - cellH;
	    					parent.rect(x3, y3, cellW, cellH);
	        			}    				
	    			}
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
		//parent.fill(255,0,0);
		//parent.text(bPlot,500,200);
		/*if (parent.mousePressed){
			System.out.println("");
			for (int pp = 0; pp < nPoints; pp++) {
				System.out.print(data2[0][bX][pp]+",");
			}
			System.out.println("");
			for (int pp = 0; pp < nPoints; pp++) {
				System.out.print(data2[0][bY][pp]+",");
			}
			System.out.println("");
			for (int pp = 0; pp < nPoints; pp++) {
				System.out.print(data2[3][bY][pp]+",");
			}
			System.out.println("");
			for (int pp = 0; pp < nPoints; pp++) {
				System.out.print(data2[4][bY][pp]+",");
			}
			System.out.println("");
				
		}*/
		
		int sS = Main.popup.sS;
		DecimalFormat df = new DecimalFormat("#.##");
		// Brushing plot
		parent.noFill();
		parent.noStroke();
		
		float size = 78;
		float xx = x+xx2;
		float yy = yy2;
		parent.fill(255,255,255);
		parent.rect(xx-30, yy-53, 700, 710);
		for (int fx=0; fx<numFunc; fx++){
			for (int fy=0; fy<numFunc; fy++){
				if (fx==1 && fy==1) continue;
				double v = scagRatio[fx][fy][bPlot][sS];
				Color c1 =  ColorScales.getColor(v, Main.colorScale, 1);
				float xx2 = xx+fx*size;
				float yy2 = yy+fy*size;
				parent.stroke(0,0,0);
	        	parent.strokeWeight(1);
	        	if (fx==0 && fy==0){
	        		parent.stroke(255,0,0);
	        		parent.strokeWeight(2);
	        	}
	        	// For Sleep data
	        	/*if (fx==5 && fy==1){
	        		c1= new Color(180,180,180);
	        	}
	        	if (fx==5 && fy==7){
	        		c1= new Color(150,150,150);
	        	}
	        	if (fx==5 && fy==3){
	        		c1= new Color(160,160,160);
	        	}
	        	*/
	        	parent.fill(c1.getRGB());
	        	if (fx==1){
	        		if (fx*numFunc+fy<=computingPair){
	        			if (fx*numFunc+fy==computingPair)
							parent.fill(255,255,255);
		        		xx2 = xx2+size/4;
		        		parent.rect(xx2, yy2, size/2, size);
		        		parent.fill(0,0,250);
		        		parent.noStroke();
	        			for (int pp = 0; pp < nPoints; pp=pp+2) {  // CHU Y for presentation ******************************
	        				// if color by the last column
	        				if (parent.cOption2.s>=0){
								double v2 = data[nVars-1][pp]*0.90;
								Color c2 =  ColorScales.getColor(v2, "circular", 1);
								//if (v2>0.3 && v2<=0.6)
								//	c2=Color.ORANGE; // For page blocks data
								parent.fill(c2.getRGB());
							}
	        				if (fx*numFunc+fy==computingPair){
		        				if (count==0){
					 				float x4 = (float) (size*(0.06f+data2[0][bX][pp]*0.76f));
									float y4 = (float) (size*(0.94f-data2[0][bY][pp]*0.88f));
							    	iX[pp].set(x4);
									iY[pp].set(y4);
									iX[pp].update();
					        		iY[pp].update();
					        		parent.ellipse(xx2+iX[pp].value, yy2 + iY[pp].value, size/16, size/16);
								}
								else if (count==1){
									float x4 = (float) (size*(0.06f+data2[fx][bX][pp]*0.76f));
									float y4 = (float) (size*(0.94f-data2[fy][bY][pp]*0.88f));
							    	iX[pp].target(x4);
									iY[pp].target(y4);
									iX[pp].update();
					        		iY[pp].update();
					        		parent.ellipse(xx2+ iX[pp].value, yy2 + iY[pp].value, size/16, size/16);
								}
								else{
									iX[pp].update();
					        		iY[pp].update();
					        		parent.ellipse(xx2+ iX[pp].value, yy2 + iY[pp].value, size/16, size/16);
								}
							}
							else{
								float x4 = (float) (size*(0.06f+data2[fx][bX][pp]*0.76f));
								float y4 = (float) (size*(0.94f-data2[fy][bY][pp]*0.88f));
						    	parent.ellipse(xx2+ x4, yy2 +y4, size/16, size/16);
							}
		        		}
		        		
	        		}
	        	}
	        	else if (fy==1){
	        		if (fx*numFunc+fy<=computingPair){
	        			if (fx*numFunc+fy==computingPair)
							parent.fill(255,255,255);
	        			
		        		yy2 = yy2+size/4;
		        		parent.rect(xx2, yy2, size, size/2);
		        		parent.fill(0,0,250);
		        		parent.noStroke();
		        		for (int pp = 0; pp < nPoints; pp=pp+2) {
		        			// if color by the last column
	        				if (parent.cOption2.s>=0){
								double v2 = data[nVars-1][pp]*0.90;
								Color c2 =  ColorScales.getColor(v2, "circular", 1);
								//if (v2>0.3 && v2<=0.6)
								//	c2=Color.ORANGE; // For page blocks data
								parent.fill(c2.getRGB());
							}
	        				if (fx*numFunc+fy==computingPair){
		        				if (count==0){
					 				float x4 = (float) (size*(0.06f+data2[0][bX][pp]*0.88f));
									float y4 = (float) (size*(0.44f-data2[0][bY][pp]*0.76f));
									iX[pp].set(x4);
									iY[pp].set(y4);
									iX[pp].update();
					        		iY[pp].update();
					        		parent.ellipse(xx2+iX[pp].value, yy2 + iY[pp].value, size/16, size/16);
								}
								else if (count==1){
									float x4 = (float) (size*(0.06f+data2[fx][bX][pp]*0.88f));
									float y4 = (float) (size*(0.44f-data2[fy][bY][pp]*0.76f));
									iX[pp].target(x4);
									iY[pp].target(y4);
									iX[pp].update();
					        		iY[pp].update();
					        		parent.ellipse(xx2+ iX[pp].value, yy2 + iY[pp].value, size/16, size/16);
								}
								else{
									iX[pp].update();
					        		iY[pp].update();
					        		parent.ellipse(xx2+ iX[pp].value, yy2 + iY[pp].value, size/16, size/16);
								}
							}
							else{
								float x4 = (float) (size*(0.06f+data2[fx][bX][pp]*0.88f));
								float y4 = (float) (size*(0.44f-data2[fy][bY][pp]*0.76f));
								parent.ellipse(xx2+ x4, yy2 +y4, size/16, size/16);
							}
		        			
		        		}
	        		}
	        	}
	        	else{
					if (fx*numFunc+fy<=computingPair){
						if (fx*numFunc+fy==computingPair)
							parent.fill(255,255,255);
	        			parent.rect(xx2, yy2, size, size);
						parent.fill(0,0,255);
						parent.noStroke();
						for (int pp = 0; pp < nPoints; pp=pp+2) {
							// if color by the last column
	        				if (parent.cOption2.s>=0){
								double v2 = data[nVars-1][pp]*0.90;
								Color c2 =  ColorScales.getColor(v2, "circular", 1);
								//if (v2>0.3 && v2<=0.6)
								//	c2=Color.ORANGE; // For page blocks data
								
								parent.fill(c2.getRGB());
							}	
	        				if (fx*numFunc+fy==computingPair){
		        				if (count==0 || computingPair==0){
					 				float x4 = (float) (size*(0.07f+data2[0][bX][pp]*0.86f));
									float y4 = (float) (size*(0.93f-data2[0][bY][pp]*0.86f));
									iX[pp].set(x4);
									iY[pp].set(y4);
									iX[pp].update();
					        		iY[pp].update();
					        		parent.ellipse(xx2+iX[pp].value, yy2 + iY[pp].value, size/16, size/16);
								}
								else if (count==1){
									float x4 = (float) (size*(0.07f+data2[fx][bX][pp]*0.86f));
									float y4 = (float) (size*(0.93f-data2[fy][bY][pp]*0.86f));
									iX[pp].target(x4);
									iY[pp].target(y4);
									iX[pp].update();
					        		iY[pp].update();
					        		parent.ellipse(xx2+ iX[pp].value, yy2 + iY[pp].value, size/16, size/16);
								}
								else{
									iX[pp].update();
					        		iY[pp].update();
					        		parent.ellipse(xx2+ iX[pp].value, yy2 + iY[pp].value, size/16, size/16);
								}
							}
							else{
								float x4 = (float) (xx2 + size*(0.07f+data2[fx][bX][pp]*0.86f));
								float y4 = (float) (yy2 + size*(0.93f-data2[fy][bY][pp]*0.86f));
								parent.ellipse(x4, y4, size/16, size/16);
							}
						}
	        		}	
	        	}
            	if (parent.cOption1.s>=0){
            		if (fx*numFunc+fy<=computingPair){
				 		xx2 = xx+fx*size;
						yy2 = yy+fy*size;
						parent.textSize(16);
						parent.textAlign(PApplet.CENTER);
						parent.fill(0,0,0);
						parent.text(df.format(v),xx2+size/2,yy2+size/2);
						parent.text(df.format(v),xx2+size/2+2,yy2+size/2);
						parent.text(df.format(v),xx2+size/2,yy2+size/2+2);
						parent.text(df.format(v),xx2+size/2+2,yy2+size/2+2);
						parent.fill(255,255,255);
						parent.text(df.format(v),xx2+size/2+1,yy2+size/2+1);
            		}
				}		
			}
		}	
		
		// Draw text for brushing plot
		for (int fx=0; fx<numFunc; fx++){
			float xx2 = xx+fx*size;
			parent.fill(0,0,0);
			parent.textSize(18);
			parent.textAlign(PApplet.CENTER);
			parent.text(funcNames[fx], xx2+size/2,yy-5);
			
			float yy2 = yy+fx*size;
			float al = -PApplet.PI/2; 
			parent.translate(xx-5,yy2+size/2);
			parent.rotate(al);
			parent.text(funcNames[fx], 0,0);
			parent.rotate(-al);
			parent.translate(-(xx-5), -(yy2+size/2));
		}
		// Draw selected variable names
		parent.fill(0,0,0);
		parent.textSize(14);
		parent.textAlign(PApplet.LEFT);
		parent.text("Variable X:  "+varNames[bX].replace("\"", ""), xx2+20,yy2-55);
		parent.text("Variable Y:  "+varNames[bY].replace("\"", ""), xx2+20,yy2-38);
		
		// Draw color legend for data points in ForestFires dataset.
		/*parent.fill(255,255,255);
		parent.rect(500, 200, 1000, 300);
		String[] months ={"Jan","Feb","Mar","Apr","May", "Jun","Jul", "Aug","Sep", "Oct", "Nov", "Dec"};
		parent.textSize(18);
		for (int i=0;i<12;i++){
			double v2 = ((double) i / 11)*0.93;
			Color c2 =  ColorScales.getColor(v2, "circular", 1);
			parent.fill(c2.getRGB());
			parent.noStroke();
			float x2 = 650+i*54;
			parent.ellipse(x2,250,10,10);
			parent.textAlign(PApplet.CENTER);
			parent.text(months[i],x2,270);
		}*/
		
		//Draw color legend for data points in pageBlocks dataset.
		/*parent.fill(255,255,255);
		parent.rect(500, 200, 1000, 300);
		String[] months ={"Text","Horizontal line","Graphic","Vertical line","Picture"};
		parent.textSize(18);
		for (int i=0;i<5;i++){
			double v2 = ((double) i / 4)*0.90;
			Color c2 =  ColorScales.getColor(v2, "circular", 1);
			if (v2>0.3 && v2<=0.6)
				c2=Color.ORANGE; // For page blocks data
			parent.fill(c2.getRGB());
			parent.noStroke();
			float x2 = 650+i*120;
			parent.ellipse(x2,250,10,10);
			parent.textAlign(PApplet.CENTER);
			parent.text(months[i],x2,270);
		}*/
	}
}
