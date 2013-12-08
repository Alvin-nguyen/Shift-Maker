package shiftmaker.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class FileService extends HttpServlet {
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws
   ServletException, IOException {
       
      BufferedReader reader= req.getReader();
      String line;
      String string = "";
      while((line = reader.readLine()) != null) {
         string += line +"\n";
      }
      
      string = URLEncoder.encode(string, "UTF-8");
      Cookie cook = new Cookie("Shift-Maker-SaveFile",string);
      
      //System.out.println(string);
      resp.addCookie(cook); 
      reader.close();
        
   }
   
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws
   ServletException, IOException {
       
      resp.setContentType("application/octet-stream");
      resp.setHeader("Content-Disposition", "attachment; filename=output.txt");


      PrintWriter write = resp.getWriter();
      Cookie[] cookies = req.getCookies();
      
      for(Cookie c: cookies) {
         if(c.getName().equals("Shift-Maker-SaveFile")) {
            write.write(URLDecoder.decode(c.getValue(), "UTF-8"));         
         }
            
      }
        write.close();
   }
}
