package ec.edu.uce.contolAsistencia.jsf.session.vacacion;

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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ddf.EscherSimpleProperty;

import ec.edu.uce.controlAsistencia.ejb.datos.DetallePuestoDto;
import ec.edu.uce.controlAsistencia.ejb.datos.Estados;
import ec.edu.uce.controlAsistencia.ejb.datos.ParametrosVacacion;
import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.ContratoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.FichaEmpleadoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.ParametroVacacionesServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PermisoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PersonaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Contrato;
import ec.edu.uce.controlAsistencia.jpa.entidades.Dependencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuestoSancion;
import ec.edu.uce.controlAsistencia.jpa.entidades.FichaEmpleado;
import ec.edu.uce.controlAsistencia.jpa.entidades.Licencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.ParametroVacacionRegimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.ParametroVacaciones;
import ec.edu.uce.controlAsistencia.jpa.entidades.Permiso;
import ec.edu.uce.controlAsistencia.jpa.entidades.Puesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.SaldoVacacion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Vacacion;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;

@ManagedBean(name = "vacacionForm")
@SessionScoped
public class VacacionForm implements Serializable {

	/**
	 * Variables
	 */
	private static final long serialVersionUID = 1L;
	private Vacacion vacacion;
	private DetallePuesto detallePuesto;
	private Estados estado;
	private List<SaldoVacacion> saldoVacacion = null;
	private boolean esActualizacion = false;
	private List<Vacacion> listaVacacion = new ArrayList<>();
	private Vacacion seleccionVacacion;
	private SaldoVacacion saldoVacacion1 = null;
	private SaldoVacacion saldoVacacion2 = null;
	private DetallePuestoDto detallePuestoEmpleado;
	private FichaEmpleado fichaEmpleado;
	private Dependencia dependencia;
	private Puesto puesto = null;
	private Regimen regimen = null;
	private PersonaDto seleccionPersona;
	private SaldoVacacion salVacaCal1;
	private SaldoVacacion salVacaCal2;
	private boolean renderBtnImprimir = false;

	/* Permiso Personal */
	private Permiso permiso;
	private List<Permiso> listaPermisos;
	private Permiso seleccionPermiso;
	private boolean valorJustificaHoras = false;
	private boolean horasJustificadas = true;
	private boolean esBloqueado = false;
	private boolean esPermitirIngreso=true;

	/***
	 * Declaracion de servicios
	 */
	@EJB
	private FichaEmpleadoServicio srvFichaEmpleado;
	@EJB
	private DependenciaServicio srvDependencia;
	@EJB
	private RegimenServicio srvRegimen;
	@EJB
	private PuestoServicio srvPuesto;
	@EJB
	private VacacionServicio srvVacacion;

	@EJB
	private DetallePuestoServicio srvDetallePuesto;

	@EJB
	private ContratoServicio srvContrato;

	@EJB
	private PersonaServicio srvPersona;
	@EJB
	private ParametroVacacionesServicio srvParamVacaciones;

	@EJB
	private PermisoServicio srvPermiso;
	
	


	// ==============================================POSTCONSTRUCT=============================================================//

	@PostConstruct
	public void init() {

	}

	// *=====================================================================METODOS====================================================================//

