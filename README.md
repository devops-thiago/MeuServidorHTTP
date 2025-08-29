# MeuServidorHTTP

Implementação simples de um Servidor HTTP em Java

## Descrição

Este projeto é uma implementação básica de um servidor HTTP em Java que demonstra conceitos fundamentais de programação em redes e protocolos HTTP. O servidor é capaz de:

- Aceitar conexões HTTP na porta 8000
- Servir arquivos HTML estáticos
- Gerenciar múltiplas conexões simultâneas usando um pool de threads
- Implementar funcionalidades básicas do protocolo HTTP/1.1 como keep-alive
- Retornar respostas HTTP adequadas (200 OK, 404 Not Found)

## Funcionalidades

- **Servidor Multi-threaded**: Utiliza um pool de threads para lidar com múltiplas conexões simultâneas
- **Servimento de Arquivos Estáticos**: Serve arquivos HTML do diretório de recursos
- **Páginas de Erro**: Retorna página customizada de erro 404
- **Suporte HTTP/1.1**: Implementa funcionalidades básicas como keep-alive
- **Logging**: Exibe informações sobre requisições recebidas

## Requisitos

- Java 17 ou superior
- Maven 3.6.0 ou superior

## Como Compilar

```bash
# Clone o repositório
git clone https://github.com/devops-thiago/MeuServidorHTTP.git
cd MeuServidorHTTP

# Compile o projeto usando Maven
mvn compile

# Ou gere o JAR
mvn package
```

## Como Executar

### Usando Maven (Recomendado)

```bash
mvn exec:java
```

### Usando Java diretamente

```bash
# Compile primeiro
mvn compile

# Execute a classe principal
java -cp target/classes br.unesp.sjrp.httpserver.Servidor
```

### Usando o JAR gerado

```bash
# Gere o JAR
mvn package

# Execute o JAR
java -cp target/meu-servidor-http-1.0.0.jar br.unesp.sjrp.httpserver.Servidor
```

## Como Usar

1. **Inicie o servidor**: Execute o comando acima para iniciar o servidor
2. **Acesse via navegador**: Abra seu navegador e acesse `http://localhost:8000`
3. **Página inicial**: O servidor servirá automaticamente o arquivo `index.html`
4. **Página de erro**: Tente acessar uma página que não existe para ver a página de erro 404

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/unesp/sjrp/httpserver/
│   │   ├── Servidor.java          # Classe principal do servidor
│   │   ├── ThreadConexao.java     # Thread para gerenciar cada conexão
│   │   ├── RequisicaoHTTP.java    # Classe para parsing de requisições HTTP
│   │   ├── RespostaHTTP.java      # Classe para gerar respostas HTTP
│   │   └── Util.java              # Utilitários (formatação de data)
│   └── resources/
│       ├── index.html             # Página inicial
│       └── 404.html               # Página de erro 404
```

## Arquitetura

### Componentes Principais

1. **Servidor**: Classe principal que cria o ServerSocket e gerencia o pool de threads
2. **ThreadConexao**: Cada conexão é tratada em uma thread separada para permitir concorrência
3. **RequisicaoHTTP**: Responsável por fazer o parsing das requisições HTTP recebidas
4. **RespostaHTTP**: Constrói e envia as respostas HTTP apropriadas
5. **Util**: Funções utilitárias, principalmente para formatação de data no padrão GMT

### Fluxo de Funcionamento

1. O servidor escuta na porta 8000
2. Para cada nova conexão, uma thread é criada no pool
3. A thread lê e faz o parsing da requisição HTTP
4. Com base no recurso solicitado, o servidor:
   - Carrega o arquivo do classpath se existir
   - Retorna página de erro 404 se não existir
5. Monta a resposta HTTP com headers apropriados
6. Envia a resposta para o cliente
7. Mantém a conexão ativa se solicitado (keep-alive)

## Configuração

- **Porta**: O servidor roda na porta 8000 (hardcoded)
- **Pool de Threads**: Máximo de 20 threads simultâneas
- **Timeout**: 3 segundos para conexões keep-alive, 300ms para outras

## Limitações

Este é um servidor HTTP simples para fins educacionais e tem várias limitações:

- Suporte limitado ao protocolo HTTP (apenas GET básico)
- Não suporta HTTPS
- Não implementa autenticação
- Não suporta upload de arquivos
- Não tem configuração externa
- Não implementa cache

## Licença

GNU General Public License v2.0

## Autor

Thiago da Silva Gonzaga (<thiagosg@sjrp.unesp.br>)

## Histórico

- v1.0.0: Conversão para Maven com JDK 17, reestruturação do projeto
