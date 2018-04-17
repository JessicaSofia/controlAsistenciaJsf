package ec.edu.uce.controlAsistencia.jsf.session.empleado;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.*;



@ManagedBean(name="busquedaEmpleado")
@SessionScoped
public class BusquedaEmpleado implements Serializable{

	
	private static final long serialVersionUID = 1L;
	

	/**
	 * Declarar variables
	 */
	
	private String txtBusquedaEmpleado;
	private List<DetallePuesto> lstDetallePuesto= new ArrayList<>();
	private DetallePuesto seleccionDetallePuesto;
	
	
	@EJB
	private DetallePuestoServicio  srvDetallePuesto;
	
	@PostConstruct
	public  void init()   {
		
	}
	
	public BusquedaEmpleado() {
		// TODO Auto-generated constructor stub
	}



	public String getTxtBusquedaEmpleado() {
		return txtBusquedaEmpleado;
	}



	public void setTxtBusquedaEmpleado(String txtBusquedaEmpleado) {
		this.txtBusquedaEmpleado = txtBusquedaEmpleado;
	}



	public List<DetallePuesto> getLstDetallePuesto() {
		return lstDetallePuesto;
	}



	public void setLstDetallePuesto(List<DetallePuesto> lstDetallePuesto) {
		this.lstDetallePuesto = lstDetallePuesto;
	}



	public DetallePuesto getSeleccionDetallePuesto() {
		return seleccionDetallePuesto;
	}



	public void setSeleccionDetallePuesto(DetallePuesto seleccionDetallePuesto) {
		this.seleccionDetallePuesto = seleccionDetallePuesto;
	}

	/**
	 * ============METODOS=====================================
	 */
	
	public void buscarEmpleado(){
		 if(!txtBusquedaEmpleado.equals("")){
			 List<DetallePuesto> listEmpleados=  srvDetallePuesto.BuscarLstPorNombres(txtBusquedaEmpleado);
			 if(listEmpleados != null){
				 lstDetallePuesto=listEmpleados;
			 }
			 else {
				 // tocar mandar el mensaje  no hay ese empleado
			 }
			
		 }
	}
	
}
