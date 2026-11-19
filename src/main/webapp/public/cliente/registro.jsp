<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp"%>
<% String error = (String) request.getAttribute("error"); %>
<div class="page-title"><h1>Crear cuenta de cliente</h1><p>Regístrate para consultar tus incidencias.</p></div>
<div class="profile-card">
<% if (error != null) { %><p class="error-message"><%=error%></p><% } %>
<form action="<%=request.getContextPath()%>/Cliente?action=register" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
<label for="nombre">Nombre</label><input id="nombre" name="nombre" required>
<label for="apellido1">Primer apellido</label><input id="apellido1" name="apellido1" required>
<label for="apellido2">Segundo apellido</label><input id="apellido2" name="apellido2">
<label for="dni">DNI</label><input id="dni" name="dni" required>
<label for="telefono">Teléfono</label><input id="telefono" name="telefono" type="tel" required>
<label for="fechaNacimiento">Fecha de nacimiento</label><input id="fechaNacimiento" name="fechaNacimiento" type="date" required>
<label for="generoId">Género</label><select id="generoId" name="generoId" required><option value="">Cargando géneros...</option></select>
<label for="email">Correo electrónico</label><input id="email" name="email" type="email" required>
<label for="password">Contraseña (mínimo 8 caracteres)</label><input id="password" name="password" type="password" minlength="8" required>
<h2>Dirección</h2>
<label for="calle">Calle</label><input id="calle" name="calle" required>
<label for="numero">Número</label><input id="numero" name="numero" required>
<label for="piso">Piso</label><input id="piso" name="piso">
<label for="puerta">Puerta</label><input id="puerta" name="puerta">
<label for="codigoPostal">Código postal</label><input id="codigoPostal" name="codigoPostal" required>
<label for="paisId">País</label><select id="paisId"><option value="">Cargando países...</option></select>
<label for="provinciaId">Provincia</label><select id="provinciaId" disabled><option value="">Selecciona un país primero</option></select>
<label for="ciudadId">Ciudad</label><select id="ciudadId" name="ciudadId" required disabled><option value="">Selecciona una provincia primero</option></select>
<label for="imagen_perfil">Imagen de perfil (opcional)</label><input id="imagen_perfil" name="imagen_perfil" type="file" accept="image/jpeg,image/png,image/gif,image/webp">
<button class="btn" type="submit">Registrarme</button>
</form></div>
<script>
$(function () {
  function fill(select, items, placeholder) {
    select.empty().append($('<option>', {value:'', text:placeholder}));
    $.each(items, function (_, item) { select.append($('<option>', {value:item.id, text:item.nombre})); });
  }
  $.ajax({
    url: '<%=request.getContextPath()%>/generos',
    type: 'GET',
    dataType: 'json',
    success: function (items) { fill($('#generoId'), items, 'Selecciona tu género'); },
    error: function (xhr, status, error) {
      console.error('Error AJAX:', status, error);
      fill($('#generoId'), [], 'No se pudieron cargar los géneros');
    }
  });
  $.ajax({
    url: '<%=request.getContextPath()%>/paises',
    type: 'GET',
    dataType: 'json',
    success: function (items) { fill($('#paisId'), items, 'Selecciona un país'); },
    error: function (xhr, status, error) {
      console.error('Error AJAX:', status, error);
      fill($('#paisId'), [], 'No se pudieron cargar los países');
    }
  });
  $('#paisId').on('change', function () {
    var provincia = $('#provinciaId'), ciudad = $('#ciudadId');
    provincia.prop('disabled', true); ciudad.prop('disabled', true);
    fill(provincia, [], 'Cargando provincias...'); fill(ciudad, [], 'Selecciona una provincia primero');
    if (!this.value) return;
    $.ajax({
      url: '<%=request.getContextPath()%>/provincia',
      type: 'GET',
      data: {'id-pais':this.value},
      dataType: 'json',
      success: function (items) {
        fill(provincia, items, 'Selecciona una provincia'); provincia.prop('disabled', false);
      },
      error: function (xhr, status, error) {
        console.error('Error AJAX:', status, error);
        fill(provincia, [], 'No se pudieron cargar las provincias');
      }
    });
  });
  $('#provinciaId').on('change', function () {
    var ciudad = $('#ciudadId'); ciudad.prop('disabled', true);
    fill(ciudad, [], 'Cargando ciudades...');
    if (!this.value) return;
    $.ajax({
      url: '<%=request.getContextPath()%>/localidad',
      type: 'GET',
      data: {provinceId:this.value},
      dataType: 'json',
      success: function (items) {
        fill(ciudad, items, 'Selecciona una ciudad'); ciudad.prop('disabled', false);
      },
      error: function (xhr, status, error) {
        console.error('Error AJAX:', status, error);
        fill(ciudad, [], 'No se pudieron cargar las ciudades');
      }
    });
  });
});
</script>
<%@ include file="/WEB-INF/jsp/common/footer.jsp"%>
