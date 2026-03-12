import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DespensaGUI extends JFrame {

    private static final Color COR_FUNDO        = new Color(18, 18, 24);
    private static final Color COR_PAINEL       = new Color(28, 28, 38);
    private static final Color COR_CARD         = new Color(36, 36, 50);
    private static final Color COR_VERDE        = new Color(72, 199, 142);
    private static final Color COR_VERDE_ESCURO = new Color(40, 120, 85);
    private static final Color COR_VERMELHO     = new Color(255, 90, 90);
    private static final Color COR_AMARELO      = new Color(255, 200, 70);
    private static final Color COR_AZUL         = new Color(100, 160, 255);
    private static final Color COR_TEXTO        = new Color(230, 230, 240);
    private static final Color COR_TEXTO_FRACO  = new Color(130, 130, 155);
    private static final Color COR_BORDA        = new Color(55, 55, 75);
    private static final Color COR_TABELA_PAR   = new Color(32, 32, 44);
    private static final Color COR_TABELA_IMPAR = new Color(28, 28, 38);
    private static final Color COR_SELECAO      = new Color(72, 199, 142, 60);

    private ItemDAO dao;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JTextField campoNome;
    private JComboBox<String> campoCategoria;
    private JSpinner campoQuantidade;
    private JTextField campoValidade;
    private JLabel labelStatus;
    private JButton btnEditar, btnDeletar;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DespensaGUI() {
        dao = new ItemDAO();
        configurarJanela();
        construirUI();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Gerenciador de Despensa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout(0, 0));
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
    }

    private void construirUI() {
        add(criarHeader(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COR_PAINEL);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 2, 0, COR_VERDE),
            new EmptyBorder(14, 24, 14, 24)
        ));
        JLabel titulo = new JLabel("DESPENSA");
        titulo.setFont(new Font("Monospaced", Font.BOLD, 22));
        titulo.setForeground(COR_VERDE);
        JLabel subtitulo = new JLabel("  Gerenciador de Itens");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(COR_TEXTO_FRACO);
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esquerda.setOpaque(false);
        esquerda.add(titulo);
        esquerda.add(subtitulo);
        header.add(esquerda, BorderLayout.WEST);
        return header;
    }

    private JSplitPane criarPainelCentral() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, criarPainelTabela(), criarPainelFormulario());
        split.setDividerLocation(640);
        split.setDividerSize(4);
        split.setBackground(COR_BORDA);
        split.setBorder(null);
        return split;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(16, 16, 10, 8));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barraBotoes.setOpaque(false);

        JButton btnTodos     = criarBotaoFiltro("Todos",             COR_VERDE);
        JButton btnArmario   = criarBotaoFiltro("Armario",           COR_AZUL);
        JButton btnGeladeira = criarBotaoFiltro("Geladeira",         new Color(100, 200, 255));
        JButton btnVencendo  = criarBotaoFiltro("Vencendo (30d)",    COR_AMARELO);

        btnTodos.addActionListener(e     -> carregarTabela());
        btnArmario.addActionListener(e   -> carregarPorCategoria("armario"));
        btnGeladeira.addActionListener(e -> carregarPorCategoria("geladeira"));
        btnVencendo.addActionListener(e  -> carregarVencidos());

        barraBotoes.add(btnTodos);
        barraBotoes.add(btnArmario);
        barraBotoes.add(btnGeladeira);
        barraBotoes.add(btnVencendo);

        String[] colunas = {"ID", "Nome", "Categoria", "Qtd", "Validade"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
            public Class<?> getColumnClass(int col) { return col == 0 || col == 3 ? Integer.class : String.class; }
        };

        tabela = new JTable(modeloTabela);
        estilizarTabela();
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormularioComSelecao();
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        scroll.getViewport().setBackground(COR_PAINEL);
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            protected void configureScrollBarColors() { thumbColor = COR_BORDA; trackColor = COR_PAINEL; }
        });

        painel.add(barraBotoes, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_PAINEL);
        painel.setBorder(new EmptyBorder(20, 16, 20, 16));

        JLabel titulo = new JLabel("Novo Item");
        titulo.setFont(new Font("Monospaced", Font.BOLD, 16));
        titulo.setForeground(COR_VERDE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Preencha os campos abaixo");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(COR_TEXTO_FRACO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(subtitulo);
        painel.add(Box.createVerticalStrut(20));
        painel.add(criarSeparador());
        painel.add(Box.createVerticalStrut(16));

        campoNome = criarCampoTexto("Ex: Arroz, Leite...", 250);
        campoCategoria = new JComboBox<>(new String[]{"armario", "geladeira"});
        estilizarComboBox(campoCategoria);
        campoQuantidade = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        estilizarSpinner(campoQuantidade);
        campoValidade = criarCampoTexto("dd/MM/yyyy  (opcional)", 250);

        painel.add(criarGrupoLabel("Nome do Item", campoNome));
        painel.add(Box.createVerticalStrut(12));
        painel.add(criarGrupoLabel("Categoria", campoCategoria));
        painel.add(Box.createVerticalStrut(12));
        painel.add(criarGrupoLabel("Quantidade", campoQuantidade));
        painel.add(Box.createVerticalStrut(12));
        painel.add(criarGrupoLabel("Validade", campoValidade));
        painel.add(Box.createVerticalStrut(24));
        painel.add(criarSeparador());
        painel.add(Box.createVerticalStrut(20));

        JButton btnInserir = criarBotao("+ Inserir Item",  COR_VERDE,      COR_VERDE_ESCURO);
        btnEditar          = criarBotao("Atualizar",       COR_AZUL,       new Color(50, 90, 160));
        btnDeletar         = criarBotao("Excluir",         COR_VERMELHO,   new Color(140, 40, 40));
        JButton btnLimpar  = criarBotao("Limpar",          COR_TEXTO_FRACO, COR_CARD);

        btnEditar.setEnabled(false);
        btnDeletar.setEnabled(false);

        btnInserir.addActionListener(e -> inserirItem());
        btnEditar.addActionListener(e  -> atualizarItem());
        btnDeletar.addActionListener(e -> deletarItem());
        btnLimpar.addActionListener(e  -> limparFormulario());

        for (JButton btn : new JButton[]{btnInserir, btnEditar, btnDeletar, btnLimpar}) {
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            painel.add(btn);
            painel.add(Box.createVerticalStrut(8));
        }
        painel.add(Box.createVerticalGlue());
        return painel;
    }

    private JPanel criarStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(COR_PAINEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, COR_BORDA),
            new EmptyBorder(6, 16, 6, 16)
        ));
        labelStatus = new JLabel("Sistema pronto.");
        labelStatus.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelStatus.setForeground(COR_VERDE);
        bar.add(labelStatus, BorderLayout.WEST);
        return bar;
    }

    // CRUD
    private void inserirItem() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) { status("Informe o nome do item.", COR_VERMELHO); return; }
        String categoria   = (String) campoCategoria.getSelectedItem();
        int quantidade     = (int) campoQuantidade.getValue();
        LocalDate validade = parsarData();
        Item item = new Item(nome, categoria, quantidade, validade);
        dao.inserir(item);
        carregarTabela();
        limparFormulario();
        status("\"" + nome + "\" inserido com sucesso!", COR_VERDE);
    }

    private void atualizarItem() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { status("Selecione um item na tabela.", COR_AMARELO); return; }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) { status("Informe o nome do item.", COR_VERMELHO); return; }
        String categoria   = (String) campoCategoria.getSelectedItem();
        int quantidade     = (int) campoQuantidade.getValue();
        LocalDate validade = parsarData();
        dao.atualizar(new Item(id, nome, categoria, quantidade, validade));
        carregarTabela();
        limparFormulario();
        status("Item #" + id + " atualizado!", COR_AZUL);
    }

    private void deletarItem() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { status("Selecione um item para excluir.", COR_AMARELO); return; }
        int id      = (int)    modeloTabela.getValueAt(linha, 0);
        String nome = (String) modeloTabela.getValueAt(linha, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Excluir \"" + nome + "\" (ID: " + id + ")?",
            "Confirmar Exclusao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.deletar(id);
            carregarTabela();
            limparFormulario();
            status("Item \"" + nome + "\" excluido.", COR_VERMELHO);
        }
    }

    private void carregarTabela() {
        preencherTabela(dao.buscarTodos());
        status(modeloTabela.getRowCount() + " itens carregados.", COR_VERDE);
    }

    private void carregarPorCategoria(String cat) {
        preencherTabela(dao.buscarPorCategoria(cat));
        status("Categoria: " + cat + " - " + modeloTabela.getRowCount() + " itens.", COR_AZUL);
    }

    private void carregarVencidos() {
        preencherTabela(dao.buscarVencidosOuProximos(30));
        status("Vencidos ou vencendo em 30 dias: " + modeloTabela.getRowCount() + " itens.", COR_AMARELO);
    }

    private void preencherTabela(List<Item> itens) {
        modeloTabela.setRowCount(0);
        for (Item item : itens) {
            modeloTabela.addRow(new Object[]{
                item.getId(),
                item.getNome(),
                item.getCategoria().equals("armario") ? "Armario" : "Geladeira",
                item.getQuantidade(),
                item.getValidade() != null ? item.getValidade().format(FORMATO_DATA) : "-"
            });
        }
    }

    private void preencherFormularioComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        campoNome.setText((String) modeloTabela.getValueAt(linha, 1));
        String cat = (String) modeloTabela.getValueAt(linha, 2);
        campoCategoria.setSelectedItem(cat.equals("Armario") ? "armario" : "geladeira");
        campoQuantidade.setValue((int) modeloTabela.getValueAt(linha, 3));
        String val = (String) modeloTabela.getValueAt(linha, 4);
        campoValidade.setText(val.equals("-") ? "" : val);
        btnEditar.setEnabled(true);
        btnDeletar.setEnabled(true);
    }

    private void limparFormulario() {
        campoNome.setText("");
        campoCategoria.setSelectedIndex(0);
        campoQuantidade.setValue(1);
        campoValidade.setText("");
        tabela.clearSelection();
        btnEditar.setEnabled(false);
        btnDeletar.setEnabled(false);
    }

    private LocalDate parsarData() {
        String texto = campoValidade.getText().trim();
        if (texto.isEmpty() || texto.equals("dd/MM/yyyy  (opcional)")) return null;
        try { return LocalDate.parse(texto, FORMATO_DATA); }
        catch (DateTimeParseException e) { status("Data invalida, use dd/MM/yyyy", COR_AMARELO); return null; }
    }

    private void status(String msg, Color cor) {
        labelStatus.setText(msg);
        labelStatus.setForeground(cor);
    }

    // Estilo
    private void estilizarTabela() {
        tabela.setBackground(COR_PAINEL);
        tabela.setForeground(COR_TEXTO);
        tabela.setFont(new Font("Monospaced", Font.PLAIN, 13));
        tabela.setRowHeight(32);
        tabela.setGridColor(COR_BORDA);
        tabela.setSelectionBackground(COR_SELECAO);
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.setShowHorizontalLines(true);
        tabela.setShowVerticalLines(false);
        tabela.setFillsViewportHeight(true);
        tabela.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(COR_CARD);
        header.setForeground(COR_VERDE);
        header.setFont(new Font("Monospaced", Font.BOLD, 12));
        header.setBorder(new MatteBorder(0, 0, 2, 0, COR_VERDE));
        header.setReorderingAllowed(false);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(sel ? COR_SELECAO : (row % 2 == 0 ? COR_TABELA_PAR : COR_TABELA_IMPAR));
                setForeground(COR_TEXTO);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        tabela.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(sel ? COR_SELECAO : (row % 2 == 0 ? COR_TABELA_PAR : COR_TABELA_IMPAR));
                setForeground(col == 3 ? COR_AMARELO : COR_TEXTO_FRACO);
                setHorizontalAlignment(col == 3 ? CENTER : RIGHT);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        int[] larguras = {45, 200, 115, 55, 100};
        for (int i = 0; i < larguras.length; i++)
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
    }

    private JTextField criarCampoTexto(String placeholder, int largura) {
        JTextField campo = new JTextField() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(COR_TEXTO_FRACO);
                    g.setFont(new Font("SansSerif", Font.ITALIC, 12));
                    g.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        campo.setBackground(COR_CARD);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(COR_VERDE);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA), new EmptyBorder(6, 10, 6, 10)));
        campo.setPreferredSize(new Dimension(largura, 36));
        return campo;
    }

    private void estilizarComboBox(JComboBox<String> combo) {
        combo.setBackground(COR_CARD);
        combo.setForeground(COR_TEXTO);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private void estilizarSpinner(JSpinner spinner) {
        spinner.setBackground(COR_CARD);
        spinner.setForeground(COR_TEXTO);
        spinner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        spinner.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setBackground(COR_CARD);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setForeground(COR_TEXTO);
    }

    private JButton criarBotao(String texto, Color corTexto, Color corFundo) {
        JButton btn = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? corFundo.darker() : getModel().isRollover() ? corFundo.brighter() : corFundo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setForeground(corTexto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        btn.setOpaque(false);
        return btn;
    }

    private JButton criarBotaoFiltro(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setForeground(cor);
        btn.setBackground(COR_CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 1), new EmptyBorder(3, 8, 3, 8)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 40)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(COR_CARD); }
        });
        return btn;
    }

    private JPanel criarGrupoLabel(String label, JComponent campo) {
        JPanel grupo = new JPanel();
        grupo.setLayout(new BoxLayout(grupo, BoxLayout.Y_AXIS));
        grupo.setOpaque(false);
        grupo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(COR_TEXTO_FRACO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        grupo.add(lbl);
        grupo.add(Box.createVerticalStrut(4));
        grupo.add(campo);
        return grupo;
    }

    private JSeparator criarSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DespensaGUI().setVisible(true));
    }
}
