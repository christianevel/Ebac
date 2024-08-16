package com.ebac.modulo39.controller;

import com.ebac.modulo39.dto.Usuario;
import com.ebac.modulo39.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOError;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    UsuarioService usuarioService;

    @InjectMocks
    UsuarioController usuarioController;

     @Test
    void obtenerUsuarios() {
        int usuarios = 5;
        List<Usuario> usuariosListExpected = crearUsuarios(usuarios);

        // Configuramos el comportamiento del mock
        when(usuarioService.obtenerUsuarios()).thenReturn(usuariosListExpected);

        // Ejecutamos el metodo del controlador
        ResponseWrapper<List<Usuario>> responseWrapper = usuarioController.obtenerUsuarios();
        List<Usuario> usuariosListActual = responseWrapper.getResponseEntity().getBody();

        // Validamos el resultado
         assertNotNull(usuariosListActual);
        assertEquals(usuarios, usuariosListActual.size());
        assertEquals(usuariosListExpected, usuariosListActual);
    }

    @Test
    void obtenerUsuariosCuandoNoExisten() {
        // Configuramos el comportamiento del mock
        when(usuarioService.obtenerUsuarios()).thenReturn(List.of());

        // Ejecutamos el metodo del controlador
        ResponseWrapper<List<Usuario>> usuarioListActual = usuarioController.obtenerUsuarios();
        List<Usuario> usuarioList = usuarioListActual.getResponseEntity().getBody();

        // Validamos el resultado
        assertNotNull(usuarioList);
        assertTrue(usuarioList.isEmpty());

        verify(usuarioService, times(1)).obtenerUsuarios();
    }

    @Test
    void obtenerUsuarioPorId() {
        long idUsuario = 1;
        Optional<Usuario> usuarioExpected = Optional.of(crearUsuarios(1).get(0));

        // Configuramos el comportamiento del mock
        when(usuarioService.obtenerUsuarioPorId(idUsuario)).thenReturn(usuarioExpected);

        // Ejecutamos el metodo del controlador
        ResponseWrapper<Usuario> usuarioResponseWrapper = usuarioController.obtenerUsuarioPorId(idUsuario);
        ResponseEntity<Usuario> responseEntity = usuarioResponseWrapper.getResponseEntity();
        Usuario usuarioActual = responseEntity.getBody();

        // Validamos el resultado
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(usuarioActual);
        assertEquals("Nombre1", usuarioActual.getNombre());
    }

    @Test
    void obtenerUsuarioPorIdCuandoNoExiste() {
        long idUsuario = 1;

        // Configuramos el comportamiento del mock
        when(usuarioService.obtenerUsuarioPorId(idUsuario)).thenReturn(Optional.empty());

        // Ejecutamos el metodo del controlador
        ResponseWrapper<Usuario> usuarioResponseWrapper = usuarioController.obtenerUsuarioPorId(idUsuario);
        ResponseEntity<Usuario> responseEntity = usuarioResponseWrapper.getResponseEntity();
        Usuario usuarioActual = responseEntity.getBody();

        // Validamos el resultado
        assertEquals(404, responseEntity.getStatusCode().value());
        assertTrue(Objects.isNull(usuarioActual));
    }

    @Test
    void crearUsuario() throws Exception {
        Usuario usuarioExpected = crearUsuarios(1).get(0);

        // Configuramos el comportamiento del mock
        when(usuarioService.crearUsuario(usuarioExpected)).thenReturn(usuarioExpected);

        // Ejecutamos el metodo del controlador
        ResponseWrapper<Usuario> usuarioResponseWrapper = usuarioController.crearUsuario(usuarioExpected);
        ResponseEntity<Usuario> responseEntity = usuarioResponseWrapper.getResponseEntity();
        Usuario usuarioActual = responseEntity.getBody();

        // Validamos el resultado
        assertEquals(201, responseEntity.getStatusCode().value());
        assertNotNull(usuarioActual);
        assertEquals(usuarioExpected, usuarioActual);
        //assertTrue(Objects.isNull(usuarioActual));
    }

    @Test
    void actualizarUsuario() {
        int idUsuario = 5;
        String nombreActualizado = "Beatriz";
        int edadActualizada = 25;

        Usuario usuarioAntiguo = new Usuario();
        usuarioAntiguo.setIdUsuario(idUsuario);
        usuarioAntiguo.setNombre("Julieta");
        usuarioAntiguo.setEdad(28);

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre(nombreActualizado);
        usuarioActualizado.setEdad(edadActualizada);

        // Configuramos el comportamiento del mock
        when(usuarioService.obtenerUsuarioPorId((long) idUsuario)).thenReturn(Optional.of(usuarioAntiguo));
        doNothing().when(usuarioService).actualizarUsuario(usuarioActualizado);

        // Ejecutamos el metodo del controlador
        ResponseWrapper<Usuario> usuarioResponseWrapper = usuarioController.actualizarUsuario((long) idUsuario, usuarioActualizado);
        ResponseEntity<Usuario> responseEntity = usuarioResponseWrapper.getResponseEntity();
        Usuario usuarioActual = responseEntity.getBody();

        // Validamos el resultado
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(usuarioActual);
        assertEquals(idUsuario, usuarioActual.getIdUsuario());
        assertEquals(nombreActualizado, usuarioActual.getNombre());
        assertEquals(edadActualizada, usuarioActual.getEdad());
    }

    @Test
    void actualizarUsuarioCuandoElUsuarioNoExiste() {
        long idUsuario = 5;
        String nombreActualizado = "Beatriz";
        int edadActualizada = 25;

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre(nombreActualizado);
        usuarioActualizado.setEdad(edadActualizada);

        // Configuramos el comportamiento del mock
        when(usuarioService.obtenerUsuarioPorId(idUsuario)).thenReturn(Optional.empty());

        // Ejecutamos el metodo del controlador
        ResponseWrapper<Usuario> usuarioResponseWrapper = usuarioController.actualizarUsuario(idUsuario, usuarioActualizado);
        ResponseEntity<Usuario> responseEntity = usuarioResponseWrapper.getResponseEntity();
        Usuario usuarioActual = responseEntity.getBody();

        // Validamos el resultado
        assertEquals(404, responseEntity.getStatusCode().value());
        assertNull(usuarioActual);
        verify(usuarioService, never()).actualizarUsuario(usuarioActualizado);
    }

    @Test
    void eliminarUsuario() {
        long idUsuario = 1;

        //Mock del usuario
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        // Configuramos el comportamiento del mock
        when(usuarioService.obtenerUsuarioPorId(idUsuario)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).eliminarUsuario(idUsuario);

        // Ejecutamos el metodo del controlador
        ResponseWrapper<Void> responseWrapper = usuarioController.eliminarUsuario(idUsuario);

        // Validamos el resultado
        assertEquals(204, responseWrapper.getResponseEntity().getStatusCode().value());
        verify(usuarioService, atLeastOnce()).eliminarUsuario(idUsuario);
    }

    @Test
    void crearUsuarioCuandoSeaMenorA18() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Nombre");
        usuario.setEdad(10);

        // Configuramos el comportamiento del mock
        doThrow(Exception.class).when(usuarioService).crearUsuario(usuario);

        // Ejecutamos el metodo del controlador
        ResponseWrapper<Usuario> usuarioResponseWrapper = usuarioController.crearUsuario(usuario);
        ResponseEntity<Usuario> responseEntity = usuarioResponseWrapper.getResponseEntity();
        Usuario usuarioActual = responseEntity.getBody();

        assertEquals(400, responseEntity.getStatusCode().value());
        assertNull(usuarioActual);
    }

    private List<Usuario> crearUsuarios(int elementos) {
        return IntStream.range(1, elementos+1)
                .mapToObj(i -> {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(i);
                    usuario.setNombre("Nombre" + i);
                    usuario.setEdad(15 + i);
                    return usuario;
                }).collect(Collectors.toList());
    }
}