<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ruben.bluewave.model.Cliente"%>
<%@ page import="com.ruben.bluewave.model.EmpleadoDTO"%>
<%
Object usuario = request.getSession().getAttribute("usuario");
String nombreUsuario = usuario instanceof Cliente ? ((Cliente) usuario).getNombre()
		: usuario instanceof EmpleadoDTO ? ((EmpleadoDTO) usuario).getNombre() : null;
String rolUsuario = (String) request.getSession().getAttribute("rolUsuario");
String contextPath = request.getContextPath();
String currentPath = request.getRequestURI();
boolean authPage = currentPath.endsWith("login.jsp") || currentPath.endsWith("registro.jsp");
boolean incidentsPage = currentPath.endsWith("incidencia-search-view.jsp") || currentPath.endsWith("incidencia-detail-view.jsp");
%>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Bluewave</title>
	<link rel="stylesheet" href="<%=contextPath%>/css/base.css">
	<link rel="stylesheet" href="<%=contextPath%>/css/header.css">
	<link rel="stylesheet" href="<%=contextPath%>/css/forms.css">
	<link rel="stylesheet" href="<%=contextPath%>/css/profile.css">
	<link rel="stylesheet" href="<%=contextPath%>/css/incidencias.css">
	<script src="<%=contextPath%>/js/jquery-3.7.1.min.js"></script>
</head>
<body class="<%=authPage ? "auth-page" : ""%>">
<div class="app-shell">
	<aside class="sidebar">
		<a class="brand" href="<%=contextPath%>/" aria-label="Bluewave, panel principal">
			<span class="brand-mark">B</span><span>BlueWave</span>
		</a>
<nav class="side-nav" aria-label="Navegacion principal">
			<a class="nav-link" href="<%=contextPath%>/"><span class="nav-icon">▦</span>Panel principal</a>
			<span class="nav-link nav-disabled"><span class="nav-icon">▤</span>Contratos</span>
			<a class="nav-link" href="<%=contextPath%>/Cliente?action=profile"><span class="nav-icon">♙</span>Clientes</a>
			<a class="nav-link <%=incidentsPage ? "active" : ""%>" href="<%=contextPath%>/incidencia?action=search"><span class="nav-icon">△</span>Incidencias</a>
			<span class="nav-link nav-disabled"><span class="nav-icon">▥</span>Estadisticas</span>
		</nav>
		<div class="sidebar-account">
			<% if (usuario != null) { %>
				<strong><%=nombreUsuario%></strong>
				<span class="account-role"><%=rolUsuario == null ? "Usuario" : rolUsuario%></span>
				<a class="logout-link" href="<%=contextPath%>/Cliente?action=logout"><span>↪</span>Cerrar sesion</a>
			<% } else { %>
				<strong>Bienvenido</strong>
				<a class="logout-link" href="<%=contextPath%>/Cliente">Iniciar sesion</a>
			<% } %>
		</div>
	</aside>
	<div class="main-shell">
		<main class="container">
