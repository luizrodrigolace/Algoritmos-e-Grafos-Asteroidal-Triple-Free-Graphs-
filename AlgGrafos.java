import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.*;

public class AlgGrafos {
    public static final String diretorio = System.getProperty("user.dir") + "/myfiles/";
    private static int arestasCont = 0;

    public static class LeitorDeArquivo {
        private final String arquivo;
        private List<String> linhas;

        /**
         * Construtor de LeitorDeArquivo
         *
         * @param arquivo nome do arquivo a ser lido
         */
        public LeitorDeArquivo(String arquivo) {
            this.arquivo = arquivo;
            this.linhas = new ArrayList<String>();
        }

        public List<String> abreArquivo(){
            Path caminho = Paths.get(diretorio+this.arquivo);
            Charset charset = StandardCharsets.UTF_8;

            try{
                this.linhas = Files.readAllLines(caminho,charset);
            }catch(IOException e){
                e.printStackTrace();
            }
            return linhas;
        }
    }

    public static class Grafo {
        private final Map<Integer, ArrayList<Integer>> verticesAdjacentes;
        private int verticesCont;
        private int arestasCont;

        /**
         * Construtor do grafo
         */
        public Grafo() {
            this.verticesAdjacentes = new HashMap<Integer,ArrayList<Integer>>();
            this.verticesCont = 0;
            this.arestasCont = 0;
        }

        /**
         * @return relacao de vertices (key) e seus vertices adjacentes (value)
         */
        public Map<Integer, ArrayList<Integer>> getVerticesAdjacentes() {
            return this.verticesAdjacentes;
        }

        /**
         *
         * @param vertice
         * @return vertices vizinhos do vertice passado no parametro
         */
        public ArrayList<Integer> getAdjByVertices(int vertice){
            return this.verticesAdjacentes.get(vertice);
        }

        /**
         * Adiciona um novo vertice ao conjunto de vertices
         * @param id = identificador do vertice
         */
        void addVertice(int id){
            this.verticesAdjacentes.putIfAbsent(id,new ArrayList<Integer>());
        }

        /**
         * Adiciona uma nova aresta id2 a lista de vertices
         * adjacentes a id1
         *
         * @param id1 vertice raiz
         * @param id2 vertice adicionado a lista de vertices adjacentes
         */
        void addAresta(int id1,int id2){
            this.verticesAdjacentes.get(id1).add(id2);
        }


        /**
         * @return quantidade de vertices do grafo
         */
        public int getVerticesCont() {
            return verticesCont;
        }


        /**
         * Setter de verticesCont = numero de vertices
         * @param verticesCont = quantidade de vertices do grafo
         */
        public void setVerticesCont(int verticesCont) {
            this.verticesCont = verticesCont;
        }

        /**
         * Getter de arestaCont = número de arestas
         * @return
         */
        public int getArestasCont() {
            return arestasCont;
        }


        /**
         * setter de arestaCont = número de arestas
         * @param arestasCont
         */
        public void setArestasCont(int arestasCont) {
            this.arestasCont = arestasCont;
        }


        /**
         *
         * @param vertice
         * @return retorna sua vizinhança fechada
         */
        public ArrayList<Integer> closedNeighborhood(int vertice){
            ArrayList<Integer> cn = this.getAdjByVertices(vertice);
            cn.add(vertice);

            return cn;
        }

        /**
         *
         * @param s
         * @param d
         * @return os verticeis alcançaveis por s dos quais não passam pela vizinhança de d
         */

        //BFS modificado - componente conexa sem a vizinhança de um determinado vertice
        public List<Integer> BFS(int s, int d) {
            // Marca todos os vertices como não visitados (Por definição coloca todos como falso)
            boolean visited[] = new boolean[this.verticesCont+1];

            // Cria uma queue
            LinkedList<Integer> queue = new LinkedList<Integer>();

            // Marca o vertice do parametro como visitado e tira da queue
            visited[s]=true;

            // Marca toda a vizinhança fechada do vertice d passado
            for (int cn : this.getAdjByVertices(d)) {
                visited[cn] = true;
            }

            //adiciona s na lista dos alcançaveis
            queue.add(s);
            List<Integer> alcancavel = new ArrayList<>();
            //adiciona s como o primeiro alcançavel
            alcancavel.add(s);

            while (queue.size() != 0){

                s = queue.poll();

                // Pega todos os vertices adjacentes e tira da queue o vertice s
                // Se o vertice adjacente não foi visitado, não marca como visitado e tira da queue
                Iterator<Integer> i = this.getAdjByVertices(s).listIterator();
                while (i.hasNext()) {
                    int n = i.next();
                    if (!visited[n]) {
                        visited[n] = true;
                        queue.add(n);
                        alcancavel.add(n);
                    }
                }
            }

            // retorna todos os vertices alcançaveis por s sem passar pela vizinhança fechada de d
            return alcancavel;
        }

