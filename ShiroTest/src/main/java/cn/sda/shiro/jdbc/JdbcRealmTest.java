package cn.sda.shiro.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;

import com.alibaba.druid.pool.DruidDataSource;

public class JdbcRealmTest {
	public static void main(String[] args) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/shiro");
		dataSource.setUsername("root");
		dataSource.setPassword("sdainfo");
		// Ȩ�����ݴ����ݿ��ȡ
		JdbcRealm jdbcRealm = new JdbcRealm();
		// ����jdbcrealm������Դ
		jdbcRealm.setDataSource(dataSource);
		// ���õ����ݿ��ѯȨ�����ݣ�Ĭ��ֵ��false��
		jdbcRealm.setPermissionsLookupEnabled(true);

		// ʹ���Զ���SQL
		// �ȼ���Ĭ�� protected static final String DEFAULT_AUTHENTICATION_QUERY = "select
		// password from users where username = ?";
		String sql = "select password from users where username = ?";
		jdbcRealm.setAuthenticationQuery(sql); // �ı�loginĬ��sql

		// �ȼ���Ĭ�� protected static final String DEFAULT_USER_ROLES_QUERY = "select
		// role_name from user_roles where username = ?";
		String roleSql = "select role_name from user_roles where username = ?";
		jdbcRealm.setUserRolesQuery(roleSql); // �ı�checkRoleĬ��sql

		// �ȼ���Ĭ�� protected static final String DEFAULT_PERMISSIONS_QUERY = "select
		// permission from roles_permissions where role_name = ?";
		String persionSql = "select permission from roles_permissions where role_name = ?";
		jdbcRealm.setPermissionsQuery(persionSql); // �ı�checkPermissionĬ��sql

		// ���� SecurityManager
		DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
		defaultSecurityManager.setRealm(jdbcRealm);

		// �����ύ��֤����
		SecurityUtils.setSecurityManager(defaultSecurityManager);
		Subject subject = SecurityUtils.getSubject();

		try {
////			����
			String password = "abcde";
	        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
	        int times = 2;
	        String algorithmName = "md5";
	        String encodedPassword = new SimpleHash(algorithmName,password,salt,times).toString();
	        System.out.printf("ԭʼ������ %s , ���ǣ� %s, ��������ǣ� %d, ��������������ǣ�%s ",password,salt,times,encodedPassword);

			//��ȡ���ݿ����û���password_salt
	        Connection conn = dataSource.getConnection();
			PreparedStatement preSql = conn.prepareStatement("select password_salt from users where username = ?");
			preSql.setString(1,  "li4");
			ResultSet rs = preSql.executeQuery();
			if (rs.next()) {
				salt = rs.getString(1);
			}
			//�����û����������Ͷ�ȡ������salt���¼�������
			encodedPassword = new SimpleHash(algorithmName,"abcde",salt,times).toString();
			//���ݼ���������ȥ���ݿ���֤
			UsernamePasswordToken token = new UsernamePasswordToken("li4", encodedPassword.toCharArray());
			subject.login(token);
			if (subject.isAuthenticated()) {
				// �Ƿ���֤
				System.out.println(token.getUsername() + " isAuthenticated: true");
			}
		} catch (AuthenticationException ea) {
			System.out.println(ea.getMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (subject.isAuthenticated()) {
				String role = "common user";
				subject.checkRole(role);
				System.out.println("role: " + role);
			}
		} catch (AuthorizationException eo) {
			System.out.println(eo.getMessage());
		}

		try {
			if (subject.isAuthenticated()) {
				String permission = "select";
				subject.checkPermission(permission);
				System.out.println(permission + " permitted");
			}
		} catch (AuthorizationException eo) {
			System.out.println(eo.getMessage());
		}

		dataSource.close();
	}
}
