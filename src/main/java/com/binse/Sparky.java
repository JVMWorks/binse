package com.binse;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.setPort;
import static spark.Spark.staticFileLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.mustache.MustacheTemplateRoute;

public class Sparky {
    
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

//	private static String toKeyValueString(final Map<String, Object> averages, final String key) {
//		return key + ": " + averages.get(key) + '\n';
//	}

	public static void main(String[] args) {
		
		setPort(Integer.parseInt(System.getenv("PORT")));

		// Will serve all static file are under "/public" in classpath if the route isn't consumed by others routes.
        // When using Maven, the "/public" folder is assumed to be in "/main/resources"
		staticFileLocation("/public");
        
		// Configure the session factory
		HibernateUtil.configureSessionFactory();
		
		get(new Route("/stats") {
			@Override
			public Object handle(Request req, Response res) {
				StringBuilder form = new StringBuilder();
				 
                form.append("<form id='stats-form' method='POST' action='/stats'>")
                        .append("Stock Tick Code: <input type='text' name='stockcode' />")
                        .append("<br/>")
                        .append("From Date(DD-MM-YYYY): <input type='text' name='fromdate' />")
                        .append("<br/>")
                        .append("To Date(DD-MM-YYYY): <input type='text' name='todate' />")
                        .append("<br/>")
                    .append("<input type='submit' value='Submit' form='stats-form' />");
                return form.toString();
			}
		});
		
		post(new MustacheTemplateRoute("/stats") {
			@Override
			@SuppressWarnings("unchecked")
			public Object handle(Request req, Response res) {
				String stockcode="", fromdate="", todate="";
				Date from=null, to=null;
				try{
				stockcode = req.queryParams("stockcode").trim().toUpperCase();
				fromdate = req.queryParams("fromdate");
				todate = req.queryParams("todate");
				
				from = sdf.parse(fromdate);				
				to = sdf.parse(todate);
				} catch (Exception e) {
					res.status(400);
					return null;
				}
				
				Map<String, Object> averages = (Map) StockTic.stats(from, to, stockcode);
				averages.put("CODE", stockcode);
				res.status(200);
                return template("stats.mustache").render(averages);
			}
		});

		get(new Route("/upload") {
			@Override
			public Object handle(Request req, Response res) {
				StringBuilder form = new StringBuilder();
				 
                form.append("<form id='csv-form' method='POST' action='/upload' enctype='multipart/form-data'>")
                        .append("Upload CSV: <input type='file' name='file' />")
                        .append("<br/>")
                    .append("<input type='submit' value='Submit' form='csv-form' />");
                return form.toString();
			}
		});
		
		post(new Route("/upload") {
			@Override
			@SuppressWarnings("unchecked")
			public Object handle(Request req, Response res) {
				try {
					List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req.raw());
					for (FileItem item : items) {
			            if (item.isFormField()) {
			                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
//			                String fieldname = item.getFieldName();
//			                String fieldvalue = item.getString();
			                // ... (do your job here)
			            } else {
			                // Process form file field (input type="file").
			                String fieldname = item.getFieldName();
			                String filename = FilenameUtils.getName(item.getName());
			                System.out.println("File Name = " + filename + ", FieldName = " + fieldname);
			                InputStream filecontent = item.getInputStream();
			                BufferedReader reader = new BufferedReader(new InputStreamReader(filecontent));
			                String line;
		                	Session session = HibernateUtil.getSessionFactory().openSession();
		                	session.beginTransaction();
			                while ((line = reader.readLine()) != null) {
			                	if( line.trim().isEmpty() || line.trim().startsWith("Date")) continue;
			                	String[] tokens = line.split(",");
			                	
			                	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			                	StockTic tic = new StockTic(filename.toUpperCase().substring(0,filename.indexOf(".")),
			                			formatter.parse(tokens[0].trim()), Float.parseFloat(tokens[1].trim()), 
			                			Float.parseFloat(tokens[2].trim()), Float.parseFloat(tokens[3].trim()), 
			                			Float.parseFloat(tokens[4].trim()),	Float.parseFloat(tokens[5].trim()), 
			                			Long.parseLong(tokens[6].trim()),Float.parseFloat(tokens[7].trim()));
			                	System.out.println(tic);
				                session.save(tic);
			                }
			                session.getTransaction().commit();
			            }
			        }
				} catch (FileUploadException | IOException | NumberFormatException | ParseException e) {
					e.printStackTrace();
				}				
				return "Upload successful";
			}
		});
		
	}
	
}
