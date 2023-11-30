package ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class ldapConnection {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// ad server 세팅 값 가져오기 
		String PROVIDER_URL = 				"ldap://192.168.227.130:389"; 													// ldap 주소값 ex) ldap://192.168.1.199:389
		String SECURITY_PRINCIPAL = 		"cn=administrator,cn=Users,dc=test,dc=com";										// ldap 서버 계정  ex) cn=admin,cn=Users,dc=manageinf,dc=com
		String SECURITY_CREDENTIALS = 		"choi12#";																		// ldap 서버 계정 비밀번호
		
		String TOP_DEPARTMENT = 			"OU=Sales,OU=Test Company,DC=test,DC=com|OU=Ceo,OU=Test Company,DC=test,DC=com|OU=Develop,OU=Test Company,DC=test,DC=com"; // ldap 에서 가져올 최상위 부서 여러개라면 |를 사용하여 구분
		//String DC = 						"dc=manageinf,dc=com";															// ldap 의 도메인 
				
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
		
		// 부서 정보 가져오기 테스트 
		List<HashMap<String, String>> getDeptInfo = ldapOrganization.getLdapDept(PROVIDER_URL, SECURITY_PRINCIPAL, SECURITY_CREDENTIALS, TOP_DEPARTMENT, getDept_Code, getDept_Name);
		System.out.println(getDeptInfo);
		
		
	}
	
	/**
	 * ldap 연결 테스트 
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
	
	/**
	 * ldap에서 설정한 부서정보 가져오는 메서드 
	 * @param PROVIDER_URL ldap 주소값 ex) ldap://192.168.11.111:389
	 * @param SECURITY_PRINCIPAL ldap 서버 계정  ex) cn=admin,cn=Users,dc=testCompany,dc=com
	 * @param SECURITY_CREDENTIALS ldap 서버 계정 비밀번호 
	 * @param TOP_DEPARTMENT 최상위 부서 ex) ou=Sales,dc=testCompany,dc=com|ou=Develop,dc=testCompany,dc=com
	 * @param deptCode 부서 코드로 설정된 옵션 이름 
	 * @param deptName 부서 이름으로 설정된 옵션  이름
	 * @return
	 */
	public List<HashMap<String, String>> getLdapDept(String PROVIDER_URL, String SECURITY_PRINCIPAL, String SECURITY_CREDENTIALS, String TOP_DEPARTMENT, String getDept_Code, String getDept_Name) {
        Hashtable<String, Object> env = new Hashtable<>();   // 연결 정보 설정
        List<HashMap<String, String>> deptList = new ArrayList<HashMap<String,String>>();
        
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

            /*
             * 	검색 제약 조건 설정
				SUBTREE_SCOPE : 기본 엔트리에서 시작하여 기본 엔트리 및 이 및에 있는 모든 것을 검색한다.  이것은 가장 느리고 가장 비싼 검색이다.
				ONELEVEL_SCOPE : 기본 엔트리 밑에 있는 엔트리들을 검색한다.
				OBJECT_SCOPE : 기본 엔트리만 검색한다. 가장 빠르고 저렴한 검색이다.   
             */
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            //controls.setReturningAttributes(new String[] { "cn"});
            
            // LDAP 검색 수행            
            // 조직에 포함된 전체 사용자 가져오기 
            /*
             * ctx.search() 메서드는 LDAP 서버에서 데이터를 검색하는 메서드입니다. 이 메서드는 SearchControls 객체를 사용하여 검색 옵션을 설정하고, 검색할 범위와 검색 조건을 지정한 Filter 객체를 전달             
             *  search() 메서드에서 두번 째로 받는 값인 objectClass 속성의 값들 
				top: 모든 객체 클래스의 상위 클래스입니다.
				person: 일반적인 사람을 나타내며, 주로 일반 사용자 계정에 사용됩니다.
				organizationalPerson: 기관의 일반적인 사람을 나타냅니다. 주로 이름, 이메일 주소, 전화번호 등과 같은 연락처 정보를 포함합니다.
				user: 일반적인 사용자 계정을 나타냅니다.
				group: 일반적인 그룹을 나타냅니다.
				organizationalUnit: 조직의 일반적인 단위를 나타냅니다. 주로 사용자 및 그룹과 같은 하위 항목을 포함합니다.
				domain: DNS 도메인을 나타냅니다.
				computer: 컴퓨터 계정을 나타냅니다.
				groupOfNames: 이름 목록 그룹을 나타냅니다.
				groupOfUniqueNames: 고유 이름 목록 그룹을 나타냅니다.
             */
            String[] TOP_DEPARTMENTArray = TOP_DEPARTMENT.split("\\|");
            
            for(int aa=0; aa<TOP_DEPARTMENTArray.length; aa++) {
            	String TOP_DEPARTMENTItem = TOP_DEPARTMENTArray[aa];
            	
	            NamingEnumeration<SearchResult> deptResults = ctx.search(TOP_DEPARTMENTItem, "(objectClass=organizationalUnit)", controls);
	            LinkedHashMap<String, String>deptCodeNameMap = new LinkedHashMap<>();
	            
	            while (deptResults.hasMore()) {
	                SearchResult searchResult = deptResults.next();
	                Attributes attrs = searchResult.getAttributes();     
	                String deptCode = null;
	                String deptName = null;
	                
	                // 부서 코드 , 부서 이름 맵핑               	
	                try {
	                	deptCode = attrs.get(getDept_Code).toString();  // 부서 코드
	                	deptCode = deptCode.replaceAll(getDept_Code+":", "").trim(); 
	                	
	                }catch(Exception e) {
	                	System.out.println(e.getMessage());
	                	continue;
	                }	
	                try {
	                	deptName = attrs.get(getDept_Name).toString();  // 부서 이름
	                	deptName = deptName.replaceAll(getDept_Name+":", "").trim();      
	                }catch(Exception e) {	                	
	                	System.out.println(e.getMessage());
	                	deptName = "";
	                	continue;
	                }
	                
                	if(!deptCode.trim().equals("") && !deptName.trim().equals("")) {
                		deptCodeNameMap.put(deptName,deptCode);                 		
                	}	               	                	                	
	            }

	            // 부서코드,부서이름,상위부서 List 정리 
	            NamingEnumeration<SearchResult> deptResults2 = ctx.search(TOP_DEPARTMENTItem, "(objectClass=organizationalUnit)", controls);
	            while (deptResults2.hasMore()) {
	            	try {
		                SearchResult searchResult = deptResults2.next();
		                Attributes attrs = searchResult.getAttributes();
		                
		                String distinguishedname = attrs.get("distinguishedname").get().toString(); // ex)받아오는 결과가 이런형식 -> OU=PM,OU=DLP Business,OU=Manage,DC=manageinf,DC=com
		                
		                String[] arrayDistinguishedname = distinguishedname.split(",");    	                
		                
		                String deptName = arrayDistinguishedname[0].toString().replaceAll("OU=", "").trim(); // 부서 이름         	                		                
		                String deptCode = deptCodeNameMap.get(deptName).toString();// 부서 코드	                		                
		                String upperDeptName = arrayDistinguishedname[1].toString(); // 상위 부서 이름
		                
		                if(!upperDeptName.contains("OU=")) {
		                    upperDeptName="";
		                }else {
		                    upperDeptName = upperDeptName.replaceAll("OU=", "");
		                }
		                
		                String upperDeptCode = ""; // 상위 부서 코드 
		                try {
		                    upperDeptCode = deptCodeNameMap.get(upperDeptName).toString();
		                }catch (Exception e) {
		                	upperDeptCode = "";
						}
		                
		                if(!deptCode.equals("noneDeptCode")) {
			                HashMap<String, String> hashmap = new HashMap<String, String>();
			                hashmap.put("DEPT_ID", deptCode);  // 부서코드
			                hashmap.put("DEPT_NAME", deptName); //부서이름
			                hashmap.put("UP_DEPT_ID", upperDeptCode); //상위부서코드
			                hashmap.put("DEPT_ORDER", "1");
			                hashmap.put("DEPT_FG", "Y");
			                hashmap.put("DEPT_USERID", null); // 부서장 ID   
			                
			                deptList.add(hashmap);
		                }
	            	}catch (Exception e) {
						continue; // 부서 list 정리 중 문제 있는 부분은 넘어가게 예외처리 
					}		        
	            }                        	            
            }
            System.out.println("Get Dept Information done.");
            
        } catch (Exception e) {
        	e.printStackTrace();        	
            return null;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close(); // Adserver와 연결 종료
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
		return deptList;
	}	
}
