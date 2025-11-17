public class Reserva {
    private Passageiro passageiro;
    private Viagem viagem;
    private int fileira;
    private int assento;

    public Reserva(Passageiro passageiro, Viagem viagem, int fileira, int assento) {
        this.passageiro = passageiro;
        this.viagem = viagem;
        this.fileira = fileira;
        this.assento = assento;
    }
    
    //  Getters 
    public Passageiro getPassageiro() { return passageiro; }
    public Viagem getViagem() { return viagem; }
    public int getFileira() { return fileira; }
    public int getAssento() { return assento; }

    //  Setters
    public void setAssento(int fileira, int assento) {
        this.fileira = fileira;
        this.assento = assento;
    }

    @Override
    public String toString() {
        return "Reserva [Passageiro=" + passageiro.getNome() + ", Viagem=" + viagem.getNumeroOnibus() +
               ", Assento=[" + fileira + "," + assento + "]]";
    }
}