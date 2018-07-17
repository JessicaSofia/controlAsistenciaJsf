package ec.edu.uce.controlAsistencia.jsf.session.sanciones;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ec.edu.uce.controlAsistencia.ejb.datos.Estados;
import ec.edu.uce.controlAsistencia.ejb.datos.Faltas;
import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.ContratoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.FichaEmpleadoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PersonaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.SancionesServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Categoria;
import ec.edu.uce.controlAsistencia.jpa.entidades.CategoriaFalta;
import ec.edu.uce.controlAsistencia.jpa.entidades.Dependencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuestoSancion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Falta;
import ec.edu.uce.controlAsistencia.jpa.entidades.FichaEmpleado;
import ec.edu.uce.controlAsistencia.jpa.entidades.Puesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.Sancion;
import ec.edu.uce.controlAsistencia.jpa.entidades.TipoLicencia;

@ManagedBean(name="sancionForm")
@SessionScoped
public class SancionForm implements   Serializable{

	/**
	 * VARIABLES
	 */
	private static final long serialVersionUID = 1L;

	private List<DetallePuestoSancion>  lstSanciones = new ArrayList<>();
	private DetallePuestoSancion  seleccionDtSancion=null;
	private PersonaDto seleccionPersona;
	private Dependencia dependencia;
	private Puesto puesto = null;
	private Regimen regimen = null;
	private  Falta falta;
	private String tipoFalta;
	private List<Falta> listaFaltas=null;
	private Sancion sancion;
	private Map<String, String> tipoFaltas;
	private CategoriaFalta categoriaFaltaAplicar;

	private DetallePuesto detallePuesto;
	private DetallePuestoSancion dtSancion=new DetallePuestoSancion();
	private boolean esActualizacion = false; 
	private List<Sancion> lstTipSanciones = null;
	private String tipoSancion;
	private int faltaId;
	private Map<String, String> tiposSanciones;
	private TipoLicencia tipoSancionEntidad;
	private boolean EsDescuento=false;
	
	@PostConstruct
	public void init() {
	this.listaFaltas = srvSanciones.listarFalta();
	this.tipoFaltas = new LinkedHashMap<>();
	this.listaFaltas.forEach((tipoFaltaEach) -> {
		tipoFaltas.put(tipoFaltaEach.getFlNombre(),String.valueOf(tipoFaltaEach.getFlId()));
	});
	
	this.lstTipSanciones= srvSanciones.listarSancion();
	this.tiposSanciones = new LinkedHashMap<>();
	this.lstTipSanciones.forEach((tipoSancionEach) -> {
		tiposSanciones.put(tipoSancionEach.getSnNombre(), String.valueOf(tipoSancionEach.getSnId()));
	});
	

		
	}
	
	/**
	 * Declaracion  de Servicios
	 * 
	 */
	@EJB
	private DependenciaServicio srvDependencia; 
	@EJB
	private RegimenServicio srvRegimen;
	@EJB
	private PuestoServicio srvPuesto;
	
	@EJB
	private DetallePuestoServicio srvDetallePuesto;
	
	@EJB
	private ContratoServicio srvContrato;
	
	@EJB
	private  PersonaServicio srvPersona;
	
	@EJB
	private SancionesServicio srvSanciones;
	
	@EJB
	private FichaEmpleadoServicio  srvFichaEmpleado;
	
	
	public List<DetallePuestoSancion> getLstSanciones() {
		lstSanciones=srvSanciones.listarSancionPorDetallePuestoId(seleccionPersona.getDtpsId());
		
		return lstSanciones;
	}
	public void setLstSanciones(List<DetallePuestoSancion> lstSanciones) {
		this.lstSanciones = lstSanciones;
	}
	public DetallePuestoSancion getSeleccionDtSancion() {
		return seleccionDtSancion;
	}
	
	
	public CategoriaFalta getCategoriaFaltaAplicar() {
		return categoriaFaltaAplicar;
	}
	public void setCategoriaFaltaAplicar(CategoriaFalta categoriaFaltaAplicar) {
		this.categoriaFaltaAplicar = categoriaFaltaAplicar;
	}
	public PersonaDto getSeleccionPersona() {
		return seleccionPersona;
	}


	public void setSeleccionPersona(PersonaDto seleccionPersona) {
		this.seleccionPersona = seleccionPersona;
	}


