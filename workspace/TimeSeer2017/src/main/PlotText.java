package main;

import static main.Main.*;

import java.awt.Color;

import processing.core.PApplet;

public class PlotText{
	public int v =-1;
	public PApplet parent;
	public float x, y;
	public float w, h;
	public double al= PI/6;
	public int wText =160;
	public int count =0;
	
	public static Color[] tColors = {
		new Color(0,0,0),
		
		new Color(255,0,0).darker(),  
		
		new Color(0,255,255).darker(),
		new Color(0,255,255).darker(),
		
		new Color(0,0,255).darker().darker(),
		new Color(0,0,255).darker(),
		new Color(0,0,255).darker(),
		new Color(0,0,255).darker().darker(),
		
		new Color(255,0,0).darker().darker(), 
		new Color(255,0,0).darker(), 
		
		new Color(255,255,0).darker().darker().darker(), 
		new Color(255,255,0).darker(), 
		new Color(255,255,0).darker(), 
		
		new Color(0,255,0).darker().darker().darker(),
		new Color(0,255,0).darker(),
		new Color(0,255,0).darker(),
		
		new Color(0,255,255).darker().darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		
		new Color(70,70,70),
		
		new Color(255,0,255).darker().darker(),
		new Color(255,0,255).darker(),
		new Color(255,0,255).darker(),
		new Color(255,0,255).darker(),
		
		new Color(0,0,0)};
	
	/*public static Color[] tColors = {new Color(0,0,0,255), new Color(255,0,0).darker(), new Color(0,255,0).darker(), new Color(0,0,255).darker(),
		new Color(255,255,0).darker(),new Color(255,0,255).darker(),new Color(0,255,255).darker(),
		new Color(255,0,0).darker().darker(), new Color(0,255,0).darker().darker(), new Color(0,0,255).darker().darker(),
		new Color(255,255,0).darker().darker(),new Color(255,0,255).darker().darker(),new Color(0,255,255).darker().darker(),
		new Color(0,255,0).darker().darker().darker(),
		new Color(255,255,0).darker().darker().darker(),new Color(255,0,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker(),
		new Color(0,255,255).darker().darker().darker()}; */
	public static int[][] varr = new int[numP][2];
	
	
	public PlotText(int var, PApplet parent_){
		parent = parent_;
		v = var;
		
		for (int i =0 ; i<numP;i++){
			varr[i] = PlotText.pairToIndex(i);
		}
		
	}
		
	public void draw(){
		w = iW[0].value;
		h = iH[0].value;
		x = plotX+ w*v;
		y = plotY + h*v;
		int o = orderVar[sS][v];
		int r = tColors[o].getRed();
		int g = tColors[o].getGreen();
		int b = tColors[o].getBlue();
		for (int i =0; i<=h; i++){
			int alpha = 10+i;
			if (Main.isTextBr[v])
				alpha =transform(alpha);
			if (alpha>255)
				alpha=255;
			else if (alpha<1)
				alpha=1;
			parent.stroke(new Color(r,g,b,alpha).getRGB());
			parent.translate(x,y-i);
			parent.rotate((float) (-al));
			parent.line(0,0, wText,0);
			parent.rotate((float) (al));
			parent.translate(-x,-(y-i));
		}
		for (int i =0; i<w; i++){
			int alpha = 10+i;
			if (Main.isTextBr[v])
				alpha =transform(alpha);
			if (alpha>255)
				alpha=255;
			else if (alpha<1)
				alpha=1;
			parent.stroke(new Color(r,g,b,alpha).getRGB());
			parent.translate(x+i,y);
			parent.rotate((float) (-al));
			parent.line(0,0, wText,0);
			parent.rotate((float) (al));
			parent.translate(-(x+i),-y);
		}
		parent.fill(tColors[o].getRGB());
		parent.translate(x+8,y-2);
		parent.rotate((float) (-al));
		parent.text(varlist[orderVar[sS][v]],0,0);
		parent.rotate((float) (al));
		parent.translate(-(x+8),-(y-2));
	}
	
