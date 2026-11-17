<%@ page pageEncoding="UTF-8" %>
<%
String numeroFiltro = request.getParameter("numero") == null ? "" : request.getParameter("numero");
String tituloFiltro = request.getParameter("titulo") == null ? "" : request.getParameter("titulo");
String desdeFiltro = request.getParameter("desde") == null ? "" : request.getParameter("desde");
String hastaFiltro = request.getParameter("hasta") == null ? "" : request.getParameter("hasta");
%>
<section class="search-box">
	<div class="search-heading"><h2>Busqueda estructurada de incidencias</h2></div>
	<form action="${pageContext.request.contextPath}/incidencia" method="get" accept-charset="UTF-8">
		<input type="hidden" name="action" value="search">
		<div class="field"><label for="numero">Numero de incidencia</label><input type="text" id="numero" name="numero" value="<%=numeroFiltro.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")%>" placeholder="INC-001"></div>
		<div class="field"><label for="titulo">Titulo</label><input type="text" id="titulo" name="titulo" value="<%=tituloFiltro.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")%>" placeholder="Texto del asunto"></div>
		<div class="field"><label for="desde">Fecha desde</label><input type="date" id="desde" name="desde" value="<%=desdeFiltro%>"></div>
		<div class="field"><label for="hasta">Fecha hasta</label><input type="date" id="hasta" name="hasta" value="<%=hastaFiltro%>"></div>
		<div class="search-actions"><button type="submit" class="btn">Buscar incidencias</button><a class="clear-search" href="${pageContext.request.contextPath}/incidencia?action=search">Limpiar filtros</a></div>
	</form>
</section>
