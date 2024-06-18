package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final String IP = "localhost";
    private static final int PORTA = 8081;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in); Socket socket = new Socket(IP, PORTA)) {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());

            int opcaoSelecionada;
            while ((opcaoSelecionada = menu(scanner)) != 5) {
                output.writeInt(opcaoSelecionada);

                procedimentoDaOperacao(opcaoSelecionada, scanner, output);

                System.out.println(input.readUTF());
            }

            System.out.println("Cliente desligado");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executa o procedimento correspondente à operação selecionada pelo usuário.
     *
     * @param opcaoSelecionada O número da opção selecionada.
     * @param scanner          O scanner para entrada de dados do usuário.
     * @param output           O stream de saída para o servidor.
     * @throws IOException Se ocorrer um erro de entrada/saída.
     */
    private static void procedimentoDaOperacao(int opcaoSelecionada, Scanner scanner, DataOutputStream output) throws IOException {
        switch (opcaoSelecionada) {
            case 2 -> alugarLivro(scanner, output);
            case 3 -> devolucaoLivro(scanner, output);
            case 4 -> cadastramentoLivro(scanner, output);
        }
    }

    /**
     * Envia o índice do livro que o usuário deseja alugar para o servidor.
     *
     * @param scanner O scanner para entrada de dados do usuário.
     * @param output  O stream de saída para o servidor.
     * @throws IOException Se ocorrer um erro de entrada/saída.
     */
    private static void alugarLivro(Scanner scanner, DataOutputStream output) throws IOException {
        System.out.printf("Digite o índice do livro que deseja alugar: ");
        output.writeInt(scanner.nextInt());
    }

    /**
     * Envia o índice do livro que o usuário deseja devolver para o servidor.
     *
     * @param scanner O scanner para entrada de dados do usuário.
     * @param output  O stream de saída para o servidor.
     * @throws IOException Se ocorrer um erro de entrada/saída.
     */
    private static void devolucaoLivro(Scanner scanner, DataOutputStream output) throws IOException {
        System.out.printf("Digite o índice do livro que deseja devolver: ");
        output.writeInt(scanner.nextInt());
    }

    /**
     * Envia os detalhes do novo livro a ser cadastrado para o servidor.
     *
     * @param scanner O scanner para entrada de dados do usuário.
     * @param output  O stream de saída para o servidor.
     * @throws IOException Se ocorrer um erro de entrada/saída.
     */
    private static void cadastramentoLivro(Scanner scanner, DataOutputStream output) throws IOException {
        scanner.nextLine();
        System.out.printf("Digite o gênero do livro: ");
        output.writeUTF(scanner.nextLine());

        System.out.printf("Digite o título do livro: ");
        output.writeUTF(scanner.nextLine());

        System.out.printf("Digite a quantidade de exemplares do livro: ");
        output.writeInt(scanner.nextInt());

        scanner.nextLine();
        System.out.printf("Digite o autor do livro: ");
        output.writeUTF(scanner.nextLine());
    }

    /**
     * Exibe o menu e solicita a escolha de uma opção ao usuário.
     *
     * @param scanner O scanner para entrada de dados do usuário.
     * @return O número da opção selecionada pelo usuário.
     */
    private static int menu(Scanner scanner) {
        System.out.println("=============== MENU ===============");
        System.out.println("1 - Listar;");
        System.out.println("2 - Alugar;");
        System.out.println("3 - Devolver;");
        System.out.println("4 - Cadastrar;");
        System.out.println("5 - Finalizar;");
        System.out.printf("Digite um número da opção que deseja: ");

        return scanner.nextInt();
    }
}