	public void CalcularVacaciones() {
		if (vacacion.getVccNumDias() > 2) {
			if (vacacion.getVccFechaInicio() != null) {
				CalcularSaldoVacacion(vacacion.getVccFechaInicio(), vacacion.getVccNumDias(),1);
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Advertencia.", "No se ha especificado una fecha de inicio."));
			}

		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Advertencia.",
							"No se puede registrar una Autorización de vacaciones cuyo número de días es menor a 3."));
		}

	}

	public void GuardarVacacion() {
		boolean retorno = false;
		// detallePuesto =
		// srvDetallePuesto.DetallePuestoBuscarPorId(seleccionPersona.getDtpsId());

		vacacion.setDtpsId(seleccionPersona.getDtpsId());
		if (esActualizacion) {
			Vacacion vac = srvVacacion.VacacionActualizar(vacacion);
			if (vac != null) {
				retorno = true;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Información.", "Aut. de vacaciones anuales actualizada exitosamente."));
				this.renderBtnImprimir = true;
			} else {
				retorno = false;
				this.renderBtnImprimir = false;
			}
		} else {
			vacacion.setVccEstado(Estados.Activo.getId());
			retorno = srvVacacion.VacionInsertar(vacacion);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Información.", "Aut. de vacaciones anuales registrada exitosamente."));
			this.renderBtnImprimir = true;
		}

		if (retorno) {
			if (salVacaCal1 != null && salVacaCal2 != null) {

				salVacaCal1.setDtpsId(seleccionPersona.getDtpsId());
				salVacaCal2.setDtpsId(seleccionPersona.getDtpsId());
				srvVacacion.ActualizarSaldoVacacion(salVacaCal1);
				srvVacacion.ActualizarSaldoVacacion(salVacaCal2);
				saldoVacacion1=salVacaCal1;
				saldoVacacion2=salVacaCal2;

			} else {
				if (salVacaCal1 == null && salVacaCal2 != null) {
					salVacaCal2.setDtpsId(seleccionPersona.getDtpsId());
					srvVacacion.ActualizarSaldoVacacion(salVacaCal2);

				}
			}
		} else {
			// no se inserto o no se actualizo
		}

	}

	public void EditarVacacion() {
		
		

	}

	/***
	 * Calcula la fecha final de vacaciones
	 * 
	 * @param fechaInicio
	 * @param numDias
	 * @return
	 */
	public Date calcularFechaFinal(Date fechaInicio, int numDias) {
		Calendar fechaFinal = Calendar.getInstance();
		fechaFinal.setTime(fechaInicio);
		fechaFinal.add(Calendar.DAY_OF_YEAR, numDias - 1);
		int dsab = fechaFinal.get(Calendar.DAY_OF_WEEK);
		if (dsab == Calendar.SATURDAY) {
			fechaFinal.add(Calendar.DAY_OF_YEAR, 1);
		}
		return (Date) fechaFinal.getTime();
	}

	/**
	 * Metodo paara generar el Numero De Autorizacion
	 * 
	 * @return
	 */
	public int generarNumAutorizacion() {
		int numAutorizacion = 0;
		if (srvVacacion.MaximaNumAutorizacion() != 0) {
			numAutorizacion = srvVacacion.MaximaNumAutorizacion() + 1;
		} else {
			numAutorizacion = 1;
		}
		return numAutorizacion;
	}

	/***
	 * calcula el Salo de vacaciones por periodos disponibles
	 * 
	 * @param fechaInicio
	 * @param num
	 */
	public void CalcularSaldoVacacion(Date fechaInicio, int num, int tipo) {
		

		int totaldias2 = 0, saldoDias2 = 0, diasReg2 = 0, diasAnt2 = 0, numFSem1 = 0, numFsTotal1 = 0;
		int totaldias1 = 0, saldoDias1 = 0, diasReg1 = 0, diasAnt1 = 0, numFSem2 = 0, numFsTotal2 = 0;

		if (salVacaCal1 != null) {
			saldoDias1 = salVacaCal1.getSlvcDiasRestantes();
			totaldias1 = salVacaCal1.getSlvcTotalDias();
			diasReg1 = salVacaCal1.getSlvcDiasRegistrados();
			diasAnt1 = salVacaCal1.getSlvcDiasAnticipados();
			numFSem1 = salVacaCal1.getSlvcNumfinsemana();
			numFsTotal1 = salVacaCal1.getSlvcTotalDias() / 7;

		}
		if (salVacaCal2 != null) {
			totaldias2 = salVacaCal2.getSlvcTotalDias();
			saldoDias2 = salVacaCal2.getSlvcDiasRestantes();
			diasReg2 = salVacaCal2.getSlvcDiasRegistrados();
			diasAnt2 = salVacaCal2.getSlvcDiasAnticipados();
			numFSem2 = salVacaCal2.getSlvcNumfinsemana();
			numFsTotal2 = salVacaCal2.getSlvcTotalDias() / 7;
		}

		if (salVacaCal1 != null && salVacaCal2 != null) {
			if(tipo==2) {

				num=calcularHoras(salVacaCal1);
			}
		if(num!=0) {
		
			int saldoTotaldias = saldoDias1 - num;
			if (saldoTotaldias < 0) {
				salVacaCal1.setSlvcDiasRegistrados(totaldias1);
				salVacaCal1.setSlvcDiasRestantes(0);
				salVacaCal1.setSlvcEstado(Estados.DesActivo.getId());
				salVacaCal1.setSlvcNumfinsemana(numFsTotal1);
				salVacaCal2.setSlvcPeriodo(1);
				int nres=saldoTotaldias*-1;
				saldoTotaldias = saldoTotaldias + saldoDias2;
				if (saldoTotaldias < 0) {
					int numres = saldoTotaldias * (-1);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Información.", "El Usuario no tiene disponible el numero de dias solicitadas"));
					salVacaCal2.setSlvcDiasAnticipados(numres);
					salVacaCal2.setSlvcDiasRegistrados(totaldias2);
					salVacaCal2.setSlvcDiasRestantes(0);
					salVacaCal2.setSlvcNumfinsemana(numFsTotal2);
					
					if(tipo==1) {
					vacacion.setVccFechaFin(calcularFechaFinal(vacacion.getVccFechaInicio(), num));
					vacacion.setVccNumDias(num);
					}

				} else {
					int sumNdias=0;
					Map<String, Integer> resultado = CalcularNumDiasADescontar(salVacaCal2, nres);
					int n = resultado.get("diasDescontar");
					if(n>nres) {
						sumNdias=n-nres;
					}
					num= num+sumNdias;
					
					salVacaCal2.setSlvcDiasRegistrados(diasReg2 + n);
					salVacaCal2.setSlvcDiasRestantes(totaldias2 - (diasReg2 + n));
					salVacaCal2.setSlvcTotalHoras(salVacaCal1.getSlvcTotalHoras());
					salVacaCal1.setSlvcTotalHoras("00:00");
					salVacaCal2.setSlvcNumfinsemana(numFSem2 + resultado.get("finSemana"));
					if(tipo==1) {
					vacacion.setVccFechaFin(
							calcularFechaFinal(vacacion.getVccFechaInicio(), resultado.get("diasCalcularFecha")));
					
					vacacion.setVccNumDias(num);;
					}
				}
			}
			
			else {
				Map<String, Integer> resultado = CalcularNumDiasADescontar(salVacaCal1, num );
				int n = resultado.get("diasDescontar");
				salVacaCal1.setSlvcDiasRegistrados(diasReg1 + n);
				salVacaCal1.setSlvcDiasRestantes(totaldias1 - (diasReg1 + n));
				salVacaCal1.setSlvcNumfinsemana(numFSem1 + resultado.get("finSemana"));
				if(tipo==1) {
				vacacion.setVccFechaFin(
						calcularFechaFinal(vacacion.getVccFechaInicio(), resultado.get("diasCalcularFecha")));

				vacacion.setVccNumDias(n);
				}

			}

		} else {
			if (salVacaCal1 == null && salVacaCal2 != null) {
				if(tipo==2) {
					num=calcularHoras(salVacaCal2);
				}
				if(num!=0) {
				int saldoTotaldias = saldoDias2 - num;
				if (saldoTotaldias < 0) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Información.", "El Usuario no tiene disponible el numero de dias solicitadas"));
					salVacaCal2.setSlvcDiasAnticipados((-1) * saldoTotaldias);
					salVacaCal2.setSlvcDiasRegistrados(totaldias2);
					salVacaCal2.setSlvcDiasRestantes(0);
					salVacaCal2.setSlvcNumfinsemana(numFsTotal2);
					if(tipo==1) {
					vacacion.setVccFechaFin(calcularFechaFinal(vacacion.getVccFechaInicio(), num));
					vacacion.setVccNumDias(num);
					}
				} else {

					Map<String, Integer> resultado = CalcularNumDiasADescontar(saldoVacacion2, num);
					int n = resultado.get("diasDescontar");
					salVacaCal2.setSlvcDiasRegistrados(diasReg2 + n);
					salVacaCal2.setSlvcDiasRestantes(totaldias2 - (diasReg2 + n));
					salVacaCal2.setSlvcNumfinsemana(numFSem2 + resultado.get("finSemana"));
					if(tipo==1) {
					vacacion.setVccFechaFin(
							calcularFechaFinal(vacacion.getVccFechaInicio(), resultado.get("diasCalcularFecha")));
					vacacion.setVccNumDias(n);
					}

				}

			  }
			}
		}
		}
	}

	public void anularVacacion() {

		boolean retorno = false;
		vacacion.setVccEstado(Estados.Anulado.getId());
		vacacion.getVccObservacionEstado();
		esBloqueado = true;
		int totaldias2 = 0, saldoDias2 = 0, diasReg2 = 0, diasAnt2 = 0, numFSem2 = 0, numFsTotal2 = 0;
		int totaldias1 = 0, saldoDias1 = 0, diasReg1 = 0, diasAnt1 = 0, numFSem1 = 0, numFsTotal1 = 0;

		if (salVacaCal1 != null) {
			saldoDias1 = salVacaCal1.getSlvcDiasRestantes();
			totaldias1 = salVacaCal1.getSlvcTotalDias();
			diasReg1 = salVacaCal1.getSlvcDiasRegistrados();
			diasAnt1 = salVacaCal1.getSlvcDiasAnticipados();
			numFSem1 = salVacaCal1.getSlvcNumfinsemana();
			numFsTotal1 = salVacaCal1.getSlvcTotalDias() / 7;

		}
		if (salVacaCal2 != null) {
			totaldias2 = salVacaCal2.getSlvcTotalDias();
			saldoDias2 = salVacaCal2.getSlvcDiasRestantes();
			diasReg2 = salVacaCal2.getSlvcDiasRegistrados();
			diasAnt2 = salVacaCal2.getSlvcDiasAnticipados();
			numFSem2 = salVacaCal2.getSlvcNumfinsemana();
			numFsTotal2 = salVacaCal2.getSlvcTotalDias() / 7;
		}
		if(saldoVacacion1!=null && saldoVacacion2!=null) {
		int dias=diasReg1-vacacion.getVccNumDias();
		salVacaCal1.setSlvcDiasRegistrados(dias);
		int diasRes=saldoDias1+vacacion.getVccNumDias();
		salVacaCal1.setSlvcDiasRestantes(diasRes);
		int numFs=salVacaCal1.getSlvcDiasRegistrados()/7;
		salVacaCal1.setSlvcNumfinsemana(numFs);
		
			
		}else {
			if(saldoVacacion1==null && saldoVacacion2!=null) {
				if(diasAnt2==0) {
				
				int dias=diasReg2-vacacion.getVccNumDias();
				if(dias<0) {
				 salVacaCal1=srvVacacion.ObtenerSaldoVacacionPorPeriodo(1, seleccionPersona.getDtpsId());
				 if(salVacaCal1!=null) {
				 salVacaCal1.setSlvcEstado(1);
				 salVacaCal2.setSlvcPeriodo(2);
				 salVacaCal2.setSlvcDiasRegistrados(0);
				 salVacaCal2.setSlvcDiasRestantes(totaldias2);
				 salVacaCal2.setSlvcNumfinsemana(0);
				 int diasReg=salVacaCal1.getSlvcDiasRegistrados()-vacacion.getVccNumDias();
					salVacaCal1.setSlvcDiasRegistrados(diasReg);
					int diasRes=saldoDias2+vacacion.getVccNumDias();
					salVacaCal1.setSlvcDiasRestantes(diasRes);
					int numFs=salVacaCal1.getSlvcDiasRegistrados()/7;
					salVacaCal1.setSlvcNumfinsemana(numFs);
				 
				 }
				 
				
				}else {
					int diasReg=diasReg2-vacacion.getVccNumDias();
					salVacaCal2.setSlvcDiasRegistrados(diasReg);
					int diasRes=saldoDias2+vacacion.getVccNumDias();
					salVacaCal2.setSlvcDiasRestantes(diasRes);
					int numFs=salVacaCal1.getSlvcDiasRegistrados()/7;
					salVacaCal1.setSlvcNumfinsemana(numFs);
					
				}
				}else {
					int numAnt=diasAnt2-vacacion.getVccNumDias();
					if(numAnt<0) {
						int dias=diasReg2-vacacion.getVccNumDias();
						if(dias<0) {
						 salVacaCal1=srvVacacion.ObtenerSaldoVacacionPorPeriodo(1, seleccionPersona.getDtpsId());
						 if(salVacaCal1!=null) {
						 salVacaCal1.setSlvcEstado(1);
						 salVacaCal2.setSlvcPeriodo(2);
						 salVacaCal2.setSlvcDiasRegistrados(0);
						 salVacaCal2.setSlvcDiasRestantes(totaldias2);
						 salVacaCal2.setSlvcNumfinsemana(0);
						 int diasReg=salVacaCal1.getSlvcDiasRegistrados()-vacacion.getVccNumDias();
							salVacaCal1.setSlvcDiasRegistrados(diasReg);
							int diasRes=saldoDias2+vacacion.getVccNumDias();
							salVacaCal1.setSlvcDiasRestantes(diasRes);
							int numFs=salVacaCal1.getSlvcDiasRegistrados()/7;
							salVacaCal1.setSlvcNumfinsemana(numFs);
						 }
						}
						
					}else {
						salVacaCal2.setSlvcDiasAnticipados(0);
					}
				}
				
				
				
			}
			
		}
		

		srvVacacion.VacacionActualizar(vacacion);

	}

	/***
	 * calcula el numero de dias a Descontar totales;
	 * 
	 * @param saldoVacacion
	 * @param num
	 * @return
	 */
	public Map<String, Integer> CalcularNumDiasADescontar(SaldoVacacion saldoVacacion, int num) {

		Map<String, Integer> retorno = new HashMap<String, Integer>();
		int n = 0;
		int n1 = 0;
		int nAñF = 0;
		int numFReg = saldoVacacion.getSlvcNumfinsemana();
		int finSemana = saldoVacacion.getSlvcTotalDias() / 7;
		int numsaldo=saldoVacacion.getSlvcDiasRestantes();
		if (finSemana != 0) {
			if (num <= 6) {

				if (numFReg < finSemana) {

					int dsFin = numFReg * 2;
					int sumReg = saldoVacacion.getSlvcDiasRegistrados() + num - dsFin;

					int fins = sumReg / 5;
					if (numFReg < fins) {

						
						nAñF = 1;
						if (num == 5) {
							n = num + 2;
							n1 = n;
						} 
						 else {
							 if(num==6){
								n=num+1; 
								n1 = n;
							 }
						 else {
							n1 = num;
						 }
						}

					} else {
						n = num;
						n1 = num;
					}

				} else {

					n = num;
					n1 = num;
				}

			} else {
				n = num;
				n1 = num;
				int dSobrantes = saldoVacacion.getSlvcDiasRegistrados() % 7;
				int sumReg = num + dSobrantes;
				nAñF = sumReg / 7;
			}

		}
		
		
		if(n>numsaldo) {
			n=numsaldo;
		}
		retorno.put("diasDescontar", n);
		retorno.put("diasCalcularFecha", n1);
		retorno.put("finSemana", nAñF);

		return retorno;

	}

	public String regresar() {
		String ruta = "/controlAsistencia/vacaciones/VacacionesRegistros.xhtml";
		vacacion = null;
		permiso = null;
		saldoVacacion = null;
		this.renderBtnImprimir = false;
		this.horasJustificadas = true;
		this.valorJustificaHoras = false;
		
		
		return ruta;
	}

	public void cargarVariablesVacacion(Vacacion seleccionVacacion) {
		

		if (seleccionVacacion == null) {
			esActualizacion = false;
			this.renderBtnImprimir = false;
			this.esBloqueado=false;
			vacacion = new Vacacion();
			vacacion.setVccNumAutorizacion(generarNumAutorizacion());
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			vacacion.setVccFechaEmision(fechaEmision);
			vacacion.setVccCopia(seleccionPersona.getDpnNombre());

		} else {
			this.esActualizacion = true;
			this.renderBtnImprimir = true;
			vacacion = seleccionVacacion;
			if(seleccionVacacion.getVccEstado()==3) {
				this.esBloqueado=true;
			}else {
				this.esBloqueado=false;
				
			}
	
		}

		salVacaCal1 = saldoVacacion1;
		salVacaCal2 = saldoVacacion2;

	}

	public void cargarVariablesPermiso(Permiso seleccionPermiso) {

		if (seleccionPermiso == null) {
			esActualizacion = false;
			permiso = new Permiso();
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			permiso.setPrmFechaRegistro(fechaEmision);

		} else {
			esActualizacion = true;
			permiso = seleccionPermiso;
		}

		salVacaCal1 = saldoVacacion1;
		salVacaCal2 = saldoVacacion2;

	}

	public void limpiar() {
		seleccionPersona = null;
		listaVacacion = null;
		saldoVacacion1 = null;
		saldoVacacion2 = null;
		dependencia = null;
		regimen = null;
		puesto = null;
		vacacion = null;
		salVacaCal1= null;
		salVacaCal2=null;
		

	}

	public void cargarVariables(PersonaDto persona) {
		limpiar();
		if (persona != null) {
			seleccionPersona = persona;
			
		}
		if(seleccionPersona.getCtnId()==0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Información.", "El Funcionario no tiene definido una Contratacion, Actualice los datos"));
			esPermitirIngreso=false;
			return;
		}
		else {
			esPermitirIngreso=true;
		}
		cargarSaldoVacaciones();
		
	}
	
	public void cargarSaldoVacaciones() {
		
	saldoVacacion = srvVacacion.listSaldoVacacionPorDetallePuestoId(seleccionPersona.getDtpsId());
		
		if (saldoVacacion.size()== 2) {
			for (SaldoVacacion s : saldoVacacion) {
				if (s.getSlvcPeriodo() == 1) {
					saldoVacacion1 = s;
				}
				if (s.getSlvcPeriodo() == 2) {
					saldoVacacion2 = s;
				}
			}
		} else {
			if(saldoVacacion.size()== 1) {
			for (SaldoVacacion s : saldoVacacion) {
				saldoVacacion2 = s;
			}
			
			}else {
				if(saldoVacacion==null || saldoVacacion.size()==0) {
					generarSaldoVacacionesNuevos();
					cargarSaldoVacaciones();
				}
			}

		}
	}

	public void verPDF() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("txt_num_auto", String.valueOf(vacacion.getVccNumAutorizacion()));
			parametros.put("txt_nombres", seleccionPersona.nombresCompetos());
			parametros.put("txt_dependencia", seleccionPersona.getDpnNombre());
			String fecha_inicio = sdf.format(vacacion.getVccFechaInicio());
			String fecha_fin = sdf.format(vacacion.getVccFechaFin());

			parametros.put("txt_dias", String.valueOf(vacacion.getVccNumDias()));
			parametros.put("txt_desde", fecha_inicio);
			parametros.put("txt_hasta", fecha_fin);
			String saldo1 = null;
			String saldo2 = null;
			if (salVacaCal1.getSlvcDiasRestantes() == 0 || salVacaCal1 == null) {
				saldo1 = "--";
			} else {
				saldo1 = String.valueOf(salVacaCal1.getSlvcDiasRestantes());
			}
			if (salVacaCal2.getSlvcDiasRestantes() == 0 || salVacaCal2 == null) {
				saldo2 = "--";
			} else {
				saldo2 = String.valueOf(salVacaCal2.getSlvcDiasRestantes());
			}
			parametros.put("txt_saldo1", saldo1);
			parametros.put("txt_saldo2", saldo2);
			parametros.put("txt_observacion", vacacion.getVccObservacion());
			parametros.put("txt_copia", this.vacacion.getVccCopia());

			File jasper = new File(FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/controlAsistencia/reportes/vacaciones.jasper"));

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros);

			InputStream rptStream = FacesContext.getCurrentInstance().getExternalContext()
					.getResourceAsStream("/controlAsistencia/reportes/vacaciones.jasper");
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			response.addHeader("Content-disposition", "attachment; filename=AUT. VACACIONES_"
					+ this.seleccionPersona.nombresCompetos() + "_" + sdf.format(new Date()).toString() + ".pdf");
			ServletOutputStream stream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
			JasperRunManager.runReportToPdfStream(rptStream, stream, parametros, new JREmptyDataSource());
			stream.flush();
			stream.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void visualizarHorasJustificadas() {
		if (this.valorJustificaHoras) {
			this.horasJustificadas = false;
		} else {
			this.horasJustificadas = true;
		}
	}

	public void guardadPermiso() {
		boolean retorno = false;

		permiso.setDtpsId(seleccionPersona.getDtpsId());

		if (esActualizacion) {
			Permiso p = srvPermiso.ActualizarPermiso(permiso);
			if (p != null) {
				retorno = true;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Información.", "Permiso actualizado exitosamente."));

			} else {
				retorno = false;
			}
		} else {
			if (this.valorJustificaHoras) {
				// El valor 1 significa que existen horas justificadas
				this.permiso.setPrmJustificacion(1);
			} else {
				this.permiso.setPrmHorasJustificadas("00:00");
			}
			retorno = srvPermiso.InsertarPermiso(permiso);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Información.", "Permiso registrado exitosamente."));

			if (retorno) {
				CalcularSaldoVacacion(permiso.getPrmFechaPermiso(), 0,2);
				salVacaCal1.setDtpsId(seleccionPersona.getDtpsId());
				srvVacacion.ActualizarSaldoVacacion(salVacaCal1);
			}

		}
	}

	public int  calcularHoras(SaldoVacacion saldoVac) {

		// Variables del metodo
		String numHorasPermiso = this.permiso.getPrmNumHoras();
		String[] arrayNumHorasPermiso = numHorasPermiso.split(":");
		String numHorasJustificadas = this.permiso.getPrmHorasJustificadas();
		String[] arrayNumHorasJustificadas = numHorasJustificadas.split(":");
		String totalHorasS1 = saldoVac.getSlvcTotalHoras();
		String[] arraytotalHorasS1 = totalHorasS1.split(":");
		int n=0;
		

		int h1, m1, h2, m2, h3, m3, resultadoRestaHoras, resultadoRestaMinutos, resultadoSumaHoras,
				resultadoSumaMinutos;
		

		// si hay horas a justificar
		if (this.valorJustificaHoras) {
			// primero resto dichas horas al tiempo total del permiso
			h1 = Integer.parseInt(arrayNumHorasPermiso[0]);
			m1 = Integer.parseInt(arrayNumHorasPermiso[1]);
			h2 = Integer.parseInt(arrayNumHorasJustificadas[0]);
			m2 = Integer.parseInt(arrayNumHorasJustificadas[1]);

			resultadoRestaHoras = h1 - h2;
			resultadoRestaMinutos = m1 - m2;

			// el resultado de la resta le sumo a las horas que tiene en saldo
			// vacaciones
			if (resultadoRestaHoras == 8) {
				n=1;
				//restarDias(1);
				saldoVac.setSlvcTotalHoras("00:00");
			} else if (resultadoRestaHoras < 8) {
				// sumo las horas resultantes a las horas existentes en saldo
				// vacaciones
				h3 = Integer.parseInt(arraytotalHorasS1[0]);
				m3 = Integer.parseInt(arraytotalHorasS1[1]);

				resultadoSumaHoras = resultadoRestaHoras + h3;
				resultadoSumaMinutos = resultadoRestaMinutos + m3;

				if (resultadoSumaMinutos == 60) {
					resultadoSumaHoras = resultadoSumaHoras + 1;
					resultadoSumaMinutos = 0;
				}

				if (resultadoSumaHoras == 8) { // 08:00
					n=1;
					//restarDias(1);
					if (resultadoSumaMinutos == 0) {
						System.out.println("min " + resultadoSumaMinutos);
						saldoVac.setSlvcTotalHoras("00:00");
					} else {
						System.out.println("min " + resultadoSumaMinutos);
						String minAux;
						if (resultadoSumaMinutos < 10) {
							minAux = "0" + String.valueOf(resultadoSumaMinutos);
						} else {
							minAux = String.valueOf(resultadoSumaMinutos);
						}
						saldoVac.setSlvcTotalHoras("00" + ":" + minAux);
					}

				} else if (resultadoSumaHoras > 8 ) { // 08:30
					 n=1;
					//restarDias(1);
					int horasAux = resultadoSumaHoras - 8;
					String minAux;
					if (resultadoSumaMinutos < 10) {
						minAux = "0" + String.valueOf(resultadoSumaMinutos);
					} else {
						minAux = String.valueOf(resultadoSumaMinutos);
					}
					saldoVac.setSlvcTotalHoras("0" + horasAux + ":" + minAux);
				}else{
					String minAux;
					if (resultadoSumaMinutos < 10) {
						minAux = "0" + String.valueOf(resultadoSumaMinutos);
					} else {
						minAux = String.valueOf(resultadoSumaMinutos);
					}
					saldoVac.setSlvcTotalHoras("0" + resultadoSumaHoras + ":" + minAux);
				}

			}else{
				h3 = Integer.parseInt(arraytotalHorasS1[0]);
				m3 = Integer.parseInt(arraytotalHorasS1[1]);

				resultadoSumaHoras = resultadoRestaHoras + h3;
				resultadoSumaMinutos = resultadoRestaMinutos + m3;

				if (resultadoSumaMinutos == 60) {
					resultadoSumaHoras = resultadoSumaHoras + 1;
					resultadoSumaMinutos = 0;
				}

				if (resultadoSumaHoras == 8) { // 08:00
					n=1;
					//restarDias(1);
					if (resultadoSumaMinutos == 0) {
						System.out.println("min " + resultadoSumaMinutos);
						saldoVac.setSlvcTotalHoras("00:00");
					} else {
						System.out.println("min " + resultadoSumaMinutos);
						String minAux;
						if (resultadoSumaMinutos < 10) {
							minAux = "0" + String.valueOf(resultadoSumaMinutos);
						} else {
							minAux = String.valueOf(resultadoSumaMinutos);
						}
						saldoVac.setSlvcTotalHoras("00" + ":" + minAux);
					}

				} else if (resultadoSumaHoras > 8 ) { // 08:30
					n=1;
					//restarDias(1);
					int horasAux = resultadoSumaHoras - 8;
					String minAux;
					if (resultadoSumaMinutos < 10) {
						minAux = "0" + String.valueOf(resultadoSumaMinutos);
					} else {
						minAux = String.valueOf(resultadoSumaMinutos);
					}
					saldoVac.setSlvcTotalHoras("0" + horasAux + ":" + minAux);
				}else{
					String minAux;
					if (resultadoSumaMinutos < 10) {
						minAux = "0" + String.valueOf(resultadoSumaMinutos);
					} else {
						minAux = String.valueOf(resultadoSumaMinutos);
					}
					saldoVac.setSlvcTotalHoras("0" + resultadoSumaHoras + ":" + minAux);
				}

				
			}
			// sin horas justificadas
		} else {

			h1 = Integer.parseInt(arrayNumHorasPermiso[0]);
			m1 = Integer.parseInt(arrayNumHorasPermiso[1]);
			h3 = Integer.parseInt(arraytotalHorasS1[0]);
			m3 = Integer.parseInt(arraytotalHorasS1[1]);
			

			if (h1 == 8 && m1 == 0) {
				n=1;
				//restarDias(1);

			} else if (h1 == 16 && m1 == 0) {
				n=2;
				//restarDias(2);

			} else {

				resultadoSumaHoras = h1 + h3;
				resultadoSumaMinutos = m1 + m3;

				if (resultadoSumaMinutos == 60) {
					resultadoSumaHoras = resultadoSumaHoras + 1;
					resultadoSumaMinutos = 0;
				}

				if (resultadoSumaHoras == 8) {
					n=1;
					//restarDias(1);
					if (resultadoSumaMinutos == 0) {
						System.out.println("min " + resultadoSumaMinutos);
						saldoVac.setSlvcTotalHoras("00:00");
					} else {
						System.out.println("min " + resultadoSumaMinutos);
						String minAux;
						if (resultadoSumaMinutos < 10) {
							minAux = "0" + String.valueOf(resultadoSumaMinutos);
						} else {
							minAux = String.valueOf(resultadoSumaMinutos);
						}
						saldoVac.setSlvcTotalHoras("00" + ":" + minAux);
					}

				} else if (resultadoSumaHoras > 8) {
					n=1;
					//restarDias(1);
					int horasAux = resultadoSumaHoras - 8;
					System.out.println("horas aux " + horasAux);
					String minAux;
					if (resultadoSumaMinutos < 10) {
						minAux = "0" + String.valueOf(resultadoSumaMinutos);
					} else {
						minAux = String.valueOf(resultadoSumaMinutos);
					}
					saldoVac.setSlvcTotalHoras("0" + horasAux + ":" + minAux);
				} else {
					String minAux;
					if (resultadoSumaMinutos < 10) {
						minAux = "0" + String.valueOf(resultadoSumaMinutos);
					} else {
						minAux = String.valueOf(resultadoSumaMinutos);
					}
					saldoVac.setSlvcTotalHoras("0" + resultadoSumaHoras + ":" + minAux);
				}

			}
		}
		
		return n;

	}
	
	public void restarDias (int opcion){
		if(opcion == 1){
//			int diasRestantesS1 = salVacaCal1.getSlvcDiasRestantes();
//			int diasRegistradosS1 = salVacaCal1.getSlvcDiasRegistrados();
			// ingresar metodo  fines de semna
			CalcularSaldoVacacion(permiso.getPrmFechaPermiso(), 1,2);
//			int resultado = diasRestantesS1 - 1;
//			int resultado2 = diasRegistradosS1 + 1;
//			salVacaCal1.setSlvcDiasRegistrados(resultado2);
//			salVacaCal1.setSlvcDiasRestantes(resultado);
			
		}else if (opcion == 2){
			CalcularSaldoVacacion(permiso.getPrmFechaPermiso(), 2,2);
//			int diasRestantesS1 = salVacaCal1.getSlvcDiasRestantes();
//			int diasRegistradosS1 = salVacaCal1.getSlvcDiasRegistrados();
//			int resultado = diasRestantesS1 - 2;
//			int resultado2 = diasRegistradosS1 + 2;
//			salVacaCal1.setSlvcDiasRegistrados(resultado2);
//			salVacaCal1.setSlvcDiasRestantes(resultado);
		}
	}

	public String regresarEmpleados() {
		String ruta = "/controlAsistencia/empleado/busquedaEmpleado.xhtml";
		this.seleccionPersona = null;
		// this.licencia = null;
		// saldoVacacion = null;
		// cargarVariables();
		return ruta;
	}
	
	
	public void generarSaldoVacacionesNuevos() {
		
		int aniosContrato=0;
		int mesesContrato=0;
		int diasVac1 =0;
		int diasVac2=0;
		SaldoVacacion sv1=null;
		SaldoVacacion sv2=null;
		ParametroVacacionRegimen parametroDias=srvParamVacaciones.buscarPorId(ParametrosVacacion.NumDiasxAnio.getId(), seleccionPersona.getCtgId());
		double promDiasVacaMes=(Integer.parseInt(parametroDias.getPrvcrgValor())/12);
	
		Contrato contrato=srvContrato.obtenerPorId(seleccionPersona.getCtnId());
		SaldoVacacion saldoVacacion=new SaldoVacacion();
		Calendar fechaActual= Calendar.getInstance();
		Calendar fechaContrato=Calendar.getInstance();
		fechaContrato.setTime(contrato.getCntFechaInicio());
		int valor = fechaActual.compareTo(fechaContrato);
		
		if(valor>0){
		
		Map<String, Integer> resultado=calcularTiempoContratacion(fechaActual,fechaContrato);
		aniosContrato=resultado.get("anios");
		mesesContrato=resultado.get("meses");
		if((aniosContrato==0) && (mesesContrato>0)){
	     
			diasVac2=(int)promDiasVacaMes*mesesContrato;
		}
		else{
			if((aniosContrato<2)) {
				diasVac1=Integer.parseInt(parametroDias.getPrvcrgValor());
				if(mesesContrato>0)
				diasVac2=(int)promDiasVacaMes*mesesContrato;
				
			}else {
				if(aniosContrato>=2) {
					diasVac1=Integer.parseInt(parametroDias.getPrvcrgValor());
					diasVac2=Integer.parseInt(parametroDias.getPrvcrgValor());
				}
			}
		}
		if(diasVac2!=0) {
			sv2=new SaldoVacacion();
			sv2.setDtpsId(seleccionPersona.getDtpsId());
			sv2.setSlvcDiasAnticipados(0);
			sv2.setSlvcDiasRegistrados(0);
			sv2.setSlvcPeriodo(2);
			sv2.setSlvcDiasRestantes(diasVac2);
			sv2.setSlvcTotalHoras("00:00");
			sv2.setSlvcTotalDias(Integer.parseInt(parametroDias.getPrvcrgValor()));
			sv2.setSlvcNumfinsemana(0);
			sv2.setSlvcEstado(Estados.Activo.getId());
		}
		
		if(diasVac1!=0) {
			sv1=new SaldoVacacion();
			sv1.setDtpsId(seleccionPersona.getDtpsId());
			sv1.setSlvcDiasAnticipados(0);
			sv1.setSlvcDiasRegistrados(0);
			sv1.setSlvcPeriodo(1);
			sv1.setSlvcDiasRestantes(diasVac1);
			sv1.setSlvcTotalHoras("00:00");
			sv1.setSlvcTotalDias(Integer.parseInt(parametroDias.getPrvcrgValor()));
			sv1.setSlvcNumfinsemana(0);
			sv1.setSlvcEstado(Estados.Activo.getId());
		}
		//Insertar registros de Saldos de vacacion
		if(sv1!=null) {
			srvVacacion.SaldoVacacionInsertar(sv1);
		}
		if(sv2!=null) {
			srvVacacion.SaldoVacacionInsertar(sv2);
		}
		
		}else{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Advertencia.", "La fecha de contracion del Funcionario a concluido "));
		return;
		}
		
		
	
	}
	
	public void actualizarSaldoVacacionesDiasAcumulados( SaldoVacacion saldoVacacion1, SaldoVacacion saldoVacacion2 ) {
		
		int aniosContrato=0;
		int mesesContrato=0;
		int diasVac1 =0;
		int diasVac2=0;
		ParametroVacacionRegimen parametroDias=srvParamVacaciones.buscarPorId(ParametrosVacacion.NumDiasxAnio.getId(), seleccionPersona.getCtgId());
		double promDiasVacaMes=(Integer.parseInt(parametroDias.getPrvcrgValor())/12);
		
		int totaldias2 = 0, saldoDias2 = 0, diasReg2 = 0, diasAnt2 = 0, numFSem2 = 0, numFsTotal2 = 0;
		int totaldias1 = 0, saldoDias1 = 0, diasReg1 = 0, diasAnt1 = 0, numFSem1 = 0, numFsTotal1 = 0;

		if (saldoVacacion1 != null) {
			saldoDias1 = saldoVacacion1.getSlvcDiasRestantes();
			totaldias1 = saldoVacacion1.getSlvcTotalDias();
			diasReg1 = saldoVacacion1.getSlvcDiasRegistrados();
			diasAnt1 = saldoVacacion1.getSlvcDiasAnticipados();
			numFSem1 = saldoVacacion1.getSlvcNumfinsemana();
			numFsTotal1 = saldoVacacion1.getSlvcTotalDias() / 7;

		}
		if (saldoVacacion2 != null) {
			totaldias2 = saldoVacacion2.getSlvcTotalDias();
			saldoDias2 = saldoVacacion2.getSlvcDiasRestantes();
			diasReg2 = saldoVacacion2.getSlvcDiasRegistrados();
			diasAnt2 = saldoVacacion2.getSlvcDiasAnticipados();
			numFSem2 = saldoVacacion2.getSlvcNumfinsemana();
			numFsTotal2 = saldoVacacion2.getSlvcTotalDias() / 7;
		}

	
		Contrato contrato=srvContrato.obtenerPorId(seleccionPersona.getCtnId());
		//SaldoVacacion saldoVacacion=new SaldoVacacion();
		Calendar fechaActual= Calendar.getInstance();
		Calendar fechaContrato=Calendar.getInstance();
		fechaContrato.setTime(contrato.getCntFechaInicio());
		int valor = fechaActual.compareTo(fechaContrato);
		
		if(valor>0){
			Map<String, Integer> resultado=calcularTiempoContratacion(fechaActual,fechaContrato);
			aniosContrato=resultado.get("anios");
			mesesContrato=resultado.get("meses");
			if((aniosContrato==0)&&(mesesContrato>0)) {
				diasVac2=(int)promDiasVacaMes*mesesContrato;
				saldoVacacion2.setSlvcTotalDias(diasVac2);
				
				int nDiasAumentar=diasVac2-totaldias2;
				
				if(nDiasAumentar>0) {
					if(diasAnt2>0) {
						int resul=diasAnt2-nDiasAumentar;
						if(resul>=0) {
							saldoVacacion2.setSlvcDiasAnticipados(resul);	
						}else {
							int aum=resul*-1;
							saldoVacacion2.setSlvcDiasAnticipados(0);
							saldoVacacion2.setSlvcDiasRestantes(saldoDias2+aum);
						}
						
					}else {
						saldoVacacion2.setSlvcDiasRestantes(saldoDias2+nDiasAumentar);
					}
					
					srvVacacion.ActualizarSaldoVacacion(saldoVacacion2);
				 
				}
				
			}else {
				if(aniosContrato<2) {
					if((saldoVacacion2!=null)&&(saldoVacacion1!=null)) {
						
						diasVac2=(int)promDiasVacaMes*mesesContrato;
						saldoVacacion2.setSlvcTotalDias(diasVac2);
						
						int nDiasAumentar=diasVac2-totaldias2;
						
						if(nDiasAumentar>0) {
							if(diasAnt2>0) {
								int resul=diasAnt2-nDiasAumentar;
								if(resul>=0) {
									saldoVacacion2.setSlvcDiasAnticipados(resul);	
								}else {
									int aum=resul*-1;
									saldoVacacion2.setSlvcDiasAnticipados(0);
									saldoVacacion2.setSlvcDiasRestantes(saldoDias2+aum);
								}
								
							}else {
								saldoVacacion2.setSlvcDiasRestantes(saldoDias2+nDiasAumentar);
							}
						 
							//Actualizar Saldo de Vacacion actual
							srvVacacion.ActualizarSaldoVacacion(saldoVacacion2);
						}
					}else {
						if((saldoVacacion2!=null)&&(saldoVacacion1==null)) {
							
							int numTotal=Integer.parseInt(parametroDias.getPrvcrgValor());
							saldoVacacion2.setSlvcTotalDias(numTotal);
							int nDiasAumentar=numTotal-totaldias2;
							if(nDiasAumentar>0) {
								if(diasAnt2>0) {
									int resul=diasAnt2-nDiasAumentar;
									if(resul>=0) {
										saldoVacacion2.setSlvcDiasAnticipados(resul);	
									}else {
										int aum=resul*-1;
										saldoVacacion2.setSlvcDiasAnticipados(0);
										saldoVacacion2.setSlvcDiasRestantes(saldoDias2+aum);
									}
									
								}else {
									saldoVacacion2.setSlvcDiasRestantes(saldoDias2+nDiasAumentar);
								}
							 
								//Actualizar saldo de vacacion actual
								srvVacacion.ActualizarSaldoVacacion(saldoVacacion2);
							}
							
						if(mesesContrato>0) {
							
							SaldoVacacion slv=new SaldoVacacion();
							slv=srvVacacion.ObtenerSaldoVacacionPorPeriodo(0, seleccionPersona.getDtpsId());
							if(slv==null) {
								diasVac2=(int)promDiasVacaMes*mesesContrato;
								slv.setSlvcEstado(1);
								slv.setSlvcTotalDias(diasVac2);
								slv.setSlvcDiasAnticipados(0);
								slv.setSlvcDiasRegistrados(0);
								slv.setSlvcDiasRestantes(diasVac2);
								slv.setSlvcNumfinsemana(0);
								slv.setSlvcPeriodo(2);
								slv.setSlvcTotalHoras("00:00");
								//Insertar Vacacion
								srvVacacion.SaldoVacacionInsertar(slv);
								
							}else {
								diasVac2=(int)promDiasVacaMes*mesesContrato;
								slv.setSlvcEstado(1);
								slv.setSlvcTotalDias(diasVac2);
								slv.setSlvcDiasAnticipados(0);
								slv.setSlvcDiasRegistrados(0);
								slv.setSlvcDiasRestantes(diasVac2);
								slv.setSlvcNumfinsemana(0);
								slv.setSlvcPeriodo(2);
								slv.setSlvcTotalHoras("00:00");
								//analizar si es mejor Actualizar 
								srvVacacion.ActualizarSaldoVacacion(slv);
							}
							
						}
					}
					
				}
				
			}
		}
			
		}
		
	}

	public Map<String, Integer> calcularTiempoContratacion(Calendar fechaActual, Calendar fechaContrato) {
		
		Map<String, Integer> retorno = new HashMap<String, Integer>();

		int ultimoDiaNuevo = fechaActual.get(Calendar.DAY_OF_MONTH);
		int ultimoDiaAnterior = fechaContrato.get(Calendar.DAY_OF_MONTH);

		int mesNuevo = fechaActual.get(Calendar.MONTH);
		int mesAnterior = fechaContrato.get(Calendar.MONTH);

		int anioNuevo = fechaActual.get(Calendar.YEAR);
		int anioAnterior = fechaContrato.get(Calendar.YEAR);
		
		Calendar fecAct = Calendar.getInstance();

		fecAct.set(anioNuevo, mesNuevo, ultimoDiaNuevo);

		Calendar fUltSan = Calendar.getInstance();
		fUltSan.set(anioAnterior, mesAnterior, ultimoDiaAnterior);

		int monthDay[] = { 31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int anios = anioNuevo - anioAnterior;
		int meses=0;
		int dias=0;

		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();

		Calendar sanAhora = Calendar.getInstance();
		sanAhora.setTime(fUltSan.getTime());
		sanAhora.add(Calendar.YEAR, anios);

		if (fecAct.compareTo(sanAhora) < 0)
			anios--;

		retorno.put("anios", anios);
		retorno.put("meses", meses);

		return retorno;
	}

	
	
	

	// ================================================================GETTERS &
	// SETTERS================================================================================//

	/*
	 * public Dependencia getDependencia() { if (dependencia == null) {
	 * dependencia = srvDependencia.ObtenerPorId(seleccionPersona.getDpnId()); }
	 * return dependencia; }
	 */

	public List<Permiso> getListaPermisos() {
		if (seleccionPersona != null) {
			listaPermisos = srvPermiso.ListaPermisoPorDetallePuestoId(seleccionPersona.getDtpsId());
		}
		return listaPermisos;
	}

	public void setListaPermisos(List<Permiso> listaPermisos) {
		this.listaPermisos = listaPermisos;
	}

	public void setDependencia(Dependencia dependencia) {
		this.dependencia = dependencia;
	}

	/*
	 * public DetallePuestoDto getDetallePuestoEmpleado() { if
	 * (detallePuestoEmpleado == null) { detallePuestoEmpleado =
	 * srvDetallePuesto.BuscarPorId(seleccionPersona.getDtpsId()); if
	 * (detallePuestoEmpleado == null) { System.out.println(" salio nulo"); } }
	 * 
	 * return detallePuestoEmpleado; }
	 */

	public void setDetallePuestoEmpleado(DetallePuestoDto detallePuestoEmpleado) {
		this.detallePuestoEmpleado = detallePuestoEmpleado;
	}

	/*
	 * public FichaEmpleado getFichaEmpleado() { fichaEmpleado =
	 * srvFichaEmpleado.BuscarPorid(seleccionPersona.getFcemId()); return
	 * fichaEmpleado; }
	 */

	public void setFichaEmpleado(FichaEmpleado fichaEmpleado) {
		this.fichaEmpleado = fichaEmpleado;
	}

	public List<Vacacion> getListaVacacion() {
		if (seleccionPersona != null) {
			listaVacacion = srvVacacion.ListaVacacionesPorDetallePuestoId(seleccionPersona.getDtpsId());
		}
		return listaVacacion;
	}

	public void setListaVacacion(List<Vacacion> listaVacacion) {
		this.listaVacacion = listaVacacion;
	}

	public Vacacion getSeleccionVacacion() {

		return seleccionVacacion;
	}

	public void setSeleccionVacacion(Vacacion seleccionVacacion) {
		this.seleccionVacacion = seleccionVacacion;
	}

	public VacacionServicio getSrvVacacion() {
		return srvVacacion;
	}

	public void setSrvVacacion(VacacionServicio srvVacacion) {
		this.srvVacacion = srvVacacion;
	}

	/*
	 * public Puesto getPuesto() { if (puesto == null) {
	 * 
	 * puesto = srvPuesto.BuscarPorId(seleccionPersona.getPstId());
	 * 
	 * } return puesto; }
	 */

	public void setPuesto(Puesto puesto) {
		this.puesto = puesto;
	}

	/*
	 * public Regimen getRegimen() {
	 * 
	 * if (regimen == null) { regimen =
	 * srvRegimen.BuscarPorId(seleccionPersona.getRgmId()); } return regimen; }
	 */

	public void setRegimen(Regimen regimen) {
		this.regimen = regimen;
	}

	public SaldoVacacion getSalVacaCal1() {
		return salVacaCal1;
	}

	public void setSalVacaCal1(SaldoVacacion salVacaCal1) {
		this.salVacaCal1 = salVacaCal1;
	}

	public SaldoVacacion getSalVacaCal2() {
		return salVacaCal2;
	}

	public void setSalVacaCal2(SaldoVacacion salVacaCal2) {
		this.salVacaCal2 = salVacaCal2;
	}

	public Permiso getPermiso() {
		return permiso;
	}

	public void setPermiso(Permiso permiso) {
		this.permiso = permiso;
	}

	public Permiso getSeleccionPermiso() {
		return seleccionPermiso;
	}

	public void setSeleccionPermiso(Permiso seleccionPermiso) {
		this.seleccionPermiso = seleccionPermiso;
	}

	public Vacacion getVacacion() {
		return vacacion;
	}

	public void setVacacion(Vacacion vacacion) {
		this.vacacion = vacacion;
	}

	public List<SaldoVacacion> getSaldoVacacion() {
		return saldoVacacion;
	}

	public void setSaldoVacacion(List<SaldoVacacion> saldoVacacion) {
		this.saldoVacacion = saldoVacacion;
	}

	public Estados getEstado() {
		return estado;
	}

	public void setEstado(Estados estado) {
		this.estado = estado;
	}

	public DetallePuesto getDetallePuesto() {
		return detallePuesto;
	}

	public void setDetallePuesto(DetallePuesto detallePuesto) {
		this.detallePuesto = detallePuesto;
	}

	public PersonaDto getSeleccionPersona() {
		return seleccionPersona;
	}

	public void setSeleccionPersona(PersonaDto seleccionPersona) {
		this.seleccionPersona = seleccionPersona;
	}

	public boolean isEsActualizacion() {
		return esActualizacion;
	}

	public void setEsActualizacion(boolean esActualizacion) {
		this.esActualizacion = esActualizacion;
	}

	public SaldoVacacion getSaldoVacacion1() {
		return saldoVacacion1;
	}

	public void setSaldoVacacion1(SaldoVacacion saldoVacacion1) {
		this.saldoVacacion1 = saldoVacacion1;
	}

	public SaldoVacacion getSaldoVacacion2() {
		return saldoVacacion2;
	}

	public void setSaldoVacacion2(SaldoVacacion saldoVacacion2) {
		this.saldoVacacion2 = saldoVacacion2;
	}

	public boolean isValorJustificaHoras() {
		return valorJustificaHoras;
	}

	public void setValorJustificaHoras(boolean valorJustificaHoras) {
		this.valorJustificaHoras = valorJustificaHoras;
	}

	public boolean isHorasJustificadas() {
		return horasJustificadas;
	}

	public void setHorasJustificadas(boolean horasJustificadas) {
		this.horasJustificadas = horasJustificadas;
	}

	public boolean isRenderBtnImprimir() {
		return renderBtnImprimir;
	}

	public void setRenderBtnImprimir(boolean renderBtnImprimir) {
		this.renderBtnImprimir = renderBtnImprimir;
	}

	public boolean isEsBloqueado() {
		return esBloqueado;
	}

	public void setEsBloqueado(boolean esBloqueado) {
		this.esBloqueado = esBloqueado;
	}

	public boolean isEsPermitirIngreso() {
		return esPermitirIngreso;
	}

	public void setEsPermitirIngreso(boolean esPermitirIngreso) {
		this.esPermitirIngreso = esPermitirIngreso;
	}

	
}
