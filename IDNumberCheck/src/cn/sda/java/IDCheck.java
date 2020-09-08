package cn.sda.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IDCheck {
	/**
	 * 18λ�������֤�����������ʽ
	 */
	public static final String REGEX_ID_NO_18 = "^" + "\\d{6}" // 6λ������
			+ "(18|19|([23]\\d))\\d{2}" // ��YYYY
			+ "((0[1-9])|(10|11|12))" // ��MM
			+ "(([0-2][1-9])|10|20|30|31)" // ��DD
			+ "\\d{3}" // 3λ˳����
			+ "[0-9Xx]" // У����
			+ "$";

	/**
	 * 15λһ�����֤�����������ʽ
	 */
	public static final String REGEX_ID_NO_15 = "^" + "\\d{6}" // 6λ������
			+ "\\d{2}" // ��YY
			+ "((0[1-9])|(10|11|12))" // ��MM
			+ "(([0-2][1-9])|10|20|30|31)" // ��DD
			+ "\\d{3}"// 3λ˳����
			+ "$";

	/**
	 * У�����֤����
	 * 
	 * <p>
	 * ������18λ�Ķ������֤����
	 * </p>
	 * 
	 * @param IDNo18 ���֤����
	 * @return true - У��ͨ��<br>
	 *         false - У�鲻ͨ��
	 * @throws IllegalArgumentException ������֤����Ϊ�ջ򳤶Ȳ�Ϊ18λ���������֤������ɹ��� <i>6λ��ַ��+
	 *                                  ����������YYYYMMDD+3λ˳���� +0~9��X(x)У����</i>
	 */
	public static boolean checkIDNo18(String IDNo18) {
		// У�����֤����ĳ���
		if (!checkStrLength(IDNo18, 18)) {
			throw new IllegalArgumentException();
		}
		// ƥ�����֤�����������ʽ
		if (!regexMatch(IDNo18, REGEX_ID_NO_18)) {
			throw new IllegalArgumentException();
		}
		// У�����֤�������֤��
		return validateCheckNumber18(IDNo18);
	}

	/**
	 * У���ַ�������
	 * 
	 * @param inputString �ַ���
	 * @param len         Ԥ�ڳ���
	 * @return true - У��ͨ��<br>
	 *         false - У�鲻ͨ��
	 */
	private static boolean checkStrLength(String inputString, int len) {
		if (inputString == null || inputString.length() != len) {
			return false;
		}
		return true;
	}

	/**
	 * ƥ��������ʽ
	 * 
	 * @param inputString �ַ���
	 * @param regex       ������ʽ
	 * @return true - У��ͨ��<br>
	 *         false - У�鲻ͨ��
	 */
	private static boolean regexMatch(String inputString, String regex) {
		return inputString.matches(regex);
	}

	/**
	 * У����У��
	 * <p>
	 * ������18λ�Ķ������֤����
	 * </p>
	 * 
	 * @param IDNo18 ���֤����
	 * @return true - У��ͨ��<br>
	 *         false - У�鲻ͨ��
	 */
	private static boolean validateCheckNumber18(String IDNo18) {
		// ��Ȩ����
		int[] W = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		char[] IDNoArray = IDNo18.toCharArray();
		int sum = 0;
		for (int i = 0; i < W.length; i++) {
			sum += Integer.parseInt(String.valueOf(IDNoArray[i])) * W[i];
		}
		// У��λ��X�����ʾ10
		if (IDNoArray[17] == 'X' || IDNoArray[17] == 'x') {
			sum += 10;
		} else {
			sum += Integer.parseInt(String.valueOf(IDNoArray[17]));
		}
		// �����11ģ1����У��ͨ��
		return sum % 11 == 1;
	}
	
	/**
	 * ����У����
	 * <p>
	 * ������18λ�Ķ������֤����
	 * </p>
	 * 
	 * @param masterNumber ������
	 * @return У����
	 */
	private static String computeCheckNumber(String masterNumber) {
	    // ��Ȩ����
	    int[] W = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
	    char[] masterNumberArray = masterNumber.toCharArray();
	    int sum = 0;
	    for (int i = 0; i < W.length; i++) {
	        sum += Integer.parseInt(String.valueOf(masterNumberArray[i])) * W[i];
	    }
	    // ����ͬ�ඨ��õ���У��������
	    String[] checkNumberArray = { "1", "0", "X", "9", "8", "7", "6", "5", "4",
	            "3", "2" };
	    // �õ�У����
	    String checkNumber = checkNumberArray[sum % 11];
	    // ����У����
	    return checkNumber;
	}
	
	/**
	 * 15λһ�����֤��������18λ�������֤����
	 * <p>
	 * Ϊ15λ��һ�����֤����������ݵ�ǰ2λ�����1λУ����
	 * </p>
	 * 
	 * @param IDNo15 15λ��һ�����֤����
	 * @return 18λ�Ķ������֤����
	 */
	public static String updateIDNo15to18(String IDNo15) {
	    // У�����֤����ĳ���
	    if (!checkStrLength(IDNo15, 15)) {
	        throw new IllegalArgumentException();
	    }
	    // ƥ�����֤�����������ʽ
	    if (!regexMatch(IDNo15, REGEX_ID_NO_15)) {
	        throw new IllegalArgumentException();
	    }
	    // �õ������룬��һ�����֤��Ϊ19XX�����ˣ����������19�����4λ
	    String masterNumber = IDNo15.substring(0, 6) + "19" + IDNo15.substring(6);
	    // ����У����
	    String checkNumber = computeCheckNumber(masterNumber);
	    // ���ر�����+У����=���������֤����
	    return masterNumber + checkNumber;
	}

	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
        String str = null; 
        System.out.println("Enter your value:"); 
        try {
			str = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		} 
        System.out.println("your value is :"+str);
        
        if (str.length()==15) {
        	str = updateIDNo15to18(str);
        }
        System.out.println("��18λ�� :"+str);

        if (checkIDNo18(str)) {
        	System.out.println("��֤ͨ��");
        } else {
        	System.out.println("��֤ʧ��");
        }
	}

}
