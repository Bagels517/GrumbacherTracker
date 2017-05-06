package edu.ycp.cs320.jporter7.populationdb.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.ycp.cs320.jporter7.controller.UserController;
import edu.ycp.cs320.jporter7.model.Reservation;
import edu.ycp.cs320.jporter7.model.User;
import edu.ycp.cs320.jporter7.populationdb.model.Book;
import edu.ycp.cs320.jporter7.populationdb.model.Pair;
import edu.ycp.cs320.jporter7.sqldemo.DBUtil;

public class DerbyDatabase implements IDatabase 
{
	static 
	{
		try 
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} 
		catch (Exception e) 
		{
			throw new IllegalStateException("Could not load Derby driver");
		}
	}
	
	private interface Transaction<ResultType> 
	{
		public ResultType execute(Connection conn) throws SQLException;
	}

	private static final int MAX_ATTEMPTS = 10;

	@Override
	public List<Pair<User, Book>> findUserAndBookByTitle(final String title) 
	{
		return executeTransaction(new Transaction<List<Pair<User,Book>>>() 
		{
			@Override
			public List<Pair<User, Book>> execute(Connection conn) throws SQLException 
			{
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try 
				{
					// retreive all attributes from both Books and Users tables
					stmt = conn.prepareStatement(
							"select users.*, books.* " +
							"  from users, books " +
							" where users.user_id = books.user_id " +
							"   and books.title = ?"
					);
					stmt.setString(1, title);
					
					List<Pair<User, Book>> result = new ArrayList<Pair<User,Book>>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) 
					{
						found = true;
						
						// create new User object
						// retrieve attributes from resultSet starting with index 1
						User user = new User();
						loadUser(user, resultSet, 1);
						
						// create new Book object
						// retrieve attributes from resultSet starting at index 4
						Book book = new Book();
						loadBook(book, resultSet, 4);
						
						result.add(new Pair<User, Book>(user, book));
					}
					
					// check if the title was found
					if (!found) 
					{
						System.out.println("<" + title + "> was not found in the books table");
					}
					
					return result;
				} 
				finally 
				{
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	public<ResultType> ResultType executeTransaction(Transaction<ResultType> txn) 
	{
		try 
		{
			return doExecuteTransaction(txn);
		} 
		catch (SQLException e) 
		{
			throw new PersistenceException("Transaction failed", e);
		}
	}
	
	public<ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException 
	{
		Connection conn = connect();
		
		try 
		{
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;
			
			while (!success && numAttempts < MAX_ATTEMPTS) 
			{
				try 
				{
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} 
				catch (SQLException e) 
				{
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) 
					{
						// Deadlock: retry (unless max retry count has been reached)
						numAttempts++;
					} 
					else 
					{
						// Some other kind of SQLException
						throw e;
					}
				}
			}
			
			if (!success) 
			{
				throw new SQLException("Transaction failed (too many retries)");
			}
			
			// Success!
			return result;
		} 
		finally 
		{
			DBUtil.closeQuietly(conn);
		}
	}

	private Connection connect() throws SQLException 
	{
		Connection conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");
		
		// Set autocommit to false to allow execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);
		
		return conn;
	}
	
	private void loadUser(User user, ResultSet resultSet, int index) throws SQLException 
	{
		user.setDbId(resultSet.getInt(index++));	
		index++;
		user.setPassword(resultSet.getString(index++));
		user.setUserName(resultSet.getString(index++));
		user.setEmail(resultSet.getString(index++));
		user.setfirstName(resultSet.getString(index++));
		user.setlastName(resultSet.getString(index++));
		user.setId(resultSet.getInt(index++));
	}
	
	private void loadActiveUser(User user, ResultSet resultSet, int index) throws SQLException 
	{
		user.setDbId(resultSet.getInt(index++));	
		user.setRoom(resultSet.getInt(index++));
	}
	
	private void loadReservation(Reservation reservation, ResultSet resultSet, int index) throws SQLException 
	{
		reservation.setReservationId(resultSet.getInt(index++));
		reservation.setUserId(resultSet.getInt(index++));
		reservation.setRoomNumber(resultSet.getInt(index++));
		reservation.setDate(resultSet.getString(index++));
		reservation.setStartTime(resultSet.getString(index++));
	}
	
	private void loadBook(Book book, ResultSet resultSet, int index) throws SQLException 
	{
		book.setBookId(resultSet.getInt(index++));
		book.setUserId(resultSet.getInt(index++));
		book.setTitle(resultSet.getString(index++));
		book.setIsbn(resultSet.getString(index++));
		book.setPublished(resultSet.getInt(index++));		
	}
	
	public void createTables() 
	{
		executeTransaction(new Transaction<Boolean>() 
		{
			@Override
			public Boolean execute(Connection conn) throws SQLException 
			{
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				PreparedStatement stmt4 = null;
				
				try 
				{
					stmt1 = conn.prepareStatement(
						"create table users (" +
						"	user_id integer primary key " +
						"		generated always as identity (start with 1, increment by 1), " +
						"	user_id2 integer unique," +
						"	password varchar(20)," +
						"	username varchar(20)," +
						"	email varchar(20)," +
						"	firstname varchar(15)," +
						"	lastname varchar(15)," +
						"	id_number integer " +
						")"
					);	
					stmt1.executeUpdate();
					
					stmt2 = conn.prepareStatement(
							"create table active (" +
							"	active_id integer primary key " +
							"		generated always as identity (start with 1, increment by 1), " +
							"	user_id integer constraint user_id references users, " +
							"	room integer " +
							")"
					);
					stmt2.executeUpdate();
					
					stmt3 = conn.prepareStatement(
							"create table reservations (" +
							"	reservation_id integer primary key " +
							"		generated always as identity (start with 1, increment by 1), " +
							"	user_id2 integer constraint user_id2 references users, " +
							"	room integer, " +
							"	reservation_date varchar(10), " +
							"	start_time varchar(10) " +
							")"
					);
					stmt3.executeUpdate();
					
					/*stmt4 = conn.prepareStatement(
							"create table userReservations (" +
							"   reservation_id integer constraint reservation_id references reservations, " +
							"   user_id2 integer constraint user_id2 references users " +
							")"
					);
					stmt4.executeUpdate();*/
					
					return true;
				} 
				finally 
				{
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
					DBUtil.closeQuietly(stmt3);
					DBUtil.closeQuietly(stmt4);
				}
			}
		});
	}
	
	public void loadInitialData() 
	{
		executeTransaction(new Transaction<Boolean>() 
		{
			@Override
			public Boolean execute(Connection conn) throws SQLException 
			{
				List<User> userList;
				List<User> activeList;
				
				try 
				{
					userList = InitialData.getUsers();
					activeList = InitialData.getActive();
				} 
				catch (IOException e) 
				{
					throw new SQLException("Couldn't read initial data", e);
				}

				PreparedStatement insertUser = null;
				PreparedStatement insertActive = null;

				try {
					// populate users table (do users first, since user_id is foreign key in books table)
					insertUser = conn.prepareStatement("insert into users (user_id2, password, username, email, firstname, lastname, id_number) values (?, ?, ?, ?, ?, ?, ?)");
					for (User user : userList) {
						//int i = 0;
//						insertUser.setInt(1, user.getUserId());	// auto-generated primary key, don't insert this
						insertUser.setInt(1, user.getDbId());
						insertUser.setString(2, user.getPassword());
						insertUser.setString(3, user.getUserName());
						insertUser.setString(4, user.getEmail());
						insertUser.setString(5, user.getFirstName());
						insertUser.setString(6, user.getLastName());
						insertUser.setInt(7, user.getId());
						insertUser.addBatch();
					}
					insertUser.executeBatch();
					
					
					// populate active user table (do this after users table,
					// since user_id must exist in users table before inserting book)
					insertActive = conn.prepareStatement("insert into active (user_id, room) values (?, ?)");
					for (User user : activeList) {
//						insertBook.setInt(1, book.getBookId());		// auto-generated primary key, don't insert this
						insertActive.setInt(1, user.getDbId());
						insertActive.setInt(2, user.getRoom());
						insertActive.addBatch();
					}
					insertActive.executeBatch();
					
					return true;
				} finally {
					DBUtil.closeQuietly(insertActive);
					DBUtil.closeQuietly(insertUser);
				}
			}
		});
	}
	
	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		System.out.println("Creating tables...");
		DerbyDatabase db = new DerbyDatabase();
		db.createTables();
		
		System.out.println("Loading initial data...");
		db.loadInitialData();
		
		System.out.println("Success!");
	}
	
	public ArrayList<User> findUserByLastName(String lastName)
	{
		return executeTransaction(new Transaction<ArrayList<User>>() 
		{
			@Override
			public ArrayList<User> execute(Connection conn) throws SQLException 
			{
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				//@SuppressWarnings("resource")

				try 
				{
					
					// a canned query to find book information (including user name) from title
					//add order by statement and change ? position and change return values
					stmt = conn.prepareStatement(
							"select users.*, books.* "
							+ "  from users, books "
							+ "  where users.user_id = books.user_id "
							+ "        and users.lastname = ?"
							+ "  order by books.title"
					);
					

					// substitute the title entered by the user for the placeholder in the query
					//change title variable to last name
					stmt.setString(1, lastName);

					ArrayList<User> result = new ArrayList<User>();
					
					// execute the query
					resultSet = stmt.executeQuery();

					Boolean found = false;
					
					while (resultSet.next()) 
					{
						found = true;
						
						// create new User object
						// retrieve attributes from resultSet starting with index 1
						User user = new User();
						loadUser(user, resultSet, 1);
						
						// create new Book object
						// retrieve attributes from resultSet starting at index 4
						Book book = new Book();
						loadBook(book, resultSet, 4);
						
						result.add(user);
					}
					
					// check if the title was found
					if (!found) 
					{
						System.out.println("<" + lastName + "> was not found in the users table");
					}
					
					return result;
				} 
				finally 
				{
					// close result set and statement
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}

			}
		});
	}
	
	public ArrayList<User> getAllUsers()
	{
		//throw new UnsupportedOperationException();
		//@Override
		return executeTransaction(new Transaction<ArrayList<User>>()
			{
				@Override
				public ArrayList<User> execute(Connection conn) throws SQLException
				{
					PreparedStatement stmt = null;
					ResultSet resultSet = null;
					try
					{
						stmt = conn.prepareStatement("select * from users");
						resultSet = stmt.executeQuery();
						
						ArrayList<User> result = new ArrayList<User>();
						
						boolean success = false;
						while (resultSet.next())
						{
							//create user and load the attributes of the user to 
							//a new user instance
							User user = new User();
							loadUser(user, resultSet, 1);
							
							//add the user to the arraylist that will be returned
							result.add(user);
							success = true;
						}
						
						if (!success) 
						{
							System.out.println("Users were not found in the users table");
						}
						
						return result;
					}
					finally
					{
						DBUtil.closeQuietly(resultSet);
						DBUtil.closeQuietly(stmt);
					}
					
			}
		});
		
	}
	
	
	public ArrayList<User> getActiveUsersInRoom(String room)
	{
		//throw new UnsupportedOperationException();
		//@Override
		return executeTransaction(new Transaction<ArrayList<User>>()
			{
				@Override
				public ArrayList<User> execute(Connection conn) throws SQLException
				{
					PreparedStatement stmt = null;
					ResultSet resultSet = null;
					try
					{
						stmt = conn.prepareStatement("select * from active"
								+ " where active.room = ?");
						stmt.setString(1, room);
						
						resultSet = stmt.executeQuery();
						
						ArrayList<User> result = new ArrayList<User>();
						
						boolean success = false;
						while (resultSet.next())
						{
							//create user and load the attributes of the user to 
							//a new user instance
							User user = new User();
							loadActiveUser(user, resultSet, 2);
							
							//add the user to the arraylist that will be returned
							result.add(user);
							success = true;
						}
						
						if (!success) 
						{
							System.out.println("Something bad happend while looking for how many people were in the room");
						}
						
						return result;
					}
					finally
					{
						DBUtil.closeQuietly(resultSet);
						DBUtil.closeQuietly(stmt);
					}
					
			}
		});
		
	}
	
	
	public ArrayList<User> getActiveUsers()
	{
		//throw new UnsupportedOperationException();
		//@Override
		return executeTransaction(new Transaction<ArrayList<User>>()
			{
				@Override
				public ArrayList<User> execute(Connection conn) throws SQLException
				{
					PreparedStatement stmt = null;
					ResultSet resultSet = null;
					try
					{
						stmt = conn.prepareStatement("select * from active");
						resultSet = stmt.executeQuery();
						
						ArrayList<User> result = new ArrayList<User>();
						
						boolean success = false;
						while (resultSet.next())
						{
							//create user and load the attributes of the user to 
							//a new user instance
							User user = new User();
							loadActiveUser(user, resultSet, 2);
							
							//add the user to the arraylist that will be returned
							result.add(user);
							success = true;
						}
						
						if (!success) 
						{
							System.out.println("Active users were not found in the active table");
						}
						
						return result;
					}
					finally
					{
						DBUtil.closeQuietly(resultSet);
						DBUtil.closeQuietly(stmt);
					}
					
			}
		});
		
	}
	
	public ArrayList<Reservation> getReservationsForRoom(String room, String date)
	{
		//throw new UnsupportedOperationException();
		//@Override
		return executeTransaction(new Transaction<ArrayList<Reservation>>()
			{
				@Override
				public ArrayList<Reservation> execute(Connection conn) throws SQLException
				{
					PreparedStatement stmt = null;
					ResultSet resultSet = null;
					try
					{
						stmt = conn.prepareStatement("select * from reservations"
								+ " where reservations.room = ? and reservations.reservation_date = ?");
						
						stmt.setString(1, room);
						stmt.setString(2, date);
						resultSet = stmt.executeQuery();
						
						ArrayList<Reservation> result = new ArrayList<Reservation>();
						
						boolean success = false;
						while (resultSet.next())
						{
							//create user and load the attributes of the user to 
							//a new user instance
							Reservation reservation = new Reservation();
							loadReservation(reservation, resultSet, 1);
							
							//add the user to the arraylist that will be returned
							result.add(reservation);
							success = true;
						}
						
						if (!success) 
						{
							System.out.println("Reservations were not found in the reservations table");
						}
						
						return result;
					}
					finally
					{
						DBUtil.closeQuietly(resultSet);
						DBUtil.closeQuietly(stmt);
					}
					
			}
		});
		
	}
	
	public User getUserFromReservationTime(String room, String time)
	{
		//@Override
		return executeTransaction(new Transaction<User>()
			{
				@Override
				public User execute(Connection conn) throws SQLException
				{
					PreparedStatement stmt = null;
					ResultSet resultSet = null;
					try
					{
						stmt = conn.prepareStatement("select users.* from users, reservations "
								+ " where reservations.room = ? and reservations.start_time = ? and "
								+ "users.user_id = reservations.user_id2");
						
						stmt.setString(1, room);
						stmt.setString(2, time);
						resultSet = stmt.executeQuery();
						
						User result = null;
						
						boolean success = false;
						while (resultSet.next())
						{
							//create user and load the attributes of the user to 
							//a new user instance
							User user = new User();
							loadUser(user, resultSet, 1);
							
							//add the user to the arraylist that will be returned
							result = user;
							success = true;
						}
						
						if (!success) 
						{
							System.out.println("User's reservation was not found in the reservations table");
						}
						
						return result;
					}
					finally
					{
						DBUtil.closeQuietly(resultSet);
						DBUtil.closeQuietly(stmt);
					}
					
			}
		});
		
	}
	
	public User removeReservation(String room, String time)
	{
		//throw new UnsupportedOperationException();
		//@Override
		return executeTransaction(new Transaction<User>()
			{
				@Override
				public User execute(Connection conn) throws SQLException
				{
					PreparedStatement stmt = null;
					int resultSet = 0;
					try
					{
						stmt = conn.prepareStatement("delete from reservations "
								+ " where reservations.room = ? and reservations.start_time = ? "
								);
						
						stmt.setString(1, room);
						stmt.setString(2, time);
						resultSet = stmt.executeUpdate();
						
						User result = new User();
						
						boolean success = false;
						if (resultSet == 0)
						{
							//create user and load the attributes of the user to 
							//a new user instance
							//User user = new User();
							//loadUser(user, resultSet, 1);
							
							//add the user to the arraylist that will be returned
							//result = user;
							//success = true;
						}
						
						if (!success) 
						{
							System.out.println("User's reservation was not found in the reservations table");
						}
						
						return result;
					}
					finally
					{
						//DBUtil.closeQuietly(resultSet);
						DBUtil.closeQuietly(stmt);
					}
					
			}
		});
		
	}
	
	public User removeActiveUser(String dbId)
	{
		//throw new UnsupportedOperationException();
		//@Override
		return executeTransaction(new Transaction<User>()
			{
				@Override
				public User execute(Connection conn) throws SQLException
				{
					PreparedStatement stmt = null;
					int resultSet = 0;
					try
					{
						stmt = conn.prepareStatement("delete from active "
								+ " where active.user_id = ? "
								);
						
						stmt.setString(1, dbId);
						resultSet = stmt.executeUpdate();
						
						User result = new User();
						
						boolean success = false;
						if (resultSet == 0)
						{
							//create user and load the attributes of the user to 
							//a new user instance
							//User user = new User();
							//loadUser(user, resultSet, 1);
							
							//add the user to the arraylist that will be returned
							//result = user;
							//success = true;
						}
						
						if (!success) 
						{
							System.out.println("User was removed from active table");
						}
						
						return result;
					}
					finally
					{
						//DBUtil.closeQuietly(resultSet);
						DBUtil.closeQuietly(stmt);
					}
					
			}
		});
		
	}
	
	public List<Pair<User, Book>> insertBook(String firstName, String lastName, String title, String isbn, String published)
	{
		return executeTransaction(new Transaction<List<Pair<User,Book>>>() 
		{
			@SuppressWarnings("resource")
			@Override
			public List<Pair<User, Book>> execute(Connection conn) throws SQLException 
			{
				PreparedStatement stmt = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				ResultSet resultSet = null;
				
				try 
				{
					stmt = conn.prepareStatement(
							"select users.user_id "
							+ "  from users "
							+ "  where users.firstname = ? and users.lastname = ?"		
					);
					
					//set variables equal to question marks and execute the query
					stmt.setString(1, firstName);
					stmt.setString(2, lastName);
					resultSet = stmt.executeQuery();
					
					//if the user was already in the the table, run this block
					if (resultSet.next())
					{
						//set object to user id and create statement to add user's new book
						Object obj = resultSet.getString(1);
						stmt2 = conn.prepareStatement(
								"insert into books (user_id, title, isbn, published) "
								+ "  values (?, ?, ?, ?)"
						);			
						

						// substitute the question marks for variables
						stmt2.setString(1, obj.toString());
						stmt2.setString(2, title);
						stmt2.setString(3, isbn);
						stmt2.setString(4, published);

						// execute the update
						stmt2.executeUpdate();
					}
					//if the user was not in the books table, run this block
					else
					{
						//create statement to insert new user into user's table
						stmt3 = conn.prepareStatement(
								"insert into users (lastname, firstname)"
								+ "  values (?, ?)"
										
						);
						
						//set ?'s equal to variables and execute update
						stmt3.setString(1, lastName);
						stmt3.setString(2, firstName);
						stmt3.executeUpdate();
						
						
						//create new statement to get new user's id
						stmt = conn.prepareStatement(
								"select users.user_id "
								+ "  from users "
								+ "  where users.firstname = ? and users.lastname = ?"		
						);
						
						//set variables equal to question marks and set resultSet equal to user's id
						stmt.setString(1, firstName);
						stmt.setString(2, lastName);
						resultSet = stmt.executeQuery();
						System.out.println("User added");
						
						//move cursor and create statement to add user's new book into books
						resultSet.next();
						Object obj = resultSet.getString(1);
						stmt2 = conn.prepareStatement(
								"insert into books (user_id, title, isbn, published) "
								+ "  values (?, ?, ?, ?)"
						);			
						

						// substitute the title entered by the user for the placeholder in the query
						stmt2.setString(1, obj.toString());
						stmt2.setString(2, title);
						stmt2.setString(3, isbn);
						stmt2.setString(4, published);

						// execute the update
						stmt2.executeUpdate();
					}
					List<Pair<User, Book>> result = new ArrayList<Pair<User,Book>>();
					
					
					while (resultSet.next()) 
					{	
						// create new User object
						// retrieve attributes from resultSet starting with index 1
						User user = new User();
						loadUser(user, resultSet, 1);
						
						// create new Book object
						// retrieve attributes from resultSet starting at index 4
						Book book = new Book();
						loadBook(book, resultSet, 4);
						
						result.add(new Pair<User, Book>(user, book));
					}
				
					return result;
				} 
				finally 
				{
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
		
	}
	
	public User insertUser(String password, String username, String email, String firstName, String lastName, String id)
	{
		return executeTransaction(new Transaction<User>() 
		{
			@SuppressWarnings("resource")
			@Override
			public User execute(Connection conn) throws SQLException 
			{
				PreparedStatement stmt = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				ResultSet resultSet = null;
				
				try 
				{
					stmt = conn.prepareStatement(
							"select users.username "
							+ "  from users "
							+ "  where users.username = ?"		
					);
					
					//set variables equal to question marks and execute the query
					stmt.setString(1, username);
					//stmt.setString(2, password);
					resultSet = stmt.executeQuery();
					
					//if the user was already in the the table, run this block
					if (resultSet.next())
					{
						System.out.println("Username already in use");
					}
					//if the user was not in the books table, run this block
					else
					{
						//create statement to insert new user into user's table
						stmt3 = conn.prepareStatement(
								"insert into users (user_id2, password, username, email, firstname, lastname, id_number)"
								+ "  values (?, ?, ?, ?, ?, ?, ?)"
										
						);
						
						ArrayList<User> temp = getAllUsers();
						//set ?'s equal to variables and execute update
						stmt3.setInt(1, (temp.size() + 1));
						stmt3.setString(2, password);
						stmt3.setString(3, username);
						stmt3.setString(4, email);
						stmt3.setString(5, firstName);
						stmt3.setString(6, lastName);
						stmt3.setString(7, id);
						stmt3.executeUpdate();
						System.out.println("User added");
						
						//create new statement to get new user's id
						stmt = conn.prepareStatement(
								"select users.user_id, users.user_id2, users.password, users.username, users.email, users.firstname, users.lastname, users.id_number "
								+ "  from users "
								+ "  where users.password = ? and users.username = ?"		
						);
						
						//set variables equal to question marks and set resultSet equal to user's id
						stmt.setString(1, password);
						stmt.setString(2, username);
						resultSet = stmt.executeQuery();
						
						/*//move cursor and create statement to add user's new book into books
						resultSet.next();
						Object obj = resultSet.getString(1);
						stmt2 = conn.prepareStatement(
								"insert into books (user_id, title, isbn, published) "
								+ "  values (?, ?, ?, ?)"
						);			
						

						// substitute the title entered by the user for the placeholder in the query
						stmt2.setString(1, obj.toString());
						stmt2.setString(2, title);
						stmt2.setString(3, isbn);
						stmt2.setString(4, published);

						// execute the update
						stmt2.executeUpdate();*/
					}
					User result = new User();
					
					
					while (resultSet.next()) 
					{	
						// create new User object
						// retrieve attributes from resultSet starting with index 1
						User user = new User();
						loadUser(user, resultSet, 1);

						result = user;
					}
				
					return result;
				} 
				finally 
				{
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
					DBUtil.closeQuietly(stmt2);
				}
			}
		});
		
	}
	
	public User insertActiveUser(String dbId, String roomNumber)
	{
		return executeTransaction(new Transaction<User>() 
		{
			@SuppressWarnings("resource")
			@Override
			public User execute(Connection conn) throws SQLException 
			{
				PreparedStatement stmt = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				ResultSet resultSet = null;
				
				try 
				{
					stmt = conn.prepareStatement(
							"select active.user_id "
							+ "  from active "
							+ "  where active.user_id = ?"		
					);
					
					//set variables equal to question marks and execute the query
					stmt.setString(1, dbId);
					//stmt.setString(2, password);
					resultSet = stmt.executeQuery();
					
					//if the user was already in the the table, run this block
					if (resultSet.next())
					{
						System.out.println("User already is active");
					}
					//if the user was not in the books table, run this block
					else
					{
						//create statement to insert new user into user's table
						stmt3 = conn.prepareStatement(
								"insert into active (user_id, room)"
								+ "  values (?, ?)"
										
						);
						
						//set ?'s equal to variables and execute update
						stmt3.setString(1, dbId);
						stmt3.setString(2, roomNumber);
						stmt3.executeUpdate();
						System.out.println("Active User added");
						
						//create new statement to get new user's id
						stmt = conn.prepareStatement(
								"select active.user_id, active.room "
								+ "  from active "
								+ "  where active.user_id = ? and active.room = ?"		
						);
						
						//set variables equal to question marks and set resultSet equal to user's id
						stmt.setString(1, dbId);
						stmt.setString(2, roomNumber);
						resultSet = stmt.executeQuery();
						
					}
					User result = new User();
					
					
					while (resultSet.next()) 
					{	
						// create new User object
						// retrieve attributes from resultSet starting with index 1
						User user = new User();
						loadActiveUser(user, resultSet, 1);

						result = user;
					}
				
					return result;
				} 
				finally 
				{
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
					DBUtil.closeQuietly(stmt3);
				}
			}
		});
		
	}
	
	public Reservation insertReservation(String date, String startTime, int roomNumber, int userId)
	{
		return executeTransaction(new Transaction<Reservation>() 
		{
			@SuppressWarnings("resource")
			@Override
			public Reservation execute(Connection conn) throws SQLException 
			{
				PreparedStatement stmt = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				ResultSet resultSet = null;
				
				try 
				{
					stmt = conn.prepareStatement(
							"select reservations.start_time "
							+ "  from reservations "
							+ "  where reservations.reservation_date = ? and reservations.start_time = ? and reservations.room = ?"		
					);
					
					//set variables equal to question marks and execute the query
					stmt.setString(1, date);
					stmt.setString(2, startTime);
					stmt.setInt(3, roomNumber);
					resultSet = stmt.executeQuery();
					
					
					//if the user was already in the the table, run this block
					if (resultSet.next())
					{
						System.out.println("Reservation already is taken");
					}
					//if the user was not in the books table, run this block
					else
					{
						//create statement to insert new user into user's table
						stmt3 = conn.prepareStatement(
								"insert into reservations (user_id2, room, reservation_date, start_time)"
								+ "  values (?, ?, ?, ?)"
										
						);
						
						//set ?'s equal to variables and execute update
						stmt3.setInt(1, userId);
						stmt3.setString(2, Integer.toString(roomNumber));
						stmt3.setString(3, date);
						stmt3.setString(4, startTime);
						stmt3.executeUpdate();
						System.out.println("Reservation added");
						
						//create new statement to get new user's id
						stmt = conn.prepareStatement(
								"select reservations.* "
								+ "  from reservations "
								+ "  where reservations.reservation_date = ? and reservations.start_time = ? and reservations.room = ?"		
						);
						
						//set variables equal to question marks and set resultSet equal to user's id
						stmt.setString(1, date);
						stmt.setString(2, startTime);
						stmt.setInt(3, roomNumber);
						resultSet = stmt.executeQuery();
						System.out.println("Reservation Retrieved");
						
					}
					Reservation result = new Reservation();
					
					
					while (resultSet.next()) 
					{	
						// create new User object
						// retrieve attributes from resultSet starting with index 1
						Reservation reservation = new Reservation();
						loadReservation(reservation, resultSet, 1);

						result = reservation;
					}
				
					return result;
				} 
				finally 
				{
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
					DBUtil.closeQuietly(stmt3);
				}
			}
		});
		
	}
	
	
}
