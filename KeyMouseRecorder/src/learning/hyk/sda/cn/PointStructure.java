package learning.hyk.sda.cn;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Structure;

public class PointStructure extends Structure {
	public int x;
	public int y;

	//������дgetFieldOrder�����������в���
	@Override
	protected List<String> getFieldOrder() {
		// Ԫ�ص����˳�������ṹԪ��������˳����ͬ����ӵ�˳��һ������д�������ǻ����������ǽṹ�����������������ʲô�ģ�ֻ��Ҫ������ƾͿ���
		// ���list���ص��Ƿ�װ�ṹ���еı�������
		List<String> a = new ArrayList<String>();
		a.add("x");
		a.add("y");
		return a;
	}
}
