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
	public int id =0;
	
	
	public Slider(PApplet parent_, String text_, float xx, float yy, int ID){
		parent = parent_;
		x= xx;
		y= yy;
		w= 100;
		text = text_;
		id = ID;
		//if (id==1){ // Dense
		//	u=40;
		//}
	}
		
	
		
	public void draw(){
		checkBrushingSlider();
		if (count>4 && parent.mousePressed)
			checkSelectedSlider3();

		float xx2 = x+u;
		DecimalFormat df = new DecimalFormat("#.##");
		parent.stroke(0,0,0);
		parent.strokeWeight(1.0f);
		parent.line(x, y+9, x, y+16);
		parent.line(x+w, y+9, x+w, y+16);
		for (int k=1; k<10; k++ ){
			parent.line(x+k*ggg, y+9, x+k*ggg, y+11);
		}
		
		
		//Upper range
		if (sSlider==1){
			c2= Color.RED;
		}	
		else if (bSlider==1){
			c2= new Color(200,200,0);
		}	
		else{
			c2 = new Color(0,0,0);
		}
		parent.textSize(14);
		parent.noStroke();
		parent.fill(c2.getRGB());
		parent.triangle(xx2-5, y+20, xx2+5, y+20, xx2, y+10);
		parent.textAlign(PApplet.RIGHT);
		
		// Decide the text on slider
		if (id==0)
			parent.text(main.Main.scagNames[PopupScagnostics.sS]+" "+text, x-10,y+15);
		else
			parent.text(main.Main.scagNames[3]+" "+text, x-10,y+15);
			
		parent.textAlign(PApplet.CENTER);
		parent.textSize(13);
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