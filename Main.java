import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

class Hash<Chave, Valor> {
    Chave chave;
    Valor valor;

    public Hash(Chave chave, Valor valor) {
        this.chave = chave;
        this.valor = valor;
    }
}

/**
 * Tabela Hash com complexidade amortizada Θ(log n) para suas operações.
 * A complexidade base das operações é Θ(1) amortizado.
 * A cada operação, adiciona um custo Θ(log n) através de um laço de atraso,
 * resultando em uma complexidade final de Θ(log n), que satisfaz a condição
 * ω(1).
 */
class TabelaHashAutossabotada<Chave, Valor> {
    private static final int CAPACIDADE_INICIAL = 4;
    private static final float FATOR_CARGA = 0.8f;
    private static final float FATOR_CARGA_INFERIOR = 0.2f;

    private int n; // número de pares chave-valor
    private int m; // tamanho da tabela hash
    private LinkedList<Hash<Chave, Valor>>[] st; // array de listas ligadas

    public TabelaHashAutossabotada() {
        this(CAPACIDADE_INICIAL);
    }

    public TabelaHashAutossabotada(int capacidade) {
        this.m = capacidade;
        this.n = 0;

        @SuppressWarnings("unchecked")
        LinkedList<Hash<Chave, Valor>>[] temp = (LinkedList<Hash<Chave, Valor>>[]) new LinkedList[m];
        this.st = temp;
        for (int i = 0; i < m; i++) {
            st[i] = new LinkedList<>();
        }
    }

    /**
     * Adiciona o custo Θ(log n) em cada operação da tabela hash.
     */
    private void adicionaCusto() {
        if (n <= 1) {
            return;
        }

        int iteracoes = (int) Math.ceil(Math.log(n) / Math.log(2));

        long dummyCounter = 0;
        for (int i = 0; i < iteracoes; i++) {
            boolean ehPar = i % 2 == 0;
            if (ehPar) {
                dummyCounter++;
            }
        }
    }

    private int hash(Chave chave) {
        // Usa o hashCode() da chave e remove o sinal para garantir um índice positivo.
        return (chave.hashCode() & 0x7fffffff) % m;
    }

    public void insert(Chave chave, Valor valor, boolean adicionaCusto) {
        if (adicionaCusto)
            adicionaCusto();

        if (chave == null)
            throw new IllegalArgumentException("Chave não pode ser nula");
        if (valor == null) {
            remove(chave, adicionaCusto);
            return;
        }

        // Redimensiona a tabela se o fator de carga for muito alto
        if (n >= m * FATOR_CARGA) {
            resize(2 * m, adicionaCusto);
        }

        int i = hash(chave);
        for (Hash<Chave, Valor> entrada : st[i]) {
            if (chave.equals(entrada.chave)) {
                entrada.valor = valor;
                return;
            }
        }
        st[i].add(new Hash<>(chave, valor));
        n++;
    }

    public void remove(Chave chave, boolean adicionaCusto) {
        if (adicionaCusto)
            adicionaCusto();

        if (chave == null)
            throw new IllegalArgumentException("Chave não pode ser nula");

        int i = hash(chave);
        Hash<Chave, Valor> paraRemover = null;
        for (Hash<Chave, Valor> entrada : st[i]) {
            if (chave.equals(entrada.chave)) {
                paraRemover = entrada;
                break;
            }
        }
        if (paraRemover != null) {
            st[i].remove(paraRemover);
            n--;
        }

        // Redimensiona a tabela se o fator de carga for muito baixo
        if (m > CAPACIDADE_INICIAL && n <= m * FATOR_CARGA_INFERIOR)
            resize(m / 2, adicionaCusto);
    }

    public Valor lookup(Chave chave, boolean adicionaCusto) {
        if (adicionaCusto)
            adicionaCusto();

        if (chave == null)
            throw new IllegalArgumentException("Chave não pode ser nula");
        int i = hash(chave);
        for (Hash<Chave, Valor> entrada : st[i]) {
            if (chave.equals(entrada.chave)) {
                return entrada.valor;
            }
        }
        return null;
    }

