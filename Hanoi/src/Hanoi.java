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
	 * 将aa上的num个盘子，借助bb，移动到cc上
	 * 
	 * @param num	要移动的盘子数量
	 * @param aa	代表初始柱上的盘子
	 * @param bb	代表移动中利用的中间柱上的盘子
	 * @param cc	代表目的柱上的盘子
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
