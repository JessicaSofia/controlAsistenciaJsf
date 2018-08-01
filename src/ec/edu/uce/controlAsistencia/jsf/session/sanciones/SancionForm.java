package ec.edu.uce.controlAsistencia.jsf.session.sanciones;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;


import ec.edu.uce.controlAsistencia.ejb.datos.Estados;

import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.ContratoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.FichaEmpleadoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.GrupoOcupacionalServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PersonaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.SancionesServicio;

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
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;

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
	private  boolean esPorFrecuencia=false;
	private Date fecha;
	
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
	
	@EJB
	private GrupoOcupacionalServicio srvGrupoOc;
	
	
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
			puesto=srvPuesto.BuscarPorId(seleccionPersona.getPstId());
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
			cargarSancionFormEdit();
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
	
	
	public boolean isEsPorFrecuencia() {
		return esPorFrecuencia;
	}
	public void setEsPorFrecuencia(boolean esPorFrecuencia) {
		this.esPorFrecuencia = esPorFrecuencia;
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
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
		
		Calendar c=Calendar.getInstance();
		c.setTime(fecha);
		dtSancion.setDtpssnAno(c.get(Calendar.YEAR));
		dtSancion.setDtpssnMes(Calendar.MONTH);
		
		 
 		String txtDias=dtSancion.getDtpssnDias();
		float valor=0;
		int min=dtSancion.getDtpssnMinutos();
		
		
		String[] Dias  = txtDias.split("-");
		int frecuencia=Dias.length;
		
		FichaEmpleado empleado= srvFichaEmpleado.BuscarPorid(seleccionPersona.getFcemId());
		falta=srvSanciones.ObtenerFaltaPorI(Integer.parseInt(tipoFalta));
		 categoriaFaltaAplicar= obtenerCategoriaFaltaPorParametros(empleado.getCategoria().getCtgId(), falta.getFlId() , min,  frecuencia);
		
		if(categoriaFaltaAplicar==null) {
//			if(Faltas.Atrasos.getId()==falta.getFlId()) {
//				categoriaFaltaAplicar= obtenerCategoriaFaltaPorParametros(empleado.getCategoria().getCtgId(), Faltas.AbandonodeTrabajo.getId() , min);
//				
//			}
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Información.", "No se ha paramatrizado estos rangos"));
			return;
		}
		dtSancion.setDtpssnFrecuencia(frecuencia);
		//  int idTiposancion=categoriaFaltaAplicar.getTipoSancion().getTpsnId();
		
		 sancion  = obtenerSancionAplicar(categoriaFaltaAplicar.getSancion());
		 if(sancion.getTipoSancion().getTpsnId()==1) {
			 if(VerificarSancionConsecutivoMuta(sancion,categoriaFaltaAplicar)) {
				 List<Sancion> sanciones= new ArrayList<>();
					sanciones = srvSanciones.ObtenerLstSancionPorTipoSancionId(2);
				for(Sancion s:sanciones) {
					if(s.getSnNivel()==1) {
						sancion=s;
						break;
					}
				 
			 }
		 }
		 }
			 
		 this.tipoSancion=String.valueOf(sancion.getSnId());
		 
if(sancion.getSnPorcentaje()!=0 && sancion.getSnPorcentaje()!=-1 ) {
	 valor=sancion.getSnPorcentaje();
	 
}else {
	if(sancion.getSnPorcentaje()!=-1) {
	if(categoriaFaltaAplicar.getCtgflPorcentajeBase()!=0) {
	if(esPorFrecuencia) {
		  valor =(dtSancion.getDtpssnFrecuencia()*categoriaFaltaAplicar.getCtgflPorcentajeBase()); 
	  }else {
		  valor=(dtSancion.getDtpssnMinutos()*categoriaFaltaAplicar.getCtgflPorcentajeBase()); 
	  }}
	}
	
}
		if(valor!=0) {
			int sueldo=puesto.getGrupoOcupacional().getGrocRmu();
				valor=(valor/100)*sueldo;
				EsDescuento=true;
		}else {
			EsDescuento=false;
			}

			dtSancion.setDtpssnValor(valor);		 				
	}
	
	public CategoriaFalta obtenerCategoriaFaltaPorParametros(int ctgId, int flId , int min,  int frc) {
		CategoriaFalta categoriaFalta= null;
		List<CategoriaFalta> parametros= srvSanciones.listarcategoriaFaltaPorCategoriaIdFaltaId(ctgId,flId);
 		for(CategoriaFalta s: parametros) {
			if(s.getCtgflMinuntosMin()!=-1 && s.getCtgflMinutosMax()!=-1) {

				if(s.getCtgflMinuntosMin()<=min && min <=s.getCtgflMinutosMax()) {
					categoriaFalta=s;
					esPorFrecuencia=false;
					break;
				}
			}
			else  {
				

				if(s.getCtgflMinuntosMin()!=-1 && s.getCtgflMinutosMax()==-1 ) {
					if( min>=s.getCtgflMinuntosMin()) {
					categoriaFalta=s;	
					esPorFrecuencia=false;
					break;
					}
				}else {
					if(s.getCtgflFrecuenciaMin()>=1 && s.getCtgflFrecuenciaMax()!=-1) {
						if(s.getCtgflFrecuenciaMin()<=frc && s.getCtgflFrecuenciaMax()>=frc ) {
							categoriaFalta=s;
							esPorFrecuencia=true;
							break;
						}
					}else {
						if((s.getCtgflFrecuenciaMin()>=1) && (s.getCtgflFrecuenciaMax()==-1)) {
							categoriaFalta=s;
							esPorFrecuencia=true;
							break;
							
						}
				
					}
						}
			
		}
			}
		return categoriaFalta;
		}
		
	
	public  Sancion  obtenerSancionAplicar(Sancion  sancion) {
		Sancion retorno =null;
		int nivel=sancion.getSnNivel();
		int tpsn= 0;
		DetallePuestoSancion dtSanUlt= null;
		Sancion ultimaSancion=  null;
		
		
			dtSanUlt=srvSanciones.obtenerUltimaSancion(seleccionPersona.getDtpsId());
			if(dtSanUlt!=null){
				ultimaSancion= dtSanUlt.getSancion();
				tpsn=ultimaSancion.getTipoSancion().getTpsnId();
		}
		
		if(ultimaSancion==null) {
			retorno=sancion;
		}else {
			if(tpsn==2) {
			if(calcularTiempoUltimaSancion(dtSancion, dtSanUlt)) {

				int ulnivel=ultimaSancion.getSnNivel();
				if(ulnivel>=nivel) {
					if(ulnivel==6) {
						retorno=sancion;
					}else {
						ulnivel=ulnivel+1;
						List<Sancion> sanciones= new ArrayList<>();
     						sanciones = srvSanciones.ObtenerLstSancionPorTipoSancionId(tpsn);
						for(Sancion s:sanciones) {
							if(s.getSnNivel()==ulnivel) {
								retorno=s;
								break;
							}
						}
						
					}
					
				}else {
					retorno=sancion;
				}
		
				
			}
			else {
				retorno=sancion;
			}
				}
			else {
				retorno=sancion;
			}
		}
		
		return retorno;
		
	}
	
	public boolean VerificarSancionConsecutivoMuta(Sancion sancion, CategoriaFalta  categoriaFal) {
		boolean retorno =false;
		Calendar fs=Calendar.getInstance();
		fs.set(dtSancion.getDtpssnAno() , dtSancion.getDtpssnMes()-1,1);

		int mes=dtSancion.getDtpssnMes();
		int n=mes-2;
		for(int i=1;i<=2; i++) {
			fs.add(Calendar.MONTH, -1);
			DetallePuestoSancion  detSan= srvSanciones.obtenerSancionPorMesAnio(seleccionPersona.getDtpsId(),categoriaFaltaAplicar.getCtgflId(),fs.get(Calendar.MONTH)+1,dtSancion.getDtpssnAno());
			if(detSan==null) {
				retorno=false;
				break;
			}else {
				retorno=true;
			}
		}
		
		
		return retorno;
		
		
	}
	
	public void cargarSancionFormEdit() {
		
			if(seleccionDtSancion.getDtpssnDescontar()==1) {

				EsDescuento=true;
			}else {
				EsDescuento=false;
			}
			falta=seleccionDtSancion.getCategoriaFalta().getFalta();
			sancion=seleccionDtSancion.getSancion();
			tipoFalta=String.valueOf(falta.getFlId());
			tipoSancion=String.valueOf(sancion.getSnId());

			
						}

	public void guardarSancion() {
		
		 if(EsDescuento) {
			 dtSancion.setDtpssnDescontar(1);
		 }
		 else {

			 dtSancion.setDtpssnDescontar(0);
		 }
		
		 dtSancion.setCategoriaFalta(categoriaFaltaAplicar);
		 dtSancion.setSancion(sancion);
		 
		if(esActualizacion) {
			srvSanciones.actualizarSancion(dtSancion);
		}else {
		 detallePuesto=srvDetallePuesto.DetallePuestoBuscarPorId(seleccionPersona.getDtpsId());
		 dtSancion.setDetallePuesto(detallePuesto); 
		 dtSancion.setDtpssnEstado(Estados.Activo.getId());
		if( srvSanciones.insertaSancion(dtSancion)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Información.", "Sancion Registrada existosamente"));
		}
		 
		}
		limpiarFormSancion();

	}

	public void anularSancion() {
		dtSancion.setDtpssnEstado(Estados.Anulado.getId());
		srvSanciones .insertaSancion(dtSancion);
	}
	
	
	
	public void regresar() {
		
	}
	
	public void verPDF()  {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("txt_num_auto", String.valueOf(dtSancion.getDtpssnNumaccion()));
			parametros.put("txt_nombres", seleccionPersona.nombresCompetos());
			parametros.put("txt_dependencia", dependencia.getDpnDescripcion());
			parametros.put("txt_cedula", seleccionPersona.getPrsIdentificacion());
			parametros.put("txt_explicacion", dtSancion.getDtpssnObservacion());
			parametros.put("txt_puesto", puesto.getPstDenominacion());
			parametros.put("txt_renumeracion", "896.00");
			parametros.put("txt_partida", "1234567890000000000");
			parametros.put("txt_individual", "123456");
			//parametros.put("txt_remuneracion", detallePuesto.getFichaEmpleado().);
			//parametros.put("txt_partida", String.valueOf(salVacaCal1.getSlvcDiasRestantes()));
			//parametros.put("txt_individual", String.valueOf(salVacaCal2.getSlvcDiasRestantes()));
			

			File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/controlAsistencia/reportes/sanciones.jasper"));
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros);
			
			
			InputStream rptStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/controlAsistencia/reportes/sanciones.jasper");
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			response.addHeader("Content-disposition", "attachment; filename=vacacion_" + sdf.format(new Date()).toString() + ".pdf");
			ServletOutputStream stream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
			JasperRunManager.runReportToPdfStream(rptStream,stream, parametros, new JREmptyDataSource());
			stream.flush();
			stream.close();
			FacesContext.getCurrentInstance().responseComplete();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