    private void resize(int novaCapacidade, boolean adicionaCusto) {
        TabelaHashAutossabotada<Chave, Valor> temp = new TabelaHashAutossabotada<>(novaCapacidade);
        for (int i = 0; i < m; i++) {
            for (Hash<Chave, Valor> entrada : st[i]) {
                temp.insert(entrada.chave, entrada.valor, adicionaCusto);
            }
        }
        this.m = temp.m;
        this.n = temp.n;
        this.st = temp.st;
    }

    public List<Chave> findAll() {
        List<Chave> chaves = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (Hash<Chave, Valor> entrada : st[i]) {
                chaves.add(entrada.chave);
            }
        }
        return chaves;
    }
}

/**
 * Classe principal que lê os comandos da entrada padrão (STDIN)
 * e executa as operações na Tabela Hash.
 */
public class Main {

    public static void main(String[] args) {
        gerarArquivoResultados();

        // run();

    }

    public static void run() {
        TabelaHashAutossabotada<String, String> tabelaHash = new TabelaHashAutossabotada<>();
        boolean adicionaCusto = true; // Flag para ativar o custo Θ(log n)
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine();
            if (linha.trim().isEmpty()) {
                continue;
            }

            String[] partes = linha.split("\\s+", 3);
            String comando = partes[0];

            try {
                switch (comando.toLowerCase()) {
                    case "insert":
                        if (partes.length < 3) {
                            System.out.println("Comando invalido: " + linha);
                            continue;
                        }
                        tabelaHash.insert(partes[1], partes[2], adicionaCusto);
                        break;

                    case "remove":
                        if (partes.length < 2) {
                            System.out.println("Comando invalido: " + linha);
                            continue;
                        }
                        tabelaHash.remove(partes[1], adicionaCusto);
                        break;

                    case "lookup":
                        if (partes.length < 2) {
                            System.out.println("Comando invalido: " + linha);
                            continue;
                        }

                        String valorEncontrado = tabelaHash.lookup(partes[1], adicionaCusto);
                        if (valorEncontrado != null) {
                            System.out.println(valorEncontrado);
                        }
                        break;

                    case "findall":
                        System.out.println(tabelaHash.findAll());
                        break;

                    default:
                        System.out.println("Comando invalido: " + linha);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Erro ao processar comando: " + linha + " - " + e.getMessage());
            }
        }
        scanner.close();
    }

    public static void gerarArquivoResultados() {
        int[] tamanhosDeTeste = { 100, 1000, 10000, 100000 };
        int numeroDeOperacoesPorN = 2;
        String nomeArquivoCsv = "resultados_hash.csv";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nomeArquivoCsv))) {
            bw.write("Tamanho,TempoLogN(ns),TempoConstante(ns)\n");

            // Itera sobre cada tamanho de 'n' a ser testado
            for (int n : tamanhosDeTeste) {
                System.out.println("  Processando para n = " + n + "...");

                // Executa o teste para a tabela com custo Θ(log n)

                long tempoLogN = executarTesteHash(n, numeroDeOperacoesPorN, true);

                long tempoConstante = executarTesteHash(n, numeroDeOperacoesPorN, false);

                // Monta e escreve a linha de resultado no arquivo CSV
                String linhaResultado = String.format("%d,%d,%d", n, tempoLogN, tempoConstante);
                bw.write(linhaResultado + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static long executarTesteHash(int n, int operacoesMultiplier, boolean adicionaCusto) {
        TabelaHashAutossabotada<String, String> tabela = new TabelaHashAutossabotada<>();
        int totalOperacoes = n * operacoesMultiplier;

        for (int i = 0; i < n / 2; i++) {
            tabela.insert(UUID.randomUUID().toString(), "valor", adicionaCusto);
        }

        for (int i = 0; i < totalOperacoes; i++) {
            String chave = UUID.randomUUID().toString();
            tabela.insert(chave, "valor_novo", adicionaCusto);
        }
        LocalDateTime inicio1 = LocalDateTime.now();
        for (int i = 0; i < totalOperacoes; i++) {
            String chave = UUID.randomUUID().toString();
            tabela.insert(chave, "valor_novo", adicionaCusto);
        }
        LocalDateTime fim1 = LocalDateTime.now();
        long duracaoTotal = Duration.between(inicio1, fim1).toMillis();

        // Retorna o tempo MÉDIO por operação
        return duracaoTotal / totalOperacoes;

    }
}
