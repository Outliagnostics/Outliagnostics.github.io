package main;
import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;
import processing.core.PFont;

public class PopupScagnostics{
	public int b = -1;
	public PApplet parent;
	public float x = 740;
	public int y = 15;
	public int w = 300;
	public int h = 28;
	public int itemH = 20;
	public int itemNum = 9;
	public Color cGray  = new Color(240,240,240);
	public static int sS = 3;
	PFont metaBold;
	
	public PopupScagnostics(PApplet parent_){
		parent = parent_;
	}
	
	
	public void draw2(){
		metaBold = parent.loadFont("Arial-BoldMT-18.vlw");
		checkBrushing();
		
		
		// Color legend
	    float xx = main.Main.xx2;
		float yy = y;
		float hh = 28;
		float g = 3;
		
		parent.noStroke();
		parent.fill(200,200,200);
		parent.textFont(metaBold,14);
		parent.textAlign(PApplet.LEFT);
		DecimalFormat df = new DecimalFormat("#.##");
		for (int i = 0; i <= 100; i++) {
			xx = xx+g;
			float val = i/100.f;
			Color color = ColorScales.getColor(val, Main.colorScale, 1f);
			parent.fill(color.getRGB());
			parent.rect(xx,yy,g+1,hh);
			if (i%20==0){
				parent.text(df.format(val),xx-2, yy-2);
			}
		}
		
		
		parent.textAlign(PApplet.CENTER);
		if (b>=0){
			parent.textFont(metaBold, 13);
			parent.fill(new Color(50,50,50,240).getRGB());
			parent.noStroke();
			parent.rect(x, y, w+6, 210);
			for (int i=0;i<Main.scagNames.length;i++){
				if (i==sS){
					parent.fill(Color.GRAY.getRGB());
					parent.rect(x,y+itemH*(i)+5,w+6,itemH);
				}
					
				if (sS==i){
					parent.fill(Color.RED.getRGB());
							
				}
				else if (i==b){
					parent.fill(Color.ORANGE.getRGB());
				}
				else
				{
					parent.fill(Color.WHITE.getRGB());
				}
				parent.textAlign(PApplet.CENTER);
				parent.text(Main.scagNames[i],x+140,y+itemH*(i+1));
			}	
		}
		else{
			parent.textFont(metaBold,15);
			parent.textAlign(PApplet.CENTER);
			parent.fill(0,0,0);
			parent.text(Main.scagNames[sS],x+w/2+10,y+21);
		}	
		
	}
	
	 public void mouseClicked() {
		if (b>=0 && b<9){
			sS =b;
		}	
   	}
	 
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (b==-1){
			if (x<mX && mX<x+w && y<=mY && mY<=itemH+25){
				b =100;
				return;
			}	
		}
		else{
			for (int i=0; i<itemNum; i++){
				if (x<=mX && mX<=x+w && y+itemH*i<=mY && mY<=y+itemH*(i+1)+5){
					b =i;
					return;
				}	
			}
		}
		b =-1;
	}
	
}