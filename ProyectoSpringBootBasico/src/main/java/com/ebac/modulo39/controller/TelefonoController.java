package com.ebac.modulo39.controller;

import com.ebac.modulo39.dto.Telefono;
import com.ebac.modulo39.service.TelefonoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class TelefonoController {

    @Autowired
    TelefonoService telefonoService;

    @GetMapping("/telefonos")
    public ResponseWrapper<List<Telefono>> obtenerTelefonos() {
        // Devuelve un objeto User que se convertirá automáticamente en JSON/XML en la respuesta.
        List<Telefono> telefonos = telefonoService.obtenerTelefonos();
        ResponseEntity<List<Telefono>> responseEntity = ResponseEntity.ok(telefonos);

        return new ResponseWrapper<>(true, "Listado de telefonos", responseEntity);
    }

    @GetMapping("/telefonos/{id}")
    public ResponseWrapper<Telefono> obtenerTelefonoPorId(@PathVariable Long id) {
        // Lógica para obtener el telefono por su ID
        // Devuelve un objeto User que se convertirá automáticamente en JSON/XML en la respuesta.
        Optional<Telefono> telefonoOptional = telefonoService.obtenerTelefonoPorId(id);
        ResponseEntity<Telefono> responseEntity = telefonoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

        return new ResponseWrapper<>(true, "Informacion del telefono " + id, responseEntity);
    }

    @PostMapping("/telefonos")
    public ResponseWrapper<Telefono> crearTelefono(@RequestBody Telefono telefono) {
        // Lógica para crear un nuevo telefono
        // Retorna ResponseEntity con el objeto User en el cuerpo y un código de estado 201 (CREATED) en la respuesta.
        try {
            Telefono telefonoCreado = telefonoService.crearTelefono(telefono);
            ResponseEntity<Telefono> responseEntity = ResponseEntity.created(new URI("http://localhost/telefonos")).body(telefonoCreado);
            return new ResponseWrapper<>(true, "Telefono creado exitosamente", responseEntity);
        } catch (Exception e) {
            ResponseEntity<Telefono> responseEntity = ResponseEntity.badRequest().build();
            return new ResponseWrapper<>(false, e.getMessage(), responseEntity);
        }
    }

    @PutMapping("/telefonos/{id}")
    public ResponseWrapper<Telefono> actualizarTelefono(@PathVariable Long id, @RequestBody Telefono telefonoActualizado) {
        // Lógica para actualizar el telefono con el ID proporcionado
        // Retorna ResponseEntity con el objeto Telefono actualizado en el cuerpo y un código de estado 200 (OK) en la respuesta.
        Optional<Telefono> telefonoOptional = telefonoService.obtenerTelefonoPorId(id);

        if (telefonoOptional.isPresent()) {
            telefonoActualizado.setIdTelefono(telefonoOptional.get().getIdTelefono());
            telefonoService.actualizarTelefono(telefonoActualizado);

            ResponseEntity<Telefono> responseEntity = ResponseEntity.ok(telefonoActualizado);
            return new ResponseWrapper<>(true, "Telefono actualizado correctamente", responseEntity);
        } else {
            ResponseEntity<Telefono> responseEntity = ResponseEntity.notFound().build();
            return new ResponseWrapper<>(false, "El telefono indicado no existe", responseEntity);
        }
    }

    @DeleteMapping("/telefonos/{id}")
    public ResponseWrapper<Void> eliminarTelefono(@PathVariable Long id) {
        // Lógica para eliminar el telefono con el ID proporcionado
        // Retorna ResponseEntity con un código de estado 204 (NO_CONTENT) en la respuesta.
        telefonoService.eliminarTelefono(id);

        ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
        return new ResponseWrapper<>(true, "Telefono eliminado correctamente", responseEntity);
    }
}
