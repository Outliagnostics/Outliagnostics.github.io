package main;

import java.awt.Color;
import java.text.DecimalFormat;

import static main.Main.bM2;
import static main.Main.bO2;
import static main.Main.bP2;
import static main.Main.found2;
import static main.Main.numS;
import static main.Main.plot2;
import static main.Main.data;
import static main.Main.scagVals;
import static main.Main.bM12;
import static main.PlotText.varr;
import static main.Main.bP12;
import static main.Main.varlist;
import static main.Main.scagColors;


import processing.core.*;

public class Ball {
	float r;
	float m;
	float x;
	float y;
	float vx;
	float vy;
	int id;
	float ka;
	String name;
	int ocurrences;
	public PApplet parent;
	float mass; // Masa
	float kspring; // Constante de resorte
	float damp; // Damping
	float rest_posx;
	float rest_posy;
	float accel = 0; // Aceleracion
	float force = 0; // Fuerza
	float dissimilarity = 0; 
	public boolean b;
	public boolean s=false;
	
	public int order = -1; 
	public int pair = -1; 
	public int vX = -1; 
	public int vY = -1; 
	public int month = -1; 
	public Integrator[] iP = new Integrator[9];
	
	
	Ball(int ID, float KA, String key, int count, PApplet pa) {
		String[] kkk = key.split(" ");
		order = Integer.parseInt(kkk[0]);
		pair  = Integer.parseInt(kkk[1]);
		month = Integer.parseInt(kkk[2]);		
		vX = plot2[pair].index[0];
		vY = plot2[pair].index[1];
		
		for (int j = 0; j < 9; j++) {
			iP[j] = new Integrator(0,0.2f,0.5f);
		}
		
		parent = pa;
		rest_posx = ((parent.width - BBP.right) / 2)
				+ BBP.left / 2;
		rest_posy = ((parent.height - BBP.down) / 2)
				+ BBP.right / 2;
		ka = KA;
		
		
		r = ka / PApplet.PI;
		m = r;
		x = parent.random(r + BBP.left, parent.width
				- BBP.right - r);
		y = parent.random(r + BBP.top, parent.height
				- BBP.down - r);
		vx = parent.random(-3, 3);
		vy = parent.random(-3, 3);
		id = ID;
		name = key;
		b = false;
		mass = PApplet
				.sqrt((((PApplet.PI
						* PApplet.pow(
								(parent.height - BBP.down - BBP.top) / 2,
								2) * 0.8f) / 2000) / PApplet.PI));
		damp = 0.9f;
		kspring = 0.01f;
		
		ocurrences = 100-count;
		dissimilarity = (count)/100f;
		
	}

	void fall() {
	}

	public void spring() {
		rest_posx = BBP.centerX;
		rest_posy = BBP.centerY;
		float mul = (1-dissimilarity); 
		
		force = -kspring * (y - rest_posy); // f=-ky
		accel = force / mass; // Asignar aceleracion
		vy = damp * (vy + accel); // Definir velocidad
		vy *= mul; 
		
		force = -kspring * (x - rest_posx); // f=-ky
		accel = force / mass; // Asignar aceleracion
		vx = damp * (vx + accel); // Definir velocidad
		vx *= mul; 
		
	}

	void bounce() {
		if (y + vy + r > parent.height - BBP.down) {
			y = parent.height - BBP.down - r;
			vx *= BBP.f;
			vy *= -BBP.b;
		}
		if (y + vy - r < BBP.top) {

			y = r + BBP.top;
			vx *= BBP.f;
			vy *= -BBP.b;
		}
		if (x + vx + r > parent.width - BBP.right) {

			x = parent.width - BBP.right - r;
			vx *= -BBP.b;
			vy *= BBP.f;
		}
		if (x + vx - r < BBP.left) {

			x = r + BBP.left;
			vx *= -BBP.b;
			vy *= BBP.f;
		}
	}

	void collide() {
		for (int i = BBP.maxBalls; i >= 0; i--) {
			if (i < BBP.balls.length){// && BBP.balls[i].dissimilarity<this.dissimilarity) {
				float X = BBP.balls[i].x;
				float Y = BBP.balls[i].y;
				float R = BBP.balls[i].r;
				float M = BBP.balls[i].m;
				float deltax = X - x;
				float deltay = Y - y;
				float d = PApplet.sqrt(PApplet.pow(deltax, 2) + PApplet.pow(deltay, 2));
				if (d <= (r + R) && d > 0 && BBP.balls[i].dissimilarity<=this.dissimilarity) {
					float dD = r + R - d;
					float theta = PApplet.atan2(deltay, deltax);
					vx += -dD * PApplet.cos(theta) * M / (m + M);
					vy += -dD * PApplet.sin(theta) * M / (m + M);
					vx *= BBP.b;
					vy *= BBP.b;
				}
			}
		}
	}

	void move() {
		if (b && parent.mousePressed && BBP.count>1) {
			x = parent.mouseX;
			y = parent.mouseY;
			vx = 0;
			vy = 0;
			BBP.draggingBall = id;
			BBP.count=0;
		} else {
			x += vx;
			y += vy;
		}
	}

	public void mouseClicked() {
		if (b) {
			s=true;
		}	
	}
		
	public boolean checkBrushing() {
		if (PApplet.dist(x, y, parent.mouseX, parent.mouseY) < r)
			b = true;
		else
			b = false;
		return b;

	}

