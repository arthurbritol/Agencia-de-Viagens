# 🚌 Sistema de Agência de Viagens (Java + Swing)

Este é um sistema de desktop para gestão de viagens de autocarro (ônibus), construído inteiramente em **Java Swing**. O projeto demonstra o uso de estruturas de dados fundamentais (Pilha, Fila e Lista) para gerir reservas, check-ins e passageiros, tudo através de uma interface gráfica interativa e profissional.

O foco deste projeto é combinar a lógica de backend (baseada em estruturas de dados) com um frontend (UI) intuitivo e responsivo, sem a necessidade de bibliotecas externas.

---

## ✨ Funcionalidades Principais

O sistema é dividido em abas (separadores) para uma navegação clara:

### 1. Reservar Viagem
* **Mapa de Poltronas Interativo:** Ao selecionar uma viagem da lista, um mapa visual do autocarro é gerado.
* **Seleção por Clique:** O utilizador clica na poltrona desejada (verde para "Livre"), e os campos de reserva são preenchidos automaticamente.
* **Feedback Visual:** Poltronas mostram o seu estado por cor (Livre, Ocupado, Selecionado).
* **Layout do Autocarro:** O mapa simula o layout real de um autocarro, incluindo o corredor central.

### 2. Minhas Reservas / Check-in
* **Consulta por CPF:** O passageiro digita o seu CPF para buscar reservas *já confirmadas*.
* **Detalhes da Reserva:** O sistema exibe os detalhes da viagem, incluindo o número do autocarro e o **número da poltrona** (ex: "Poltrona 08").
* **Check-in (Lógica de Pilha):** O passageiro pode realizar o check-in. Passageiros que fazem check-in são adicionados a uma **Pilha (Stack)**, simulando a ordem de embarque (LIFO - Último a entrar é o primeiro a sair).
* **Alterar e Cancelar:** O utilizador pode alterar a sua poltrona ou cancelar a passagem.

### 3. Consultas
* Permite visualizar listas de **Viagens Disponíveis** e **Viagens Esgotadas**.
* Permite consultar os detalhes completos de uma viagem (incluindo o mapa de poltronas) pelo número do autocarro.

### 4. Administrador
* **Fila de Reservas (Lógica de Fila):** Novas reservas não são confirmadas imediatamente. Elas entram numa **Fila (Queue)** de processamento.
* **Processamento FIFO:** O administrador pode processar a próxima reserva da fila (FIFO - Primeiro a entrar é o primeiro a sair), confirmando-a e ocupando a poltrona.

---

## 📂 Estrutura dos Ficheiros

O projeto é dividido em dois componentes principais: o **Backend (Lógica)** e o **Frontend (UI)**.

### Backend (A Lógica de Negócio)

* `Viagem.java`: Classe de modelo (POJO). Representa uma viagem de autocarro, contendo origem, destino e o mapa de poltronas (armazenado como `boolean[][]`).
* `Passageiro.java`: Classe de modelo. Guarda os dados do passageiro (nome, cpf, email) e faz validações básicas.
* `Reserva.java`: Classe de modelo. Vincula um `Passageiro` a uma `Viagem` e a uma poltrona específica (coordenadas `[fileira][assento]`).
* `SistemaAgencia.java`: O "cérebro" do backend. Esta classe é "headless" (não sabe nada sobre a interface gráfica). Ela gere as listas (`List<Viagem>`), a fila de pendências (`Queue<Reserva>`) e a pilha de check-ins (`Stack<Passageiro>`). Todos os métodos de negócio (reservar, cancelar, processar) estão aqui.

### Frontend (A Interface Gráfica)

* `Main.java`: O ponto de entrada da aplicação. A sua única função é definir o "Look and Feel" (visual) para que o aplicativo use a aparência nativa do sistema operativo (Windows, macOS, etc.) e, em seguida, criar e exibir a janela principal.
* `AgenciaSwingUI.java`: O coração da interface gráfica. Este ficheiro constrói todas as janelas, abas, botões, listeners e o mapa de poltronas interativo. Ele captura as ações do utilizador (cliques) e chama os métodos correspondentes no `SistemaAgencia.java` para executar a lógica.

---

## 🚀 Como Executar

Este projeto usa **Java Swing puro**, que é parte nativa do JDK (Java Development Kit). Você **não precisa** de bibliotecas externas, SDKs do JavaFX, ou argumentos de VM (`--module-path`).

### 1. Requisitos
* Qualquer **JDK (Java Development Kit)** versão 8 ou mais recente.

### 2. Passos para Executar (via IDE - Recomendado)
1.  Abra o projeto na sua IDE favorita (IntelliJ, VS Code, Eclipse, etc.).
2.  Coloque todos os 6 ficheiros `.java` no mesmo pacote.
3.  Abra o ficheiro `Main.java`.
4.  Clique no botão "Run" (Executar).

A aplicação será compilada e executada sem nenhuma configuração adicional.

### 3. Passos para Executar (via Terminal)
1.  Navegue até à pasta onde estão os 6 ficheiros `.java`.
2.  Compile todos os ficheiros:
    ```bash
    javac *.java
    ```
3.  Execute a classe `Main`:
    ```bash
    java Main
    ```
