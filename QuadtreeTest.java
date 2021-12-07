/*
author: Bansharee Ireen
date: 05.02.2021
purpose: altering scaffold to write my own test methods (hitTest() and test2()) for PS2.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Hard-coded tests for point quadtrees, Dartmouth CS 10, Fall 2017
 * 
 * @author Chris Bailey-Kellogg, Fall 2017, extracted from other code, augmented
 * @author CBK, Winter 2021, minor improvements
 *
 */
public class QuadtreeTest {
	/**
	 * Is the tree of the expected size, both from size() and from allPoints()?
	 * @param tree
	 * @param size
	 * @return
	 */
	private static int testSize(PointQuadtree<Dot> tree, int size) {
		int errs = 0;
		
		if (tree.size() != size) {
			errs++;
			System.err.println("wrong size: got "+tree.size()+" but expected "+size);
		}
		List<Dot> points = tree.allPoints();
		if (points.size() != size) {
			errs++;
			System.err.println("wrong points size: got "+points.size()+" but expected "+size);
		}

		return errs;
	}
	
	/**
	 * A simple testing procedure, making sure actual is expected, and printing a message if not
	 * @param x		query x coordinate
	 * @param y		query y coordinate
	 * @param r		query circle radius
	 * @param expectedCircleRectangle	how many times Geometry.circleIntersectsRectangle is expected to be called
	 * @param expectedInCircle			how many times Geometry.pointInCircle is expected to be called
	 * @param expectedHits				how many points are expected to be found
	 * @return  0 if passed; 1 if failed
	 */
	private static int testFind(PointQuadtree<Dot> tree, int x, int y, int r, int expectedCircleRectangle, int expectedInCircle, int expectedHits) {
		Geometry.resetNumInCircleTests();
		Geometry.resetNumCircleRectangleTests();
		int errs = 0;
		int num = tree.findInCircle(x, y, r).size();

		String which = "find near ("+x+","+y+") with radius "+r;
		if (Geometry.getNumCircleRectangleTests() != expectedCircleRectangle) {
			errs++;
			System.err.println(which+": wrong # circle-rectangle, got "+Geometry.getNumCircleRectangleTests()+" but expected "+expectedCircleRectangle);
		}
		if (Geometry.getNumInCircleTests() != expectedInCircle) {
			errs++;
			System.err.println(which+": wrong # in circle, got "+Geometry.getNumInCircleTests()+" but expected "+expectedInCircle);
		}
		if (num != expectedHits) {
			errs++;
			System.err.println(which+": wrong # hits, got "+num+" but expected "+expectedHits);
		}
		return errs;
	}

	/**
	 * test tree 0 -- first three points from figure in handout
	 * hardcoded point locations for 800x600
	 */
	private static void test0() {
		PointQuadtree<Dot> tree = new PointQuadtree<Dot>(new Dot(300,400, "A"), 0,0,800,600); // start with A
		tree.insert(new Dot(150,450, "B"));
		tree.insert(new Dot(250,550, "C"));
		int bad = 0;
		bad += testSize(tree, 3);
		bad += testFind(tree, 0,0,900,3,3,3);		// rect for all; circle for all; find all
		bad += testFind(tree, 300,400,10,3,2,1);		// rect for all; circle for A,B; find A
		bad += testFind(tree, 150,450,10,3,3,1);		// rect for all; circle for all; find B
		bad += testFind(tree, 250,550,10,3,3,1);		// rect for all; circle for all; find C
		bad += testFind(tree, 150,450,150,3,3,2);	// rect for all; circle for all; find B, C
		bad += testFind(tree, 140,440,10,3,2,0);		// rect for all; circle for A,B; find none
		bad += testFind(tree, 750,550,10,2,1,0);		// rect for A,B; circle for A; find none
		if (bad==0) System.out.println("test 0 passed!");
		else System.out.println("test 0 failed!");
	}

