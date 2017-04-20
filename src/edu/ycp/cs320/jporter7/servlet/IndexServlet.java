package edu.ycp.cs320.jporter7.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.jporter7.controller.PopulationCounterController;
import edu.ycp.cs320.jporter7.model.PopulationCounter;
import edu.ycp.cs320.jporter7.populationdb.persist.IDatabase;

public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private IDatabase db;

    @Override
    public void init() throws ServletException
    {
        this.db = (IDatabase)getServletContext().getAttribute("database");
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
		if (req.getParameter("Grumbacher") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/grumbacher");
		}
		else if (req.getParameter("WeightRoom") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/weightRoom");
		}
		else if (req.getParameter("Reservations") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/reservations");
		}
		else if (req.getParameter("RacquetBall") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/reservations");
		}
		else
		{
			throw new ServletException("Unknown Command");
		}
		
	}
	
}
