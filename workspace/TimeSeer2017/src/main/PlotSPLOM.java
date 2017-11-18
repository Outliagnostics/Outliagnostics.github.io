package main;

import static main.Main.*;

import java.awt.Color;

import processing.core.PApplet;

public class PlotSPLOM{
	public int pair =-1;
	public PApplet parent;
	public float x, y;
	public float w, h;
	public int b=-1;
	public boolean s=false;
	public int[] index;
	public int id;
	
	public static float xS0 =682;
	public static float xS =xS0-1440;
	public static int yS =110;
	public static int wS =40;
	public static int hS =40;
	
	public PlotSPLOM(int pair_, PApplet parent_){
		parent = parent_;
		pair = pair_;
		id= pair;
		index = pairToIndex(pair);
		bIndex[0] =-1;
		bIndex[1] =-1;
	}
		
	public void draw(){
		w = iW[id].value;
		h = iH[id].value;
		float stepX= Main.plotX;
		int stepY= Main.plotY; 
		x = stepX+index[1]*w;
		y = stepY+index[0]*h;
		
		int i1 = orderVar[sS][index[0]];
		int i2 = orderVar[sS][index[1]];
		if (i1>i2){
			pair = indexToPair(i1,i2);
		}
		else{
			pair = indexToPair(i2,i1);
		}
		
		double scag = (meanYear[sS][pair]-meanYearMin[sS])/(meanYearMax[sS]-meanYearMin[sS]); 
		Color color = ColorScales.getColor(scag,"temperature", 1f);
		parent.fill(color.getRGB());
		parent.noStroke();
		parent.rect(x, y-h+1, w-1, h-1);
		
		parent.stroke(new Color(0,0,0,150).getRGB());
		parent.strokeWeight(1);
		if (b>=0 || Main.isTextBr[index[0]] || Main.isTextBr[index[1]]){
			parent.stroke(new Color(0,0,0,255).getRGB());
			parent.strokeWeight(1.4f);				
		}
		
		for (int d=2; d<w-2;d++){
			parent.line(x+d,(float) (y-2-((h-5)*scagVals[d][sS][pair])),
					x+d-1,(float) (y-2-((h-5)*scagVals[d-1][sS][pair])));
		}
		drawSelected();
	}
	
	
	public void draw2(){
		float stepX= Main.plotX;
		int stepY= Main.plotY; 
		w = Main.plotW-dif1;
		if (index[1]<bIndex[1]){
			x = stepX+index[1]*w;
		}	
		else if (index[1]==bIndex[1]){
			x = stepX+index[1]*w;
			w = Main.plotW+(numV-2)*dif1;
			
		}	
		else{
			x = stepX+index[1]*w +(numV-1)*dif1;
		}
		h = Main.plotH-dif2;
		if (index[0]<bIndex[0]){
			y = stepY+index[0]*h;
		}	
		else if (index[0]==bIndex[0]){
			y = stepY+(index[0]-1)*h +Main.plotH+(numV-2)*dif2;
			h = Main.plotH+(numV-2)*dif2;
		}	
		else{
			y = stepY+index[0]*h +(numV-1)*dif2 ;
		}
		int i1 = orderVar[sS][index[0]];
		int i2 = orderVar[sS][index[1]];
		if (i1>i2){
			pair = indexToPair(i1,i2);
		}
		else{
			pair = indexToPair(i2,i1);
		}
		
		
		double scag = (meanYear[sS][pair]-meanYearMin[sS])/(meanYearMax[sS]-meanYearMin[sS]); 
		//System.out.println(scag);
		Color color = ColorScales.getColor(scag,"temperature", 1f);
		parent.fill(color.getRGB());
		parent.noStroke();
		parent.rect(x, y-h+1, w-1, h-1);
		
		parent.stroke(new Color(0,0,0,150).getRGB());
		parent.strokeWeight(1);
		if (b>=0){
			parent.stroke(new Color(0,0,0,255).getRGB());
			parent.strokeWeight(1.4f);				
		}
		for (int d=2; d<w-2;d++){
			parent.line(x+d,(float) (y-2-((h-5)*scagVals[d][sS][pair])),
					x+d-1,(float) (y-2-((h-5)*scagVals[d-1][sS][pair])));
		}
		
		drawSelected();
	}
	
	
	public void draw3(){
		// Lensing X-Axis
		w = Main.plotW-dif3;
		if (index[1]==0)
			pX[0] = Main.plotX;
		if (index[1]<bIndex[1]){
			double mid = (bIndex[1]-1)/2.;
			int dd = (int) ((index[1]-mid)*dif3);
			w += dd; 
		}	
		else if (index[1]==bIndex[1]){
			w = Main.plotW+(numV-2)*dif3;
		}	
		else{
			double mid = bIndex[1]+(numV-bIndex[1]-1)/2.;
			int dd = (int) ((mid-index[1])*dif3);
			w += dd; 
		}
		pX[index[1]+1]=pX[index[1]] +w; 
		x =pX[index[1]];
		pW[index[1]] = w; 
		
		// Lensing Y-Axis
		h = Main.plotH-dif4;
		if (index[0]==1)
			pY[1] = Main.plotY;
		
		if (index[0]<bIndex[0]){
			double mid = (bIndex[0])/2.;
			int dd = (int) ((index[0]-mid)*dif4);
			h += dd; 
		}	
		else if (index[0]==bIndex[0]){
			h = Main.plotH+(numV-2)*dif4;
		}	
		else{
			double mid = bIndex[0]+(numV-bIndex[0])/2.;
			int dd = (int) ((mid-index[0])*dif4);
			h += dd; 
		}
		pY[index[0]+1]=pY[index[0]] +h; 
		y =pY[index[0]]+h;
		pH[index[0]] = h; 
		
		int i1 = orderVar[sS][index[0]];
		int i2 = orderVar[sS][index[1]];
		if (i1>i2){
			pair = indexToPair(i1,i2);
		}
		else{
			pair = indexToPair(i2,i1);
		}
		
		double scag = (meanYear[sS][pair]-meanYearMin[sS])/(meanYearMax[sS]-meanYearMin[sS]); 
		Color color = ColorScales.getColor(scag,"temperature", 1f);
		parent.fill(color.getRGB());
		parent.noStroke();
		parent.rect(x, y-h+1, w-1, h-1);
		
		//Change saturation depend on how far from the brushing plot
		int bIndex = -1;
		for (int p =0; p<numP;p++){
			if (plots[p].b>=0)
				bIndex =p;
		}
		
		if (bIndex<0){
			parent.stroke(new Color(0,0,0,150).getRGB());
		}
		else{
			int dis = Math.max(Math.abs(index[0]-plots[bIndex].index[0]), Math.abs(index[1]-plots[bIndex].index[1]));
			int sat = 150-18*dis;
			if (sat<1) sat = 1;
				parent.stroke(new Color(0,0,0,sat).getRGB());
		}
		parent.strokeWeight(1);
		if (b>=0){
			parent.stroke(new Color(0,0,0,255).getRGB());
			parent.strokeWeight(1.4f);				
		}
		for (int d=2; d<w-2;d++){
			parent.line(x+d,(float) (y-2-((h-5)*scagVals[d][sS][pair])),
					x+d-1,(float) (y-2-((h-5)*scagVals[d-1][sS][pair])));
		}
		drawSelected();
	}
	
	
	public void drawSelectedScag(int order) {
		xS =xS0+plotX-25;
		for (int s=0;s<9;s++){
			float xS2 =xS+s*wS;
			double scag = (meanYear[s][pair]-meanYearMin[s])/(meanYearMax[s]-meanYearMin[s]); 
			Color color;
			if (s==sS)
				color = ColorScales.getColor(scag,"temperature", 1f);
			else 
				color = ColorScales.getColor(scag,"temperature", 0.3f);
			if (order==2){
				color = ColorScales.getColor(scag,"temperature", 1f);
			}
				
			parent.fill(color.getRGB());
			parent.noStroke();
			parent.rect(xS2, yS+(order)*hS, wS-1, hS-1);
			
			if (s==sS)
				parent.stroke(new Color(0,0,0,150).getRGB());
			else
				parent.stroke(new Color(0,0,0,20).getRGB());
			parent.strokeWeight(1);
			if (order==2){
				parent.stroke(new Color(0,0,0,255).getRGB());
				parent.strokeWeight(1);
			}	
			if (b>=0){
				parent.strokeWeight(1f);				
			}
			for (int d=2; d<wS-2;d++){
				parent.line(xS2+d,(float) (yS+(order+1)*hS-2-((hS-5)*scagVals[d][s][pair])),
						xS2+d-1,(float) (yS+(order+1)*hS-2-((hS-5)*scagVals[d-1][s][pair])));
			}
			
			if (s==sS){
				parent.noFill();
				parent.strokeWeight(1);
				parent.stroke(Color.BLACK.getRGB());
				parent.rect(xS2, yS+(order)*hS-1, wS-1, hS);
			}	
			// Draw variable
		}
		float xS2 =xS+9*wS;
		int yS2 =yS+(order)*hS;
		int v1 = orderVar[sS][index[1]];
		int v2 = orderVar[sS][index[0]];
		int red   = PlotText.tColors[v1].getRed();
		int green = PlotText.tColors[v1].getGreen();
		int blue  = PlotText.tColors[v1].getBlue();
		for (int i =0; i<hS/2; i++){
			int alpha = 15+5*(hS/2-i);
			if (alpha>255)
				alpha=255;
			parent.stroke(new Color(red,green,blue,alpha).getRGB());
			parent.strokeWeight(1);
			parent.line(xS2,yS2+i, xS2+150,yS2+i);
		}
		red   = PlotText.tColors[v2].getRed();
		green = PlotText.tColors[v2].getGreen();
		blue  = PlotText.tColors[v2].getBlue();
		for (int i =0; i<hS/2-1; i++){
			int alpha = 20+5*(i);
			if (alpha>255)
				alpha=255;
			parent.stroke(new Color(red,green,blue,alpha).getRGB());
			parent.strokeWeight(1);
			parent.line(xS2,yS2+hS/2+i, xS2+150,yS2+hS/2+i);
		}
		parent.fill(PlotText.tColors[v1].getRGB());
		parent.text(varlist[v1], xS2+3, yS2+15);
		parent.fill(PlotText.tColors[v2].getRGB());
		parent.text(varlist[v2], xS2+3, yS2+33);
	}
		
	
	public void drawSelected() {
		// Draw Selected
		if (s){
			parent.noFill();
			parent.strokeWeight(1);
			parent.stroke(Color.BLACK.getRGB());
			parent.rect(x-1, y-h, w, h);
		}
	}

		
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x<mX && mX<=x+w && y-h<mY && mY<=y){
			b =0;
			bIndex[0] =index[0];
			bIndex[1] =index[1];
			return;
		}	
		b =-1;
	}
	
	public void checkSelected() {
		if (b>=0){
			s = !s;
		}
	}
	
	//
	public static int[] pairToIndex(int p){
		int[] index = new int[2];
		index[0]=-1;
		index[1]=-1;
		int k=0;
		for (int r=1;r<numV;r++){
			for (int c=0;c<r;c++){
				if (k==p){
					index[0]=r;
					index[1]=c;
				}
				k++;
			}
		}
		return index;
	}
	
	public static int indexToPair(int i1,int i2){
		int k=0;
		for (int r=1;r<numV;r++){
			for (int c=0;c<r;c++){
				if (r== i1 && c==i2){
					return k;
				}
				k++;
			}
		}
		return -1;
	}
	
		
	public static void computeMean(){
		for (int s=0; s<9;s++){
			for (int p=0; p<numP;p++){
				for (int d=0; d<nM;d++){
					meanYear[s][p] += scagVals[d][s][p];
				}
				meanYear[s][p] /=nM;
			}
		}
		for (int s=0; s<9;s++){
			meanYearMin[s] = Double.MAX_VALUE;
			meanYearMax[s] = Double.MIN_VALUE;
			for (int p=0; p<numP;p++){
				if (meanYear[s][p]>meanYearMax[s])
					meanYearMax[s] = meanYear[s][p];
				if (meanYear[s][p]<meanYearMin[s])
					meanYearMin[s] = meanYear[s][p];
			}
		}	
	}
	
	// Compute scagnostic for 1 variable against all other over a year
	public static void computeMeanVar(){
		for (int s=0; s<9;s++){
			for (int v=0; v<numV;v++){
				meanYearVar[s][v] =0;
				for (int p=0; p<numP;p++){
					int[] indexes = pairToIndex(p);
					if (indexes[0]==v || indexes[1]==v){
						meanYearVar[s][v] += meanYear[s][p];
					}
				}
			}
		}
	}
	
	// Compute order variables based on scagnostics
	public static void orderVar(){
		for (int s=0; s<9;s++){
			orderVar[s] = order(meanYearVar[s]);
		}
	}
	public static int[] order(double[] a){
		int[] o = new int[a.length];
		double maxV = Double.MAX_VALUE;
		for (int i=0; i<a.length;i++){
			double m= Double.MIN_VALUE;
			int index= -1;
			for (int j=0; j<a.length;j++){
				if (a[j]>m && a[j]<maxV){
					m = a[j];
					index = j;
				}
			}
			o[i] =index;
			maxV = m;
		}
		return o;
	}
	
	
}