public boolean calcularTiempoUltimaSancion(DetallePuestoSancion dtSancionNueva,	DetallePuestoSancion dtSancionAnterior) {
	  
	String diasNuevo=dtSancionNueva.getDtpssnDias();
	String diasAnterior=dtSancionAnterior.getDtpssnDias();
	
	String[] dias1 = diasNuevo.split("-"); 
	String[] dias2 = diasAnterior.split("-"); 
	
	int n2=dias2.length;
	
	int ultimoDiaNuevo=Integer.parseInt(dias1[0]);
	int ultimoDiaAnterior=Integer.parseInt(dias2[n2-1]);
	
	int mesNuevo=dtSancionNueva.getDtpssnMes()-1;
	int mesAnterior=dtSancionAnterior.getDtpssnMes()-1;
	
	int anioNuevo=dtSancionNueva.getDtpssnAno();
	int anioAnterior=dtSancionAnterior.getDtpssnAno();
	
	boolean retorno = false;
	Calendar fecAct= Calendar.getInstance();
	
	fecAct.set(anioNuevo,mesNuevo,ultimoDiaNuevo);
	
	Calendar fUltSan= Calendar.getInstance();
	fUltSan.set(anioAnterior , mesAnterior,ultimoDiaAnterior);


	 
		 int monthDay[]= { 31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		 int anios = anioNuevo - anioAnterior;
         int Meses;
         int Dias;
         
         GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();

         Calendar sanAhora=Calendar.getInstance();
         sanAhora.setTime(fUltSan.getTime());
         sanAhora.add(Calendar.YEAR, anios);
       
         if (fecAct.compareTo(sanAhora) < 0)
             anios--;


         
         if(anios>=1) {
        	 retorno=false;
         }else {
        	 retorno=true;
         }
  
         return retorno;
}

	
}
