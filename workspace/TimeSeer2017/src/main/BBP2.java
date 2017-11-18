package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.*;
import static main.Main.*;

public class BBP2{
	public static int nro_linea = 0;
	public static int nro_espacio = 0;

	public static PFont font;
	public static String par_actual = ""; // valor actual del escaneo del texto
	public static int arrastrando = -1;

	public static int total_pairs = 0;
	public float k_total;

	public static int down;
	public static int top;
	public static int right;
	public static int left;
	public static int maxBalls = 100;
	public static int bubble_plots = 0;

	public static Ball2[] balls = new Ball2[0];
	public static float b = 0.85f; // Rebote
	public static float f = 0.10f; // Friccion
	
	public static boolean show_name = false;
	public static boolean show_rect = false;
	public static boolean show_background = true;
	public static boolean show_info = false;
	public static boolean show_scag = false;
	public static boolean no_gravity = false;
	public PApplet parent;
	public static int count=0;
	
	
	public BBP2(PApplet p, int l, int r, int t, int d) {
		parent =p;
		calcularKtotal();
		font = parent.loadFont("Arial-BoldMT-18.vlw");
		down = d;
		top = t;
		right = r;
		left = l;
		balls = new Ball2[0];
	}
	public void createBalls() {
		int k=0;
		for (int p =0; p<numP;p++){
			for (int m=1;m<nM;m++){
				newKP(k,p,m);
				k++;
			}
		}
	}
	public void draw() {
		if (parent.keyPressed)
			keyPressed() ;
		for (int i = 0; i < balls.length; i++) {
			balls[i].x += parent.random(-1, 1);
			balls[i].y += parent.random(-1, 1);
		}
		
		if (Main.change){
			total_pairs = 0;
			bubble_plots = 0;
			for (int i = 0; i < balls.length; i++) {
				if (balls[i].active) {
					total_pairs += balls[i].ocurrences;
					bubble_plots++;
				}
			}
			calcularKtotal();
			for (int i = 0; i < balls.length; i++) {
				if (balls[i].active) {
					float kprima = (k_total * balls[i].ocurrences) / total_pairs;
					balls[i].ka = kprima;
					balls[i].r = PApplet.sqrt(((kprima) / PApplet.PI));
					balls[i].iR.target(balls[i].r);					
				}
			}
			Main.change =false;
		}
		for (int i = 0; i < balls.length; i++) {
			if (balls[i].active) {
				balls[i].bounce();
				balls[i].collide();
				balls[i].move();
				balls[i].checkBrushing();
				balls[i].display();
			}	
		}
		count++;
		if (count>1000)
			count=1000;
	}

	void newKP(int id, int p, int m) {
		calcularKtotal();
		float ka;
		if (balls.length > 0)
			ka = k_total / balls.length;
		else
			ka = k_total;
		Ball2[] tempBall = Ball2.append(balls, ka, id, p,m, parent);
		balls = tempBall;
		balls[0].s =true;
	}

	void ordenarArrays() {
		Ball2[] temp_ocurrencias = new Ball2[balls.length];
		System.arraycopy(balls, 0, temp_ocurrencias, 0, balls.length);
		Ball2 temp;
		int i, j;
		for (i = temp_ocurrencias.length - 1; i >= 0; i--)
			for (j = 0; j < i; j++)
				if (temp_ocurrencias[j].ocurrences < temp_ocurrencias[j + 1].ocurrences) {
					temp = temp_ocurrencias[j];
					temp_ocurrencias[j] = temp_ocurrencias[j + 1];
					temp_ocurrencias[j + 1] = temp;
				}
		balls = temp_ocurrencias;
	}

	void calcularKtotal() {
		float high = parent.height - top - down;
		float width = parent.width - left - right;
		if (bubble_plots <= 1) {
			if (high < width)
				k_total = PApplet.PI * PApplet.pow(high / 2, 2f) * 0.8f;
			else
				k_total = PApplet.PI * PApplet.pow(width / 2f, 2f) * 0.8f;
		} else if (bubble_plots > 1 && bubble_plots <= 6)
			k_total = width * high * 0.65f;
		else if (bubble_plots > 6 && bubble_plots <= 20)
			k_total = width * high * 0.75f;
		else if (bubble_plots > 20 && bubble_plots <= 50)
			k_total = width * high * 0.80f;
		else if (bubble_plots > 50 && bubble_plots <= 200)
			k_total = width * high * 0.86f;
		else if (bubble_plots > 200)
			k_total = width * high * 0.92f;

	}

	public void keyPressed() {
		if (parent.key == '+') { // viendo mas burbujas
			maxBalls++;
		}
		if (parent.key == '-') { // viendo menos burbujas
			if (maxBalls > 2)
				maxBalls--;
		}
		if (parent.key == 'i' || parent.key == 'I') { // mostrar info en burbujas
				show_info = !show_info;
		}
		if (parent.key == 'n' || parent.key == 'N') { // mostrar info en burbujas
				show_name = !show_name;
		}
		if (parent.key == 's') { // shaking
			for (int i = 0; i < balls.length; i++) {
				balls[i].x += parent.random(-10, 10);
				balls[i].y += parent.random(-10, 10);
			}
		}
		if (parent.key == 'r') { // shaking
			show_rect = !show_rect;
		}
		if (parent.key == 'o') { // shaking
			show_scag = !show_scag;
		}
		if (parent.key == 'u') { // shaking
			show_background = !show_background;
		}
		if (parent.key == 'd' || parent.key == 'D') { // redistribuyendo
			for (int i = 0; i < balls.length; i++) {
				balls[i].x = parent.random(balls[i].r + left, parent.width - right
						- balls[i].r);
				balls[i].y = parent.random(balls[i].r + top, parent.height - down
						- balls[i].r);
			}
		}
	}

}
