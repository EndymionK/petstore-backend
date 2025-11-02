package com.petstoreweb.petstore_backend;

import com.petstoreweb.petstore_backend.entity.Usuario;
import com.petstoreweb.petstore_backend.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling // Habilita las tareas programadas (@Scheduled)
public class PetstoreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetstoreBackendApplication.class, args);
    }

    /**
     * Este Bean se ejecutará automáticamente al iniciar la aplicación.
     * Es perfecto para realizar pruebas iniciales o cargar datos.
     */
    @Bean
    public CommandLineRunner testDatabaseConnection(UsuarioService usuarioService) {
        return args -> {
            System.out.println("==================================================");
            System.out.println("==> INICIANDO PRUEBA DE CONEXIÓN Y LECTURA DE BD <==");
            System.out.println("==================================================");

            // Usamos el servicio para obtener todos los usuarios.
            List<Usuario> usuarios = usuarioService.findAllUsers();

            if (usuarios.isEmpty()) {
                System.out.println("==> PRUEBA FINALIZADA: No se encontraron usuarios en la tabla tblUsuarios.");
            } else {
                System.out.println("==> PRUEBA EXITOSA: Se encontraron " + usuarios.size() + " usuarios:");
                // Imprimimos la información de cada usuario.
                for (Usuario user : usuarios) {
                    System.out.println("    -> ID: " + user.getId() + ", Nombre: " + user.getNombre() + ", Rol: " + user.getRol());
                }
            }
            System.out.println("==================================================");
        };
    }
}