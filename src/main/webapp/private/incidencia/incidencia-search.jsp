<%@ page pageEncoding="UTF-8" %>
<%
String numeroFiltro = request.getParameter("numero") == null ? "" : request.getParameter("numero");
String tituloFiltro = request.getParameter("titulo") == null ? "" : request.getParameter("titulo");
String desdeFiltro = request.getParameter("desde") == null ? "" : request.getParameter("desde");
String hastaFiltro = request.getParameter("hasta") == null ? "" : request.getParameter("hasta");
String estadoFiltro = request.getParameter("estadoIncidenciaId") == null ? "" : request.getParameter("estadoIncidenciaId");
String tipoFiltro = request.getParameter("tipoIncidenciaId") == null ? "" : request.getParameter("tipoIncidenciaId");
String clienteIdFiltro = request.getParameter("clienteId") == null ? "" : request.getParameter("clienteId");
boolean admin = Boolean.TRUE.equals(request.getAttribute("admin"));
%>
<section class="search-box">
	<div class="search-heading"><h2>Busqueda estructurada de incidencias</h2></div>
	<form action="${pageContext.request.contextPath}/incidencia" method="get" accept-charset="UTF-8">
		<input type="hidden" name="action" value="search">
		<div class="field"><label for="numero">Numero de incidencia</label><input type="text" id="numero" name="numero" value="<%=numeroFiltro.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")%>" placeholder="INC-001"></div>
		<div class="field"><label for="titulo">Titulo</label><input type="text" id="titulo" name="titulo" value="<%=tituloFiltro.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")%>" placeholder="Texto del asunto"></div>
		<div class="field"><label for="desde">Fecha desde</label><input type="date" id="desde" name="desde" value="<%=desdeFiltro%>"></div>
		<div class="field"><label for="hasta">Fecha hasta</label><input type="date" id="hasta" name="hasta" value="<%=hastaFiltro%>"></div>
		<% if (admin) { %>
		<div class="field"><label for="estadoIncidenciaId">Estado</label><select id="estadoIncidenciaId" name="estadoIncidenciaId" data-selected="<%=estadoFiltro%>"><option value="">Todos los estados</option></select></div>
		<div class="field"><label for="tipoIncidenciaId">Tipo de incidencia</label><select id="tipoIncidenciaId" name="tipoIncidenciaId" data-selected="<%=tipoFiltro%>"><option value="">Todos los tipos</option></select></div>
		<div class="field"><label for="clienteId">ID del cliente</label><input type="number" min="1" id="clienteId" name="clienteId" value="<%=clienteIdFiltro%>" placeholder="ID de cliente"></div>
		<% } %>
		<div class="search-actions"><button type="submit" class="btn">Buscar incidencias</button><a class="clear-search" href="${pageContext.request.contextPath}/incidencia?action=search">Limpiar filtros</a></div>
	</form>
</section>
<% if (admin) { %>
<script>
$(function () {
  [{id:'#estadoIncidenciaId', url:'estados-incidencia', label:'Todos los estados'},
   {id:'#tipoIncidenciaId', url:'tipos-incidencia', label:'Todos los tipos'}].forEach(function (source) {
    var select = $(source.id), selected = select.data('selected');
    $.ajax({
      url: '<%=request.getContextPath()%>/' + source.url,
      type: 'GET',
      dataType: 'json',
      success: function (items) {
        select.empty().append($('<option>', {value:'', text:source.label}));
        $.each(items, function (_, item) {
          select.append($('<option>', {value:item.id, text:item.nombre, selected:String(item.id) === String(selected)}));
        });
      },
      error: function (xhr, status, error) {
        console.error('Error AJAX:', status, error);
        select.empty().append($('<option>', {value:'', text:'No se pudieron cargar las opciones'}));
      }
    });
  });
});
</script>
<% } %>