        // Tenta achar uma tripla asteroidal
        public List<List<Integer>> findTriple() {
            List<List<Integer>> triplas = new ArrayList<>();
            for (int vertice1 : this.getVerticesAdjacentes().keySet()) {
                for (int vertice2 : this.getVerticesAdjacentes().keySet()) {
                    for (int vertice3 : this.getVerticesAdjacentes().keySet()) {

                        //verificando se existe caminho de 1 para o 3 sem passar pelos vizinhos de 2
                        if (this.BFS(vertice1, vertice2).contains(vertice3) && !this.closedNeighborhood(vertice1).contains(vertice2) && !this.closedNeighborhood(vertice1).contains(vertice3)) {
                            //verificando se existe caminho de 2 para o 3 sem passar pelos vizinhos de 1
                            if (this.BFS(vertice2, vertice1).contains(vertice3) && !this.closedNeighborhood(vertice2).contains(vertice1) && !this.closedNeighborhood(vertice2).contains(vertice3)) {
                                //verificando se existe caminho de 2 para o 1 sem passar pelos vizinhos de 3
                                if (this.BFS(vertice2, vertice3).contains(vertice1) && !this.closedNeighborhood(vertice2).contains(vertice1) && !this.closedNeighborhood(vertice2).contains(vertice3)) {
                                    List<Integer> tripla = new ArrayList<>();
                                    tripla.add(vertice1);
                                    tripla.add(vertice3);
                                    tripla.add(vertice2);

                                    triplas.add(tripla);
                                    return triplas;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        //verifica se o grafo é asteroidal sem tripla
        public void is_atfree(){
            if(this.findTriple() == null) {
                System.out.println("O grafo é asteroidal sem tripla\n");
            }
            else{
                System.out.println("O grafo não é asteroidal sem tripla, pois possui a tripla asteroidal: " + this.findTriple().get(0) + "\n");
                System.out.println("Isso é: \n");
                for(List<Integer> tripla: this.findTriple()){
                    System.out.println("Existe um caminho de "+ tripla.get(0) + " até " + tripla.get(1) + " sem passar pelos vizinhos de " + tripla.get(2));
                    System.out.println("Existe um caminho de "+ tripla.get(0) + " até " + tripla.get(2) + " sem passar pelos vizinhos de " + tripla.get(1));
                    System.out.println("Existe um caminho de "+ tripla.get(1) + " até " + tripla.get(2) + " sem passar pelos vizinhos de " + tripla.get(0));
                    break;
                }
            }
        }

        //verificando se o grafo é direcionado ou não
        public boolean is_undirected(){
            for( int v1 : this.getVerticesAdjacentes().keySet()) {
                for( int v2 : this.getAdjByVertices(v1)) {
                    //if (v2.nbhood.get( v1.id ) == null)

                    if (this.getAdjByVertices(v2) != null) {
                        if (!this.getAdjByVertices(v2).contains(v1)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }


    public static void criaVertice(String linha, Grafo grafo){
        //processando as informações da linha

        linha = linha.replaceAll("\\s+", " "); //retirando o excesso de espaços
        String[] verticeInfo = linha.split("="); // id = vertices adjacentes

        //processando o identificador do vertice
        String[] pieces = linha.split(" ");
        int id = Integer.parseInt( pieces[0] );

        grafo.addVertice(id);

        //Processando os vertices adjacentes
        if(verticeInfo.length > 1) { //Se houverem vertices adjacentes
            String[] adj = verticeInfo[1].split(" ");

            if(Objects.equals(verticeInfo[1].split(" ")[0],"")){ //caso comece com espaco, descartamos o primeiro item
                adj = Arrays.copyOfRange(verticeInfo[1].split(" "), 1, verticeInfo[1].split(" ").length);

                int[] adjToArray = Arrays.stream(adj).mapToInt(Integer::parseInt).toArray();

                for(int verticeId : adjToArray){

                    grafo.addAresta(id,verticeId);
                    grafo.setArestasCont(grafo.getArestasCont()+1); //verificar
                }
            }
        }
    }


    public static void main(String[] args){
        LeitorDeArquivo leitorDeArquivo = new LeitorDeArquivo("grafo.txt"); //caso de problema, altere para "grafo.txt.txt"
        Grafo grafo = new Grafo();
        int quantVertices = 0;

        //Criando o grafo a partir das linhas do arquivo
        for (String linha : leitorDeArquivo.abreArquivo()){
            criaVertice(linha,grafo);
            grafo.setVerticesCont(++quantVertices);
        }

        //imprimendo o grafo gerado
        for (Map.Entry<Integer,ArrayList<Integer>> entry : grafo.getVerticesAdjacentes().entrySet()) {
            System.out.println("Vertice = " + entry.getKey() + " Vertices adjacentes = " + entry.getValue().toString());
        }
        System.out.println("\n");

        //verificando se o grafo é não direcionado
        if(grafo.is_undirected()){
            grafo.is_atfree();
        }
        else{
            System.out.println("O grafo inserido é direcionado. Insira um grafo não direcionado.");
            return;
        }

    }
}
