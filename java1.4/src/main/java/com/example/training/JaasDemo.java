package com.example.training;

import java.security.PrivilegedAction;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

/**
 * This works only if you are in Active Directory. Otherwise install manually Kerberos
 */
public class JaasDemo {

  public static void main(String[] a) throws Exception {
    System.setProperty("java.security.auth.login.config", "login.conf");
    LoginContext ctx = new LoginContext("MyApp");
    ctx.login();
    Subject subj = ctx.getSubject();

    final Subject subjFinal = subj;
    Subject.doAs(subj, new PrivilegedAction() {
      public Object run() {
        // resurse protejate (ex. acces la fișiere/rețea conform Policy)
        System.out.println("User autenticat: " + subjFinal.getPrincipals());
        return null;
      }
    });
    ctx.logout();
  }
}
