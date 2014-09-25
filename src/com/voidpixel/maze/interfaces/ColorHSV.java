package com.voidpixel.maze.interfaces;
import java.awt.Color;

public class ColorHSV {
	//The hue, saturation, value and alpha
	protected double h,s,v,a;
	
	//A constructor
	public ColorHSV(double h, double s, double v) {
		this.h = h;
		this.s = s; 
		this.v = v;
		this.a = 1;
		
		ClampValues();
	}

	//A constructor
	public ColorHSV(double h, double s, double v, double a) {
		this.h = h;
		this.s = s; 
		this.v = v;
		this.a = a;
		
		ClampValues();
	}

	//Creates a color given doubles for the values instead of int's
	protected Color CreateColor(double r, double g, double b, double a) {
		return new Color((int)(255 * Clamp(r,0,1)), (int)(255 * Clamp(g,0,1)), (int)(255 * Clamp(b,0,1)), (int)(255 * Clamp(a,0,1)));
	}
	
	//Returns the hue
	public double GetHue() {
		return h;
	}
	
	//Returns the saturation
	public double GetSaturation() {
		return s;
	}
	
	//Returns the value
	public double GetValue() {
		return v;
	}
	
	//Returns the alpha
	public double GetAlpha() {
		return a;
	}
	
	//Converts it into a color. Got information from website. TODO: Get Reference Link
	public Color GetColor() {
		int i;
		double f, p, q, t;
		double r,g,b;
		if( s == 0 ) {
			// achromatic (grey)
			r = g = b = v;
			return CreateColor(r,g,b,a);
		}
		h /= 60;			// sector 0 to 5
		i = (int) Math.floor( h );
		f = h - i;			// factorial part of h
		p = v * ( 1 - s );
		q = v * ( 1 - s * f );
		t = v * ( 1 - s * ( 1 - f ) );
		switch( i ) {
			case 0:
				r = v;
				g = t;
				b = p;
				break;
			case 1:
				r = q;
				g = v;
				b = p;
				break;
			case 2:
				r = p;
				g = v;
				b = t;
				break;
			case 3:
				r = p;
				g = q;
				b = v;
				break;
			case 4:
				r = t;
				g = p;
				b = v;
				break;
			default:		// case 5:
				r = v;
				g = p;
				b = q;
				break;
		}
		
		return CreateColor(r,g,b,a);
	}
	
	//Clamp the HSVA values betweeen 0 and 360 for the hue, and 0 to 1 for the rest
	protected void ClampValues() {
		this.h = Clamp(h, 0, 360);
		this.s = Clamp(s, 0, 1);
		this.v = Clamp(v, 0, 1);
		this.a = Clamp(v, 0, 1);
	}
	
	//Clamps the given value between max and min.	
	protected double Clamp(double value, double min, double max) {
		return value > max ? max : (value < min ? min : value);
	}
}
