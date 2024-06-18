package entities;

public class Livro {
	private String autor;
	private String titulo;
	private String genero;
	private int qntExemplares;

	
	protected Livro(String autor, String titulo, String genero, int qntExemplares) {
		this.autor = autor;
		this.titulo = titulo;
		this.genero = genero;
		this.qntExemplares = qntExemplares;
	}
	
	public String getAutor() {
		return autor;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getGenero() {
		return genero;
	}

	public int getExemplares() {
		return qntExemplares;
	}
	
	protected void alugar() {
		this.qntExemplares--;
	}
	
	protected void devolver() {
		this.qntExemplares++;
	}
	
	@Override
	public String toString() {
		return String.format("Titulo: %s \nAutor: %s \nGenero: %s \nExemplares: %d\n\n", 
				titulo, autor, genero, qntExemplares);
	}
	
}
