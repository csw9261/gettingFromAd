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
		
		// ad server ���� �� �������� 
		String PROVIDER_URL = 				"ldap://192.168.227.130:389"; 													// ldap �ּҰ� ex) ldap://192.168.1.199:389
		String SECURITY_PRINCIPAL = 		"cn=administrator,cn=Users,dc=test,dc=com";										// ldap ���� ����  ex) cn=admin,cn=Users,dc=manageinf,dc=com
		String SECURITY_CREDENTIALS = 		"choi12#";																		// ldap ���� ���� ��й�ȣ
		
		String TOP_DEPARTMENT = 			"OU=Sales,OU=Test Company,DC=test,DC=com|OU=Ceo,OU=Test Company,DC=test,DC=com|OU=Develop,OU=Test Company,DC=test,DC=com"; // ldap ���� ������ �ֻ��� �μ� ��������� |�� ����Ͽ� ����
		//String DC = 						"dc=manageinf,dc=com";															// ldap �� ������ 
				
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
		
		// �μ� ���� �������� �׽�Ʈ 
		List<HashMap<String, String>> getDeptInfo = ldapOrganization.getLdapDept(PROVIDER_URL, SECURITY_PRINCIPAL, SECURITY_CREDENTIALS, TOP_DEPARTMENT, getDept_Code, getDept_Name);
		System.out.println(getDeptInfo);
		
		
	}
	
	/**
	 * ldap ���� �׽�Ʈ 
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
	
	/**
	 * ldap���� ������ �μ����� �������� �޼��� 
	 * @param PROVIDER_URL ldap �ּҰ� ex) ldap://192.168.11.111:389
	 * @param SECURITY_PRINCIPAL ldap ���� ����  ex) cn=admin,cn=Users,dc=testCompany,dc=com
	 * @param SECURITY_CREDENTIALS ldap ���� ���� ��й�ȣ 
	 * @param TOP_DEPARTMENT �ֻ��� �μ� ex) ou=Sales,dc=testCompany,dc=com|ou=Develop,dc=testCompany,dc=com
	 * @param deptCode �μ� �ڵ�� ������ �ɼ� �̸� 
	 * @param deptName �μ� �̸����� ������ �ɼ�  �̸�
	 * @return
	 */
	public List<HashMap<String, String>> getLdapDept(String PROVIDER_URL, String SECURITY_PRINCIPAL, String SECURITY_CREDENTIALS, String TOP_DEPARTMENT, String getDept_Code, String getDept_Name) {
        Hashtable<String, Object> env = new Hashtable<>();   // ���� ���� ����
        List<HashMap<String, String>> deptList = new ArrayList<HashMap<String,String>>();
        
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

            /*
             * 	�˻� ���� ���� ����
				SUBTREE_SCOPE : �⺻ ��Ʈ������ �����Ͽ� �⺻ ��Ʈ�� �� �� �׿� �ִ� ��� ���� �˻��Ѵ�.  �̰��� ���� ������ ���� ��� �˻��̴�.
				ONELEVEL_SCOPE : �⺻ ��Ʈ�� �ؿ� �ִ� ��Ʈ������ �˻��Ѵ�.
				OBJECT_SCOPE : �⺻ ��Ʈ���� �˻��Ѵ�. ���� ������ ������ �˻��̴�.   
             */
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            //controls.setReturningAttributes(new String[] { "cn"});
            
            // LDAP �˻� ����            
            // ������ ���Ե� ��ü ����� �������� 
            /*
             * ctx.search() �޼���� LDAP �������� �����͸� �˻��ϴ� �޼����Դϴ�. �� �޼���� SearchControls ��ü�� ����Ͽ� �˻� �ɼ��� �����ϰ�, �˻��� ������ �˻� ������ ������ Filter ��ü�� ����             
             *  search() �޼��忡�� �ι� °�� �޴� ���� objectClass �Ӽ��� ���� 
				top: ��� ��ü Ŭ������ ���� Ŭ�����Դϴ�.
				person: �Ϲ����� ����� ��Ÿ����, �ַ� �Ϲ� ����� ������ ���˴ϴ�.
				organizationalPerson: ����� �Ϲ����� ����� ��Ÿ���ϴ�. �ַ� �̸�, �̸��� �ּ�, ��ȭ��ȣ ��� ���� ����ó ������ �����մϴ�.
				user: �Ϲ����� ����� ������ ��Ÿ���ϴ�.
				group: �Ϲ����� �׷��� ��Ÿ���ϴ�.
				organizationalUnit: ������ �Ϲ����� ������ ��Ÿ���ϴ�. �ַ� ����� �� �׷�� ���� ���� �׸��� �����մϴ�.
				domain: DNS �������� ��Ÿ���ϴ�.
				computer: ��ǻ�� ������ ��Ÿ���ϴ�.
				groupOfNames: �̸� ��� �׷��� ��Ÿ���ϴ�.
				groupOfUniqueNames: ���� �̸� ��� �׷��� ��Ÿ���ϴ�.
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
	                
	                // �μ� �ڵ� , �μ� �̸� ����               	
	                try {
	                	deptCode = attrs.get(getDept_Code).toString();  // �μ� �ڵ�
	                	deptCode = deptCode.replaceAll(getDept_Code+":", "").trim(); 
	                	
	                }catch(Exception e) {
	                	System.out.println(e.getMessage());
	                	continue;
	                }	
	                try {
	                	deptName = attrs.get(getDept_Name).toString();  // �μ� �̸�
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

	            // �μ��ڵ�,�μ��̸�,�����μ� List ���� 
	            NamingEnumeration<SearchResult> deptResults2 = ctx.search(TOP_DEPARTMENTItem, "(objectClass=organizationalUnit)", controls);
	            while (deptResults2.hasMore()) {
	            	try {
		                SearchResult searchResult = deptResults2.next();
		                Attributes attrs = searchResult.getAttributes();
		                
		                String distinguishedname = attrs.get("distinguishedname").get().toString(); // ex)�޾ƿ��� ����� �̷����� -> OU=PM,OU=DLP Business,OU=Manage,DC=manageinf,DC=com
		                
		                String[] arrayDistinguishedname = distinguishedname.split(",");    	                
		                
		                String deptName = arrayDistinguishedname[0].toString().replaceAll("OU=", "").trim(); // �μ� �̸�         	                		                
		                String deptCode = deptCodeNameMap.get(deptName).toString();// �μ� �ڵ�	                		                
		                String upperDeptName = arrayDistinguishedname[1].toString(); // ���� �μ� �̸�
		                
		                if(!upperDeptName.contains("OU=")) {
		                    upperDeptName="";
		                }else {
		                    upperDeptName = upperDeptName.replaceAll("OU=", "");
		                }
		                
		                String upperDeptCode = ""; // ���� �μ� �ڵ� 
		                try {
		                    upperDeptCode = deptCodeNameMap.get(upperDeptName).toString();
		                }catch (Exception e) {
		                	upperDeptCode = "";
						}
		                
		                if(!deptCode.equals("noneDeptCode")) {
			                HashMap<String, String> hashmap = new HashMap<String, String>();
			                hashmap.put("DEPT_ID", deptCode);  // �μ��ڵ�
			                hashmap.put("DEPT_NAME", deptName); //�μ��̸�
			                hashmap.put("UP_DEPT_ID", upperDeptCode); //�����μ��ڵ�
			                hashmap.put("DEPT_ORDER", "1");
			                hashmap.put("DEPT_FG", "Y");
			                hashmap.put("DEPT_USERID", null); // �μ��� ID   
			                
			                deptList.add(hashmap);
		                }
	            	}catch (Exception e) {
						continue; // �μ� list ���� �� ���� �ִ� �κ��� �Ѿ�� ����ó�� 
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
                    ctx.close(); // Adserver�� ���� ����
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
		return deptList;
	}	
}
