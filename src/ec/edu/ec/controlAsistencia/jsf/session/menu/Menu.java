package ec.edu.ec.controlAsistencia.jsf.session.menu;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ec.edu.uce.controlAsistencia.ejb.datos.DetallePuestoDto;
import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.FichaEmpleadoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.GrupoOcupacionalServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Dependencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.FichaEmpleado;
import ec.edu.uce.controlAsistencia.jpa.entidades.GrupoOcupacional;
import ec.edu.uce.controlAsistencia.jpa.entidades.Puesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.SaldoVacacion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Vacacion;

@ManagedBean(name = "registrosVacacion")
@SessionScoped
public class Menu implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void irPagina(int opcion){
		if(opcion == 1){
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("/controlAsistencia/vacaciones/VacacionesRegistros.xhtml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
