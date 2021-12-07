/*
author: Bansharee Ireen
date: 05.02.2021
purpose: altered scaffold for PS2.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// lower-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// x, y coordinates of the point to insert
		double px = p2.getX();
		double py = p2.getY();

		// x, y coordinates of the current point
		int x = (int) point.getX();
		int y = (int) point.getY();

		if (x <= px && px <= x2 && y1 <= py && py <= y) {				// if point in 1st quadrant
			if (hasChild(1)) c1.insert(p2);						// and has child, then inset in child
			else c1 = new PointQuadtree<E>(p2, x, y1, x2, y);			// if not, insert new tree

		} else if (x1 <= px && px <= x && y1 <= py && py <= y) {		// if point in 2nd quadrant
			if (hasChild(2)) c2.insert(p2);
			else c2 = new PointQuadtree<E>(p2, x1, y1, x, y);

		} else if (x1 <= px && px <= x && y <= py && py <= y2) {		// if point in 3rd quadrant
			if (hasChild(3)) c3.insert(p2);
			else c3 = new PointQuadtree<E>(p2, x1, y, x, y2);

		} else if (x <= px && px <= x2 && y <= py && py <= y2) {		// if point in 4th quadrant
			if (hasChild(4)) c4.insert(p2);
			else c4 = new PointQuadtree<E>(p2, x, y, x2, y2);
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		int num = 1;

		// increasing the num recursively
		if (hasChild(1)) num += c1.size();
		if (hasChild(2)) num += c2.size();
		if (hasChild(3)) num += c3.size();
		if (hasChild(4)) num += c4.size();

		return num;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE -- efficiency matters!
		ArrayList<E> pointsList = new ArrayList<E>();	// creating list to store all points
		addToAllPoints(pointsList);				// using a helper function to add points to list efficiently
		return pointsList;
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE -- efficiency matters!
		ArrayList<E> circleList = new ArrayList<E>();	// creating list to store points within circle
		findPoints(circleList, cx, cy, cr);		// using a helper function to add points to list efficiently
		return circleList;
	}

	// TODO: YOUR CODE HERE for any helper methods
	/**
	 * helper method for findInCircle() that adds points that hit the circle to the given list
	 * @param circleList	the list to use in findInCircle
	 * @param cx			circle center x
	 * @param cy  			circle center y
	 * @param cr  			circle radius
	 */
	public void findPoints(List<E> circleList, double cx, double cy, double cr) {
		// if circle intersects rectangle
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {

			// if a point is found within circle, add it to the list
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) circleList.add(point);

			// recurse with all children
			for (int quadrant = 1; quadrant <= 4; quadrant++) {
				if (hasChild(quadrant)) getChild(quadrant).findPoints(circleList, cx, cy, cr);
			}
		}
	}

	/**
	 * helper method for allPoints() that adds all the points in the tree to the given pointList
	 * @param pointList		given list of points
	 */
	public void addToAllPoints(List<E> pointList) {
		// base case: adding the point with no children
		if (!hasChild(1) && !hasChild(2) && !hasChild(3) && !hasChild(4)) {
			pointList.add(point);
		}
		else {
			// finding the children to recursively add to the list
			if (hasChild(1)) c1.addToAllPoints(pointList);
			if (hasChild(2)) c2.addToAllPoints(pointList);
			if (hasChild(3)) c3.addToAllPoints(pointList);
			if (hasChild(4)) c4.addToAllPoints(pointList);

			pointList.add(point);
		}
	}

	/** toString method to check if the child-parent relationships are set up correctly
	 *  the first line (before '||') states the coordinates of the root of the tree that calls this method
	 * @return		a string in the format: point1 has a child cq, at point2; separated by '||'
	 */
	public String toString() {
		String statement = "point ("+point.getX()+", "+point.getY()+") || ";

		if (hasChild(1)) {
			statement += "point ("+point.getX()+", "+point.getY()+") has child c1, ";
			statement += c1.toString();
		}
		if (hasChild(2)) {
			statement += "point ("+point.getX()+", "+point.getY()+") has child c2, ";
			statement += c2.toString();
		}
		if (hasChild(3)) {
			statement += "point ("+point.getX()+", "+point.getY()+") has child c3, ";
			statement += c3.toString();
		}
		if (hasChild(4)) {
			statement += "point ("+point.getX()+", "+point.getY()+") has child c4, ";
			statement += c4.toString();
		}
		return statement;
	}
}
