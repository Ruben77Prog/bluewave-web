<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp"%>
<% String error = (String) request.getAttribute("error"); %>
<div class="page-title"><h1>Crear cuenta de cliente</h1><p>Registrate para consultar tus incidencias.</p></div>
<div class="profile-card">
<% if (error != null) { %><p class="error-message"><%=error%></p><% } %>
<form action="<%=request.getContextPath()%>/Cliente?action=register" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
<label for="nombre">Nombre</label><input id="nombre" name="nombre" required>
<label for="apellido1">Primer apellido</label><input id="apellido1" name="apellido1" required>
<label for="email">Correo electronico</label><input id="email" name="email" type="email" required>
<label for="password">Contrasena (minimo 8 caracteres)</label><input id="password" name="password" type="password" minlength="8" required>
<label for="imagen_perfil">Imagen de perfil (opcional)</label><input id="imagen_perfil" name="imagen_perfil" type="file" accept="image/jpeg,image/png,image/gif,image/webp">
<button class="btn" type="submit">Registrarme</button>
</form></div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp"%>
