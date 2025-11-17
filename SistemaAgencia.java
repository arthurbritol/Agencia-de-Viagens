import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.stream.Collectors;

public class SistemaAgencia {
    
    private List<Viagem> viagens;
    private List<Reserva> reservasConfirmadas; 
    private Queue<Reserva> reservasPendentes;
    private Stack<Passageiro> passageirosCheckedIn;

    public SistemaAgencia() {
        this.viagens = new ArrayList<>();
        this.reservasConfirmadas = new ArrayList<>(); 
        this.reservasPendentes = new LinkedList<>();
        this.passageirosCheckedIn = new Stack<>();
    }

    // Métodos de Viagem 
    public void adicionarViagem(Viagem viagem) {
        viagens.add(viagem);
    }
    
    public Viagem buscarViagem(int numeroOnibus) {
        for (Viagem v : viagens) {
            if (v.getNumeroOnibus() == numeroOnibus) {
                return v;
            }
        }
        return null;
    }

    public List<Viagem> getViagensDisponiveis() {
        return viagens.stream()
                .filter(v -> v.getVagasDisponiveis() > 0)
                .collect(Collectors.toList());
    }
    
    public List<Viagem> getViagensEsgotadas() {
         return viagens.stream()
                .filter(v -> v.getVagasDisponiveis() == 0)
                .collect(Collectors.toList());
    }

    // Métodos de Reserva
    
    public String solicitarReserva(Reserva reserva) {
        if (!reserva.getViagem().isAssentoValido(reserva.getFileira(), reserva.getAssento())) {
            return "Erro: Assento ["+reserva.getFileira()+","+reserva.getAssento()+"] é inválido.";
        }
        if (reserva.getViagem().isAssentoDisponivel(reserva.getFileira(), reserva.getAssento())) {
            reservasPendentes.offer(reserva); 
            return "Reserva solicitada! Aguardando processamento do admin.";
        } else {
            return "Erro: O assento ["+reserva.getFileira()+","+reserva.getAssento()+"] não está disponível.";
        }
    }

    public String processarProximaReserva() {
        Reserva reserva = reservasPendentes.poll(); 
        
        if (reserva != null) {
            Viagem viagem = reserva.getViagem();
            boolean sucesso = viagem.ocuparAssento(reserva.getFileira(), reserva.getAssento());
            
            if (sucesso) {
                reservasConfirmadas.add(reserva); 
                return "Reserva de " + reserva.getPassageiro().getNome() + " CONFIRMADA!";
            } else {
                return "Falha ao confirmar reserva de " + reserva.getPassageiro().getNome() + 
                       ". O assento foi ocupado.";
            }
        } else {
            return "Fila de reservas pendentes está vazia.";
        }
    }
    
    public Queue<Reserva> getReservasPendentes() {
        return reservasPendentes;
    }
    
    // Métodos de Check-in
    public String realizarCheckIn(String cpf) {
        Reserva reserva = buscarReservaConfirmada(cpf);
        if (reserva == null) {
            return "Erro: Nenhuma reserva confirmada encontrada para o CPF " + cpf;
        }
        
        for(Passageiro p : passageirosCheckedIn) {
            if(p.getCpf().equals(cpf)) {
                return "Erro: Passageiro " + p.getNome() + " já fez check-in.";
            }
        }
        
        passageirosCheckedIn.push(reserva.getPassageiro()); // LIFO
        return reserva.getPassageiro().getNome() + " fez o check-in com sucesso!";
    }

    public String alterarAssento(String cpf, int novaFileira, int novoAssento) {
        Reserva reserva = buscarReservaConfirmada(cpf);
        if (reserva == null) {
            return "Erro: Nenhuma reserva confirmada encontrada para o CPF " + cpf;
        }
        
        Viagem viagem = reserva.getViagem();
        
        if (!viagem.isAssentoDisponivel(novaFileira, novoAssento)) {
            return "Erro: O novo assento [" + novaFileira + "," + novoAssento + "] não está disponível.";
        }
        
        viagem.liberarAssento(reserva.getFileira(), reserva.getAssento());
        viagem.ocuparAssento(novaFileira, novoAssento);
        reserva.setAssento(novaFileira, novoAssento);
        
        return "Sucesso! Assento de " + reserva.getPassageiro().getNome() + 
                           " alterado para [" + novaFileira + "," + novoAssento + "].";
    }

    public String cancelarPassagem(String cpf) {
        Reserva reservaPendente = buscarReservaNaLista(cpf, reservasPendentes);
        if (reservaPendente != null) {
            reservasPendentes.remove(reservaPendente);
            return "Reserva pendente de " + reservaPendente.getPassageiro().getNome() + " cancelada.";
        }

        Reserva reservaConfirmada = buscarReservaConfirmada(cpf);
        if (reservaConfirmada != null) {
            reservaConfirmada.getViagem().liberarAssento(reservaConfirmada.getFileira(), reservaConfirmada.getAssento());
            reservasConfirmadas.remove(reservaConfirmada);
            passageirosCheckedIn.remove(reservaConfirmada.getPassageiro()); 
            return "Reserva confirmada de " + reservaConfirmada.getPassageiro().getNome() + " cancelada. O assento foi liberado.";
        }
        
        return "Nenhuma reserva (pendente ou confirmada) encontrada para o CPF " + cpf;
    }
    
    
    // Métodos Auxiliares 
    public Reserva buscarReservaConfirmada(String cpf) {
        return buscarReservaNaLista(cpf, this.reservasConfirmadas);
    }
    
    private Reserva buscarReservaNaLista(String cpf, Iterable<Reserva> lista) {
        for (Reserva r : lista) {
            if (r.getPassageiro().getCpf().equals(cpf)) {
                return r;
            }
        }
        return null;
    }

    public List<Reserva> getReservasConfirmadas() {
        return reservasConfirmadas;
    }

    public Stack<Passageiro> getPassageirosCheckedIn() {
        return passageirosCheckedIn;
    }

    public List<Viagem> getTodasViagens() {
        return viagens;
    }
}