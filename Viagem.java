public class Viagem {
    private int numeroOnibus;
    private String origem;
    private String destino;
    private String horarioPartida;
    private String horarioChegada;
    private boolean[][] assentos; 
    private int capacidadeMaxima;
    private int assentosOcupados;

    public Viagem(int numeroOnibus, String origem, String destino, String horarioPartida, String horarioChegada, int fileiras, int assentosPorFileira) {
        if (String.valueOf(numeroOnibus).length() != 4) {
            throw new IllegalArgumentException("O número do ônibus deve ter 4 dígitos.");
        }
        this.numeroOnibus = numeroOnibus;
        this.origem = origem;
        this.destino = destino;
        this.horarioPartida = horarioPartida;
        this.horarioChegada = horarioChegada;
        this.assentos = new boolean[fileiras][assentosPorFileira];
        this.capacidadeMaxima = fileiras * assentosPorFileira;
        this.assentosOcupados = 0;
    }

    //  Getters 
    public int getNumeroOnibus() {
        return numeroOnibus;
    }
    
    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public int getVagasDisponiveis() {
        return capacidadeMaxima - assentosOcupados;
    }
    
    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public boolean[][] getAssentos() {
        return assentos;
    }

    // Métodos de Assento
    public boolean isAssentoValido(int fileira, int lugar) {
         if (fileira >= 0 && fileira < assentos.length && lugar >= 0 && lugar < assentos[0].length) {
            return true;
        }
        return false;
    }

    public boolean isAssentoDisponivel(int fileira, int lugar) {
        if (!isAssentoValido(fileira, lugar)) {
            return false; 
        }
        return !assentos[fileira][lugar];
    }

    public boolean ocuparAssento(int fileira, int lugar) {
        if (isAssentoDisponivel(fileira, lugar)) {
            assentos[fileira][lugar] = true;
            assentosOcupados++;
            return true;
        }
        return false;
    }
    
    public boolean liberarAssento(int fileira, int lugar) {
        if (!isAssentoValido(fileira, lugar) || assentos[fileira][lugar] == false) {
            return false;
        }
        assentos[fileira][lugar] = false;
        assentosOcupados--;
        return true;
    }
    
    public String getMapaAssentosTexto() {
        StringBuilder mapa = new StringBuilder("Mapa de Assentos (X=Ocupado, O=Livre):\n   ");
        
        for (int j = 0; j < assentos[0].length; j++) {
             mapa.append("[" + j + "]");
        }
        mapa.append("\n");

        for (int i = 0; i < assentos.length; i++) {
            mapa.append("[" + i + "] "); // Número da fileira
            for (int j = 0; j < assentos[i].length; j++) {
                mapa.append((assentos[i][j] ? "[X]" : "[O]") + " ");
            }
            mapa.append("\n");
        }
        return mapa.toString();
    }


    @Override
    public String toString() {
        return "Viagem [Ônibus=" + numeroOnibus + ", Origem=" + origem + ", Destino=" + destino + 
               ", Vagas=" + getVagasDisponiveis() + "/" + capacidadeMaxima + "]";
    }
}