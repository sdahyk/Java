package learning.hyk.sda.cn;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public class MouseMonitor {
	public interface CLibrary extends StdCallLibrary {
        CLibrary INSTANCE = (CLibrary)
            Native.load((Platform.isWindows() ? "user32" : "c"),CLibrary.class);

        boolean GetCursorPos(Pointer pt);
    }
	 public static void main(String[] args) {
		 PointStructure a = new PointStructure();
		 a.x = 10;
		 a.y = 20;
		 a.write();
		 Pointer pt = a.getPointer();
		 System.out.println(CLibrary.INSTANCE.GetCursorPos(pt));
		 a.read();
		 System.out.println(a.x+"/"+a.y);
	 }
}
