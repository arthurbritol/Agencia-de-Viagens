import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AgenciaSwingUI extends JFrame {

    private SistemaAgencia sistema;

    // Componentes da UI (Nível de Classe)
    private JList<Viagem> listaViagensDisponiveis;
    private JList<Reserva> listaReservasPendentes;
    private JTextArea consultaResultadoArea;
    private JLabel statusBarLabel;
    
    // Aba de Reserva
    private JPanel seatMapPanel;
    private JTextField reservaNomeField, reservaCpfField, reservaEmailField, reservaIdadeField;
    private JTextField reservaFileiraField, reservaAssentoField, reservaPoltronaField;
    private JLabel selectedSeatLabel;

    // Aba de Gerenciamento
    private JTextField gerenciarCpfField;
    private JTextArea gerenciarDetalhesArea; 
    private JButton btnCheckin, btnAlterarAssento, btnCancelar; 
    private JTextField alterarFileiraField, alterarAssentoField;
    private String cpfBuscaAtual; 
    
    // Aba de Consultas
    private JTextField consultaNumOnibusField;
    
    // Cores
    private final Color colorLivre = new Color(76, 175, 80); // Verde
    private final Color colorOcupado = new Color(244, 67, 54); // Vermelho
    private final Color colorSelecionado = new Color(33, 150, 243); // Azul

    public AgenciaSwingUI() {
        this.sistema = new SistemaAgencia();
        loadInitialData();

        setTitle("Sistema de Agência de Viagens");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        getContentPane().setLayout(new BorderLayout());

        // Criação das abas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.addTab(" 1. Reservar Viagem ", createReservaPanel());
        tabbedPane.addTab(" 2. Minhas Reservas / Check-in ", createGerenciarPanel());
        tabbedPane.addTab(" 3. Consultas ", createConsultaPanel());
        tabbedPane.addTab(" 4. Administrador ", createAdminPanel());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Barra de Status
        statusBarLabel = new JLabel("Pronto. Bem-vindo!");
        statusBarLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        getContentPane().add(statusBarLabel, BorderLayout.SOUTH);

        refreshLists();
    }

    private void loadInitialData() {
        sistema.adicionarViagem(new Viagem(2904, "Serra-ES", "Vitória-ES", "08:00", "09:00", 5, 4));
        sistema.adicionarViagem(new Viagem(3105, "Serra-ES", "Rio de Janeiro-RJ", "21:00", "06:00", 12, 4));
        sistema.adicionarViagem(new Viagem(4001, "Vitória-ES", "São Paulo-SP", "20:00", "08:00", 12, 4));
    }

    // Métodos da Aba 1: Reservar
    private JPanel createReservaPanel() {
        JPanel mainPanel = createBorderedPanel(new GridLayout(1, 2, 10, 10));

        // Painel da Esquerda (Lista)
        listaViagensDisponiveis = new JList<>();
        listaViagensDisponiveis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel listPanel = createTitledPanel(" 1. Selecione a Viagem ", new JScrollPane(listaViagensDisponiveis));
        
        // Painel da Direita (Mapa e Formulário)
        JPanel rightPanel = createBorderedPanel(new BorderLayout(10, 10));
        
        seatMapPanel = new JPanel();
        rightPanel.add(createTitledPanel(" 2. Selecione o Assento ", seatMapPanel), BorderLayout.CENTER);

        // Formulário (Campos)
        reservaNomeField = new JTextField();
        reservaCpfField = new JTextField();
        reservaEmailField = new JTextField();
        reservaIdadeField = new JTextField();
        reservaPoltronaField = new JTextField();
        reservaPoltronaField.setEditable(false);
        reservaPoltronaField.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Campos ocultos
        reservaFileiraField = new JTextField();
        reservaAssentoField = new JTextField();

        // Helper que cria o grid de formulário
        JPanel fieldsGrid = createFormGridPanel(
            new JLabel("Nome:"), reservaNomeField,
            new JLabel("CPF (11 dígitos):"), reservaCpfField,
            new JLabel("E-mail:"), reservaEmailField,
            new JLabel("Idade:"), reservaIdadeField,
            new JLabel("Poltrona (Auto):"), reservaPoltronaField
        );

        // Botão de Reserva
        JButton btnReservar = createButton("Confirmar Solicitação de Reserva", e -> handleReservar());
        btnReservar.setBackground(new Color(63, 81, 181));
        btnReservar.setForeground(Color.WHITE);
        btnReservar.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel formPanel = createTitledPanel(" 3. Preencha seus Dados ", fieldsGrid);
        formPanel.add(btnReservar, BorderLayout.SOUTH);
        rightPanel.add(formPanel, BorderLayout.SOUTH);

        mainPanel.add(listPanel);
        mainPanel.add(rightPanel);
        
        // Listener
        listaViagensDisponiveis.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Viagem selectedViagem = listaViagensDisponiveis.getSelectedValue();
                if (selectedViagem != null) updateSeatMap(selectedViagem);
            }
        });
        
        return mainPanel;
    }
    
    // Métodos da Aba 2: Gerenciar
    private JPanel createGerenciarPanel() {
        JPanel mainPanel = createBorderedPanel(new BorderLayout(10, 10));

        // Topo: Busca por CPF
        gerenciarCpfField = new JTextField(20);
        JButton btnBuscar = createButton("Buscar Reservas", e -> handleBuscarReserva());
        JPanel searchPanel = createTitledPanel(" 1. Busque sua Reserva ", 
            new JLabel("Digite seu CPF (11 dígitos):"), gerenciarCpfField, btnBuscar
        );
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Centro: Detalhes
        gerenciarDetalhesArea = new JTextArea("Sua reserva aparecerá aqui...");
        gerenciarDetalhesArea.setEditable(false);
        gerenciarDetalhesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        mainPanel.add(createTitledPanel(" 2. Detalhes da Reserva ", new JScrollPane(gerenciarDetalhesArea)), BorderLayout.CENTER);

        // Sul: Ações
        btnCheckin = createButton("Realizar Check-in [3]", e -> handleCheckIn());
        btnCheckin.setFont(new Font("Arial", Font.BOLD, 14));
        
        alterarFileiraField = new JTextField(5);
        alterarAssentoField = new JTextField(5);
        btnAlterarAssento = createButton("Alterar Assento [4]", e -> handleAlterarAssento());
        JPanel alterarPanel = createFlowPanel(new JLabel("Nova Fileira (Interna):"), alterarFileiraField, 
                                            new JLabel("Novo Assento (Interno):"), alterarAssentoField, btnAlterarAssento);

        btnCancelar = createButton("Cancelar Passagem [5]", e -> handleCancelar());
        btnCancelar.setBackground(colorOcupado);
        btnCancelar.setForeground(Color.WHITE);
        
        JPanel actionsGrid = new JPanel(new GridLayout(0, 1, 10, 10));
        actionsGrid.add(btnCheckin);
        actionsGrid.add(alterarPanel);
        actionsGrid.add(btnCancelar);
        
        mainPanel.add(createTitledPanel(" 3. Ações Disponíveis ", actionsGrid), BorderLayout.SOUTH);
        
        setGerenciarActionsEnabled(false);
        return mainPanel;
    }

    // Métodos da Aba 3: Consultas
    private JPanel createConsultaPanel() {
        JPanel mainPanel = createBorderedPanel(new BorderLayout(10, 10));

        // Topo: Botões
        JButton btnDisponiveis = createButton("Viagens Disponíveis [6]", e -> handleConsultarDisponiveis());
        JButton btnEsgotadas = createButton("Viagens Esgotadas [7]", e -> handleConsultarEsgotadas());
        consultaNumOnibusField = new JTextField(8);
        JButton btnDetalhes = createButton("Detalhes da Viagem [9]", e -> handleConsultarDetalhes());
        
        JPanel buttonPanel = createFlowPanel(
            btnDisponiveis, btnEsgotadas, new JSeparator(SwingConstants.VERTICAL),
            new JLabel("Nº Ônibus:"), consultaNumOnibusField, btnDetalhes
        );
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Centro: Área de Texto
        consultaResultadoArea = new JTextArea("Resultados das consultas aparecerão aqui...");
        consultaResultadoArea.setEditable(false);
        consultaResultadoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        mainPanel.add(new JScrollPane(consultaResultadoArea), BorderLayout.CENTER);

        return mainPanel;
    }

    // Métodos da Aba 4: Admin
    private JPanel createAdminPanel() {
        JPanel mainPanel = createBorderedPanel(new BorderLayout(10, 10));

        // Topo: Botões
        JButton btnProcessar = createButton("Processar Próxima Reserva da Fila [2]", e -> handleProcessarReserva());
        JButton btnRefresh = createButton("Atualizar Lista [8]", e -> refreshLists());
        mainPanel.add(createFlowPanel(btnProcessar, btnRefresh), BorderLayout.NORTH);
        
        // Centro: Lista
        listaReservasPendentes = new JList<>();
        mainPanel.add(createTitledPanel(" Fila de Reservas Pendentes (FIFO) ", new JScrollPane(listaReservasPendentes)), BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void handleReservar() {
        try {
            Viagem viagem = listaViagensDisponiveis.getSelectedValue();
            if (viagem == null) {
                showError("Nenhuma viagem selecionada.");
                return;
            }
            
            // Pega as coordenadas internas dos campos (que são preenchidos pelo clique)
            int fileira = Integer.parseInt(reservaFileiraField.getText());
            int assento = Integer.parseInt(reservaAssentoField.getText());
            
            String nome = reservaNomeField.getText();
            String cpf = reservaCpfField.getText();
            String email = reservaEmailField.getText();
            int idade = Integer.parseInt(reservaIdadeField.getText());
            
            Passageiro p = new Passageiro(nome, idade, cpf, email);
            Reserva r = new Reserva(p, viagem, fileira, assento);
            
            String resultado = sistema.solicitarReserva(r);
            
            if (resultado.startsWith("Erro")) {
                showError(resultado);
            } else {
                showInfo(resultado);
                // Limpa os campos visíveis
                reservaNomeField.setText("");
                reservaCpfField.setText("");
                reservaEmailField.setText("");
                reservaIdadeField.setText("");
                reservaPoltronaField.setText("");
            }
            refreshLists(); // Atualiza tudo
            
        } catch (NumberFormatException ex) {
            showError("Dados inválidos. Selecione uma viagem, um assento, e preencha todos os campos do passageiro.");
        } catch (IllegalArgumentException ex) {
            showError("Erro no cadastro: " + ex.getMessage());
        }
    }
    
    private void handleBuscarReserva() {
        String cpf = gerenciarCpfField.getText();
        if (cpf.isEmpty() || cpf.length() != 11) {
            showError("Por favor, digite um CPF válido de 11 dígitos.");
            return;
        }

        Reserva r = sistema.buscarReservaConfirmada(cpf);
        
        if (r != null) {
            this.cpfBuscaAtual = cpf; 
            
            // Calcula o número bonito da poltrona
            Viagem v = r.getViagem();
            int assentosPorFileira = v.getAssentos()[0].length;
            int fileira = r.getFileira();
            int assento = r.getAssento();
            int numeroPoltrona = (fileira * assentosPorFileira) + assento + 1;
            String poltronaStr = String.format("%02d (Interno: [%d,%d])", numeroPoltrona, fileira, assento);

            gerenciarDetalhesArea.setText(
                "Reserva Encontrada:\n\n" +
                "  Passageiro: " + r.getPassageiro().getNome() + "\n" +
                "  Ônibus: " + v.getNumeroOnibus() + "\n" +
                "  Origem: " + v.getOrigem() + "\n" +
                "  Destino: " + v.getDestino() + "\n" +
                "  Poltrona: " + poltronaStr
            );
            setGerenciarActionsEnabled(true);
            
            if (sistema.getPassageirosCheckedIn().contains(r.getPassageiro())) {
                btnCheckin.setText("Check-in JÁ REALIZADO");
                btnCheckin.setEnabled(false);
                btnAlterarAssento.setEnabled(false); 
                btnCancelar.setEnabled(false);
            }
            
        } else {
            gerenciarDetalhesArea.setText("Nenhuma reserva CONFIRMADA encontrada para este CPF.\n" +
                "(Verifique se sua reserva ainda está pendente na aba 'Administrador')");
            setGerenciarActionsEnabled(false);
        }
    }
    
    private void handleCheckIn() {
        String resultado = sistema.realizarCheckIn(this.cpfBuscaAtual);
        showInfo(resultado);
        statusBarLabel.setText(resultado);
        resetGerenciarTab();
    }
    
    private void handleAlterarAssento() {
        try {
            int novaFileira = Integer.parseInt(alterarFileiraField.getText());
            int novoAssento = Integer.parseInt(alterarAssentoField.getText());
            
            Reserva r = sistema.buscarReservaConfirmada(cpfBuscaAtual);
            if (r != null) {
                showInfo("Mapa da sua viagem atual:\n" + r.getViagem().getMapaAssentosTexto() + "\nVerifique se o assento [" + novaFileira + "," + novoAssento + "] está livre.");
            }
            
            String resultado = sistema.alterarAssento(this.cpfBuscaAtual, novaFileira, novoAssento);
            showInfo(resultado);
            statusBarLabel.setText(resultado);
            
            refreshLists(); 
            resetGerenciarTab();
            
        } catch (NumberFormatException ex) {
            showError("Fileira e Assento (internos) devem ser números.");
        }
    }
    
    private void handleCancelar() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente cancelar a reserva do CPF: " + this.cpfBuscaAtual + "?", 
            "Confirmar Cancelamento", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = sistema.cancelarPassagem(this.cpfBuscaAtual);
            showInfo(resultado);
            statusBarLabel.setText(resultado);
            refreshLists(); 
            resetGerenciarTab();
        }
    }
    
    private void handleConsultarDisponiveis() {
        consultaResultadoArea.setText("--- Viagens Disponíveis [6] ---\n\n");
        List<Viagem> viagens = sistema.getViagensDisponiveis();
        if (viagens.isEmpty()) {
            consultaResultadoArea.append("Nenhuma viagem disponível no momento.");
        } else {
            viagens.forEach(v -> consultaResultadoArea.append(v.toString() + "\n"));
        }
    }
    
    private void handleConsultarEsgotadas() {
        consultaResultadoArea.setText("--- Viagens Esgotadas [7] ---\n\n");
        List<Viagem> viagens = sistema.getViagensEsgotadas();
        if (viagens.isEmpty()) {
            consultaResultadoArea.append("Nenhuma viagem esgotada no momento.");
        } else {
            viagens.forEach(v -> consultaResultadoArea.append(v.toString() + "\n"));
        }
    }
    
    private void handleConsultarDetalhes() {
        try {
            int numOnibus = Integer.parseInt(consultaNumOnibusField.getText());
            Viagem v = sistema.buscarViagem(numOnibus);
            if (v == null) {
                showError("Viagem não encontrada.");
                return;
            }

            consultaResultadoArea.setText("--- Detalhes da Viagem " + numOnibus + " [9] ---\n\n");
            consultaResultadoArea.append(v.toString() + "\n\n");
            consultaResultadoArea.append(v.getMapaAssentosTexto() + "\n");
            
            consultaResultadoArea.append("--- Passageiros ---\n");
            
            consultaResultadoArea.append("\nReservas Pendentes:\n");
            sistema.getReservasPendentes().stream()
                .filter(r -> r.getViagem() == v)
                .forEach(r -> consultaResultadoArea.append("  - " + r.getPassageiro().getNome() + "\n"));
                
            consultaResultadoArea.append("\nReservas Confirmadas:\n");
            sistema.getReservasConfirmadas().stream()
                .filter(r -> r.getViagem() == v)
                .forEach(r -> consultaResultadoArea.append("  - " + r.getPassageiro().getNome() + "\n"));

            consultaResultadoArea.append("\nPassageiros com Check-in:\n");
            sistema.getPassageirosCheckedIn().stream()
                .filter(p -> sistema.buscarReservaConfirmada(p.getCpf()).getViagem() == v)
                .forEach(p -> consultaResultadoArea.append("  - " + p.getNome() + "\n"));
                
        } catch (NumberFormatException ex) {
            showError("Número do ônibus deve ser numérico.");
        }
    }
    
    private void handleProcessarReserva() {
        String resultado = sistema.processarProximaReserva();
        showInfo(resultado);
        statusBarLabel.setText(resultado);
        refreshLists(); 
    }
    
    // LÓGICA DO MAPA DE ASSENTOS
    private void updateSeatMap(Viagem viagem) {
        seatMapPanel.removeAll();
        selectedSeatLabel = null; 
        
        boolean[][] assentos = viagem.getAssentos();
        int fileiras = assentos.length;
        int assentosPorFileira = assentos[0].length;
        
        int cols = assentosPorFileira + (assentosPorFileira / 3) + (assentosPorFileira % 2 != 0 ? 1 : 0);
        if (assentosPorFileira == 4) cols = 5; // 2 + 1 + 2
        else if (assentosPorFileira == 3) cols = 4; // 2 + 1 + 1
        else if (assentosPorFileira == 2) cols = 2; // 1 + 1
        
        seatMapPanel.setLayout(new GridLayout(fileiras, cols, 5, 5));
        
        for (int i = 0; i < fileiras; i++) {
            if (assentosPorFileira == 4) {
                createSeatLabel(i, 0, assentos[i][0], assentosPorFileira);
                createSeatLabel(i, 1, assentos[i][1], assentosPorFileira);
                seatMapPanel.add(new JLabel("")); // Corredor
                createSeatLabel(i, 2, assentos[i][2], assentosPorFileira);
                createSeatLabel(i, 3, assentos[i][3], assentosPorFileira);
            } else if (assentosPorFileira == 3) {
                createSeatLabel(i, 0, assentos[i][0], assentosPorFileira);
                createSeatLabel(i, 1, assentos[i][1], assentosPorFileira);
                seatMapPanel.add(new JLabel("")); // Corredor
                createSeatLabel(i, 2, assentos[i][2], assentosPorFileira);
            } else {
                for (int j = 0; j < assentosPorFileira; j++) {
                    createSeatLabel(i, j, assentos[i][j], assentosPorFileira);
                }
            }
        }
        
        seatMapPanel.revalidate();
        seatMapPanel.repaint();
        
        reservaPoltronaField.setText("");
        reservaFileiraField.setText("");
        reservaAssentoField.setText("");
    }
    
    private void createSeatLabel(int fileira, int assento, boolean ocupado, int assentosPorFileira) {
        int numeroPoltrona = (fileira * assentosPorFileira) + assento + 1;
        String seatText = String.format("%02d", numeroPoltrona);
        
        JLabel seatLabel = new JLabel(seatText);
        seatLabel.setOpaque(true);
        seatLabel.setHorizontalAlignment(SwingConstants.CENTER);
        seatLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        seatLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        if (ocupado) {
            seatLabel.setBackground(colorOcupado);
            seatLabel.setForeground(Color.WHITE);
        } else {
            seatLabel.setBackground(colorLivre);
            seatLabel.setForeground(Color.WHITE);
            seatLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            seatLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectedSeatLabel != null) {
                        selectedSeatLabel.setBackground(colorLivre);
                        selectedSeatLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    }
                    
                    // Preenche os campos
                    reservaFileiraField.setText(String.valueOf(fileira)); 
                    reservaAssentoField.setText(String.valueOf(assento)); 
                    reservaPoltronaField.setText(seatText); 
                    
                    seatLabel.setBackground(colorSelecionado);
                    seatLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                    selectedSeatLabel = seatLabel;
                    
                    statusBarLabel.setText("Poltrona " + seatText + " selecionada.");
                }
            });
        }
        seatMapPanel.add(seatLabel);
    }
    
    // Métodos auxiliares da UI
    
    private void refreshLists() {
        Viagem selectedViagem = listaViagensDisponiveis.getSelectedValue();
        
        List<Viagem> disponiveis = sistema.getViagensDisponiveis();
        listaViagensDisponiveis.setListData(disponiveis.toArray(new Viagem[0]));
        
        if (selectedViagem != null && disponiveis.contains(selectedViagem)) {
            listaViagensDisponiveis.setSelectedValue(selectedViagem, true);
            updateSeatMap(selectedViagem);
        } else if (!disponiveis.isEmpty()) {
            listaViagensDisponiveis.setSelectedIndex(0); 
        } else {
            seatMapPanel.removeAll();
            seatMapPanel.revalidate();
            seatMapPanel.repaint();
        }
        
        List<Reserva> pendentes = new ArrayList<>(sistema.getReservasPendentes());
        listaReservasPendentes.setListData(pendentes.toArray(new Reserva[0]));
        
        statusBarLabel.setText("Listas atualizadas.");
    }
    
    private void setGerenciarActionsEnabled(boolean enabled) {
        btnCheckin.setEnabled(enabled);
        btnAlterarAssento.setEnabled(enabled);
        btnCancelar.setEnabled(enabled);
        alterarFileiraField.setEnabled(enabled);
        alterarAssentoField.setEnabled(enabled);
        
        if (!enabled) {
            btnCheckin.setText("Realizar Check-in [3]");
            alterarFileiraField.setText("");
            alterarAssentoField.setText("");
            this.cpfBuscaAtual = null;
        }
    }
    
    private void resetGerenciarTab() {
        gerenciarCpfField.setText("");
        gerenciarDetalhesArea.setText("Sua reserva aparecerá aqui...");
        setGerenciarActionsEnabled(false);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
        statusBarLabel.setText("Erro: " + message);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Informação", JOptionPane.INFORMATION_MESSAGE);
        statusBarLabel.setText(message);
    }
    
    // Cria um botão e já anexa sua ação
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    // Cria um painel com borda simples e um layout
    private JPanel createBorderedPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }
    
    // Cria um painel com um título e um componente principal (como uma lista).
    private JPanel createTitledPanel(String title, Component content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    // Cria um painel com um título e múltiplos componentes (como um formulário).
    private JPanel createTitledPanel(String title, Component... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        for (Component c : components) {
            panel.add(c);
        }
        return panel;
    }

    // Cria um painel de formulário em estilo de grade (Label, Campo, Label, Campo...).
    private JPanel createFormGridPanel(Component... components) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5)); 
        for (Component c : components) {
            panel.add(c);
        }
        return panel;
    }

    // Cria um painel de fluxo simples (FlowLayout) para botões.
    private JPanel createFlowPanel(Component... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        for (Component c : components) {
            panel.add(c);
        }
        return panel;
    }
}