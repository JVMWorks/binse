package com.binse;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.setPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;

import spark.Request;
import spark.Response;
import spark.Route;

public class Sparky {
    
	public static void main(String[] args) {
		
		setPort(Integer.parseInt(System.getenv("PORT")));
		
		// Configure the session factory
		HibernateUtil.configureSessionFactory();
		
		get(new Route("/hi") {
			@Override
			public Object handle(Request req, Response res) {
				return "HelloWorld";
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
