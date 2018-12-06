package main;

import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;
import static main.Main.*;

public class Slider1{
	public int pair =-1;
	public PApplet parent;
	public float x,y,h;
	public float l =-1;
	public float u =-1;
	int count =0;
	public static Color c1  = new Color(130,130,80);
	public static Color c2  = new Color(160,80,80);
	
	public int bSlider = -1;
	public int sSlider = -1;
	public int gap = 9;
	
	
	public Slider1(PApplet parent_, float x_, float y_, float u_, float l_){
		parent = parent_;
		x = x_;
		y = y_;
		l = l_;
		u = u_;
		h= u_;
	}
		
		
	public void draw(){
		parent.noStroke();
		checkBrushingSlider();
		if (count>4 && parent.mousePressed)
			checkSelectedSlider3();

		float yy1 = y+l;
		float yy3 = y+l+h+gap2;
		float yy5 = y+l+2*h+2*gap2;
		float yy2 = y+u;
		float yy4 = y+u+h+gap2;
		float yy6 = y+u+2*h+2*gap2;
		
		//Lower range
		if (sSlider==0){
			c1= Color.WHITE;
		}	
		else if (bSlider==0){
			c1= Color.PINK;
		}	
		else{
			c1 = c2 = Color.GRAY;
		}
		parent.fill(c1.getRGB());
		if (selectedPlots.size()>0)
			parent.triangle(x, yy1, x+12, yy1, x, yy1+8);
		if (selectedPlots.size()>1)
			parent.triangle(x, yy3, x+12, yy3, x, yy3+8);
		if (selectedPlots.size()>2)
			parent.triangle(x, yy5, x+12, yy5, x, yy5+8);
	
		if (bSlider>=0 || sSlider>=0){
			DecimalFormat df = new DecimalFormat("#.##");
			String tl = df.format((h-l)/h);
			if ((bSlider>=0 && bSlider%2==0)|| sSlider%2==0) {
				parent.text(tl,x+12,yy1-1);
				parent.text(tl,x+12,yy3-1);
				parent.text(tl,x+12,yy5-1);
			}	
		}
		
		//Upper range
		if (sSlider==1){
			c2= Color.WHITE;
		}	
		else if (bSlider==1){
			c2= Color.PINK;
		}	
		else{
			c2 = Color.GRAY;
		}
		parent.fill(c2.getRGB());
		if (selectedPlots.size()>0)
			parent.triangle(x, yy2, x+12, yy2, x, yy2-8);
		if (selectedPlots.size()>1)
			parent.triangle(x, yy4, x+12, yy4, x, yy4-8);
		if (selectedPlots.size()>2)
			parent.triangle(x, yy6, x+12, yy6, x, yy6-8);
		if (bSlider>=0|| sSlider>=0){
			DecimalFormat df = new DecimalFormat("#.##");
			String tu = df.format((h-u)/h);
			if ((bSlider>=0 && bSlider%2==1)|| sSlider%2==1) {
					parent.text(tu,x+14,yy2+12);
					parent.text(tu,x+14,yy4+12);
					parent.text(tu,x+14,yy6+12);
			}		
		}
		count++;
	    if (count==10000)
	    	count=200;
	}
	
	
	public void checkBrushingSlider() {
		float yy1 = y+l;
		float yy3 = y+l+h+gap2;
		float yy5 = y+l+2*h+2*gap2;
		float yy2 = y+u;
		float yy4 = y+u+h+gap2;
		float yy6 = y+u+2*h+2*gap2;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x-2<mX && mX<x+16){
			if (yy1-4<mY && mY<yy1+12){
				bSlider =0;
				return;
			}
			else if (yy3-4<mY && mY<yy3+12){
				bSlider =0;
				return;
			}
			else if (yy5-4<mY && mY<yy5+12){
				bSlider =0;
				return;
			}
			else if (yy2-12<mY && mY<yy2+4){
				bSlider =1; 
				return;
			}
			else if (yy4-12<mY && mY<yy4+4){
				bSlider =1; 
				return;
			}
			else if (yy6-12<mY && mY<yy6+4){
				bSlider =1; 
				return;
			}
		}
		bSlider =-1;
	}
	
	public void checkSelectedSlider1() {
		sSlider = bSlider;
	}
	public void checkSelectedSlider2() {
		sSlider = -1;
	}
	public int checkSelectedSlider3() {
		if (sSlider==0){
			l += (parent.mouseY - parent.pmouseY);
			if (l>=u-15) l=u-15;
			if (l<0)  l=0;
		}	
		else if (sSlider==1){
			u += (parent.mouseY - parent.pmouseY);
			if (u<=l+15) u=l+15;
			if (u>h)  u=h;
		}	
		return sSlider;
	}
}