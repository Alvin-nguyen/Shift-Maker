package shiftmaker.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class IcsService extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws
	ServletException, IOException {

		BufferedReader reader= req.getReader();
		String line;
		String string = "";
		while((line = reader.readLine()) != null) {
			string += line +"\n";
		}

		//System.out.println(string);
		string = URLEncoder.encode(string, "UTF-8");
		ArrayList<String> splitStrings = new ArrayList<String>();

		int fileLoop=0;
		while(fileLoop < string.length()) {
			if(fileLoop+2000 >= string.length()) {
				splitStrings.add(string.substring(fileLoop, string.length()));
			} else {
				splitStrings.add(string.substring(fileLoop, fileLoop+2000));
			}
			
			fileLoop+=2000;
		}

		Cookie cook = new Cookie("Shift-Maker-Ics-Count",""+splitStrings.size());

		for(int i=0;i<splitStrings.size();i++) {
			Cookie tempCook = new Cookie("Shift-Maker-Ics-"+i, splitStrings.get(i));
			resp.addCookie(tempCook);
		}

		//System.out.println(string);
		resp.addCookie(cook); 
		reader.close();

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws
	ServletException, IOException {

		resp.setContentType("application/octet-stream");
		resp.setHeader("Content-Disposition", "attachment; filename=calendar.ics");


		PrintWriter write = resp.getWriter();
		Cookie[] cookies = req.getCookies();
		int cookieCount = 0;
		String fullFile = "";

		for(Cookie c: cookies) {
			if(c.getName().equals("Shift-Maker-Ics-Count")) {
				cookieCount = Integer.parseInt(c.getValue());
			} 
		}

		for(int i = 0; i < cookieCount; i++) {
			for(Cookie c: cookies) {
				if(c.getName().equals("Shift-Maker-Ics-"+i)) {
					fullFile += c.getValue();
				} 
			}
		}

		fullFile = URLDecoder.decode(fullFile, "UTF-8");

		write.write(fullFile);
		write.close();
	}
}
