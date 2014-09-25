package com.voidpixel.maze.main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Canvas extends JComponent{
	private static final long serialVersionUID = 1L;

	public Program program;
	public Image screen;
	
	public Canvas(Program program){
		this.program = program;
		
		createScreen();
	}
	
	public void setSize(int width, int height) {
		program.setSize(width, height);
		
	}
	
	public void saveScreen() {

	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-HH-mm-ss-SS");
	    Date now = new Date(System.currentTimeMillis());
		String path = "Maze-Generation-" + sdf.format(now) + ".png";
		
	    
		saveScreen("bin/gens/" + path);
	}
	
	public void saveScreen(String path) {
		this.setName("Lachee's Tile Dungeon Algorithm - saving image...");
		try {
			long startTime = System.nanoTime();
			System.out.println("Drawing Image before save...");
			
			BufferedImage image = new BufferedImage(screen.getWidth(null), screen.getHeight(null), BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(screen, 0, 0, screen.getWidth(null), screen.getHeight(null), null);
			
			File file =  new File(path);
			file.mkdirs();
			
			System.out.println("Attempting to save \"" + file.getAbsolutePath() +"\"");
			
			ImageIO.write((RenderedImage)image, "png", file);
			
			System.out.println("File \"" + file.getAbsolutePath() + "\" saved");
			System.out.println("Done in " + ((double)(System.nanoTime() - startTime)/1e9) + "s");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setName("Lachee's Tile Dungeon Algorithm");
	}
	
	public void createScreen() {
		screen = this.createVolatileImage(getWidth(), getHeight());
	}
	
	public int getScreenWidth() {
		if(screen == null) return getWidth();
		return screen.getWidth(null);
	}
	
	public int getScreenHeight() { 
		if(screen == null) return getWidth();
		return screen.getHeight(null);
	}

	public void paintComponent(Graphics g) {
		createScreen();
	}
	
	public void render() {
		if(screen == null) return;
		Graphics g = screen.getGraphics();
		g.clearRect(0, 0, getWidth(), getHeight());
		program.game.render(g);
		g.dispose();
		
		g = this.getGraphics();
		//g.setXORMode(Color.black);
		g.drawImage(screen, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
	}
	

	public static void drawCircle(Graphics g, int x, int y, int radius) {
		g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
	}
	
	public static void fillCircle(Graphics g, int x, int y, int radius) {
		g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
	}
	
	public static void drawChar(Graphics g, char ch, int x, int y) {
		int width = g.getFontMetrics().charWidth(ch);
		int height = g.getFontMetrics().getHeight();
		g.drawString(ch + "", x - (width / 2), y + (height/4));
	}
	
}
