
package main;

import entities.Libro;
import entities.FichaBibliografica;
import service.LibroService;
import service.FichaBibliograficaService;

import java.util.List;
import java.util.Scanner;


 // Manejo de todas las operaciones CRUD y búsquedas relevantes.

public class AppMenu {

    private final LibroService libroService;
    private final FichaBibliograficaService fichaService;
    private final Scanner scanner;

    public AppMenu() {
        this.libroService = new LibroService();
        this.fichaService = new FichaBibliograficaService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("Bienvenido al Sistema de Gestion Bibliografica");

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elija una opcion: ");

            try {
                switch (opcion) {
                    case 1 -> crearLibroConFicha();
                    case 2 -> buscarLibroPorId();
                    case 3 -> listarLibros();
                    case 4 -> buscarLibroPorTitulo();
                    case 5 -> actualizarLibro();
                    case 6 -> eliminarLibro();
                    case 7 -> buscarFichaPorIsbn();
                    case 8 -> verFichaDeLibro();
                    case 9 -> System.out.println("Gracias por usar el sistema!");
                    default -> System.out.println("Opcion invalida. Intente nuevamente.");
                }
            } catch (Exception e) {
                System.err.println("Error inesperado: " + e.getMessage());
            }
            if (opcion != 9) esperarEnter();
        } while (opcion != 9);

