package learning.hyk.sda.cn;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
/**
 * Created by lenovo on 2017/4/27.
 * ʹ��winID����ô��ڵ����ͺͱ��⣬Ȼ������Ϣ������������
 *
 */
public class JnaTest {
    public static void main(String[] args) {

        HWND hwnd = User32.INSTANCE.FindWindow
                (null, "�ޱ��� - ���±�"); // ��һ��������Windows����Ĵ����࣬�ڶ��������Ǵ���ı��⡣����Ϥwindows��̵���Ҫ����һЩWindows�������ݽṹ��֪ʶ������������windows��Ϣѭ�����������Ķ������ÿ�̫�ࡣ
        hwnd = User32.INSTANCE.FindWindow
                (null, "KeyMouseRecorder&Player");
        if (hwnd == null) {
            System.out.println("�ޱ��� - ���±� is not running");
        }
        else{
            User32.INSTANCE.ShowWindow(hwnd, 9 );        // SW_RESTORE
            User32.INSTANCE.SetForegroundWindow(hwnd);   // bring to front

            //User32.INSTANCE.GetForegroundWindow() //��ȡ����ǰ̨����
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
//��Windows�У�User32.dll�ļ�ӵ�д����Ĳ����û������API�����Կ���JNA�ڰ�������Ҳ������DLL���������ɡ