	/**
	 * test tree 1 -- figure in handout
	 * hardcoded point locations for 800x600
	 */
	private static void test1() {
		PointQuadtree<Dot> tree = new PointQuadtree<Dot>(new Dot(300,400, "A"), 0,0,800,600); // start with A
		tree.insert(new Dot(150,450, "B"));
		tree.insert(new Dot(250,550, "C"));
		tree.insert(new Dot(450,200, "D"));
		tree.insert(new Dot(200,250, "E"));
		tree.insert(new Dot(350,175, "F"));
		tree.insert(new Dot(500,125, "G"));
		tree.insert(new Dot(475,250, "H"));
		tree.insert(new Dot(525,225, "I"));
		tree.insert(new Dot(490,215, "J"));
		tree.insert(new Dot(700,550, "K"));
		tree.insert(new Dot(310,410, "L"));
		int bad = 0;
		bad += testSize(tree, 12);
		bad += testFind(tree, 150,450,10,6,3,1); 	// rect for A [D] [E] [B [C]] [K]; circle for A, B, C; find B
		bad += testFind(tree, 500,125,10,8,3,1);		// rect for A [D [G F H]] [E] [B] [K]; circle for A, D, G; find G
		bad += testFind(tree, 300,400,15,10,6,2);	// rect for A [D [G F H]] [E] [B [C]] [K [L]]; circle for A,D,E,B,K,L; find A,L
		bad += testFind(tree, 495,225,50,10,6,3);	// rect for A [D [G F H [I [J]]]] [E] [B] [K]; circle for A,D,G,H,I,J; find H,I,J
		bad += testFind(tree, 0,0,900,12,12,12);		// rect for all; circle for all; find all
		if (bad==0) System.out.println("test 1 passed!");
		else System.out.println("test 1 failed!");
	}

	/**
	 * my own test to use instead of testFind(), checks my findInCircle() in PointQuadtree
	 * @param tree
	 * @param qx				query x coordinate
	 * @param qy				query y coordinate
	 * @param qr				query circle radius
	 * @param expectedHitList	list of coordinate of the dots hit by the query circle
	 * @return					true if the above expected list matches the actual hitList
	 */
	public static boolean hitTest(PointQuadtree<Dot> tree, double qx, double qy, double qr, ArrayList<Integer> expectedHitList) {
		List<Dot> find = tree.findInCircle(qx, qy, qr);			// getting list of found Dots
		ArrayList<Integer> hitList = new ArrayList<Integer>();		// list of x,y coordinates of the dots actually hit

		for (Dot d : find) {	// getting x,y values of dots to compare with expected int list passed to method
			int dx = (int) d.getX();
			int dy = (int) d.getY();
			hitList.add(dx);
			hitList.add(dy);
		}

		System.out.println("Hit coordinates: " + hitList);
		return (hitList.equals(expectedHitList));
	}

	/**
	 * runs my created hitTest() above as well as the provided testSize() to test PointQuadtree methods
	 */
	public static void test2() {
		PointQuadtree<Dot> tree = new PointQuadtree<Dot>(new Dot(400,300, "A"), 0,0,800,600);
		// testing with 2 dots in the same spot
		tree.insert(new Dot(0, 0, "B"));
		tree.insert(new Dot(0, 0, "C"));

		// placing dots between A and B/C
		tree.insert(new Dot(200, 150, "D"));
		tree.insert(new Dot(100, 75, "E"));

		// placing dot on the line of A
		tree.insert(new Dot(400, 200, "A.5"));

		int badSize = 0;
		badSize += testSize(tree, 6);	// recognizes B & C as different points in the same spot
		if (badSize == 0) System.out.println("passed size test!");
		else System.out.println("failed size test.");

		// creating point near both B and C to see if both are recognized
		ArrayList<Integer> expectedHitList = new ArrayList<>();
		expectedHitList.add(0);
		expectedHitList.add(0);
		expectedHitList.add(0);
		expectedHitList.add(0);
		if (hitTest(tree, 5, 0, 5, expectedHitList)) System.out.println("1. passed hit test!");
		else System.out.println("1. failed hit test.");

		// creating point to touch D just barely
		ArrayList<Integer> expectedHitList1 = new ArrayList<>();
		expectedHitList1.add(200);
		expectedHitList1.add(150);
		if (hitTest(tree, 300, 150, 100, expectedHitList1)) System.out.println("2. passed hit test!");
		else System.out.println("2. failed hit test.");

		// creating point so that it doesn't hit D by one pixel
		ArrayList<Integer> expectedHitList2 = new ArrayList<>();	// expected to stay empty
		// qr = 99 instead of 100
		if (hitTest(tree, 300, 150, 99, expectedHitList2)) System.out.println("3. passed hit test!");
		else System.out.println("3. failed hit test.");

		System.out.println(tree);		// my toString() that tells me who has what child and where
	}

	public static void main(String[] args) {
		test0();
		test1();

		test2();	// my test
	}
}
