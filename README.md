Indexer
Indexer é uma ferramenta de linha de comando escrita em Java que permite indexar arquivos de texto, recuperar as palavras mais frequentes de um arquivo e classificar arquivos por relevância para um termo de pesquisa usando TF-IDF.

Pré-requisitos
Java JDK (Recomendado JDK 8 ou superior)

Compilação
Para compilar o programa, navegue até o diretório contendo o arquivo Indexer.java e execute:

javac Indexer.java

Isso irá gerar um arquivo bytecode Indexer.class que você pode executar.

Execução
O programa suporta três modos principais de operação:

--freq: Recupera as N palavras mais frequentes de um arquivo.
--freq-word: Retorna a frequência de uma palavra específica em um arquivo.
--search: Classifica arquivos por relevância para um termo usando TF-IDF.

Exemplos de Uso
1. Obter as N palavras mais frequentes de um arquivo

java Indexer --freq 5 path/to/file.txt

Este comando irá mostrar as 5 palavras mais frequentes no arquivo file.txt.

2. Obter a frequência de uma palavra específica em um arquivo

java Indexer --freq-word "example" path/to/file.txt

Este comando irá mostrar a frequência da palavra "example" no arquivo file.txt.

3. Classificar arquivos por relevância para um termo

java Indexer --search "example" path/to/file1.txt path/to/file2.txt

Este comando irá classificar file1.txt e file2.txt por sua relevância para a palavra "example" usando TF-IDF.
