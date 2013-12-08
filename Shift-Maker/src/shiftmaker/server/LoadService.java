package shiftmaker.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class LoadService extends HttpServlet {
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws
   ServletException, IOException {
      BufferedReader reader= req.getReader();
      String line;
      String string = "";
      reader.readLine();
      reader.readLine();
      reader.readLine();
      reader.readLine();
      while((line = reader.readLine()) != null) {
         if(line.contains("----")) {
        	 break;
         }
    	 string += "\n"+ line;
      }
      
      resp.setContentType("text/plain");
      PrintWriter out = resp.getWriter();
      out.write(string);
      out.close();
   }
}
