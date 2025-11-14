
package entities;

import java.util.Objects;

public class Libro {

    private Long id;
    private String titulo;
    private String autor;
    private String editorial;
    private Integer anioEdicion;
    private Boolean eliminado;
    private FichaBibliografica fichaBibliografica;

    // Constructor vacío
    public Libro() {
        this.eliminado = false;
    }

    // Constructor completo 
    public Libro(String titulo, String autor, String editorial, Integer anioEdicion) {
        this();
        setTitulo(titulo);
        setAutor(autor);
        setEditorial(editorial);
        this.anioEdicion = anioEdicion;
    }

    // Getters y setters con validaciones
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El titulo no puede ser nulo o vacio");
        }
        if (titulo.length() > 150) {
            throw new IllegalArgumentException("El titulo excede los 150 caracteres");
        }
        this.titulo = titulo.trim();
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor no puede ser nulo o vacio");
        }
        if (autor.length() > 120) {
            throw new IllegalArgumentException("El autor excede los 120 caracteres");
        }
        this.autor = autor.trim();
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        if (editorial != null && editorial.length() > 100) {
            throw new IllegalArgumentException("La editorial excede los 100 caracteres");
        }
        this.editorial = editorial != null ? editorial.trim() : null;
    }

    public Integer getAnioEdicion() {
        return anioEdicion;
    }

    public void setAnioEdicion(Integer anioEdicion) {
        if (anioEdicion != null && (anioEdicion < 1450 || anioEdicion > 2025)) {
            throw new IllegalArgumentException("Anio de edicion fuera de rango valido (1450–2025)");
        }
        this.anioEdicion = anioEdicion;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado != null ? eliminado : false;
    }

    public FichaBibliografica getFichaBibliografica() {
        return fichaBibliografica;
    }

    public void setFichaBibliografica(FichaBibliografica fichaBibliografica) {
        // Validación de unicidad: no permitir reasignar si ya tiene una
        if (this.fichaBibliografica != null && fichaBibliografica != null) {
            throw new IllegalStateException("El Libro ya tiene una FichaBibliografica asociada (relación 1:1)");
        }
        this.fichaBibliografica = fichaBibliografica;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return Objects.equals(id, libro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", editorial='" + editorial + '\'' +
                ", anioEdicion=" + anioEdicion +
                ", eliminado=" + eliminado +
                '}';
    }
}