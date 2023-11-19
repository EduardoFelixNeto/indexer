import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Indexer {

    // Índice invertido global para armazenar a frequência de cada palavra em cada arquivo
    private static Map<String, Map<String, Integer>> invertedIndex = new HashMap<>();
    // Mapa para armazenar o total de palavras em cada arquivo
    private static Map<String, Integer> totalWordsInFiles = new HashMap<>();

    /**
     * Função para tokenizar um texto.
     * Esta função extrai todas as palavras do texto que têm pelo menos 2 caracteres e são formadas apenas por letras.
     * @param text O texto a ser tokenizado
     * @return Uma lista de tokens/palavras
     */
    private static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        Matcher m = Pattern.compile("\\b[a-zA-Z]{2,}\\b").matcher(text.toLowerCase());
        while (m.find()) {
            tokens.add(m.group());
        }
        return tokens;
    }

    /**
     * Função para indexar um arquivo.
     * Esta função lê um arquivo linha por linha, tokeniza cada linha e atualiza o índice invertido e o mapa totalWordsInFiles.
     * @param filePath O caminho para o arquivo a ser indexado
     */
    private static void indexFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int totalWords = 0;
        while ((line = reader.readLine()) != null) {
            for (String token : tokenize(line)) {
                // Atualiza o índice invertido com a contagem de palavras
                invertedIndex.computeIfAbsent(token, k -> new HashMap<>()).merge(filePath, 1, Integer::sum);
                totalWords++;
            }
        }
        // Atualiza o mapa com o total de palavras do arquivo
        totalWordsInFiles.put(filePath, totalWords);
        reader.close();
    }

    /**
     * Função para obter as N palavras mais frequentes de um arquivo.
     * @param n O número de palavras top para recuperar
     * @param filePath O caminho para o arquivo
     * @return Uma lista das N palavras mais frequentes e suas contagens
     */
    private static List<Map.Entry<String, Integer>> freqNWords(int n, String filePath) {
        Map<String, Integer> wordCounts = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : invertedIndex.entrySet()) {
            if (entry.getValue().containsKey(filePath)) {
                wordCounts.put(entry.getKey(), entry.getValue().get(filePath));
            }
        }
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(wordCounts.entrySet());
        sortedList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        return sortedList.subList(0, Math.min(n, sortedList.size()));
    }

    /**
     * Função para calcular o valor TF-IDF de um termo para um arquivo específico.
     * @param term O termo a ser verificado
     * @param filePath O caminho para o arquivo
     * @param totalFiles O número total de arquivos
     * @return O valor TF-IDF do termo para o arquivo
     */
    private static double tfidf(String term, String filePath, int totalFiles) {
        int termFrequency = invertedIndex.getOrDefault(term, new HashMap<>()).getOrDefault(filePath, 0);
        int docsWithTerm = invertedIndex.getOrDefault(term, new HashMap<>()).size();
        double tf = (double) termFrequency / totalWordsInFiles.get(filePath);
        double idf = Math.log((double) totalFiles / (1 + docsWithTerm)); // Adiciona 1 para evitar divisão por zero
        return tf * idf;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: Indexer <option> <parameters>...");
            return;
        }
        
        String option = args[0];
        
        switch (option) {
            case "--freq":
                // Retorna as N palavras mais frequentes de um arquivo
                int n = Integer.parseInt(args[1]);
                String file = args[2];
                indexFile(file);
                for (Map.Entry<String, Integer> entry : freqNWords(n, file)) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
                break;

            case "--freq-word":
                // Retorna a frequência de uma palavra específica em um arquivo
                String word = args[1];
                String wordFile = args[2];
                indexFile(wordFile);
                System.out.println(word + ": " + invertedIndex.getOrDefault(word.toLowerCase(), new HashMap<>()).getOrDefault(wordFile, 0));
                break;

            case "--search":
                // Classifica arquivos por relevância para um termo usando TF-IDF
                String term = args[1];
                List<String> files = new ArrayList<>(Arrays.asList(args).subList(2, args.length));
                for (String f : files) {
                    indexFile(f);
                }
                Map<String, Double> tfidfScores = new HashMap<>();
                for (String f : files) {
                    tfidfScores.put(f, tfidf(term.toLowerCase(), f, files.size()));
                }
                tfidfScores.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
                break;

            default:
                System.out.println("Opção inválida.");
                break;
        }
    }
}
