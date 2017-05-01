package edu.ycp.cs320.jporter7.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.jporter7.controller.PopulationCounterController;
import edu.ycp.cs320.jporter7.model.PopulationCounter;
import edu.ycp.cs320.jporter7.model.User;
import edu.ycp.cs320.jporter7.populationdb.persist.IDatabase;
import edu.ycp.cs320.jporter7.simulator.SwipeSimulator;
import javafx.scene.input.SwipeEvent;

public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private IDatabase db;
	private User newSwipeUser = new User();
	private SwipeSimulator swipeSim = new SwipeSimulator(newSwipeUser, db);
	private int counter;

    @Override
    public void init() throws ServletException
    {
        this.db = (IDatabase)getServletContext().getAttribute("database");
        this.counter = 22;
    }
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		PopulationCounter model = new PopulationCounter();
		PopulationCounterController popController = new PopulationCounterController(model, db);
		popController.setModel();
		req.setAttribute("model", model);
		
		Object username = req.getSession().getAttribute("username");
		Object password = req.getSession().getAttribute("password");
		Object user = req.getSession().getAttribute("user");
		User user2 = (User)user;
		//HttpSession session= req.getSession(false); 
		if (username == null || username.equals("") || password == null || password.equals(""))
		{
			System.out.println("Need to login");
			resp.sendRedirect(req.getContextPath() + "/login");
		}
		else
		{
			req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
		}
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		if (req.getParameter("TotalPopulation") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/index");
		}
		else if (req.getParameter("WeightRoom") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/weightRoom");
		}
		else if (req.getParameter("FieldHouse") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/index");
		}
		else if (req.getParameter("WolfBasketball") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/wolfBasketball");
		}
		else if (req.getParameter("Pool") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/pool");
		}
		else if (req.getParameter("DanceRoom") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/danceRoom");
		}
		else if (req.getParameter("RacquetBall") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/racquetBall");
		}
		else if (req.getParameter("RockWall") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/rockWall");
		}
		else if (req.getParameter("Test Button") != null)
		{
			swipeSim.swipe(newSwipeUser, db, counter++);
			resp.sendRedirect(req.getContextPath() + "/index");
		}
		else
		{
			throw new ServletException("Unknown Command");
		}
		
	}
	
}
