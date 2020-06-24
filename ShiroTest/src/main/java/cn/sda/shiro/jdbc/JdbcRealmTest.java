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
		// 权限数据从数据库获取
		JdbcRealm jdbcRealm = new JdbcRealm();
		// 设置jdbcrealm的数据源
		jdbcRealm.setDataSource(dataSource);
		// 启用到数据库查询权限数据（默认值：false）
		jdbcRealm.setPermissionsLookupEnabled(true);

		// 使用自定义SQL
		// 等价于默认 protected static final String DEFAULT_AUTHENTICATION_QUERY = "select
		// password from users where username = ?";
		String sql = "select password from users where username = ?";
		jdbcRealm.setAuthenticationQuery(sql); // 改变login默认sql

		// 等价于默认 protected static final String DEFAULT_USER_ROLES_QUERY = "select
		// role_name from user_roles where username = ?";
		String roleSql = "select role_name from user_roles where username = ?";
		jdbcRealm.setUserRolesQuery(roleSql); // 改变checkRole默认sql

		// 等价于默认 protected static final String DEFAULT_PERMISSIONS_QUERY = "select
		// permission from roles_permissions where role_name = ?";
		String persionSql = "select permission from roles_permissions where role_name = ?";
		jdbcRealm.setPermissionsQuery(persionSql); // 改变checkPermission默认sql

		// 构建 SecurityManager
		DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
		defaultSecurityManager.setRealm(jdbcRealm);

		// 主体提交认证请求
		SecurityUtils.setSecurityManager(defaultSecurityManager);
		Subject subject = SecurityUtils.getSubject();

		try {
////			加密
			String password = "abcde";
	        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
	        int times = 2;
	        String algorithmName = "md5";
	        String encodedPassword = new SimpleHash(algorithmName,password,salt,times).toString();
	        System.out.printf("原始密码是 %s , 盐是： %s, 运算次数是： %d, 运算出来的密文是：%s ",password,salt,times,encodedPassword);

			//读取数据库中用户的password_salt
	        Connection conn = dataSource.getConnection();
			PreparedStatement preSql = conn.prepareStatement("select password_salt from users where username = ?");
			preSql.setString(1,  "li4");
			ResultSet rs = preSql.executeQuery();
			if (rs.next()) {
				salt = rs.getString(1);
			}
			//根据用户输入的密码和读取出来的salt重新计算密码
			encodedPassword = new SimpleHash(algorithmName,"abcde",salt,times).toString();
			//根据计算后的密码去数据库验证
			UsernamePasswordToken token = new UsernamePasswordToken("li4", encodedPassword.toCharArray());
			subject.login(token);
			if (subject.isAuthenticated()) {
				// 是否认证
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
