import java.util.regex.Pattern;

public class Passageiro {
    private String nome;
    private int idade;
    private String cpf;
    private String email;

    public Passageiro(String nome, int idade, String cpf, String email) {
        if (!isCpfValido(cpf)) {
            throw new IllegalArgumentException("CPF inválido. Deve conter 11 dígitos numéricos.");
        }
        if (!isEmailValido(email)) {
            throw new IllegalArgumentException("E-mail inválido. Deve conter '@'.");
        }
        this.nome = nome;
        this.idade = idade;
        this.cpf = cpf;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }
    
    public String getEmail() {
        return email;
    }
    
    public int getIdade() {
        return idade;
    }

    // Validações 
    private boolean isCpfValido(String cpf) {
        return cpf != null && cpf.matches("\\d{11}");
    }

    private boolean isEmailValido(String email) {
        return email != null && email.contains("@");
    }

    @Override
    public String toString() {
        return "Passageiro [Nome=" + nome + ", CPF=" + cpf + ", Email=" + email + "]";
    }
}