package candidjava;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class DownloadFile extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	
	static final long serialVersionUID = 1L;
	/**
	 * size of file
	 */
	private static final int BUFSIZE = 40960;
	
	private static String DIRECTORY = "E:\\FiletoDownload"; 
	
	private static String NAMEFILE = "demoNO1";
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * create directory before using it
		 * 
		 */
		createDirectory();
		
		String filePath = pathfileName();
		/**
		 * the way to get path on the server, we can use the existed folder outside project on the server instead of the one in the project
		 */
		String pathInProject  = getServletContext().getRealPath("") +File.separator + "jrxml" + File.separator + "demoNO1.jrxml";
		
	    JasperReport jasperReport;
	    /**
	     * generate report
	     */
		try {
			jasperReport = JasperCompileManager.compileReport(pathInProject);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,new HashMap(), new JREmptyDataSource());
		    JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);

		} catch (JRException e) {
			e.printStackTrace();
		}
	    
		/** 
		 * required
		 */
		File file = new File(filePath);

		/**
		 * download file from server
		 */
		int length = 0;
		ServletOutputStream outStream = response.getOutputStream();
		response.setContentType("text/html");
		response.setContentLength((int) file.length());

		String fileName = (new File(filePath)).getName();
		response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + "\"");

		byte[] byteBuffer = new byte[BUFSIZE];
		DataInputStream in = new DataInputStream(new FileInputStream(file));

		while ((in != null) && ((length = in.read(byteBuffer)) != -1)) {
			outStream.write(byteBuffer, 0, length);
		}
		
		in.close();
		outStream.close();
		
		/**
		 * after download report from server, delete this file on the server.
		 * after close the working stream 
		 */
		file.delete();
	}
	
	/**
	 * steps:
	 * create file in the existed folder on server
	 * download file from the existed folder
	 */
	
	private void createDirectory(){
		
		File file = new File(DIRECTORY);
		
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
	 
	}
	/**
	 * create value by current time
	 * @return
	 */
	private String pathfileName(){
		
		Date date = new Date();
		
		String filePath = DIRECTORY + File.separator + NAMEFILE + String.valueOf(date.getTime())  +".pdf";
		
		return filePath;
	}
}
