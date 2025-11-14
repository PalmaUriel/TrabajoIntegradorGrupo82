
package entities;
import java.util.Objects;


public class FichaBibliografica {

    private Long id;
    private String isbn;
    private String clasificacionDewey;
    private String estanteria;
    private String idioma;
    private Boolean eliminado;

    // Constructor vacío
    public FichaBibliografica() {
        this.eliminado = false;
    }

    // Constructor completo (sin id)
    public FichaBibliografica(String isbn, String clasificacionDewey, String estanteria, String idioma) {
        this();
        setIsbn(isbn);
        setClasificacionDewey(clasificacionDewey);
        setEstanteria(estanteria);
        setIdioma(idioma);
    }

    // Getters y setters con validaciones
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn != null) {
            String clean = isbn.replaceAll("[\\s\\-]", ""); // Normalizacion, se quitan espacios y guiones
            if (clean.length() != 10 && clean.length() != 13) {
                throw new IllegalArgumentException("ISBN debe tener 10 o 13 dígitos (sin guiones)");
            }
            if (!clean.matches("\\d{9}[\\dXx]|\\d{12}[\\dXx]")) {
                throw new IllegalArgumentException("ISBN contiene caracteres invalidos");
            }
            if (isbn.length() > 17) {
                throw new IllegalArgumentException("ISBN excede los 17 caracteres (con guiones)");
            }
        }
        this.isbn = isbn;
    }

    public String getClasificacionDewey() {
        return clasificacionDewey;
    }

    public void setClasificacionDewey(String clasificacionDewey) {
        if (clasificacionDewey != null && clasificacionDewey.length() > 20) {
            throw new IllegalArgumentException("Clasificacion Dewey excede los 20 caracteres");
        }
        this.clasificacionDewey = clasificacionDewey != null ? clasificacionDewey.trim() : null;
    }

    public String getEstanteria() {
        return estanteria;
    }

    public void setEstanteria(String estanteria) {
        if (estanteria != null && estanteria.length() > 20) {
            throw new IllegalArgumentException("Estanteria excede los 20 caracteres");
        }
        this.estanteria = estanteria != null ? estanteria.trim() : null;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        if (idioma != null && idioma.length() > 30) {
            throw new IllegalArgumentException("Idioma excede los 30 caracteres");
        }
        this.idioma = idioma != null ? idioma.trim() : null;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado != null ? eliminado : false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FichaBibliografica that = (FichaBibliografica) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FichaBibliografica{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", clasificacionDewey='" + clasificacionDewey + '\'' +
                ", estanteria='" + estanteria + '\'' +
                ", idioma='" + idioma + '\'' +
                ", eliminado=" + eliminado +
                '}';
    }
}