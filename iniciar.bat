@echo off

rem Definindo variáveis de caminho dos projetos
set BIBLIOTECA_CLIENTE_PATH=BibliotecaCliente
set BIBLIOTECA_SERVER_PATH=BibliotecaServer

rem Iniciar o servidor Maven (BibliotecaServer)
echo Iniciando o servidor Maven...
cd %BIBLIOTECA_SERVER_PATH%
start cmd /c mvn exec:java

rem Aguardar um momento para que o servidor Maven inicie (opcional)
timeout /t 10 /nobreak > nul

rem Iniciar o cliente Java (BibliotecaCliente)
echo Iniciando o cliente Java...
cd ..\%BIBLIOTECA_CLIENTE_PATH%\bin
java -cp . main.Cliente

rem Mantém o prompt aberto para visualização de mensagens
pause >nul
