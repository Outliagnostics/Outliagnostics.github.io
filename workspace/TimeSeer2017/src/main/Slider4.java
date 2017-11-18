package main;

import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;
import static main.Main.sgap1;
import static main.Main.disArray;

public class Slider4{
	int count =0;
	public int pair =-1;
	public PApplet parent;
	public int x,y;
	public int h; 
	public int l =-1;
	public int u =-1;
	public float lM =-1;
	public float uM =-1;
	public float hM =-1;
	
	public static Color c1  = new Color(125,125,125);
	public static Color c2  = new Color(125,125,125);
	
	public int bSlider = -1;
	public int sSlider = -1;
	
	
	public Slider4(PApplet parent_){
		parent = parent_;
		l = 0;
		u = 200;
		x= 40;
		y= 250;
		h= (BBP.maxDisimilarity)*sgap1;
	}
		
	public void update(){
		lM = l/(100f*sgap1);
		uM = u/(100f*sgap1);
		hM = h/(100f*sgap1);
	}
		
	public void draw(){
		checkBrushingSlider();
		if (count>4 && parent.mousePressed)
			checkSelectedSlider3();

		update();
		int yy1 = y+l;
		int yy2 = y+u;
		DecimalFormat df = new DecimalFormat("#.##");

		parent.stroke(Color.GRAY.getRGB());
		parent.strokeWeight(1.0f);
			parent.line(x, y, x, y+8);
			for (int j=0; j<=BBP.maxDisimilarity/10; j++ ){
				parent.line(x-5, y+j*10*sgap1, x, y+j*10*sgap1);
				if (j==BBP.maxDisimilarity/10) break;
				for (int k=1; k<10; k++ ){
					parent.line(x-2, y+j*10*sgap1+k*sgap1, x, y+j*10*sgap1+k*sgap1);
				}
			}
		
		//Lower range
		if (sSlider==0){
			c1= Color.WHITE;
		}	
		else if (bSlider==0){
			c1= Color.PINK;
		}	
		else{
			c1 = new Color(170,170,170);
		}
		parent.noStroke();
		parent.fill(c1.getRGB());
		parent.triangle(x-10, yy1-5, x-10, yy1+5, x, yy1);
		parent.textAlign(PApplet.RIGHT);
		String lT =df.format(lM);
		parent.text(lT,x-12,yy1+4);
		
		//Upper range
		if (sSlider==1){
			c2= Color.WHITE;
		}	
		else if (bSlider==1){
			c2= Color.PINK;
		}	
		else{
			c2 = new Color(170,170,170);
		}
		parent.noStroke();
		parent.fill(c2.getRGB());
		parent.triangle(x-10, yy2-5, x-10, yy2+5, x, yy2);
		parent.text(df.format(uM), x-12,yy2+4);
		parent.textAlign(PApplet.LEFT);
		
		if (sSlider==2){
			c2= Color.WHITE;
		}	
		else if (bSlider==2){
			c2= Color.PINK;
		}	
		else{
			c2 = new Color(170,170,170);
		}
		parent.stroke(c2.getRGB());
		parent.line(x-30,y+h,x+40,y+h);
		parent.fill(c2.getRGB());
		parent.text(df.format(hM), x-25,y+h+14);
		parent.noStroke();
		
		count++;
	    if (count==10000)
	    	count=200;
	}
	
	
	
	public void checkBrushingSlider() {
		int yy1 = y+l;
		int yy2 = y+u;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		
		if (x-30<mX && mX < x && yy1-15<mY && mY<yy1+15){
			bSlider =0;
			return;
		}	
		else if (x-30<mX && mX < x && yy2-15<mY && mY<yy2+15){
			bSlider =1; 
			return;
		}
		else if (x-30<mX && mX < x+40 && y+h-10<mY && mY<y+h+25){
			bSlider =2; 
			return;
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
			if (l>=u) l=u-1;
			if (l<0)  l=0;
		}	
		else if (sSlider==1){
			u += (parent.mouseY - parent.pmouseY);
			if (u<=l) u=l+1;
			if (u>h)  u=h;
			
		}
		else if (sSlider==2){
			h += (parent.mouseY - parent.pmouseY);
			if (h<=u+10) h=u+11;
			BBP.maxDisimilarity = h/sgap1;
			h = BBP.maxDisimilarity*sgap1; 
		}
		
			
		updateMaxBalls();
		return sSlider;
	}
	
		public void updateMaxBalls() {
			int count =0;
			for (int p =0; p<disArray.length;p++){
				for (int m=0;m<disArray[0].length;m++){
					if (Main.disArray[p][m]<=u/sgap1){
						count++;
					}
				}	
			}	
			BBP.sasBalls =count;
			if (count<BBP.MAX_BALL_SHOWING)
				BBP.maxBalls =count;
			else
				BBP.maxBalls =BBP.MAX_BALL_SHOWING;
		}	
}