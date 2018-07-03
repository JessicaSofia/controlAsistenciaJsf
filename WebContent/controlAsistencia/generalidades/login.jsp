<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/general.css">
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/fontStyles.css">
		<title>Inicio de Sesión</title>
	</head>
	<body class="login"  >
	
		<script type="text/javascript">
			document.onkeydown = function {
			if(event.keyCode == 123) {
			return false;
			}
			if(e.ctrlKey && e.shiftKey && e.keyCode == 'i'.charCodeAt(0)){
			return false;
			}
			if(e.ctrlKey && e.shiftKey && e.keyCode == 'j'.charCodeAt(0)){
			return false;
			}
			if(e.ctrlKey && e.keyCode == 'u'.charCodeAt(0)){
			return false;
			}
			}
		</script>
	
		<div >
			<div style="position:absolute;top:2vw;left:2vw">
				<div >
					<h1 style="font-size:6.8vw;color:#48B1F0;margin-bottom:5px; ">Bienvenido</h1>
				</div>
				<div style="border-bottom:3px solid #E20613;">
				</div>
				<div style="width: 100%;">
					<h1 style="font-size: 2.3vw;color: #FFFFFF;margin: 0;padding: 0;font-weight: 500;
    						   align-items: center;text-align: center;"></h1>
				</div>
				<div   >
					<h1 style="font-size:1.3vw;color:#FFFFFF;letter-spacing:0.45vw; ">SISTEMA DE CCONTROL DE ASISTENCIA</h1>
				</div>
			</div>
			<a href="#dialogoLogin" onclick="document.getElementById('j_username').value='';document.getElementById('j_password').value='';">
				<div class="iniciarSesion">
				</div>
			</a>
			<div style="background-image:url('<%=request.getContextPath()%>/img/logoUce.png');position:absolute;bottom:0%;right:10px;width:6vw;height:5vw;background-size: cover;">
			</div>
			
			<a href="<%=request.getContextPath()%>/controlAsistencia/generalidades/index.jsf" >
				<div class="salirLogin">
					<h1 style="color: rgb(0,118,189); font-size: 1em;  font-size: small;">Regresar</h1>
				</div>
			</a>
			
			
			<div style="position:absolute;bottom:1%;right: 8%;letter-spacing:0.1em;text-align:right;width:46vw;line-height: 5px;">
				<p style="font-size: 0.9vw;">&#169; Copyright Dirección de Tecnologías de la </p>
				<p style="font-size: 0.9vw;">Información y Telecomunicaciones</p>
				<p style="font-size: 0.9vw;">UCE 2016.</p>
			</div>
			
			<div id="dialogoLogin" class="modalDialog">
				<div>
					<div style="height:30px;">
						<a href="#close" title="Cerrar" class="close">X</a>
					</div>
					<div style="position:absolute;top: 20%;left: 50%;transform: translate(-50%, -50%);">
						<h1 style="font-weight: bold;font-size:1.69vw;color:#FFFFFF;letter-spacing: 0.05em;">INICIAR SESIÓN</h1>
					</div>
					<form action="<%=request.getContextPath()%>/j_spring_security_check" method="post" class="formularioLogin" >
						<div style="position: absolute;left: 60%;transform: translate(-50%, -50%);width: 100%;">
							<input type="text" id="j_username" name="j_username" style="font-size:1.5vw;width:17vw;line-height:2.57vw;padding-left:1vw;"/>
							<img src="<%=request.getContextPath()%>/img/iconoUsuario.png" style="position:fixed;height:3vw;"/>
						</div>
						<div style="position: absolute;top: 50%;left: 60%;transform: translate(-50%, -50%);width: 100%;">
							<input type="password" id="j_password" name="j_password" style="font-size:1.5vw;width:17vw;line-height:2.57vw;padding-left:1vw;" />
							<img src="<%=request.getContextPath()%>/img/iconoClave.png" style="position:fixed;height:3vw;"/>
						</div>
						<div style="position: absolute;top: 100%;left: 60%;transform: translate(-50%, -50%);width: 100%;">
							<input type="submit" value="Ingresar" style="line-height:2.57vw;;width:21.4vw;font-size:1.5vw;background-color:#AA9003;border-width:0px;color:#FFFFFF;"/>
						</div>
						<div style="position:absolute;top: 100%;left: 50%;transform: translate(-50%, -50%);">
					</div>
					</form>
					<div>
					</div>
				</div>
			</div>
		</div>
	</body>

</html>