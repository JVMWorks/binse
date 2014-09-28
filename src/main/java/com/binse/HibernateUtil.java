package com.binse;

import java.net.URI;
import java.net.URISyntaxException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	private static Session session;

	static SessionFactory configureSessionFactory() throws HibernateException, URISyntaxException {

		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		Configuration cfg = new Configuration().configure();
        cfg.setProperty("hibernate.connection.username", getUserName(dbUri));
        cfg.setProperty("hibernate.connection.password", getPassword(dbUri));
        cfg.setProperty("hibernate.connection.url", 
        		"jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath());

		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
		sessionFactory = cfg.buildSessionFactory(serviceRegistry);

		return sessionFactory;
	}

	private static String getPassword(URI dbUri) {
		String userInfo = dbUri.getUserInfo();
		if (userInfo != null && userInfo.split(":").length==2) {
			return userInfo.split(":")[1];
		}
		return "";
	}

	private static String getUserName(URI dbUri) {
		return dbUri.getUserInfo().split(":")[0];
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public synchronized static Session getSession() {
		if (session == null || !session.isOpen()) {
			session = sessionFactory.openSession();
		}
		return session;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}

}