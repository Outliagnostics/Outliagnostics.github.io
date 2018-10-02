package main;

import java.awt.Color;
import processing.core.PApplet;
import static main.Main.sgap1;
import static main.Main.disArray;

public class Slider2{
	int count =0;
	public int pair =-1;
	public PApplet parent;
	public int x,y;
	public int w; 
	public int l =-1;
	public int u =-1;
	public float lM =-1;
	public float uM =-1;
	
	
	public static Color c1  = new Color(125,125,125);
	public static Color c2  = new Color(125,125,125);
	
	public int bSlider = -1;
	public int sSlider = -1;
	
	
	public Slider2(PApplet parent_){
		parent = parent_;
		l = 0;
		u = 100;
		x= 150;
		y= 715;
		w=200*sgap1;
	}
		
	public void update(){
		lM = l/(100f*sgap1);
		uM = u/(100f*sgap1);
	}
		
	public void draw(){
		checkBrushingSlider();
		if (count>4 && parent.mousePressed)
			checkSelectedSlider3();

		update();
		int xx1 = x+l;
		int xx2 = x+u;
		parent.stroke(Color.GRAY.getRGB());
		parent.strokeWeight(1.0f);
		for (int i=0; i<3;i++ ){
			int xx = x+(i*100*sgap1);
			parent.line(xx, y, xx, y+8);
			if (i>=2) break;
			for (int j=0; j<10; j++ ){
				parent.line(xx+j*10*sgap1, y, xx+j*10*sgap1, y+4);
				for (int k=1; k<10; k++ ){
					parent.line(xx+j*10*sgap1+k*sgap1, y, xx+j*10*sgap1+k*sgap1, y+1);
				}
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
			c1 = new Color(125,125,125);
		}
		parent.noStroke();
		parent.fill(c1.getRGB());
		parent.triangle(xx1, y, xx1-5, y+10, xx1+5, y+10);
		parent.textAlign(PApplet.RIGHT);
		String lT =lM+"";
		if (lM==0)
			lT ="0";	
		parent.text(lT,xx1+4,y+20);
		
		//Upper range
		if (sSlider==1){
			c2= Color.WHITE;
		}	
		else if (bSlider==1){
			c2= Color.PINK;
		}	
		else{
			c2 = new Color(125,125,125);
		}
		parent.noStroke();
		parent.fill(c2.getRGB());
		parent.triangle(xx2, y, xx2-5, y+10, xx2+5, y+10);
		parent.textAlign(PApplet.LEFT);
		parent.text(uM+"",xx2-10,y+20);
		
		
		count++;
	    if (count==10000)
	    	count=200;
	}
	
	
	public void checkBrushingSlider() {
		int xx1 = x+l;
		int xx2 = x+u;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		
		if (xx1-30<mX && mX < xx1+6 && y-15<mY && mY<y+40){
			bSlider =0;
			return;
		}	
		else if (xx2-6<mX && mX < xx2+30 && y-15<mY && mY<y+40){
			bSlider =1; 
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
			l += (parent.mouseX - parent.pmouseX);
			if (l>=u) l=u-1;
			if (l<0)  l=0;
		}	
		else if (sSlider==1){
			u += (parent.mouseX - parent.pmouseX);
			if (u<=l) u=l+1;
			if (u>w-1)  u=w-1;
			
		}	
			
		updateMaxBalls();
		return sSlider;
	}
	
		public void updateMaxBalls() {
			int count =0;
			for (int p =0; p<disArray.length;p++){
				for (int m=0;m<disArray[0].length;m++){
					if (Main.disArray[p][m]<u/sgap1){
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