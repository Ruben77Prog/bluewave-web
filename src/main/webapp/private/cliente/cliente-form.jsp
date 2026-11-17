<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp"%>

<%@ page import="com.ruben.bluewave.model.Cliente"%>

<%
Cliente cliente = (Cliente) request.getAttribute("cliente");
String error = (String) request.getAttribute("error");
String message = (String) request.getAttribute("message");
%>

<div class="page-title">
	<h1>Perfil del cliente</h1>
	<p>Actualiza tus datos y tu imagen de perfil.</p>
</div>

<div class="profile-card">
	<% if (error != null) { %>
	<p class="error-message"><%=error%></p>
	<% } %>
	<% if (message != null) { %>
	<p class="success-message"><%=message%></p>
	<% } %>

	<form action="<%=request.getContextPath()%>/Cliente?action=profile"
		method="post" enctype="multipart/form-data" accept-charset="UTF-8">
		<label for="nombre">Nombre</label>
		<input type="text" id="nombre" name="nombre" value="<%=cliente.getNombre() == null ? "" : cliente.getNombre()%>" required>

		<label for="apellido1">Primer apellido</label>
		<input type="text" id="apellido1" name="apellido1" value="<%=cliente.getApellido1() == null ? "" : cliente.getApellido1()%>">

		<label for="apellido2">Segundo apellido</label>
		<input type="text" id="apellido2" name="apellido2" value="<%=cliente.getApellido2() == null ? "" : cliente.getApellido2()%>">

		<label for="telefono">Telefono</label>
		<input type="text" id="telefono" name="telefono" value="<%=cliente.getTelefono() == null ? "" : cliente.getTelefono()%>">

		<label for="imagen">Imagen de perfil</label>
		<input class="file-input" type="file" id="imagen_perfil" name="imagen_perfil"
			accept="image/jpeg,image/png,image/gif,image/webp">

		<div class="form-actions">
			<input class="btn" type="submit" value="Guardar perfil">
		</div>
	</form>

	<% if (session.getAttribute("fotoFormato") != null) { %>
	<div class="profile-image-preview">
		<img src="<%=request.getContextPath()%>/Cliente?action=photo" alt="Imagen de perfil">
		<a href="<%=request.getContextPath()%>/Cliente?action=photo&amp;download=true">Descargar foto</a>
	</div>
	<% } %>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp"%>
