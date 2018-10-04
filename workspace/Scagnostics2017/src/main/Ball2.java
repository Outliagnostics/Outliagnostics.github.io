package main;

import java.awt.Color;
import java.text.DecimalFormat;

import static main.Main.numS;
import static main.Main.plot2;
import static main.Main.data;
import static main.Main.scagVals;
import static main.Main.bM12;
import static main.PlotText.varr;
import static main.Main.bP12;
import static main.Main.varlist;
import static main.Main.scagColors;
import static main.Main.scagNames;


import processing.core.*;

public class Ball2 {
	float r;
	float m;
	float x;
	float y;
	float vx;
	float vy;
	int id;
	float ka;
	int ocurrences;
	public PApplet parent;
	float kspring; // Constante de resorte
	float damp; // Damping
	float rest_posx;
	float rest_posy;
	float accel = 0; // Aceleracion
	float force = 0; // Fuerza
	public boolean b;
	public boolean s=false;
	
	public int pair = -1; 
	public int vX = -1; 
	public int vY = -1; 
	public int month = -1; 
	public Integrator[] iP = new Integrator[9];
	public Integrator iR = new Integrator(0,0.1f,0.5f);
	public boolean active= true;
	
	
	Ball2(int ID, float KA, int id_, int p_, int m_, PApplet pa) {
		pair  = p_;
		month = m_;		
		vX = plot2[pair].index[0];
		vY = plot2[pair].index[1];
		
		id = id_;
		if (id>=BBP2.maxBalls)
			active= false;
		//if (active)
		//System.out.println("id:"+id+" "+active);
		
		
		for (int j = 0; j < 9; j++) {
			iP[j] = new Integrator(0,0.2f,0.5f);
		}
		
		parent = pa;
		rest_posx = ((parent.width - BBP2.right) / 2)
				+ BBP2.left / 2;
		rest_posy = ((parent.height - BBP2.down) / 2)
				+ BBP2.right / 2;
		ka = KA;
		
		
		r = PApplet.sqrt(ka / PApplet.PI);
		iR.target(r);
		
		m = r;
		x = 820;
		y = 340;
		vx = parent.random(-3, 3);
		vy = parent.random(-3, 3);
		ocurrences = 10;
		b = false;
		damp = 0.85f;
		kspring = 0.01f;
	}

	void fall() {

	}

	public void spring() {
		rest_posx = ((parent.width - BBP2.right) / 2)
				+ BBP2.left / 2;
		rest_posy = ((parent.height - BBP2.down) / 2)
				+ BBP2.right / 2;

		if (BBP2.balls.length > 0
				&& (BBP2.balls[0].ocurrences - BBP2.balls[BBP2.bubble_plots - 1].ocurrences) > 0) {
			float A = BBP2.balls[0].ocurrences; // maximo original
			float C = ocurrences; // valor original
			float B = BBP2.balls[BBP2.bubble_plots - 1].ocurrences; // minimo
																			// original
			float D = 5; // nuevo maximo
			float E; // nuevo minimo
			if (BBP2.bubble_plots > 20)
				E = -1;
			else
				E = 0;
			kspring = -1 * (((A - C) / (A - B)) * (D - E) - D);
		}
		if (BBP2.bubble_plots == 1)
			kspring = 4;

		force = -kspring * (y - rest_posy); // f=-ky
		accel = force; // Asignar aceleracion
		vy = damp * (vy + accel); // Definir velocidad
		vx = damp * (vx + accel); // Definir velocidad
	}

	void bounce() {
		if (y + vy + r > parent.height - BBP2.down) {
			y = parent.height - BBP2.down - r;
			vx *= BBP2.f;
			vy *= -BBP2.b;
		}
		if (y + vy - r < BBP2.top) {

			y = r + BBP2.top;
			vx *= BBP2.f;
			vy *= -BBP2.b;
		}
		if (x + vx + r > parent.width - BBP2.right) {

			x = parent.width - BBP2.right - r;
			vx *= -BBP2.b;
			vy *= BBP2.f;
		}
		if (x + vx - r < BBP2.left) {

			x = r + BBP2.left;
			vx *= -BBP2.b;
			vy *= BBP2.f;
		}
	}

