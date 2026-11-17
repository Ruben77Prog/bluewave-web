<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp"%>

<%@ page import="com.ruben.bluewave.model.IncidenciaDTO"%>

<%
IncidenciaDTO incidencia = (IncidenciaDTO) request.getAttribute("incidencia");
%>

<div class="detail-card">

	<h1>
		<%=incidencia.getTitulo()%>
	</h1>
	<p><strong>Numero:</strong> <%=incidencia.getNumeroIncidencia()%></p>
	<p><strong>Tipo:</strong> <%=incidencia.getTipoIncidenciaNombre()%></p>
	<p><strong>Estado:</strong> <%=incidencia.getEstadoIncidenciaNombre()%></p>
	<p><strong>Fecha:</strong> <%=incidencia.getFechaIncidencia()%></p>

	<p class="detail-description">
		<%=incidencia.getDescripcion()%>
	</p>

	<a class="btn"
		href="<%=request.getContextPath()%>/incidencia?action=search">

		Volver a incidencias </a>

</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp"%>
