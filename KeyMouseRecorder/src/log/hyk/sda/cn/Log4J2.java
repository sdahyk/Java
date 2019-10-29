package log.hyk.sda.cn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
public class Log4J2 {
//	public static final Logger loggerMy = LogManager.getLogger("Log4J2");
//	public static final Logger loggerMy = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
	public static final Logger loggerMy = LogManager.getLogger("mylog");
	public static void main(String[] args) {
		for(int i = 0; i < 50000; i++) {  
	        loggerMy.trace("trace level");  
	        loggerMy.debug("debug level");  
	        loggerMy.info("info level");  
	        loggerMy.warn("warn level");  
	        loggerMy.error("error level");  
	        loggerMy.fatal("fatal level");  
	    }  
	    try {  
	        Thread.sleep(1000 * 61);  
	    } catch (InterruptedException e) {}  
	    loggerMy.trace("trace level");  
	    loggerMy.debug("debug level");  
	    loggerMy.info("info level");  
	    loggerMy.warn("warn level");  
	    loggerMy.error("error level");  
	    loggerMy.fatal("fatal level");
	}
}