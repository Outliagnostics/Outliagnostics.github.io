package main;
import java.awt.Color;

import processing.core.PApplet;
import processing.core.PFont;

public class Button extends PApplet{
	public int b = -1;
	public PApplet parent;
	public int y = 0;
	public int w = 93;
	public int h = 25;
	public float x2 = 0;
	public int w2 = 200;
	public int itemNum = 9;
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	public Color cGray  = new Color(240,240,240);
	public Button(PApplet parent_){
		parent = parent_;
	}
	public int count =0;
	
	
	public void draw(){
		checkBrushing();
		parent.textFont(metaBold,13);
		parent.fill(155,155,155);
		parent.stroke(0,0,0);
		if (b>0){
			parent.fill(0,0,0);
			parent.stroke(155,155,155);
		}	
		parent.rect(x2, y, w, h);
		
		parent.fill(new Color(255,255,255,255).getRGB());
		parent.text("Browse file...",x2+8,y+16);
			
		count++;
	    if (count==10000)
	    	count=200;
	}
	
	 
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x2<mX && mX<x2+w && y<mY && mY<h){
			b =100;
			return;
		}
		b =-1;
	}
	
}