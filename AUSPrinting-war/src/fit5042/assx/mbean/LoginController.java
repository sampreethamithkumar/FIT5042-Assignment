package fit5042.assx.mbean;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import fit5042.assx.entities.Users;
import fit5042.assx.repository.UserService;


/**
 * 
 * @author sampreeth
 * Reference https://stackoverflow.com/questions/3841361/jsf-http-session-login
 */
@ManagedBean
@RequestScoped
public class LoginController implements Serializable{

	private Users user;
	private Principal principal;
	

    @EJB
    private UserService userService;

    public Users getUser() {
    	if (user == null) {
            principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
            if (principal != null) {
            	System.out.println(principal.getName());
                user = userService.find(principal.getName()); // Find User by j_username.
            }
        }
        return user;
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml";
    }
    
//    public Sting logout() {
//    	if (principal != null) {    		
//    		subject.getPrincipals().remove(principal);
////    		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
//    	}
//    	return "../../";
//    }


}
