package edu.ycp.cs320.jporter7.simulator;

import java.util.ArrayList;
import java.util.Random;

import edu.ycp.cs320.jporter7.model.PopulationCounter;
import edu.ycp.cs320.jporter7.model.User;
import edu.ycp.cs320.jporter7.populationdb.persist.IDatabase;

public class SwipeSimulator 
{
	private User user;
	private IDatabase db;
	
	public SwipeSimulator(User user, IDatabase db)
	{
		this.user = user;
		this.db = db;
	}
	/// no parameter
	public void swipe(User user, IDatabase db, int counter)
	{
		/*
		    User user = new User();
			user.setDbId(14);
			user.setRoom(3);
			db.insertActiveUser(Integer.toString(user.getDbId()), Integer.toString(user.getRoom()));
		 */
		
		//Create random numbers
		
		//variables
		
		User newUser = new User();
		double rand = Math.random() * 115 +1;
		int roomNum = 0; // initialize room to zero then set it inside the logic 
		ArrayList<User> ActiveUserDB = db.getActiveUsers();
		ArrayList<User> allUsers = db.getAllUsers();
		//int dbsize= ActiveUserDB.size();
		//int counter = 22;  // set dbID to the users data base ID
		
		//logic/if-statements and add into database
		
		/*  //////////////Room Assignments////////////////////// 	
						DanceRoom			= 1
						FieldHouse		 	= 2
						Pool 				= 3
						RacquetBall			= 4	
						RockWall			= 5
						WeightRoom 			= 6
						WolfBasketball 		= 7
						Wrestle				= 8
		*/
											//Percent Chance
			// if they are dancing				|||| 5%
		if(rand < 5 && rand >= 0){		
			roomNum = 1;
			
			int roomPop = db.getActiveUsersInRoom(Integer.toString(1)).size();
			System.out.println("This is the Dance rooms population: " + roomPop);
			if(roomPop==30){
				//if room is full set roomNum to 0;
				roomNum = 0;
			}
		}
			// if they are in the field house	|||| 5%
		if(rand < 40 && rand >= 35){
			roomNum = 2;
			
			int roomPop = db.getActiveUsersInRoom(Integer.toString(2)).size();
			System.out.println("This is the Field House rooms population: " + roomPop);
			if(roomPop==200){
				//if room is full set roomNum to 0;
				roomNum = 0;
			}
		}
			// if they are in the pool			|||| 10%		
		if(rand < 50 && rand >= 40){
			roomNum = 3;
			
			int roomPop = db.getActiveUsersInRoom(Integer.toString(3)).size();
			System.out.println("This is the Pool rooms population: " + roomPop);
			if(roomPop==75){
				//if room is full set roomNum to 0;
				roomNum = 0;
			}
		}
			// if they are playing racket ball 	|||| 5%
		if(rand < 25 && rand >= 20){
			roomNum = 4;
			
			int roomPop = db.getActiveUsersInRoom(Integer.toString(4)).size();
			System.out.println("This is the Racket Ball rooms population: " + roomPop);
			if(roomPop==6){
				//if room is full set roomNum to 0;
				roomNum = 0;
			}
			
		}
			// if they are on the rock wall		|||| 10%
		if(rand < 35 && rand >= 25){
			roomNum = 5;
			
			int roomPop = db.getActiveUsersInRoom(Integer.toString(5)).size();
			System.out.println("This is the Rock wall rooms population: " + roomPop);
			if(roomPop==10){
				//if room is full set roomNum to 0;
				roomNum = 0;
			}
			
		}
			// if they are in the weight room	|||| 50%
		if(rand >= 50 && rand <= 100){
			roomNum = 6;
			int roomPop = db.getActiveUsersInRoom(Integer.toString(6)).size();
			System.out.println("This is the Weight rooms population: " + roomPop);
			if(roomPop==75){
				//if room is full set roomNum to 0;
				roomNum = 0;
			}
		}
			// if they are shooting some hoops  |||| 10%
		if(rand < 20 && rand >=10){
			roomNum = 7;
			
			int roomPop = db.getActiveUsersInRoom(Integer.toString(7)).size();
			System.out.println("This is the Wolf Basketball rooms population: " + roomPop);
			if(roomPop==200){
				//if room is full set roomNum to 0;
				roomNum = 0;
			}
		}
			// if they are wrestling			||| 5%
		if(rand < 10 && rand >= 5){
			roomNum = 8;
			
			int roomPop = db.getActiveUsersInRoom(Integer.toString(8)).size();
			System.out.println("This is the wrestling rooms population: " + roomPop);
			if(roomPop==30){
				//if room is full set roomNum to 0;
				roomNum = 0;
			}
		}
		
		// Unswipe 
		if(rand >100 && rand <= 115){
			double rand2 =Math.random() * (counter -22) + 22;
			//int min = 22;
			//int max = counter;
			//Random rand4 = null;
			//int randomNum = rand4.nextInt((max - min) + 1) + min;
			
			int rand3 = (int)rand2;
			db.removeActiveUser(Integer.toString(rand3));
			System.out.println("The following User ID is removed " + counter );

		}else{
		//set dbId and set room
		newUser.setDbId(counter);
		newUser.setRoom(roomNum);
		//db method to insert into the db				
		db.insertActiveUser(Integer.toString(allUsers.get(counter).getDbId()), Integer.toString(newUser.getRoom()));
		
		//testing 
		System.out.println("The following will be set in the DataBase : Db id number is " + counter + " RoomNumber is " +roomNum);
		}
	}	
}
