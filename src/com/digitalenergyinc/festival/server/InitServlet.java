package com.digitalenergyinc.festival.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.digitalenergyinc.fest.client.Constants;
import com.digitalenergyinc.fest.server.beans.DBException;
import com.digitalenergyinc.fest.server.beans.MovieList;

/**
 * Initializes context info for servlet .
 * 
 * <p>Title: Movie Scheduling</p>
 * <p>Description: Builds a movie schedule for a filmgoer at a film festival.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */
/**
 * @author Rene
 *
 */
public final class InitServlet
extends HttpServlet {
	private static final long serialVersionUID = 7960177706647853636L;
	static Logger logger = Logger.getLogger(InitServlet.class);    // logging class

	private transient ServletContext servletContext = null;

	/* 
	 * Destroy!
	 */
	public void destroy() {
		logger.debug("Finalizing init servlet");
		servletContext.removeAttribute(Constants.MOVIE_LIST);
	}

	/* 
	 * Initialize routine to load data into context.
	 */
	public void init() throws ServletException {

		// Process our servlet initialization parameters
		servletContext = getServletContext();

		logger.debug("Initializing gwtCal init servlet ");

		setFilmData();		// for user registration
	}

	/* 
	 * Sets film data into servlet context.
	 */
	public void setFilmData() 
	{
		try {
			MovieList movies = new MovieList();
			servletContext.setAttribute(Constants.MOVIE_LIST, movies);
			
		} catch (DBException e) {
			e.printStackTrace();
			logger.error("Init DB Error:"+e.getMessage());
		}
		
	}
}
