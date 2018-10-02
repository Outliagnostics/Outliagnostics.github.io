package main;

import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;

public class Slider{
	int count =0;
	public int pair =-1;
	public PApplet parent;
	public float x,y;
	public int w; 
	public int u = 0;
	
	public Color c1  = new Color(125,125,125);
	public Color c2  = new Color(125,125,125);
	
	public int bSlider = -1;
	public int sSlider = -1;
	public int ggg =10;
	public String text ="";
	public float value =0;
	
	
	public Slider(PApplet parent_){
		parent = parent_;
		x= 200;
		y= 2;
		w= 100;
		text = "Filter";
	}
		
	
		
	public void draw(){
		checkBrushingSlider();
		if (count>4 && parent.mousePressed)
			checkSelectedSlider3();

		float xx2 = x+u;
		DecimalFormat df = new DecimalFormat("#.##");
		parent.stroke(120,120,120);
		parent.strokeWeight(1.0f);
		parent.line(x, y+9, x, y+16);
		parent.line(x+w, y+9, x+w, y+16);
		for (int k=1; k<10; k++ ){
			parent.line(x+k*ggg, y+9, x+k*ggg, y+11);
		}
		
		
		//Upper range
		if (sSlider==1){
			c2= Color.BLACK;
		}	
		else if (bSlider==1){
			c2= Color.PINK;
		}	
		else{
			c2 = new Color(170,170,170);
		}
		parent.textSize(13);
		parent.noStroke();
		parent.fill(c2.getRGB());
		parent.triangle(xx2-5, y+20, xx2+5, y+20, xx2, y+10);
		parent.textAlign(PApplet.RIGHT);
		parent.text(text, x-10,y+15);
		
		parent.textAlign(PApplet.CENTER);
		parent.textSize(12);
		value = (float)u/100;
		parent.text(df.format(value), xx2,y+8);
		parent.textAlign(PApplet.LEFT);
		
		count++;
	    if (count==10000)
	    	count=200;
	}
	
	
	
	public void checkBrushingSlider() {
		float xx2 = x+u;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		
		if (xx2-20<mX && mX < xx2+20 && y<mY && mY<y+25){
			bSlider =1; 
			return;
		}
		bSlider =-1;
	}
	
	public void mousePressed() {
		sSlider = bSlider;
	}
	public void mouseRelease() {
		sSlider = -1;
	}	
	public int checkSelectedSlider3() {
		if (sSlider==1){
			u += (parent.mouseX - parent.pmouseX);
			if (u<0) u=0;
			if (u>w)  u=w;
		}
		return sSlider;
	}
		
}