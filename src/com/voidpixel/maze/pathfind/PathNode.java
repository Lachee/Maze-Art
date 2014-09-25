package com.voidpixel.maze.pathfind;

import java.awt.Point;

public class PathNode {
	public Point point;
	public Point parentPoint;
	
	public int h_heuristicValue = 0;
	public int g_movementCost = 0;
	public int f_totalCost = 0;
	
	public boolean blocked = false;	
	
	public PathNode(boolean isBlocked, Point p) {
		blocked = isBlocked;
		point = p;
	}
	
	public PathNode(boolean isBlocked, Point p, Point target) {
		blocked = isBlocked;
		point = p;
		
		this.calculateHeuristic(p.x, p.y, target.x,target.y);
		this.calculateCosts();
	}
	
	public int calculateHeuristic(int x, int y, int targetX, int targetY) {
		int xTotal = (x > targetX ? x-targetX : targetX-x);
		int yTotal = (y > targetY ? y-targetY : targetY-y);
		h_heuristicValue = xTotal + yTotal;
		return h_heuristicValue;
	}
	
	public int calculateCosts() {
		f_totalCost = h_heuristicValue + g_movementCost;
		return f_totalCost;
	}
}
