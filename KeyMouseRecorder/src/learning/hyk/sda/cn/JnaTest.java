package learning.hyk.sda.cn;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
/**
 * Created by lenovo on 2017/4/27.
 * 使用winID来获得窗口的类型和标题，然后发送消息或者其他操作
 *
 */
public class JnaTest {
    public static void main(String[] args) {

        HWND hwnd = User32.INSTANCE.FindWindow
                (null, "无标题 - 记事本"); // 第一个参数是Windows窗体的窗体类，第二个参数是窗体的标题。不熟悉windows编程的需要先找一些Windows窗体数据结构的知识来看看，还有windows消息循环处理，其他的东西不用看太多。
        hwnd = User32.INSTANCE.FindWindow
                (null, "KeyMouseRecorder&Player");
        if (hwnd == null) {
            System.out.println("无标题 - 记事本 is not running");
        }
        else{
            User32.INSTANCE.ShowWindow(hwnd, 9 );        // SW_RESTORE
            User32.INSTANCE.SetForegroundWindow(hwnd);   // bring to front

            //User32.INSTANCE.GetForegroundWindow() //获取现在前台窗口
            WinDef.RECT qqwin_rect = new  WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hwnd, qqwin_rect);
            int qqwin_width = qqwin_rect.right-qqwin_rect.left;
            int qqwin_height = qqwin_rect.bottom-qqwin_rect.top;

            User32.INSTANCE.MoveWindow(hwnd, 700, 100, qqwin_width, qqwin_height, true);
            for(int i = 700; i > 100; i -=10) {
                User32.INSTANCE.MoveWindow(hwnd, i, 100, qqwin_width, qqwin_height, true);   // bring to front
                try {
                    Thread.sleep(80);
                }catch(Exception e){}
            }
            //User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, null, null);  // can be WM_QUIT in some occasio
        }
    }
}
//在Windows中，User32.dll文件拥有大量的操作用户界面的API。可以看到JNA在包命名上也遵照了DLL的命名规律。