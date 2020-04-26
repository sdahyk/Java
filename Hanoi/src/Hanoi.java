import java.util.Stack;

public class Hanoi {

	public static void main(String[] args) {
		int disks = 4;
		Stack<Integer> a = new Stack<Integer>();
		Stack<Integer> b = new Stack<Integer>();
		Stack<Integer> c = new Stack<Integer>();
		for (int i = disks; i > 0; i--) {
			a.push(i);
		}
		doHanoi(disks, a, b, c);

	}

	/**
	 * ��aa�ϵ�num�����ӣ�����bb���ƶ���cc��
	 * 
	 * @param num	Ҫ�ƶ�����������
	 * @param aa	�����ʼ���ϵ�����
	 * @param bb	�����ƶ������õ��м����ϵ�����
	 * @param cc	����Ŀ�����ϵ�����
	 * @author hanyk
	 */
	public static void doHanoi(int num, Stack<Integer> aa, Stack<Integer> bb, Stack<Integer> cc) {
		if (num == 1) {
			cc.push(aa.pop());
			System.out.println(aa.toString() + bb.toString() + cc.toString());
		} else {
			doHanoi((num - 1), aa, cc, bb);
			cc.push(aa.pop());
			System.out.println(aa.toString() + bb.toString() + cc.toString());
			doHanoi((num -1), bb, aa, cc);
		}
	}

}