	public void draw2(){
		w= plotW;
		h= plotH;
		x = plotX+ w*v;
		y = plotY + h*v;
		
		if (v<bIndex[0] && v<bIndex[1]){
			x -= dif1*v;
			y -= dif2*v;
			w -= dif1;
			h -= dif2;
		}
		else if (v==bIndex[1]){
			x -= dif1*v;
			y -= dif2*v;
			w += (numV-2)*dif1;
			h -= dif2;
		}
		else if (v>bIndex[1] && v<bIndex[0]){
			x -= dif1*v - (numV-1)*dif1 ;
			y -= dif2*v;
			w -= dif1;
			h -= dif2;
		}
		else if (v==bIndex[0]){
			x -= dif1*v - (numV-1)*dif1 ;
			y -= dif2*v - (numV-1)*dif2;
			w -= dif1;
			h -= dif2 - (numV-1)*dif2;
		}
		else if (v>bIndex[1] && v>bIndex[0]){
			x -= dif1*v - (numV-1)*dif1 ;
			y -= dif2*v - (numV-1)*dif2;
			w -= dif1;
			h -= dif2;
		}
		
		int o = orderVar[sS][v];
		int r = tColors[o].getRed();
		int g = tColors[o].getGreen();
		int b = tColors[o].getBlue();
		for (int i =0; i<=h; i++){
			int alpha = 10+i;
			if (alpha>255)
				alpha=255;
			parent.stroke(new Color(r,g,b,alpha).getRGB());
			parent.translate(x,y-i);
			parent.rotate((float) (-al));
			parent.line(0,0, wText,0);
			parent.rotate((float) (al));
			parent.translate(-x,-(y-i));
		}
		for (int i =0; i<w; i++){
			int alpha = 10+i;
			if (alpha>255)
				alpha=255;
			parent.stroke(new Color(r,g,b, alpha).getRGB());
			parent.translate(x+i,y);
			parent.rotate((float) (-al));
			parent.line(0,0, wText,0);
			parent.rotate((float) (al));
			parent.translate(-(x+i),-y);
		}
		parent.fill(tColors[o].getRGB());
		parent.translate(x+8,y-2);
		parent.rotate((float) (-al));
		parent.text(varlist[orderVar[sS][v]],0,0);
		parent.rotate((float) (al));
		parent.translate(-(x+8),-(y-2));
	}
	
	
	public void draw3(){
		w= pW[v];
		h= pH[v];
		x = pX[v];
		y = pY[v+1];
		if (v==0)
			h = w/2;
		else if (v==numV-1)
			w = h/2; 
		int o = orderVar[sS][v];
		int r = tColors[o].getRed();
		int g = tColors[o].getGreen();
		int b = tColors[o].getBlue();
		for (int i =0; i<=h; i++){
			int alpha = 10+i;
			if (alpha>255)
				alpha=255;
			parent.stroke(new Color(r,g,b,alpha).getRGB());
			parent.translate(x,y-i);
			parent.rotate((float) (-al));
			parent.line(0,0, wText,0);
			parent.rotate((float) (al));
			parent.translate(-x,-(y-i));
		}
		for (int i =0; i<w; i++){
			int alpha = 10+i;
			if (alpha>255)
				alpha=255;
			parent.stroke(new Color(r,g,b, alpha).getRGB());
			parent.translate(x+i,y);
			parent.rotate((float) (-al));
			parent.line(0,0, wText,0);
			parent.rotate((float) (al));
			parent.translate(-(x+i),-y);
		}
		parent.fill(tColors[o].getRGB());
		parent.translate(x+8,y-2);
		parent.rotate((float) (-al));
		parent.text(varlist[orderVar[sS][v]],0,0);
		parent.rotate((float) (al));
		parent.translate(-(x+8),-(y-2));
	}
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		double mY2 =mY+Math.tan(al)*(mX-x);
		double ww = wText*Math.cos(al);
		double mX2 =mX-(1/Math.tan(al))*(y-mY);
		double hh = wText*Math.sin(al);
		//parent.rect((float) mX2, (float) mY, 5,5);
		if (x<mX && mX<=x+ww && y-h<mY2 && mY2<=y){
			Main.isTextBr[v] =true;
			return;
		}
		else if (y-hh<mY && mY<=y && x<mX2 && mX2<=x+w){
			Main.isTextBr[v] =true;
			return;
		}
		Main.isTextBr[v] =false;	
	}

	public void checkPressed() {
		if (count>3 && parent.mousePressed){
			 if (Main.isTextBr[v]){
					count=0;
					Main.isTextSe[v] = !Main.isTextSe[v];
					updatePlots();
					//System.out.println("AAAAA: "+v);
			}	
		}
		count++;
	    if (count==10000)
	    	count=200;
	}
	public void updatePlots(){
		for (int p =0; p<numP;p++){
			int[] index = pairToIndex(p);
			if (index[0]==v || index[1]==v)
				plots[p].s = Main.isTextSe[v];
		}
	}
	
	public int transform(int a){
		return (a-10)*6;
	}
		
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