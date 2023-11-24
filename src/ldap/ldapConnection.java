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
		
		// ad server 세팅 값 가져오기 
		String PROVIDER_URL = 				"ldap://192.168.227.130:389"; 													// ldap 주소값 ex) ldap://192.168.1.199:389
		String SECURITY_PRINCIPAL = 		"cn=administrator,cn=Users,dc=test,dc=com";										// ldap 서버 계정  ex) cn=admin,cn=Users,dc=manageinf,dc=com
		String SECURITY_CREDENTIALS = 		"choi12#";																		// ldap 서버 계정 비밀번호
		
		String TOP_DEPARTMENT = 			"OU=Sales,OU=Test Company,DC=test,DC=com|OU=Ceo,OU=Test Company,DC=test,DC=com|OU=Develop,OU=Test Company,DC=test,DC=com"; // ldap 에서 가져올 최상위 부서 여러개라면 |를 사용하여 구분
		String DC = 						"dc=manageinf,dc=com";															// ldap 의 도메인 
				
		String getDept_Code = "ou";			// Active Directory에 부서코드로 설정된 특성(attribute)
		String getDept_Name = "ou";			// Active Directory에 부서이름으로 설정된 특성(attribute)
		
		ldapConnection ldapOrganization = new ldapConnection();
				
		// Ldap과 연결 테스트
		Boolean connectionTest = ldapOrganization.ldapConnectTest(PROVIDER_URL,SECURITY_PRINCIPAL, SECURITY_CREDENTIALS);
		if(connectionTest == true) {
			System.out.println(PROVIDER_URL+" 연결 성공");
		}else {
			System.out.println(PROVIDER_URL+" 연결 실패");
		}				
	}
	
	/**
	 * 연결 테스트 
	 * @param PROVIDER_URL
	 * @param SECURITY_PRINCIPAL
	 * @param SECURITY_CREDENTIALS
	 * @param TOP_DEPARTMENT
	 * @return true(연결성공),false(연결실패)
	 */
	public Boolean ldapConnectTest(String PROVIDER_URL, String SECURITY_PRINCIPAL, String SECURITY_CREDENTIALS) {
		
        // 연결 정보 설정
        Hashtable<String, Object> env = new Hashtable<>();        
        /*
         * JNDI(Java Naming and Directory Interface) API를 사용하여 다양한 네트워크 서비스에 연결할 때, 해당 서비스의 구현 클래스를 지정하는 속성 
         * LDAP 서비스에 연결하기 위해서는 LDAP 서버의 구현 클래스인 LdapCtxFactory를 지정해야함
         */
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");       
        /*
         * Context.PROVIDER_URL은 LDAP 서버의 주소를 지정하는 속성입니다. 
         * LDAP 프로토콜을 사용하여 LDAP 서버에 연결할 때, 이 속성에 LDAP 서버의 주소를 설정
         * LDAP의 기본 포트번호는 389
         */
        env.put(Context.PROVIDER_URL, PROVIDER_URL); // "ldap://localhost:389/dc=example,dc=com"        
        /*
         * LDAP 서버에 연결할 때 사용하는 인증 방식을 설정하는 속성
         * none: 인증 없이 연결을 시도합니다. 
         * simple: 사용자 이름과 비밀번호를 이용하여 간단한 인증을 시도합니다.
         * DIGEST-MD5: MD5 기반의 다이제스트 인증 방식을 이용하여 인증을 시도합니다.
         */
        env.put(Context.SECURITY_AUTHENTICATION, "simple");                
        /*
         * Context.SECURITY_PRINCIPAL은 LDAP 서버에 로그인하기 위한 사용자의 DN(Distinguished Name)을 지정하는 속성
         */
        env.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL); // "cn=admin,dc=example,dc=com"
        env.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);
        DirContext ctx = null;

        try {
            // LDAP 서버에 연결
            ctx = new InitialDirContext(env);            
        }catch(Exception e) {
        	return false;
        }
		
		return true;
		
	}		
}