        scanner.close();
    }

    private void mostrarMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Crear Libro");
        System.out.println("2. Buscar Libro por ID");
        System.out.println("3. Listar todos los Libros activos");
        System.out.println("4. Buscar Libro por titulo");
        System.out.println("5. Actualizar Libro");
        System.out.println("6. Eliminar Libro");
        System.out.println("7. Buscar Ficha por ISBN");
        System.out.println("8. Ver Ficha asociada a un Libro");
        System.out.println("9. Salir");
    }

    // ===== Operaciones de Libro =====

    private void crearLibroConFicha() {
        System.out.println("\n--- Crear Libro ---");
        try {
            String titulo = leerStringNoVacio("Titulo (max. 150): ");
            String autor = leerStringNoVacio("Autor (max. 120): ");
            String editorial = leerString("Editorial, opcional (max. 100): ", true);
            Integer anio = leerEnteroOpcional("Anio de edicion, opcional: ");

            Libro libro = new Libro(titulo, autor, editorial, anio);

            System.out.println("Datos de la Ficha Bibliografica:");
            String isbn = leerString("ISBN, opcional (10 o 13 digitos, max. 17 caracteres): ", true);
            String dewey = leerString("Clasificacion Dewey, opcional (max. 20): ", true);
            String estanteria = leerString("Estanteria, opcional (max. 20): ", true);
            String idioma = leerString("Idioma, opcional (max. 30): ", true);

            FichaBibliografica ficha = new FichaBibliografica(isbn, dewey, estanteria, idioma);

            libroService.crearLibroConFicha(libro, ficha);

            System.out.println("   Libro y Ficha creados con exito.");
            System.out.println("   ID Libro: " + libro.getId());
            System.out.println("   ID Ficha: " + ficha.getId());
            if (ficha.getIsbn() != null) System.out.println("   ISBN: " + ficha.getIsbn());

        } catch (Exception e) {
            System.err.println("   No se pudo crear: " + e.getMessage());
        }
    }

    private void buscarLibroPorId() {
        Long id = leerLong("ID del Libro: ");
        try {
            Libro libro = libroService.obtenerPorId(id);
            if (libro != null) {
                System.out.println("   Libro encontrado:");
                System.out.println(libro);

                //Cargar ficha independientemente
                FichaBibliografica ficha = fichaService.buscarPorIdLibro(id);
                if (ficha != null) {
                    System.out.println("   Ficha: " + ficha);
                } else {
                    System.out.println("   Sin ficha asociada.");
                }
            } else {
                System.out.println("   Libro no encontrado.");
            }
        } catch (Exception e) {
            System.err.println("   Error: " + e.getMessage());
        }
    }

    private void listarLibros() {
        System.out.println("\n---   Lista de Libros (activos) ---");
        try {
            List<Libro> libros = libroService.listarTodos();
            if (libros.isEmpty()) {
                System.out.println("   No hay libros activos registrados.");
            } else {
                System.out.printf(" %d libro(s) activo(s):\n", libros.size());
                for (Libro l : libros) {
                    System.out.println(l);
                }
            }
        } catch (Exception e) {
            System.err.println(" Error al listar: " + e.getMessage());
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("\n---  Buscar Libro por titulo ---");
        String titulo = leerStringNoVacio("Titulo (se buscara como '%titulo%'): ");
        try {
            List<Libro> resultados = libroService.buscarPorTitulo(titulo);
            if (resultados.isEmpty()) {
                System.out.println(" No se encontraron libros con titulo que contenga: " + titulo.toUpperCase());
            } else {
                System.out.printf(" %d resultado(s):\n", resultados.size());
                for (Libro l : resultados) {
                    System.out.println(l);
                }
            }
        } catch (Exception e) {
            System.err.println(" Error en la busqueda: " + e.getMessage());
        }
    }

   private void actualizarLibro() {
    System.out.println("\n---Actualizar Libro---");
    Long id = leerLong("ID del Libro a actualizar: ");
    try {
        Libro existente = libroService.obtenerPorId(id);
        if (existente == null) {
            System.out.println("Libro no encontrado o eliminado.");
            return;
        }

        System.out.println("Libro actual: " + existente);
        String nuevoTitulo = leerString("Nuevo titulo (actual: " + existente.getTitulo() + ", Enter para mantener): ", true);
        String nuevoAutor = leerString("Nuevo autor (actual: " + existente.getAutor() + ", Enter para mantener): ", true);
        String nuevaEditorial = leerString("Nueva editorial (actual: " + existente.getEditorial() + ", Enter para mantener): ", true);
        Integer nuevoAnio = leerEnteroOpcional("Nuevo anio (actual: " + existente.getAnioEdicion() + ", Enter para mantener): ");

        if (nuevoTitulo != null && !nuevoTitulo.isEmpty()) existente.setTitulo(nuevoTitulo);
        if (nuevoAutor != null && !nuevoAutor.isEmpty()) existente.setAutor(nuevoAutor);
        if (nuevaEditorial != null && !nuevaEditorial.isEmpty()) existente.setEditorial(nuevaEditorial);
        if (nuevoAnio != null) existente.setAnioEdicion(nuevoAnio);

        libroService.actualizar(existente);
        System.out.println("Libro actualizado con exito.");
    } catch (Exception e) {
        System.err.println("No se pudo actualizar: " + e.getMessage());
    }
}
    private void eliminarLibro() {
        System.out.println("\n--- ️ Eliminar Libro ---");
        Long id = leerLong("ID del Libro a eliminar: ");
        try {
            Libro libro = libroService.obtenerPorId(id);
            if (libro == null) {
                System.out.println(" Libro no encontrado o ya eliminado.");
                return;
            }
            System.out.println("Libro a eliminar: " + libro);
            String confirmacion = leerString("Esta seguro? (S/N): ", false).toUpperCase();
            if ("S".equals(confirmacion)) {
                libroService.eliminarLogico(id);
                System.out.println(" Libro marcado como eliminado.");
            } else {
                System.out.println("  Operacion cancelada.");
            }
        } catch (Exception e) {
            System.err.println(" Error al eliminar: " + e.getMessage());
        }
    }

    // ===== Operaciones de FichaBibliografica =====

    private void buscarFichaPorIsbn() {
        System.out.println("\n--- Buscar Ficha por ISBN ---");
        String isbn = leerStringNoVacio("ISBN: ");
        try {
            FichaBibliografica ficha = fichaService.buscarPorIsbn(isbn);
            if (ficha != null) {
                System.out.println(" Ficha encontrada:");
                System.out.println(ficha);
            } else {
                System.out.println(" No se encontro una Ficha activa con ISBN: " + isbn);
            }
        } catch (Exception e) {
            System.err.println(" Error al buscar ficha: " + e.getMessage());
        }
    }

    private void verFichaDeLibro() {
    System.out.println("\n--- Ver Ficha asociada a un Libro ---");
    Long idLibro = leerLong("ID del Libro: ");
    try {
        Libro libro = libroService.obtenerPorId(idLibro);
        if (libro == null) {
            System.out.println(" Libro no encontrado o eliminado.");
            return;
        }

        // Buscar ficha directamente por idLibro 
        FichaBibliografica ficha = fichaService.buscarPorIdLibro(idLibro);
        if (ficha != null) {
            System.out.println(" Ficha asociada al Libro '" + libro.getTitulo() + "':");
            System.out.println(ficha);
        } else {
            System.out.println(" El Libro no tiene una FichaBibliografica activa asociada.");
        }
    } catch (Exception e) {
        System.err.println(" Error al cargar ficha: " + e.getMessage());
    }
    }

    // ===== Helpers de entrada =====

    private String leerString(String mensaje, boolean opcional) {
        System.out.print(mensaje);
        String input = scanner.nextLine().trim();
        if (opcional && input.isEmpty()) return null;
        if (!opcional && input.isEmpty()) return leerString(mensaje, opcional); 
        return input;
    }

    private String leerStringNoVacio(String mensaje) {
        return leerString(mensaje, false);
    }

    private Long leerLong(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println(" Ingrese un numero entero valido.");
            }
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(" Ingrese un numero entero valido.");
            }
        }
    }

    private Integer leerEnteroOpcional(String mensaje) {
        System.out.print(mensaje);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(" Valor no numerico ignorado.");
            return null;
        }
    }

    private void esperarEnter() {
        System.out.println("Presione ENTER para continuar...");
        scanner.nextLine(); 
    }
    
}
