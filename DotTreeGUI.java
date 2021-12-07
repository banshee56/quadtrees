/*
author: Bansharee Ireen
date: 05.02.2021
purpose: completing scaffold so that it can draw quadtrees for PS2.
 */

import java.awt.*;
import java.util.List;

import javax.swing.*;

/**
 * Driver for interacting with a quadtree:
 * inserting points, viewing the tree, and finding points near a mouse press
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for dots
 * @author CBK, Fall 2016, generics, dots, extended testing
 */
public class DotTreeGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe
	private static final int dotRadius = 5;				// to draw dot, so it's visible
	private static final Color[] rainbow = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA};
			// to color different levels differently

	private PointQuadtree<Dot> tree = null;			// holds the dots
	private char mode = 'a';						// 'a': adding; 'q': querying with the mouse
	private int mouseX, mouseY;						// current mouse location, when querying
	private int mouseRadius = 10;					// circle around mouse location, for querying
	private boolean trackMouse = false;				// if true, then print out where the mouse is as it moves
	private List<Dot> found = null;					// who was found near mouse, when querying
	
	public DotTreeGUI() {
		super("dot tree", width, height);
	}

	/**
	 * DrawingGUI method, here keeping track of the location and redrawing to show it
	 */
	@Override
	public void handleMouseMotion(int x, int y) {
		if (mode == 'q') {
			mouseX = x; mouseY = y;
			repaint();
		}
		if (trackMouse) {
			System.out.println("@ ("+x+","+y+")");
		}
	}

	/**
	 * DrawingGUI method, here either adding a new point or querying near the mouse
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (mode == 'a') {		// if in adding mode
			// Add a new dot at the point

			// if tree is null, we create a new tree at the clicked point
			if (tree == null) tree = new PointQuadtree<Dot>(new Dot(x, y), 0, 0, width, height);
			else tree.insert(new Dot(x, y));		// inserting a new dot to the tree
		}

		else if (mode == 'q' && tree != null) {		// if in querying mode
			// Set "found" to what tree says is near the mouse press
			found = tree.findInCircle(x, y, mouseRadius+dotRadius);	// detecting any blobs within the mouse circle
		}

		else {
			System.out.println("clicked at ("+x+","+y+")");
		}
		repaint();
	}

	/**
	 * DrawingGUI method, here toggling the mode between 'a' and 'q'
	 * and increasing/decreasing mouseRadius via +/-
	 */
	@Override
	public void handleKeyPress(char key) {
		if (key=='a' || key=='q') mode = key;
		else if (key=='+') {
			mouseRadius += 10;
		}
		else if (key=='-') {
			mouseRadius -= 10;
			if (mouseRadius < 0) mouseRadius=0;
		}
		else if (key=='m') {
			trackMouse = !trackMouse;
		}

		repaint();
	}
	
	/**
	 * DrawingGUI method, here drawing the quadtree
	 * and if in query mode, the mouse location and any found dots
	 */
	@Override
	public void draw(Graphics g) {
		if (tree != null) drawTree(g, tree, 0);
		if (mode == 'q') {
			g.setColor(Color.BLACK);
			g.drawOval(mouseX-mouseRadius, mouseY-mouseRadius, 2*mouseRadius, 2*mouseRadius);			
			if (found != null) {
				g.setColor(Color.BLACK);
				for (Dot d : found) {
					g.fillOval((int)d.getX()-dotRadius, (int)d.getY()-dotRadius, 2*dotRadius, 2*dotRadius);
				}
			}
		}
	}

	/**
	 * Draws the dot tree
	 * @param g		the graphics object for drawing
	 * @param tree	a dot tree (not necessarily root)
	 * @param level	how far down from the root qt is (0 for root, 1 for its children, etc.)
	 */
	public void drawTree(Graphics g, PointQuadtree<Dot> tree, int level) {
		// Set the color for this level
		g.setColor(rainbow[level % rainbow.length]);
		// Draw this node's dot and lines through it
		Dot dot = tree.getPoint();		// placing our current dot in a variable for easy access

		// drawing the circular dots while accounting for its radius
		g.fillOval((int) dot.getX()-dotRadius, (int) dot.getY()-dotRadius, 2*dotRadius, 2*dotRadius);

		g.drawLine(tree.getX1(), (int) dot.getY(), tree.getX2(), (int) dot.getY());		// draws horizontal line
		g.drawLine((int) dot.getX(), tree.getY1(), (int) dot.getX(), tree.getY2());		// draws vertical line

		// Recurse with children
		level += 1;		// increases level by 1 before drawing children
		if (tree.hasChild(1)) drawTree(g, tree.getChild(1), level);
		if (tree.hasChild(2)) drawTree(g, tree.getChild(2), level);
		if (tree.hasChild(3)) drawTree(g, tree.getChild(3), level);
		if (tree.hasChild(4)) drawTree(g, tree.getChild(4), level);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DotTreeGUI();
			}
		});
	}
}
