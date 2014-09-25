package com.voidpixel.maze.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.voidpixel.maze.generation.MazeGenerator;
import com.voidpixel.maze.interfaces.ColorHSV;
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
	
	public boolean restartMine = false;
	public boolean instantMine = false;
	
	public int width = 101;
	public int height = 101;
	
	public int lineAlpha = 255;
	
	MazeGenerator maze;
	public int[][] map;
	
	//All points dug by miners
	ArrayList<ColorPoint> pointsDug; 
	
	//The miners digging the maze
	ArrayList<Miner> miners;
	
	//The amount of empty blocks
	public int emptyBlocks;
	public int cbc = 0;
	
	//The maze's color
	public Color mazeColor = new Color(210, 210, 50);
	
	public MainGame(Program program, Canvas canvas) {
		MainGame.instance = this;
		
		this.program = program;
		this.canvas = canvas;
		
		createMap(100, 100);
	}
	
	public void createMap(int width, int height) {
	
		lineAlpha = 255;
		mazeColor = new ColorHSV(Math.random() * 360, 1.0, 1.0).GetColor();
		
		this.width = width;
		this.height = height;
		
		maze = new MazeGenerator((this.width) / 2, (this.height) / 2);
		
		maze.generate();
		maze.placeStartAndEnd();
		map = maze.getMap();
		
		canvas.setSize(width / 2 * 10, height / 2 * 10);
		
		pointsDug = new ArrayList<ColorPoint>();
		miners = new ArrayList<Miner>();
		
		int i = 0;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(emptySpace(x,y)) i++;
			}
		}
		
		this.emptyBlocks = i + 1;
		cbc = 0;
		
		int sx = maze.getStart().x;
		int sy = maze.getStart().y;
		
		int dx = 0;
		int dy = 0;
		
		if(emptySpace(sx, sy - 1))
			dy--;
		else if(emptySpace(sx, sy + 1))
			dy++;
		else if(emptySpace(sx - 1, sy))
			dx--;
		else if(emptySpace(sx + 1, sy))
			dx++;
		
		//Jim is a person too!
		Miner jim = new Miner(sx, sy, dx, dy);
		miners.add(jim);
		
		restartMine = false;
	}
	
	public boolean emptySpace(int x, int y) {
		if(!inBounds(x,y)) return false;
		return map[x][y] == 1;
	}
	
	public boolean inBounds(int x, int y) { 
		if(x < 0 || x >= width || y < 0 || y >= height) return false;
		return true;
	}
	
	public void update(double delta) {
		frameCount++;
		if(frameCount >= program.framerate) {
			secondFlash = !secondFlash;
			frameCount = 0;
		}
				
		if(!restartMine) {
			if(instantMine) {
				lineAlpha = 0;
				while(instantMine && miners.size() > 0)
					stepMiners();
			}
			
			if(((double)cbc / (double)emptyBlocks) < 0.75) {
				lineAlpha = 255;
			}else{
				if(lineAlpha != 0)
					lineAlpha--;
			}
			
			stepMiners();
		}else{
			createMap(this.width, this.height);
		}
	}
	
	public void stepMiners() {
		if(miners.size() == 0) return;
		
		ArrayList<Miner> deadJims = new ArrayList<Miner>();
		ArrayList<Miner> newJims = new ArrayList<Miner>();
		
		for(Miner oldBuddyJim : miners) {

			cbc += 2;

			int x = oldBuddyJim.x;
			int y = oldBuddyJim.y;

			int dx = oldBuddyJim.dx;
			int dy = oldBuddyJim.dy;
			
			//Where we are
			pointsDug.add(new ColorPoint(x, y, cbc));
			
			//Where we have been
			//pointsDug.add(new ColorPoint(x - dx, y - dy, cbc));
			
			//Every side!
			//up - down
			pointsDug.add(new ColorPoint(x, y-1, cbc));
			pointsDug.add(new ColorPoint(x, y+1, cbc));
			
			//left - right
			pointsDug.add(new ColorPoint(x - 1, y, cbc));
			pointsDug.add(new ColorPoint(x + 1, y, cbc));
		
			
			if (dx == 0) {
				//Check in x direction
				if(emptySpace(x - 1, y))
					newJims.add(new Miner(x - 2, y, -1, 0));
				
				if(emptySpace(x + 1, y))
					newJims.add(new Miner(x + 2, y, 1, 0));
				
			}else if(dy == 0) {
				//Check in y direciton
				if(emptySpace(x, y - 1))
					newJims.add(new Miner(x, y - 2, 0, -1));
				
				if(emptySpace(x, y + 1))
					newJims.add(new Miner(x, y + 2, 0, 1));				
			}
			
			if(!emptySpace(x + dx, y + dy)) { 
				deadJims.add(oldBuddyJim);
				continue;
			}
			
			oldBuddyJim.x += dx * 2;
			oldBuddyJim.y += dy * 2;
		}
		
		miners.addAll(newJims);
		miners.removeAll(deadJims);
		
		if(miners.size() == 0) {
			instantMine = false;
			System.out.println("Finished Trace. cbc: " + cbc + " emptyBlocks: " + emptyBlocks);
		}
	}
	
	public void render(Graphics g) {	
		
		if(!restartMine) {
			for(ColorPoint cp : pointsDug) {
				double p = (double)(emptyBlocks - cp.colorCount) / (double)emptyBlocks;
	
				
				Color c = new Color((int)(mazeColor.getRed() * p), (int)(mazeColor.getGreen() * p), (int)(mazeColor.getBlue() * p));
				maze.displayPointWithoutWalls(g, 0, 0, 10, new Point(cp.x, cp.y), c);
			}
		}
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Color color = new Color(0, 0, 0, lineAlpha);
				
				maze.displayPoint(g, 0, 0, 10, new Point(x,y), map[x][y] == 0 ? color : new Color(0,0,0,0));
			}
		}
		
		
		/*
		for(Miner jimbo : miners) {
			int x = jimbo.x;
			int y = jimbo.y;
			
			maze.displayPoint(g, 0, 0, 10, new Point(x,y), Color.cyan);
		}
		*/
		
		
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			instantMine = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_R) {
			restartMine = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_S) {
			canvas.saveScreen();
		}
	}
	
	public void keyMoved(KeyEvent e) {
	}
	
	public void keyClaim(KeyEvent e) {
	}
	
}
