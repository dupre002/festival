package com.digitalenergyinc.festival.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.PropertyConfigurator;

/**
 * Servlet initialization for logger.
 */
public class Log4jInit extends HttpServlet 
{

	private static final long serialVersionUID = 5016716144012262037L;

public void init() {
    String prefix =  getServletContext().getRealPath("/");
    String file = getInitParameter("log4j-init-file");
    // if the log4j-init-file is not set, then no point in trying
    if(file != null) {
      PropertyConfigurator.configure(prefix+file);
    }
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) {
  }
}
