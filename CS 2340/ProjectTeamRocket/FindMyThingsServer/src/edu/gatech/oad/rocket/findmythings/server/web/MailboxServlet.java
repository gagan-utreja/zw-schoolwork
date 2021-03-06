package edu.gatech.oad.rocket.findmythings.server.web;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class MailboxServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(MailboxServlet.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = -5510648263425183234L;

	public MailboxServlet() {
		super();
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session, req.getInputStream());

			String subject = message.getSubject();

			LOG.info("Got an email. Subject = " + subject);

			String contentType = message.getContentType();
			LOG.info("Email Content Type : " + contentType);
		}
		catch (Exception ex) {
			LOG.warning("Failure in receiving email : " + ex.getMessage());
		}
	}


}
