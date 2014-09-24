package com.binse;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.setPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import spark.Request;
import spark.Response;
import spark.Route;

public class Sparky {
	
	public static void main(String[] args) {
		
		setPort(Integer.parseInt(System.getenv("PORT")));
		
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
				
				// HACK -- because the servlet needs a MultiPartConfig annotation
//				MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
//				req.raw().setAttribute("org.eclipse.multipartConfig", multipartConfigElement);
				
				// Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();

//				boolean isMultipart = ServletFileUpload.isMultipartContent(req);
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
			                InputStream filecontent = item.getInputStream();
//			                java.util.Scanner scanner = new java.util.Scanner(filecontent,"UTF-8").useDelimiter(",");
//			                String theString = scanner.hasNext() ? scanner.next() : "";
			                BufferedReader reader = new BufferedReader(new InputStreamReader(filecontent));
			                String line;
			                while ((line = reader.readLine()) != null) {
			                	System.out.println(line);
			                }
//			                scanner.close();
			            }
			        }
				} catch (FileUploadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return "Upload successful";
			}
		});
		
	}
	
}
