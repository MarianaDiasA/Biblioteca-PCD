
# Biblioteca - PCD

Projeto que faz a implementação de uma biblioteca de livros.


## Funcionalidades

Realiza as seguinte operações com os livros:
- Listar
- Alugar
- Devolver
- Cadastrar


## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/MarianaDiasA/Biblioteca-PCD.git
```

Entre no diretório do projeto

```bash
  cd Biblioteca-PCD
```

**Se** estiver usando Windows, basta executar: 

```bash
  iniciar.bat
```

**Senão:**

1 - Entre na pasta do servidor

```bash
  cd BibliotecaServer
```

2 - Execute o servidor com o seguinte comando Maven:

```bash
  mvn exec:java
```

3 - Execute o projeto do cliente.


## Stack utilizada

**Servidor:** Java 17, Maven, json-simple e gson.

**Cliente:** Java 17.

