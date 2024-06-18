package main;

import entities.Servidor;

public class Program {

	public static void main(String[] args) {
		Servidor server = new Servidor(8081, "livros.json");
		server.iniciar();
	}
}
