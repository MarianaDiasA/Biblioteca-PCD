package entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.*;

public class Servidor {
	private Socket cliente;
	private ServerSocket servidor;
	private String nomeArquivo;
	private int porta;
	private ArrayList<Livro> livros = new ArrayList<>();

	public Servidor(int porta, String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
		this.porta = porta;

		inicializar();
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int port) {
		this.porta = port;
	}

	/**
	 * Inicializa o servidor e aguarda conexões de clientes.
	 * 
	 * @throws IOException Exceção de E/S que pode ocorrer durante a inicialização
	 *                     do servidor ou manipulação de mensagens.
	 */
	public void iniciar() {
		try {
			criarSocket();
			esperarClientes();
			processarMensagens();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fecharRecursos();
		}
	}

	/**
	 * Inicializa a lista de livros a partir de um arquivo JSON.
	 * 
	 * Este método lê um arquivo JSON contendo informações sobre os livros
	 * disponíveis e inicializa a lista de livros com base nos dados fornecidos no
	 * arquivo.
	 * 
	 * @throws IOException    Exceção de E/S que pode ocorrer ao ler o arquivo JSON.
	 * @throws ParseException Exceção que indica um erro ao fazer o parsing do
	 *                        conteúdo do arquivo JSON.
	 */
	private void inicializar() {
		try {
			JSONArray array = lerArquivoJSON(nomeArquivo);
			processarLivros(array);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lê um arquivo JSON e retorna a lista de livros contida no arquivo.
	 * 
	 * @param nomeArquivo O caminho do arquivo JSON a ser lido.
	 * @return Um JSONArray contendo a lista de livros do arquivo JSON.
	 * @throws IOException    Exceção de E/S que pode ocorrer ao ler o arquivo JSON.
	 * @throws ParseException Exceção que indica um erro ao fazer o parsing do
	 *                        conteúdo do arquivo JSON.
	 */
	private JSONArray lerArquivoJSON(String nomeArquivo) throws IOException, ParseException {
		try (FileReader reader = new FileReader(nomeArquivo)) {
			Object obj = new JSONParser().parse(reader);
			JSONObject json = (JSONObject) obj;
			return (JSONArray) json.get("livros");
		}
	}

	/**
	 * Processa a lista de livros obtida a partir do arquivo JSON e adiciona os
	 * livros à lista de livros do sistema.
	 * 
	 * @param array O JSONArray contendo a lista de livros a ser processada.
	 */
	private void processarLivros(JSONArray array) {
		for (Object livroObj : array) {
			JSONObject livro = (JSONObject) livroObj;

			String titulo = (String) livro.get("titulo");
			String autor = (String) livro.get("autor");
			String genero = (String) livro.get("genero");
			long exemplares = (long) livro.get("exemplares");

			livros.add(new Livro(autor, titulo, genero, (int) exemplares));
		}
	}

	/**
	 * Inicia o servidor na porta especificada no objeto.
	 * 
	 * @throws IOException Exceção de E/S que pode ocorrer durante a inicialização
	 *                     do servidor.
	 */
	private void criarSocket() throws IOException {
		this.servidor = new ServerSocket(getPorta());
		System.out.println("[INICIADO] Ouvindo a porta " + getPorta());
	}

	/**
	 * Aguarda a conexão de um cliente e exibe informações sobre a conexão.
	 * 
	 * @throws IOException Exceção de E/S que pode ocorrer durante a aceitação da
	 *                     conexão do cliente.
	 */
	private void esperarClientes() throws IOException {
		this.cliente = servidor.accept();
		System.out.println("[CONECTADO] Novo Cliente: " + getClienteIP(cliente));
		System.out.println("[CONECTADO] Hostname: " + InetAddress.getLocalHost().getHostName());
	}

	/**
	 * Processa as mensagens recebidas do cliente e envia as respostas
	 * correspondentes. O servidor continua a processar mensagens até receber uma
	 * mensagem para encerrar a conexão.
	 * 
	 * @throws IOException Exceção de E/S que pode ocorrer durante a manipulação de
	 *                     mensagens.
	 */
	private void processarMensagens() throws IOException {
		while (true) {
			DataInputStream input = new DataInputStream(cliente.getInputStream());

			int mensagemRecebida = input.readInt();
			if (mensagemRecebida == 5)
				break;

			String resposta = menuOpcao(mensagemRecebida);

			DataOutputStream output = new DataOutputStream(cliente.getOutputStream());
			output.writeUTF(resposta);
		}
	}

	/**
	 * Fecha os recursos (servidor e cliente) quando não são mais necessários.
	 */
	private void fecharRecursos() {
		System.out.println("[" + getClienteIP(cliente) + "]" + " Encerramento do servidor solicitado");
		try {
			cliente.close();
			servidor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Executa uma opção do menu com base na chave fornecida.
	 *
	 * @param key A chave que representa a opção do menu a ser executada.
	 * @return Uma string contendo a resposta à opção do menu.
	 * @throws IOException Exceção de E/S que pode ocorrer durante a execução da
	 *                     opção do menu.
	 */
	private String menuOpcao(int key) throws IOException {
		switch (key) {
		case 1:
			System.out.println("[" + getClienteIP(cliente) + "]" + " Listagem solicitada");
			return listar();
		case 2:
			System.out.println("[" + getClienteIP(cliente) + "]" + " Alugar livro solicitado");
			return alugar();
		case 3:
			System.out.println("[" + getClienteIP(cliente) + "]" + " Devolução solicitada");
			return devolver();
		case 4:
			System.out.println("[" + getClienteIP(cliente) + "]" + " Cadastramento solicitado");
			return cadastrar();
		default:
			return "";
		}
	}

	/**
	 * Lista os livros presentes no array chamando o método .toString().
	 * 
	 * @return Uma String concatenando o retorno do .toString() de todos os livros.
	 */
	private String listar() {
		int index = 0;

		String res = "\n=========== LIVROS ===========\n";
		for (Livro livro : livros) {
			res += String.format("Index: %d\n", index);
			res += livro.toString();
			index++;
		}

		return res;
	}

	/**
	 * Aluga um livro para o cliente.
	 *
	 * @return Uma mensagem indicando o resultado da operação de aluguel.
	 * @throws IOException Exceção de E/S que pode ocorrer durante a comunicação com
	 *                     o cliente ou atualização do arquivo JSON.
	 */
	private String alugar() throws IOException {
		DataInputStream input = new DataInputStream(cliente.getInputStream());
		int index = input.readInt();

		if (index < 0 || index >= livros.size())
			return "Índice inválido.";

		Livro livro = livros.get(index);
		if (livro.getExemplares() <= 0)
			return "Este livro não está disponível para aluguel.";

		livro.alugar();

		atualizarArquivoJSON();

		return "Livro alugado com sucesso!";
	}

	/**
	 * Método para processar a devolução de um livro pelo cliente.
	 *
	 * @return Uma mensagem indicando o resultado da operação de devolução.
	 * @throws IOException Se ocorrer um erro de entrada/saída durante a operação.
	 */
	private String devolver() throws IOException {
		DataInputStream input = new DataInputStream(cliente.getInputStream());
		int index = input.readInt();

		if (index < 0 || index >= livros.size())
			return "Índice inválido.";

		Livro livro = livros.get(index);
		livro.devolver();

		atualizarArquivoJSON();

		return "Livro devolvido com sucesso!";
	}

	/**
	 * Recebe os dados de um novo livro do cliente, cadastra o livro, atualiza o
	 * arquivo JSON e retorna uma mensagem de sucesso.
	 *
	 * @return Uma mensagem indicando que o novo livro foi cadastrado com sucesso.
	 * @throws IOException Se ocorrer um erro de entrada/saída durante a operação.
	 */
	private String cadastrar() throws IOException {
		DataInputStream input = new DataInputStream(cliente.getInputStream());
		String genero = input.readUTF();
		String titulo = input.readUTF();
		int exemplares = input.readInt();
		String autor = input.readUTF();

		livros.add(new Livro(autor, titulo, genero, exemplares));

		atualizarArquivoJSON();

		return "Novo livro cadastrado com sucesso";
	}

	/**
	 * Obtém o endereço IP do cliente.
	 *
	 * @param cliente O socket do cliente.
	 * @return O endereço IP do cliente.
	 */
	private String getClienteIP(Socket cliente) {
		return cliente.getInetAddress().getHostAddress();
	}

	/**
	 * Atualiza o arquivo JSON com os dados atualizados dos livros.
	 *
	 * @throws IOException Exceção de E/S que pode ocorrer durante a atualização do
	 *                     arquivo JSON.
	 */
	@SuppressWarnings("unchecked")
	private void atualizarArquivoJSON() throws IOException {
		JSONArray jsonArray = obterDadosLivros();

		JSONObject json = new JSONObject();
		json.put("livros", jsonArray);

		String jsonString = formatarJson(json);

		FileWriter file = new FileWriter(nomeArquivo);

		file.write(jsonString);
		file.close();

		System.out.println("Arquivo JSON atualizado com sucesso.");
	}

	/**
	 * Formata um objeto JSON em uma string formatada e legível.
	 *
	 * @param json O objeto JSON a ser formatado.
	 * @return Uma string contendo o JSON formatado de forma legível.
	 */
	private String formatarJson(JSONObject json) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement je = JsonParser.parseString(json.toJSONString());
		String prettyJsonString = gson.toJson(je);
		return prettyJsonString;
	}

	/**
	 * Obtém os dados dos livros do array e converte para o formato JSON.
	 *
	 * @return Um JSONArray contendo os dados dos livros.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray obterDadosLivros() {
		JSONArray jsonArray = new JSONArray();
		for (Livro livro : livros) {
			JSONObject livroJson = new JSONObject();
			livroJson.put("titulo", livro.getTitulo());
			livroJson.put("autor", livro.getAutor());
			livroJson.put("genero", livro.getGenero());
			livroJson.put("exemplares", livro.getExemplares());
			jsonArray.add(livroJson);
		}

		return jsonArray;
	}
}
