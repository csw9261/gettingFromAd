package ldap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.PagedResultsControl;

public class ldapConnection {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// ad server ���� �� �������� 
		String PROVIDER_URL = 				"ldap://192.168.227.130:389"; 													// ldap �ּҰ� ex) ldap://192.168.1.199:389
		String SECURITY_PRINCIPAL = 		"cn=administrator,cn=Users,dc=test,dc=com";										// ldap ���� ����  ex) cn=admin,cn=Users,dc=manageinf,dc=com
		String SECURITY_CREDENTIALS = 		"choi12#";																		// ldap ���� ���� ��й�ȣ
		
		String TOP_DEPARTMENT = 			"OU=Sales,OU=Test Company,DC=test,DC=com|OU=Ceo,OU=Test Company,DC=test,DC=com|OU=Develop,OU=Test Company,DC=test,DC=com"; // ldap ���� ������ �ֻ��� �μ� ��������� |�� ����Ͽ� ����
		String DC = 						"dc=manageinf,dc=com";															// ldap �� ������ 
				
		String getDept_Code = "ou";			// Active Directory�� �μ��ڵ�� ������ Ư��(attribute)
		String getDept_Name = "ou";			// Active Directory�� �μ��̸����� ������ Ư��(attribute)
		
		ldapConnection ldapOrganization = new ldapConnection();
				
		// Ldap�� ���� �׽�Ʈ
		Boolean connectionTest = ldapOrganization.ldapConnectTest(PROVIDER_URL,SECURITY_PRINCIPAL, SECURITY_CREDENTIALS);
		if(connectionTest == true) {
			System.out.println(PROVIDER_URL+" ���� ����");
		}else {
			System.out.println(PROVIDER_URL+" ���� ����");
		}				
	}
	
	/**
	 * ���� �׽�Ʈ 
	 * @param PROVIDER_URL
	 * @param SECURITY_PRINCIPAL
	 * @param SECURITY_CREDENTIALS
	 * @param TOP_DEPARTMENT
	 * @return true(���Ἲ��),false(�������)
	 */
	public Boolean ldapConnectTest(String PROVIDER_URL, String SECURITY_PRINCIPAL, String SECURITY_CREDENTIALS) {
		
        // ���� ���� ����
        Hashtable<String, Object> env = new Hashtable<>();        
        /*
         * JNDI(Java Naming and Directory Interface) API�� ����Ͽ� �پ��� ��Ʈ��ũ ���񽺿� ������ ��, �ش� ������ ���� Ŭ������ �����ϴ� �Ӽ� 
         * LDAP ���񽺿� �����ϱ� ���ؼ��� LDAP ������ ���� Ŭ������ LdapCtxFactory�� �����ؾ���
         */
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");       
        /*
         * Context.PROVIDER_URL�� LDAP ������ �ּҸ� �����ϴ� �Ӽ��Դϴ�. 
         * LDAP ���������� ����Ͽ� LDAP ������ ������ ��, �� �Ӽ��� LDAP ������ �ּҸ� ����
         * LDAP�� �⺻ ��Ʈ��ȣ�� 389
         */
        env.put(Context.PROVIDER_URL, PROVIDER_URL); // "ldap://localhost:389/dc=example,dc=com"        
        /*
         * LDAP ������ ������ �� ����ϴ� ���� ����� �����ϴ� �Ӽ�
         * none: ���� ���� ������ �õ��մϴ�. 
         * simple: ����� �̸��� ��й�ȣ�� �̿��Ͽ� ������ ������ �õ��մϴ�.
         * DIGEST-MD5: MD5 ����� ��������Ʈ ���� ����� �̿��Ͽ� ������ �õ��մϴ�.
         */
        env.put(Context.SECURITY_AUTHENTICATION, "simple");                
        /*
         * Context.SECURITY_PRINCIPAL�� LDAP ������ �α����ϱ� ���� ������� DN(Distinguished Name)�� �����ϴ� �Ӽ�
         */
        env.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL); // "cn=admin,dc=example,dc=com"
        env.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);
        DirContext ctx = null;

        try {
            // LDAP ������ ����
            ctx = new InitialDirContext(env);            
        }catch(Exception e) {
        	return false;
        }
		
		return true;
		
	}		
}
