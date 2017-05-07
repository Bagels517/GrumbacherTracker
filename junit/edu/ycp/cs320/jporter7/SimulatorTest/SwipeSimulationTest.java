package edu.ycp.cs320.jporter7.SimulatorTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JList.DropLocation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.Test;
import org.junit.Before;

import edu.ycp.cs320.jporter7.model.*;
import edu.ycp.cs320.jporter7.populationdb.persist.*;
import edu.ycp.cs320.jporter7.controller.*;
import edu.ycp.cs320.jporter7.simulator.*;



public class SwipeSimulationTest 
{
	private IDatabase db = null;
	private IDatabase swipeDb = null;
	private User user = new User();
	//set up tuples
	
	ArrayList<User> activeUsers = null;
	ArrayList<User> UserList = null;
	ArrayList<User> SwipeList = null;
	private SwipeSimulator swiper = new SwipeSimulator(user, db);
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// creating DB instance here
		DatabaseProvider.setInstance(new DerbyDatabase());
		db = DatabaseProvider.getInstance();		
		swipeDb = DatabaseProvider.getInstance();
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testRetrieveAllUser() {
		UserList = db.getAllUsers();
		assertEquals("400", Integer.toString(UserList.size()));
//		user = db.retrieveUser("test");
//		assertEquals("test", user.getID());
//		user = db.retrieveUser("blonge");
//		assertNotEquals("false", user.getID());
	}
	
	@Test
	public void testGetActiveUsers() {
		
		activeUsers = db.getActiveUsers();
		assertEquals("11", Integer.toString(activeUsers.size()));
	}
	
	@Test
	public void TestGetActiveInRoom() {
		activeUsers = db.getActiveUsersInRoom("4");
		assertEquals("3", Integer.toString(activeUsers.size()));
	}
	
	@Test
	public void testSwipeAction() {
		
		SwipeList = swipeDb.getActiveUsers();
		assertEquals("10", Integer.toString(SwipeList.size()));
		
		int counter =22;
		swiper.swipe(user, swipeDb, counter );
		
		SwipeList = swipeDb.getActiveUsers();
		assertEquals("11", Integer.toString(SwipeList.size()));
	}
	

	
	
	
	


	
	


}