	public void setSeleccionDtSancion(DetallePuestoSancion seleccionDtSancion) {
		this.seleccionDtSancion = seleccionDtSancion;
	}
	public Dependencia getDependencia() {
		if(seleccionPersona !=null) {
			dependencia =srvDependencia.ObtenerPorId(seleccionPersona.getDpnId());
			
		}
		
		return dependencia;
	}
	public void setDependencia(Dependencia dependencia) {
		this.dependencia = dependencia;
	}
	public Puesto getPuesto() {
		if(seleccionPersona !=null) {
			puesto =srvPuesto.BuscarPorId(seleccionPersona.getPstId());
		}
		return puesto;
	}
	public void setPuesto(Puesto puesto) {
		this.puesto = puesto;
	}
	public Regimen getRegimen() {
		if(seleccionPersona !=null) {
			regimen =srvRegimen.BuscarPorId(seleccionPersona.getRgmId());
		}
		return regimen;
	}
	public void setRegimen(Regimen regimen) {
		this.regimen = regimen;
	}
	public DetallePuesto getDetallePuesto() {
		return detallePuesto;
	}
	public void setDetallePuesto(DetallePuesto detallePuesto) {
		this.detallePuesto = detallePuesto;
	}
	public DetallePuestoSancion getDtSancion() {
		if(seleccionDtSancion!=null) {
			dtSancion=seleccionDtSancion;
			esActualizacion=true;
		}else {
			
			esActualizacion=false;
			dtSancion.setDtpssnNumaccion(generarNumAutorizacion());
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			dtSancion.setDtpssnFechaEmision(fechaEmision);
			dtSancion.setDtpssnDescontar(0);
		}
		return dtSancion;
	}
	public void setDtSancion(DetallePuestoSancion dtSancion) {
		this.dtSancion = dtSancion;
	}
	public boolean isEsActualizacion() {
		return esActualizacion;
	}
	public void setEsActualizacion(boolean esActualizacion) {
		this.esActualizacion = esActualizacion;
	}
	public List<Sancion> getLstTipSanciones() {
		if(lstTipSanciones==null) {
			lstTipSanciones=srvSanciones.listarSancion();
		}
		return lstTipSanciones;
	}
	public void setLstTipSanciones(List<Sancion> lstTipSanciones) {
		this.lstTipSanciones = lstTipSanciones;
	}
	public String getTipoSancion() {
		return tipoSancion;
	}
	public void setTipoSancion(String tipoSancion) {
		this.tipoSancion = tipoSancion;
	}
	public Map<String, String> getTiposSanciones() {
		return tiposSanciones;
	}
	public void setTiposSanciones(Map<String, String> tiposSanciones) {
		this.tiposSanciones = tiposSanciones;
	}
	public TipoLicencia getTipoSancionEntidad() {
		return tipoSancionEntidad;
	}
	public void setTipoSancionEntidad(TipoLicencia tipoSancionEntidad) {
		this.tipoSancionEntidad = tipoSancionEntidad;
	}
	
	public Falta getFalta() {
		return falta;
	}
	public void setFalta(Falta falta) {
		this.falta = falta;
	}
	
	public Sancion getSancion() {
		return sancion;
	}
	public void setSancion(Sancion sancion) {
		this.sancion = sancion;
	}
	public List<Falta> getListaFaltas() {
		if(listaFaltas==null) {
			listaFaltas=srvSanciones.listarFalta();
		}
		return listaFaltas;
	}
	public void setListaFaltas(List<Falta> listaFaltas) {
		this.listaFaltas = listaFaltas;
	}
	
	
	public String getTipoFalta() {
		return tipoFalta;
	}
	public void setTipoFalta(String tipoFalta) {
		this.tipoFalta = tipoFalta;
	}
	public Map<String, String> getTipoFaltas() {
		return tipoFaltas;
	}
	public void setTipoFaltas(Map<String, String> tipoFaltas) {
		this.tipoFaltas = tipoFaltas;
		
	}
	
	public int getFaltaId() {
		return faltaId;
	}
	public void setFaltaId(int faltaId) {
		this.faltaId = faltaId;
	}
	
	
	public boolean isEsDescuento() {
		if(dtSancion.getDtpssnDescontar()==1) {
			EsDescuento=true;
		}else {
			EsDescuento=false;
		}
		return EsDescuento;
	}
	public void setEsDescuento(boolean esDescuento) {
		EsDescuento = esDescuento;
	}
	/**
	 * Metodos
	 */
	
