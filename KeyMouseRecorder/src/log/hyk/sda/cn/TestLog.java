package log.hyk.sda.cn;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestLog {
	public static void main (String[] args) {
		Logger log = Logger.getLogger("tesglog");  
        log.setLevel(Level.ALL);  
        FileHandler fileHandler;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = df.format(new Date());
        try {
			fileHandler = new FileHandler(fileName+".log");
	        fileHandler.setLevel(Level.ALL);  
	        fileHandler.setFormatter(new LogFormatter());  
	        log.addHandler(fileHandler);  
	        log.info("This is test java util log");
	        log.log(Level.INFO, "test");
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
