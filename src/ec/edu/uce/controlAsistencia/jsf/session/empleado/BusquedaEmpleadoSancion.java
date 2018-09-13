package ec.edu.uce.controlAsistencia.jsf.session.empleado;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ec.edu.uce.controlAsistencia.ejb.datos.DetallePuestoDto;
import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PersonaServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.*;



@ManagedBean(name="busquedaEmpleadoSancion")
@SessionScoped
public class BusquedaEmpleadoSancion implements Serializable{

	
	private static final long serialVersionUID = 1L;
	

	/**
	 * Declarar variables
	 */
	
	private String txtBusquedaEmpleado;
	private List<PersonaDto> lstPersona= new ArrayList<>();
	private PersonaDto  seleccionPersona;
	private List<DetallePuestoDto> lstDetallePuesto= new ArrayList<>();
	private DetallePuestoDto seleccionDetallePuesto;
	
	
	
	@EJB
	private DetallePuestoServicio  srvDetallePuesto;
	
	@EJB
	private PersonaServicio  srvPersona;
	
	@EJB
	private DependenciaServicio  srvDependencia;
	@PostConstruct
	public  void init()   {
		
	}
	
	public BusquedaEmpleadoSancion() {
		// TODO Auto-generated constructor stub
	}


	

	public List<PersonaDto> getLstPersona() {
		return lstPersona;
	}

	public void setLstPersona(List<PersonaDto> lstPersona) {
		this.lstPersona = lstPersona;
	}

	public PersonaDto getSeleccionPersona() {
		return seleccionPersona;
	}

	public void setSeleccionPersona(PersonaDto seleccionPersona) {
		this.seleccionPersona = seleccionPersona;
	}

	public String getTxtBusquedaEmpleado() {
		return txtBusquedaEmpleado;
	}



	public void setTxtBusquedaEmpleado(String txtBusquedaEmpleado) {
		this.txtBusquedaEmpleado = txtBusquedaEmpleado;
	}



	public List<DetallePuestoDto> getLstDetallePuesto() {
		return lstDetallePuesto;
	}



	public void setLstDetallePuesto(List<DetallePuestoDto> lstDetallePuesto) {
		this.lstDetallePuesto = lstDetallePuesto;
	}



	public DetallePuestoDto getSeleccionDetallePuesto() {
		return seleccionDetallePuesto;
	}



	public void setSeleccionDetallePuesto(DetallePuestoDto seleccionDetallePuesto) {
		this.seleccionDetallePuesto = seleccionDetallePuesto;
	}

	/**
	 * ============METODOS=====================================
	 */
	
	public void buscarEmpleado(){
		 if(!txtBusquedaEmpleado.equals("")){
			 List<PersonaDto> listEmpleados=  srvPersona.BuscarPorNombres(txtBusquedaEmpleado);
			 if(listEmpleados != null){
				 lstPersona=listEmpleados;
				 if(listEmpleados.size()==0) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Información.", "No se encontro el funcionario "));
				 }	 
			
			 }
			 else {
				 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Información.", "No se encontro el funcionario "));
			 }
			
		 }   
	}
	public String ObtenerNombreDependencia(int dpnId){
		String Nombre="";
		Nombre=srvDependencia.ObtenerPorId(dpnId).getDpnDescripcion();
		return Nombre;
		
	}
}
                      