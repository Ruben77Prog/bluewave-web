<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ruben.bluewave.model.IncidenciaDTO" %>
<%@ page import="com.ruben.bluewave.model.Results" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp"%>
<%
Results<IncidenciaDTO> results = (Results<IncidenciaDTO>) request.getAttribute("results");
String error = (String) request.getAttribute("error");
boolean esAdmin = Boolean.TRUE.equals(request.getAttribute("admin"));
%>
<section class="incidencias-page">
	<div class="incidencias-heading">
		<div>
			<h1>Incidencias</h1>
			<p><%=results == null ? (esAdmin ? "Busca incidencias por estado, cliente o tipo" : "Consulta las incidencias de tu cuenta") : results.getTotal() + (esAdmin ? (results.getTotal() == 1 ? " incidencia encontrada" : " incidencias encontradas") : (results.getTotal() == 1 ? " incidencia de tu cuenta" : " incidencias de tu cuenta"))%></p>
		</div>
	</div>
	<% if (error != null) { %><p class="error-message"><%=error%></p><% } %>
	<%@ include file="/private/incidencia/incidencia-search.jsp"%>
	<% if (!esAdmin) { %><div class="customer-notice">Como cliente, puedes consultar las incidencias asociadas a tu cuenta. Para modificar o eliminar una incidencia, contacta con soporte.</div><% } %>
	<% if (results != null) { %>
		<% if (results.getPage().isEmpty()) { %>
			<div class="empty-state"><strong>No hay incidencias que coincidan</strong><p>Prueba con otros terminos o ajusta el periodo de busqueda.</p></div>
		<% } else { %>
			<ol class="incidencias">
			<% for (IncidenciaDTO incidencia : results.getPage()) { %>
				<li class="incidencia-card">
					<div class="incident-meta"><span class="incident-number"><%=incidencia.getNumeroIncidencia()%></span></div>
					<a class="incidencia-title" href="<%=contextPath%>/incidencia?action=detail&id=<%=incidencia.getId()%>"><%=incidencia.getTitulo()%></a>
				<p>Consulta el detalle de esta incidencia y su informacion asociada.</p>
				</li>
			<% } %>
			</ol>
		<% } %>
	<% } %>
</section>
<%@ include file="/WEB-INF/jsp/common/footer.jsp"%>
