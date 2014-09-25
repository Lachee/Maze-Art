package com.voidpixel.maze.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.voidpixel.maze.interfaces.*;
import com.voidpixel.maze.main.Canvas;
import com.voidpixel.maze.main.Program;

public class MainGame{
	
	//Make this a singleton
	public static MainGame instance;
	
	public Program program;
	public Canvas canvas;
	
	//How long (in seconds) a tick should be.
	public double tickRate = 0;
	protected long tickStart = 0;
	
	protected int frameCount = 0;
	public boolean secondFlash = false;
	public boolean drawGrid = false;
	
	public MainGame(Program program, Canvas canvas) {
		MainGame.instance = this;
		
		this.program = program;
		this.canvas = canvas;
	}
	
	public void update(double delta) {
		frameCount++;
		if(frameCount >= program.framerate) {
			secondFlash = !secondFlash;
			frameCount = 0;
		}
		
	}
	
	
	public void render(Graphics g) {		
	}
	
	public void keyReleased(KeyEvent e) {
	}
	
	public void keyMoved(KeyEvent e) {
	}
	
	public void keyClaim(KeyEvent e) {
	}
	
}
