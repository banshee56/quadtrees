/*
author: Bansharee Ireen
date: 05.02.2021
purpose: altering scaffold so that blobs either turn red or are destroyed upon collision for PS2.
 */
import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;							// all the blobs
	private List<Blob> colliders;						// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	private PointQuadtree<Blob> blobTree;				// holds the blobs

	public CollisionGUI() {
		super("super collider", width, height);

		blobs = new ArrayList<Blob>();
		colliders = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds a blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision: "+k);
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// Ask all the blobs to draw themselves.
		for (Blob b : blobs) {
			g.setColor(Color.BLACK); // draw each blob in black
			b.draw(g);
		}

		// Ask the colliders to draw themselves in red.
		if (collisionHandler == 'c') {		// if set to 'c'
			for (Blob c : colliders) {
				g.setColor(Color.RED);		// draw each collider in red
				c.draw(g);
			}
		}
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// Create the tree
		blobTree = new PointQuadtree<>(blobs.get(0), 0, 0, width, height);	// with the 0th blob
		for (Blob b : blobs) {					// inserting all blobs in list to blobTree
			if (b == blobs.get(0)) continue;	// not adding the 0th blob twice
			blobTree.insert(b);
		}

		for (Blob c : blobs) {		// for each blob, finding any other blob that collides with it
			// found is a list of blobs colliding with current blob
			List<Blob> found = blobTree.findInCircle(c.x, c.y, 2*c.r);

			// removing the current blob from its own found list
			if (found.size() == 1) found.remove(c);

			if (colliders == null) colliders = found;	// if colliders = null after 'd'estruction, set it to found
			else colliders.addAll(found);				// otherwise, add to colliders so they stay red after 'c'ollision
		}
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