	public void limpiarFormSancion() {
		seleccionDtSancion=null;
		dtSancion =  new DetallePuestoSancion();
		tipoSancion="";
		tipoFalta="";
		
	}
	
	
	/**
	 * Metodo paara  generar el Numero De Autorizacion
	 * @return
	 */
	public int generarNumAutorizacion(){
		int numAutorizacion=0;
		if(srvSanciones.MaximaNumAutorizacion()!=0){
			numAutorizacion=srvSanciones.MaximaNumAutorizacion()+1;
		}else{
			numAutorizacion=1;
		}
		return numAutorizacion;
	} 
	
	public void calcularSancion() {
		 
		String txtDias=dtSancion.getDtpssnDias();
		float valor=0;
		int min=dtSancion.getDtpssnMinutos();
		
		String[] Dias  = txtDias.split("-");
		int frecuencia=Dias.length;
		
		FichaEmpleado empleado= srvFichaEmpleado.BuscarPorid(seleccionPersona.getFcemId());
		falta=srvSanciones.ObtenerFaltaPorI(Integer.parseInt(tipoFalta));
		 categoriaFaltaAplicar= obtenerCategoriaFaltaPorParametros(empleado.getCategoria().getCtgId(), falta.getFlId() , min);
		
		if(categoriaFaltaAplicar==null) {
			if(Faltas.Atrasos.getId()==falta.getFlId()) {
				categoriaFaltaAplicar= obtenerCategoriaFaltaPorParametros(empleado.getCategoria().getCtgId(), Faltas.AbandonodeTrabajo.getId() , min);
				
			}
		}
		dtSancion.setDtpssnFrecuencia(frecuencia);
		  int idTiposancion=categoriaFaltaAplicar.getTipoSancion().getTpsnId();
		  if(idTiposancion==1) {
			valor=dtSancion.getDtpssnMinutos()*categoriaFaltaAplicar.getCtgflPorcentajeBase();
			EsDescuento=true;
			
		  }
		  else {
			  EsDescuento =false;
					  
		  }
		  

			dtSancion.setDtpssnValor(valor);
			
			 sancion  = obtenerSancionAplicar(categoriaFaltaAplicar.getTipoSancion().getTpsnId());

			 this.tipoSancion=String.valueOf(sancion.getSnId());
			 
				}
	
	public CategoriaFalta obtenerCategoriaFaltaPorParametros(int ctgId, int flId , int min) {
		CategoriaFalta categoriaFalta= null;
		List<CategoriaFalta> parametros= srvSanciones.listarcategoriaFaltaPorCategoriaIdFaltaId(ctgId,flId);
		for(CategoriaFalta s: parametros) {
			if(s.getCtgflMinuntosMin()!=-1 && s.getCtgflMinutosMax()!=-1) {

				if(s.getCtgflMinuntosMin()<=min && min <=s.getCtgflMinutosMax()) {
					categoriaFalta=s;
					break;
				}
			}
			else  {
				

				if(s.getCtgflMinuntosMin()!=-1 && s.getCtgflMinutosMax()==-1 ) {
					if( min>s.getCtgflMinuntosMin()) {
					categoriaFalta=s;	
				}
		}
			}
		}
		
		return categoriaFalta;
		
	}
	
	public  Sancion  obtenerSancionAplicar(int tpSnId) {
		Sancion retorno =null;
		int nivel=1;
		List<Sancion> sanciones= new ArrayList<>();
		sanciones = srvSanciones.ObtenerLstSancionPorTipoSancionId(tpSnId);
		for(Sancion s:sanciones) {
			if(s.getSnNivel()==nivel) {
				retorno=s;
			}
		}
		return retorno;
		
	}

	public void guardarSancion() {
		
		 if(EsDescuento) {
			 dtSancion.setDtpssnDescontar(1);
		 }
		 else {

			 dtSancion.setDtpssnDescontar(0);
		 }
		
		
		 detallePuesto=srvDetallePuesto.DetallePuestoBuscarPorId(seleccionPersona.getDtpsId());
		 dtSancion.setCategoriaFalta(categoriaFaltaAplicar);
		 dtSancion.setDtpssnEstado(Estados.Activo.getId());
		 dtSancion.setSancion(sancion);
		 dtSancion.setDetallePuesto(detallePuesto); 
		 srvSanciones.insertaSancion(dtSancion);
		 limpiarFormSancion();

	}

	public void anularSancion() {
		dtSancion.setDtpssnEstado(Estados.Anulado.getId());
		srvSanciones .insertaSancion(dtSancion);
	}
	
	public void regresar() {
		
	}
	
}
