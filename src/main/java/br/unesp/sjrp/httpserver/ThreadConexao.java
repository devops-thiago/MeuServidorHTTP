/*
 * Copyright (C) 2014 Thiago da Silva Gonzaga <thiagosg@sjrp.unesp.br>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package br.unesp.sjrp.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thiago da Silva Gonzaga <thiagosg@sjrp.unesp.br>
 */
public class ThreadConexao implements Runnable {

    private final Socket socket;
    private boolean conectado;

    public ThreadConexao(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        conectado = true;
        //imprime na tela o IP do cliente
        System.out.println(socket.getInetAddress());
        while (conectado) {
            try {
                //cria uma requisicao a partir do InputStream do cliente
                RequisicaoHTTP requisicao = RequisicaoHTTP.lerRequisicao(socket.getInputStream());
                //se a conexao esta marcada para se mantar viva entao seta keepalive e o timeout
                if (requisicao.isManterViva()) {
                    socket.setKeepAlive(true);
                    socket.setSoTimeout(requisicao.getTempoLimite());
                } else {
                    //se nao seta um valor menor suficiente para uma requisicao
                    socket.setSoTimeout(300);
                }

                //se o caminho foi igual a / entao deve pegar o /index.html
                String nomeArquivo = requisicao.getRecurso();
                if (nomeArquivo.equals("/")) {
                    nomeArquivo = "/index.html";
                }
                
                // Remove a barra inicial se existir para acessar o resource
                if (nomeArquivo.startsWith("/")) {
                    nomeArquivo = nomeArquivo.substring(1);
                }

                RespostaHTTP resposta;
                byte[] conteudoArquivo;

                // Tenta carregar o arquivo do classpath (recursos)
                InputStream resourceStream = ThreadConexao.class.getClassLoader().getResourceAsStream(nomeArquivo);
                
                if (resourceStream != null) {
                    // Arquivo encontrado, criar resposta de sucesso
                    resposta = new RespostaHTTP(requisicao.getProtocolo(), 200, "OK");
                    conteudoArquivo = resourceStream.readAllBytes();
                    resourceStream.close();
                } else {
                    // Arquivo não encontrado, criar resposta de erro 404
                    resposta = new RespostaHTTP(requisicao.getProtocolo(), 404, "Not Found");
                    InputStream errorStream = ThreadConexao.class.getClassLoader().getResourceAsStream("404.html");
                    if (errorStream != null) {
                        conteudoArquivo = errorStream.readAllBytes();
                        errorStream.close();
                    } else {
                        // Fallback se 404.html não existir
                        conteudoArquivo = "<html><body><h1>404 - Not Found</h1></body></html>".getBytes();
                    }
                }

                //define o conteúdo da resposta
                resposta.setConteudoResposta(conteudoArquivo);
                //converte o formato para o GMT espeficicado pelo protocolo HTTP
                String dataFormatada = Util.formatarDataGMT(new Date());
                //cabeçalho padrão da resposta HTTP/1.1
                resposta.setCabecalho("Location", "http://localhost:8000/");
                resposta.setCabecalho("Date", dataFormatada);
                resposta.setCabecalho("Server", "MeuServidor/1.0");
                resposta.setCabecalho("Content-Type", "text/html");
                resposta.setCabecalho("Content-Length", resposta.getTamanhoResposta());
                //cria o canal de resposta utilizando o outputStream
                resposta.setSaida(socket.getOutputStream());
                resposta.enviar();
            } catch (IOException ex) {
                //quando o tempo limite terminar encerra a thread
                if (ex instanceof SocketTimeoutException) {
                    try {
                        conectado = false;
                        socket.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(ThreadConexao.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }

        }
    }

}