	void display() {
		float A = BBP.balls[0].ocurrences; // maximo original
		float C = ocurrences; // valor original
		float B = BBP.balls[BBP.bubble_plots - 1].ocurrences; // minimo
		if (BBP.llenar_burbujas)
			parent.fill(255, 0, 255);
		else
			parent.noFill();
		
		if (b)
			parent.fill(255, 0, 0, 115);
			// stroke(ColorLineasGrales);
			float lc = -1 * (((A - C) / (A - B)) * (60 - 200) - 60);
			float lcalpha = -1 * (((A - C) / (A - B)) * (255 - 90) - 255);
			if (A == B)
				lcalpha = 255;
			if (lc > 255)
				lc = 255;
			else if (lc < 1)
				lc = 1;

			Color local = new Color(0, 0, 0, 120);
			parent.stroke(local.getRGB());
			parent.strokeWeight(r / 20);
			// noFill();
			
			float tamanio = r * 0.8f;
			parent.textFont(BBP.font, tamanio);
			//parent.fill(255, 0,255, lcalpha);
			int count = 100 - ocurrences;
			
			float val = ((float) BBP.maxDisimilarity -count)/BBP.maxDisimilarity;
			if (val<0) val =0;
			else if (val>1) val=1;
			Color color = ColorScales.getColor(val, "rainbow", 1f);
			parent.fill(color.getRGB());
			
			float rr = r*0.99f;
			//parent.noStroke();
			
			// Selected Plot is centered
			if (pair==bP12 && month==bM12){
				x= BBP.centerX;
				y= BBP.centerY;
			}
			
			if (BBP.show_background){
				if (pair==bP12 && month==bM12){
					if (BBP.show_rect){
						parent.rect(x-r*0.78f,y-r*0.78f,r*1.56f,r*1.56f);
					}
					else
						parent.ellipse(x, y, rr*2, rr*2);
				}
				else{
					
					if (s && BBP.bBall==id){
						parent.stroke(255,0,255);
						bO2 = order;
						bP2 = pair;
						bM2 = month ;
						found2 = true;
					}
					else if (b && BBP.bBall==id){
						parent.stroke(255,255,255);
					}
					
					if (BBP.show_rect)
						parent.rect(x-r*0.78f,y-r*0.78f,r*1.56f,r*1.56f);
					else{
					
						//System.out.println("BALL:"+month+" "+x+" "+r);
						parent.ellipse(x, y, rr*2, rr*2);
					}	
				}
			
				parent.noStroke();
				parent.fill(0,0,0);
				for (int s = 0; s < numS; s++) {
					float x3 = x - rr*0.65f + rr*1.3f*data[month][vX][s];
					float y3 = y - rr*0.65f + rr*1.3f*(1-data[month][vY][s]);
					parent.ellipse(x3, y3, 2+rr/12, 2+rr/12);
				}
			}
			
				
			drawPie();
			parent.textAlign(PApplet.CENTER);
			if (BBP.show_info) {
				float size = 3+r* 0.25f;
				if (pair!=bP12 || month!=bM12){
					DecimalFormat df = new DecimalFormat("#.##");
					parent.textFont(BBP.font, size);
					parent.fill(150, 0, 150);
					parent.text(df.format(dissimilarity) , x, y-r*0.74f);
					
				}
			}
			if (BBP.show_name){
				float size = 5+r * 0.2f;
				parent.textFont(BBP.font, size);
				parent.fill(255, 255, 255);
				parent.text(varlist[varr[pair][0]], x, y +r*0.9f);
				
				parent.fill(255, 55, 255);
				parent.text("Day "+(month+1), x, y -r*0.81f);
				
				parent.fill(255, 255, 255);
				parent.textAlign(PApplet.LEFT);
				parent.translate(x-r*0.77f,y+r/2);
				parent.rotate((float) (-PApplet.PI/2.));
				parent.text(varlist[varr[pair][1]],0,0);
				parent.rotate((float) (PApplet.PI/2.));
				parent.translate(-(x-r*0.77f),-(y+r/2));
			}	
			
	}
	public static Ball[] append(Ball t[], float ka, String key, int count,
			PApplet p) {
		Ball temp[] = new Ball[t.length + 1];
		System.arraycopy(t, 0, temp, 0, t.length);
		temp[t.length] = new Ball(t.length, ka, key, count, p);
		return temp;
	}
	
	public void drawPie() {
		parent.textAlign(PApplet.LEFT);
		float lastAng = -PApplet.PI/2;
		parent.strokeWeight(1);
		parent.textFont(BBP.font, 10+r/20);
		for (int i = 0; i < 9; i++){
			float v = 0;
			if (BBP.show_scag)
				v = scagVals[month][i][pair]*r*2;
			iP[i].target(v);
			iP[i].update();
			
			Color color =scagColors[i];
			
			parent.fill(color.getRGB());
			parent.arc(x,y, iP[i].value, iP[i].value, lastAng, lastAng+PApplet.PI*2/9);
			if (BBP.show_scag && BBP.show_info && pair==bP12 && month==bM12)
				drawPieLabel(x,y,lastAng, lastAng+PApplet.PI*2/9, Main.scagNames[i],color.darker());
		    lastAng += PApplet.PI*2/9;  
		}
	}
	public void drawPieLabel(float cX, float cY,float al1 , float al2, String name,Color color) {
		float al = al1 + PApplet.PI/9;
		float eX = (r*0.2f);
		if (al<=PApplet.PI/2){  
			parent.translate(cX,cY);
			parent.rotate((float) (al+0.1));
			parent.fill(color.getRGB());
			parent.text(name,eX,0);
			parent.rotate((float) (-al-0.1));
			parent.translate(-(cX),-(cY));
		}
		else{
			al = al -PApplet.PI; 
			parent.translate(cX,cY);
			parent.rotate((float) (al-0.1));
			parent.fill(color.getRGB());
			float ww = parent.textWidth(name);
			parent.text(name,-ww-eX,0);
			parent.rotate((float) (-al+0.1));
			parent.translate(-(cX),-(cY));
		}
	}
}
