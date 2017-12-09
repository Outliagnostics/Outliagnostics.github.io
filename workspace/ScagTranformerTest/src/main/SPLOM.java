package main;

import static main.MainTestCompareRatio.*;

import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;

public class SPLOM {
	public PApplet parent;
	public static int step=0;;
	public static float w = 20;
	public static float h = 20;
	public int BINSIZE = 10; 
	public int[][][][] bin;
	
	public SPLOM(PApplet p) {
		
   }
	
	
	
	// Draw rescaleable SPOM
	/*public void draw(){
		int k = 0;
		float pSize = PApplet.min(w,h)*0.08f;
		
		int bX = -1;
		int bY = -1;
		int bK = -1;
		int sS = 2;
		int mid = numRatio/2;
		for (int i = 1; i < nVars; i++) {
            for (int j = 0; j < i; j++) {
            	float xx = 50+w*j;
            	float yy = 50+h*i;
            	parent.noStroke();
            	Color color = ColorScales.getColor(scagRatio[0][mid][k][sS],"temperature", 1);
            	parent.fill(color.getRGB());
            	parent.rect(xx,yy,w,h);
            	if (xx<parent.mouseX && parent.mouseX<xx+w 
            			&& yy<parent.mouseY && parent.mouseY <yy+h){
            		bX = j;
            		bY = i;
            		bK = k;
            	}	
            	
            	parent.noStroke();
            	float cellW = w/BINSIZE;
            	float cellH = h/BINSIZE;
            	for (int b1 = 0; b1 <= BINSIZE; b1++) {
        			for (int b2 = 0; b2 <= BINSIZE; b2++) {
        				if (bin[j][i][b2][b1]==0) continue;
        				int count = bin[j][i][b2][b1]*100;
        				if (count>255) count=255;
        				parent.fill(0,0,0,count);
        				float x3 = xx + cellW*b2;
    					float y3 = yy + h - cellH*b1;
    					parent.rect(x3-cellW/2, y3-cellH/2, cellW, cellH);
        			}    				
    			}
    			parent.noStroke();
    			parent.fill(Color.BLACK.getRGB());
				for (int s = 0; s < nPoints; s++) {
					float x3 = (float) (xx + w*data[0][j][s]);
					float y3 = (float) (yy + h*(1 - data[0][i][s]));
					parent.ellipse(x3, y3, pSize, pSize);
				}
				
    			//parent.textSize(9);
    			//parent.fill(Color.MAGENTA.getRGB());
    			//parent.text(j,xx,yy+9);
    			//parent.text(i,xx,yy+18);
    			
            	k++;
            }
        }    
		if (bX>=0 && bY>=0){
			float xx =800;
			float yy =100;
			float ww =w*10;
			float hh =h*10;
			float ss = PApplet.min(ww,hh)*0.08f;
			parent.noStroke();
        	Color color = ColorScales.getColor(scagRatio[0][mid][bK][sS],"temperature", 1);
        	parent.fill(color.getRGB());
        	parent.rect(xx,yy,ww,hh);
			parent.fill(Color.BLACK.getRGB());
			for (int s = 0; s < nPoints; s++) {
				float x3 = (float) (xx + ww*data[0][bX][s]);
				float y3 = (float) (yy + hh*(1 - data[0][bY][s]));
				parent.ellipse(x3, y3, ss, ss);
			}
		}
		
    }*/
	
	
	
	
	// Draw SPLOM with best ratios
	public void draw2(){
		int k = 0;
		int bX = -1;
		int bY = -1;
		int sS = MainTestCompareRatio.popup.sS;
		int mid = numRatio/2;
		float margin = .05f;
		int bPlot =-1;
		DecimalFormat df = new DecimalFormat("#.##");
		
		// Original SPLOM
		float s1=670/(nVars-1); 
		for (int i = 1; i < nVars; i++) {
            for (int j = 0; j < i; j++) {
            	float xx = x+720+s1*j;
            	float yy = y+20+s1*(i-1);
            	parent.noStroke();
            	Color color = ColorScales.getColor(scagRatio[0][mid][k][sS],"temperature", 1);
            	parent.fill(color.getRGB());
            	parent.rect(xx+1,yy+1,s1-1,s1-1);
            	if (xx<parent.mouseX && parent.mouseX<xx+s1 
            			&& yy<parent.mouseY && parent.mouseY <yy+s1){
            		bX = j;
            		bY = i;
            		bPlot =k;
                }	
            	parent.noStroke();
            	parent.fill(Color.BLACK.getRGB());
				for (int s = 0; s < nPoints; s++) {
					float x3 = (float) (xx + s1*(margin+data[0][j][s]*(1-2*margin)));
					float y3 = (float) (yy + s1*(1-margin - data[0][i][s]*(1-2*margin)));
					parent.ellipse(x3, y3, s1/16, s1/16);
				}
				k++;
            }
		}    
		
		float s2 = 670/(nVars-1); 
		k=0;
		for (int i = 1; i < nVars; i++) {
            for (int j = 0; j < i; j++) {
            	float scaleX2 = 1;
				float scaleY2 = 1;
				int maxR = maxRatio[sF][k][sS];
				if (ratios[maxRatio[sF][k][sS]]<1)
					scaleX2 = ratios[maxRatio[sF][k][sS]];
				else
					scaleY2 = 1/ratios[maxRatio[sF][k][sS]];
				Color c1 = ColorScales.getColor(scagRatio[0][mid][k][sS],"temperature", 1);
            	Color c2 =  ColorScales.getColor(scagRatio[sF][maxR][k][sS], "temperature", 1f);
				
				float xx2 = x+10+s2*j;
            	float yy2 = y+20+s2*(i-1);
            	
            	
            	// Check if a scatterplot satisfy the slider condition
            	float limit = slider.value;
             	boolean isSatisfied = false;
            	if (((float) (scagRatio[sF][maxR][k][sS]-scagRatio[0][mid][k][sS]))>=limit){
            		isSatisfied = true;
                }
            	
            	// Draw regular SPLOM
            	float xx3 = xx2;
            	float yy3 = yy2;
            	parent.noStroke();
            	parent.fill(c1.getRGB());
            	if (isSatisfied)
    				parent.rect(xx3,yy3,s2,s2);
            	if (isSatisfied && xx3<parent.mouseX && parent.mouseX<xx3+s2 
            			&& yy3<parent.mouseY && parent.mouseY <yy3+s2){
            		bX = j;
            		bY = i;
            		bPlot =k;
            	}	
            	
            	if (ratios[maxR]<1)
            		xx2 += (1-ratios[maxR])*s2/2; 
            	else	
            		yy2 -= (1-1/ratios[maxR])*s2/2;
				if (isSatisfied){
					parent.fill(c2.getRGB());
					parent.rect(xx2, yy2+s2*(1-scaleY2), s2*scaleX2, s2*scaleY2);
					parent.fill(0,0,0);
					if (ratios[maxR]<1)
						for (int pp = 0; pp < nPoints; pp++) {
							float x4 = (float) (xx2 + s2*(0.05f+dataRatio[maxR][j][pp]*(0.80f+0.1*ratios[maxR]))) ;
							float y4 = (float) (yy2 + s2*(0.95f-data[sF][i][pp]*0.9f));
							parent.ellipse(x4, y4, s2/20, s2/20);
						}
					else
						for (int pp = 0; pp < nPoints; pp++) {
							float x4 = (float) (xx2 + s2*(0.05f+data[sF][j][pp]*0.9f));
							float y4 = (float) (yy2 + s2*(0.95f-dataRatio[maxR][i][pp]*(0.80 +0.1/ratios[maxR])));
							parent.ellipse(x4, y4, s2/20, s2/20);
						}
            	}
				// Draw regular SPLOM
				parent.stroke(0,0,0);
            	parent.strokeWeight(1);
            	parent.noFill();
            	if (isSatisfied)
            		parent.rect(xx3,yy3,s2,s2);
            	parent.noStroke();
            	
				k++;
            }
        }    
		if (bPlot>=0 && bX>=0 && bY>=0){
			int maxR = maxRatio[sF][bPlot][sS] ;
			Color c1 =  ColorScales.getColor(scagRatio[0][mid][bPlot][sS], "temperature", 1);
			Color c2 =  ColorScales.getColor(scagRatio[sF][maxR][bPlot][sS], "temperature", 1);
			float size2 = 90;
			
			float bScaleX2 =1;
			float bScaleY2 =1;
			
			if (ratios[maxR]<1){
				bScaleY2 = bScaleY2/ratios[maxR];
				bScaleX2 =1;
			}
			else if (ratios[maxR]>1){
				bScaleX2 = bScaleX2*ratios[maxR];
				bScaleY2 =1;
			}	
			
			float shiftY = 0;
			if (bScaleY2>1){
				shiftY = size2*(bScaleY2-1);
			}
			float ww = size2*2+50;
			float hh = size2+50;
			float xx3 = x+200;
			float yy3 = 100;
			if (bScaleX2>1)
				ww = size2*(bScaleX2 +1)+50;
			if (bScaleY2>1)
				hh = size2*(bScaleY2)+50;
			
			parent.fill(255,255,255);
			parent.rect(xx3-20,60,ww+20,hh+30);
			
			parent.stroke(0,0,0);
        	parent.strokeWeight(1);
        	parent.fill(c1.getRGB());
			parent.rect(xx3, yy3, size2, size2);
			parent.fill(c2.getRGB());
			parent.rect(xx3+size2+20, yy3+size2*(1-bScaleY2)+shiftY, size2*bScaleX2, size2*bScaleY2);
			parent.fill(Color.BLACK.getRGB());
			for (int pp = 0; pp < nPoints; pp++) {
				float x3 = (float) (xx3 + size2*(0.05f+data[0][bX][pp]*0.9f));
				float y3 = (float) (yy3 + size2*(0.95f-data[0][bY][pp]*0.9f));
				parent.ellipse(x3, y3, size2/15, size2/15);
			//	System.out.println(data[0][bX][pp]+","+data[0][bY][pp]);
			}
			if (ratios[maxR]<1){
				for (int pp = 0; pp < nPoints; pp++) {
					float x4 = (float) (xx3 + size2*(0.05f+bScaleY2*dataRatio[maxR][bX][pp]*0.9f))+ size2+20;
					float y4 = (float) (yy3 + size2*bScaleY2*(0.95f-data[sF][bY][pp]*0.9f) );
					parent.ellipse(x4, y4, size2/15, size2/15);
				}
			}
			else{
				for (int pp = 0; pp < nPoints; pp++) {
					float x4 = (float) (xx3 + size2*bScaleX2*(0.05f+data[sF][bX][pp]*0.9f))+ size2+20;
					float y4 = (float) (yy3 + size2*(0.96f-bScaleX2*dataRatio[maxR][bY][pp]*0.92f));
					parent.ellipse(x4, y4, size2/15, size2/15);
				}
			}
			// Draw text for brushing plot
			parent.fill(0,0,0);
			parent.textSize(20);
			parent.textAlign(PApplet.CENTER);
			parent.text("Ratio of 1:1", xx3+size2/2,yy3-8);
			String text = (int) ratios[maxR]+":1";
			if (ratios[maxR]<1)
				text = "1:"+ (int) (1/ratios[maxR]);
			parent.text("Ratio of "+ text, xx3+size2+20+size2*bScaleX2/2,yy3-8);
			parent.text(scagNames[sS]+"="+df.format(scagRatio[0][mid][bPlot][sS]), xx3+size2/2,yy3+size2+23);
			float y5 = yy3+size2+23;
			if (ratios[maxR]<1)
				y5 = yy3+size2/ratios[maxR]+23;
			parent.text(scagNames[sS]+"="+df.format(scagRatio[sF][maxR][bPlot][sS]), xx3+size2+20+size2*bScaleX2/2,y5);
			
		}
		parent.fill(0,0,0);
		parent.textSize(30);
		parent.text("Function="+funcNames[sF], 400,32);
	}
}
