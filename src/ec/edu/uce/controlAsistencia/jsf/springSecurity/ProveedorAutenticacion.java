package ec.edu.uce.controlAsistencia.jsf.springSecurity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.UsuarioRolServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.UsuarioServicio;

public class ProveedorAutenticacion implements AuthenticationProvider, Serializable{
	
	
/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	
	private UsuarioServicio srvUsuarioEjb;
	private UsuarioRolServicio srvUsuarioRolEjb;
	
	
@Override
public Authentication authenticate(Authentication authentication) throws
AuthenticationException{
String username = authentication.getName();
String password = (String) authentication.getCredentials();


//verificacion de usuario y password
if(username.equalsIgnoreCase("administrador")){
if(!password.equals("123456")){
throw new BadCredentialsException("Usuario y/o Contraseña  incorrectos.");
}
}else{
throw new BadCredentialsException("Usuario y/o Contraseña incorrectos.");
}
//asignacion de permisos
List<ProveedorPermisos> permisos = new ArrayList<ProveedorPermisos>();
permisos.add(new ProveedorPermisos("ADMINISTRADOR"));
DetalleUsuario usuario = new DetalleUsuario(username, password , permisos);
return new UsernamePasswordAuthenticationToken(usuario, password, permisos);
}
@Override
public boolean supports(Class<?> arg0){
return true;
}

public UsuarioServicio getSrvUsuarioEjb() {
	return srvUsuarioEjb;
}
public void setSrvUsuarioEjb(UsuarioServicio srvUsuarioEjb) {
	this.srvUsuarioEjb = srvUsuarioEjb;
}
public UsuarioRolServicio getSrvUsuarioRolEjb() {
	return srvUsuarioRolEjb;
}
public void setSrvUsuarioRolEjb(UsuarioRolServicio srvUsuarioRolEjb) {
	this.srvUsuarioRolEjb = srvUsuarioRolEjb;
}


}


