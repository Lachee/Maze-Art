package com.voidpixel.maze.pathfind;

import java.awt.Point;
import java.util.ArrayList;

public class PathFinder {
	public static boolean log = false;
	
	PathNode[][] nodes;
	
	Point currentNode;
	Point targetNode;
	Point startNode;
	
	ArrayList<Point> openNodes = new ArrayList<Point>();
	ArrayList<Point> closeNodes = new ArrayList<Point>();
		
	int movementCost = 10;
	
	boolean reachEndPt = false;
	
	public PathFinder(int[][] map, int[] blockedID, Point startNode, Point endNode) {
		this.startNode = startNode;
		this.currentNode = startNode;
		this.targetNode = endNode;
		this.setNodes(map, blockedID);
	}
	
	public void setNodes(int[][] map, int[] blockedID) {
		nodes = new PathNode[map.length][map[0].length];
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[0].length; y++) {
				boolean par1 = false;
				for(int i = 0; i < blockedID.length; i++) {
					if(par1) continue;
					if(map[x][y] == blockedID[i]) {
						nodes[x][y] = new PathNode(true,new Point(x,y),targetNode);
						par1 = true;
					}
				}
				
				if(!par1)
					nodes[x][y] = new PathNode(false, new Point(x,y),targetNode);
			}
		}
	}

	public Point[] pathfindNodes() {
		findPath();
		return tracePath();
	}
	
	public Point[] tracePath() {
		if(log) System.out.println("[-] Tracing Path...");
		ArrayList<Point> path = new ArrayList<Point>();
		Point searchPoint = targetNode;
		
		boolean reachStartPt = false;
		
		while(!reachStartPt) {
			path.add(searchPoint);
			searchPoint = getNode(searchPoint).parentPoint;
			
			if(searchPoint == null){
				if(log) System.out.println("[!] Trace Pathing could not find path");
				return new Point[0];
			}
			
			reachStartPt = searchPoint.equals(startNode);			
		}
		
		Point[] points = new Point[path.size()];		
		for(int i = 0; i < path.size(); i++) 
			points[i] = path.get(i);
		

		if(log) System.out.println("[-] Traced Path");
		return points;
	}
	
	
	
	///////////////////////////// INTERNAL
	void findPath() {
		if(log) System.out.println("[-] Finding Path...");
		reachEndPt = false;
		closeNodes.clear(); openNodes.clear();
		
		while(!reachEndPt) {
			if(currentNode == null) {
				if(log) System.out.println("[!] Pathing Finding could not find a path to target");
				reachEndPt = true;
				return;
			}
			
			//Check UP Node
			if(!reachEndPt && validateNode(currentNode.x, currentNode.y+1))
				reachEndPt = testNode(currentNode.x,currentNode.y+1) == 0;
			
			//Check DOWN Node
			if(!reachEndPt && validateNode(currentNode.x, currentNode.y-1))
				reachEndPt = testNode(currentNode.x,currentNode.y-1) == 0;
			
			//Check LEFT Node
			if(!reachEndPt && validateNode(currentNode.x-1, currentNode.y))
				reachEndPt = testNode(currentNode.x-1,currentNode.y) == 0;
			
			//Check RIGHT Node
			if(!reachEndPt && validateNode(currentNode.x+1, currentNode.y))
				reachEndPt = testNode(currentNode.x+1,currentNode.y) == 0;
			
			if(!reachEndPt) {
				closeNodes.add(currentNode);
				openNodes.remove(currentNode);
				currentNode = calulcateSmallestNode();
			}
		}

		if(log) System.out.println("[-] Found Path");
	}
	
	/// Flags 0 - Hit Target, 1 - Finished Method, 2 - is blocked or null, 3 - already checked
	byte testNode(int x, int y) { return testNode(new Point(x,y)); } 
	byte testNode(Point p) {
		if(targetNode.equals(p)) {
			getNode(p).parentPoint = currentNode;
			return 0;
		}
		
		if(closeNodes.contains(p))
			return 3;		
		
		if(nodes[p.x][p.y].blocked || nodes[p.x][p.y] == null )
			return 2;

		if(openNodes.contains(p)) {
			int newCost = getNode(currentNode).g_movementCost + movementCost;
			if(newCost < getNode(p).g_movementCost) {
				getNode(p).parentPoint = currentNode;
				getNode(p).g_movementCost = newCost;
				getNode(p).calculateCosts();
			}
		}else{
			getNode(p).parentPoint = currentNode;
			getNode(p).g_movementCost = getNode(currentNode).g_movementCost + movementCost;
			getNode(p).calculateCosts();
			
			openNodes.add(p);
		}
	
		return 1;
	}
	
	///// GETS
	Point calulcateSmallestNode() {
		Point smallestPoint = new Point();
		int smallestF = -1;
		
		for(Point point : openNodes) {
			if(smallestF < 0 || getNode(point).calculateCosts() < smallestF) {
				smallestPoint = point;
				smallestF = getNode(point).f_totalCost;
			}
		}
		
		if(smallestF == -1)
			return null;
		
		return smallestPoint;
	}
	
	PathNode getNode(Point point) { return getNode(point.x,point.y); } 
	PathNode getNode(int x, int y) { return nodes[x][y]; }
	
	boolean validateNode(int x, int y) {
		if(x >= nodes.length || y >= nodes[0].length || x < 0 || y < 0)
			return false;
		
		return true;
	}
	
}