	void collide() {
		for (int i = BBP2.balls.length-1; i >= 0; i--) {
			if (BBP2.balls[i].active){
				float X = BBP2.balls[i].x;
				float Y = BBP2.balls[i].y;
				float R = BBP2.balls[i].r;
				float M = BBP2.balls[i].m;
				float deltax = X - x;
				float deltay = Y - y;
				float d = PApplet.sqrt(PApplet.pow(deltax, 2) + PApplet.pow(deltay, 2));
				if (d < r + R && d > 0) {
					float dD = r + R - d;
					float theta = PApplet.atan2(deltay, deltax);
					vx += -dD * PApplet.cos(theta) * M / (m + M);
					vy += -dD * PApplet.sin(theta) * M / (m + M);
					vx *= BBP2.b;
					vy *= BBP2.b;
				}
			}
		}
	}

	void move() {
		if (b && parent.mousePressed && BBP2.count>1) {
			x = parent.mouseX;
			y = parent.mouseY;
			vx = 0;
			vy = 0;
			BBP2.arrastrando = id;
			s=!s;
			BBP2.count=0;
		} else {
			x += vx;
			y += vy;
		}
	}

	void checkBrushing() {
		if (PApplet.dist(x, y, parent.mouseX, parent.mouseY) < r)
			b = true;
		else
			b = false;

	}

	void display() {
		iR.update();
		float rr = iR.value*0.99f;
		parent.fill(255,255,255,200);
		parent.noStroke();
		
		if (BBP2.show_background){
			if (BBP2.show_rect)
				parent.rect(x-r*0.78f,y-r*0.78f,r*1.56f,r*1.56f);
			else{
				parent.ellipse(x, y, rr*2, rr*2);
			}		
			if (BBP2.show_name){
				float size = 3+r * 0.14f;
				parent.textFont(BBP2.font, size);
				parent.fill(255, 0, 255,200);
				parent.text(varlist[varr[pair][0]], x, y +r*0.73f);
				
				parent.textAlign(PApplet.LEFT);
				parent.translate(x-r*0.65f,y+r/2);
				parent.rotate((float) (-PApplet.PI/2.));
				parent.text(varlist[varr[pair][1]],0,0);
				parent.rotate((float) (PApplet.PI/2.));
				parent.translate(-(x-r*0.65f),-(y+r/2));
				
				parent.textAlign(PApplet.CENTER);
				parent.textFont(BBP2.font, size);
				parent.fill(120, 0, 120);
				int year = month/12 + 1990;
				int m = month%12;
				parent.text(Main.months[m]+" "+year,x, y -r*0.8f);
		}	
			parent.noStroke();
			parent.fill(0,0,0,200);
			for (int s = 0; s < numS; s++) {
				float x3 = x - rr*0.65f + rr*1.3f*data[month][vX][s];
				float y3 = y - rr*0.65f + rr*1.3f*(1-data[month][vY][s]);
				parent.ellipse(x3, y3, 2+rr/12, 2+rr/12);
			}
		}
		
		drawPie();
		parent.textAlign(PApplet.CENTER);
		if (BBP2.show_info) {
			float size = r * 0.3f;
			if (pair!=bP12 || month!=bM12){
				DecimalFormat df = new DecimalFormat("#.##");
				parent.textFont(BBP2.font, size);
				parent.fill(255, 255, 0,200);
				parent.text(df.format((25-ocurrences)/100f), x, y-r*0.74f);
				
			}
		}
		
			
	}
	public static Ball2[] append(Ball2 t[], float ka, int id, int p, int m,
			PApplet parent) {
		Ball2 temp[] = new Ball2[t.length + 1];
		System.arraycopy(t, 0, temp, 0, t.length);
		temp[t.length] = new Ball2(t.length, ka, id, p,m, parent);
		return temp;
	}
	
	public void drawPie() {
		parent.textAlign(PApplet.LEFT);
		float lastAng = -PApplet.PI/2;
		parent.strokeWeight(1);
		parent.textFont(BBP2.font, 10+r/20);
		for (int i = 0; i < 9; i++){
			float v = 0;
			if (BBP2.show_scag)
				v = scagVals[month][i][pair]*r*2;
			iP[i].target(v);
			iP[i].update();
			
			Color color =scagColors[i];
			
			parent.fill(color.getRGB());
			parent.arc(x,y, iP[i].value, iP[i].value, lastAng, lastAng+PApplet.PI*2/9);
			if (BBP2.show_scag && BBP2.show_info && pair==bP12 && month==bM12)
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
