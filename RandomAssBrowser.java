import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RandomAssBrowser extends JFrame implements ActionListener, HyperlinkListener {
    private JTextField addressBar;
    private JButton backButton, forwardButton, refreshButton, homeButton;
    private JLabel statusLabel;
    private List<String> history;
    private int currentHistoryIndex;
    private final String homePage = "https://www.google.com";
    private final Color GLASS_WHITE = new Color(255, 255, 255, 200);
    private final Color GLASS_GRAY = new Color(128, 128, 128, 150);
    private final Color APPLE_BLUE = new Color(0, 122, 255);
    private final Color BACKGROUND_GRAY = new Color(246, 246, 246);
    private final Color BORDER_GRAY = new Color(200, 200, 200);
    private JEditorPane pane;

    public RandomAssBrowser() {
        super("RandomAssBrowser by imreallyadi");

        // Enable anti-aliasing for smoother text
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        
        // Initialize history and index before components that use them
        history = new ArrayList<>();
        currentHistoryIndex = -1;

        initializeComponents();
        setupGlassyLayout();
        setupEventHandlers();

        setupWindow();
        loadPage(homePage);
    }

    private void setupWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Make window background slightly transparent
        setBackground(BACKGROUND_GRAY);

        // Try to enable transparency (works on some systems)
        try {
            if (GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().isWindowTranslucencySupported(
                GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
                setOpacity(0.98f);
            }
        } catch (Exception e) {
            // Transparency not supported, continue normally
        }
    }

    private void initializeComponents() {
        // Create glassy navigation buttons
        backButton = createGlassButton("←", "Back");
        forwardButton = createGlassButton("→", "Forward");
        refreshButton = createGlassButton("↻", "Refresh");
        homeButton = createGlassButton("⌂", "Home");

        // Apple-style address bar
        addressBar = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass background
                g2.setColor(GLASS_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Subtle border
                g2.setColor(BORDER_GRAY);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        addressBar.setOpaque(false);
        addressBar.setFont(new Font(".SF NS Text", Font.PLAIN, 14));
        addressBar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        addressBar.setForeground(Color.BLACK);

        // Content pane with glass effect
        pane = new JEditorPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass background
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        pane.setEditable(false);
        pane.setContentType("text/html");
        pane.setOpaque(false);

        // Glassy status bar
        statusLabel = new JLabel("Ready") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass background
                g2.setColor(GLASS_GRAY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        statusLabel.setOpaque(false);
        statusLabel.setFont(new Font(".SF NS Text", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setForeground(Color.DARK_GRAY);

        updateNavigationButtons();
    }

    private JButton createGlassButton(String text, String tooltip) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass background
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 122, 255, 150));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 180));
                } else {
                    g2.setColor(GLASS_WHITE);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Subtle border
                g2.setColor(BORDER_GRAY);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font(".SF NS Text", Font.PLAIN, 16));
        button.setForeground(Color.BLACK);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(40, 32));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void setupGlassyLayout() {
        // Custom panel with glass background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(250, 250, 250),
                    0, getHeight(), new Color(240, 240, 240)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);

        // Top toolbar with glass effect
        JPanel toolbar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass toolbar background
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 15, 15);

                // Subtle shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(7, 7, getWidth()-10, getHeight()-10, 15, 15);

                g2.dispose();
            }
        };
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Navigation buttons panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        navPanel.setOpaque(false);
        navPanel.add(backButton);
        navPanel.add(forwardButton);
        navPanel.add(refreshButton);
        navPanel.add(homeButton);

        // Address bar container
        JPanel addressPanel = new JPanel(new BorderLayout());
        addressPanel.setOpaque(false);
        addressPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addressPanel.add(addressBar, BorderLayout.CENTER);

        toolbar.add(navPanel, BorderLayout.WEST);
        toolbar.add(addressPanel, BorderLayout.CENTER);

        // Content area with glass scroll pane
        JScrollPane scrollPane = new JScrollPane(pane) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass background for scroll pane
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(5, 0, getWidth()-10, getHeight()-5, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Status bar container
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        // Add everything to main panel
        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void setupEventHandlers() {
        addressBar.addActionListener(this);
        pane.addHyperlinkListener(this);

        backButton.addActionListener(e -> goBack());
        forwardButton.addActionListener(e -> goForward());
        refreshButton.addActionListener(e -> refresh());
        homeButton.addActionListener(e -> goHome());

        pane.addPropertyChangeListener(evt -> {
            if ("page".equals(evt.getPropertyName())) {
                statusLabel.setText("Loading...");
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == addressBar) {
            String url = addressBar.getText().trim();
            if (!url.isEmpty()) {
                loadPage(url);
            }
        }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
            return;
        }

        if (evt instanceof HTMLFrameHyperlinkEvent frameEvt) {
            HTMLDocument doc = (HTMLDocument) pane.getDocument();
            doc.processHTMLFrameHyperlinkEvent(frameEvt);
        } else {
            try {
                String url = evt.getURL().toString();
                loadPage(url);
            } catch (NullPointerException | IllegalArgumentException e) {
                showError("Error following link: " + e.getMessage());
            }
        }
    }

    private void loadPage(String urlString) {
        String finalUrlString = urlString;
        try {
            if (!finalUrlString.startsWith("http://") && !finalUrlString.startsWith("https://")) {
                if (finalUrlString.contains(".")) {
                    finalUrlString = "http://" + finalUrlString;
                } else {
                    finalUrlString = "https://www.google.com/search?q=" +
                            java.net.URLEncoder.encode(finalUrlString, "UTF-8");
                }
            }

            URL url = new URL(finalUrlString);
            statusLabel.setText("Loading: " + finalUrlString);

            final String urlStringForWorker = finalUrlString;
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws java.io.IOException {
                    pane.setPage(url);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        addressBar.setText(urlStringForWorker);
                        addToHistory(urlStringForWorker);
                        statusLabel.setText("Done");
                    } catch (Exception e) {
                        showError("Failed to load page: " + e.getMessage());
                    }
                }
            };
            worker.execute();

        } catch (java.net.MalformedURLException e) {
            showError("Error loading page: " + e.getMessage());
        } catch (java.io.UnsupportedEncodingException e) {
            showError("Error encoding URL: " + e.getMessage());
        }
    }

    private void addToHistory(String url) {
        if (currentHistoryIndex < history.size() - 1) {
            history = new ArrayList<>(history.subList(0, currentHistoryIndex + 1));
        }
        history.add(url);
        currentHistoryIndex = history.size() - 1;
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        backButton.setEnabled(currentHistoryIndex > 0);
        forwardButton.setEnabled(currentHistoryIndex < history.size() - 1);

        // Update button appearance based on state
        backButton.repaint();
        forwardButton.repaint();
    }

    private void goBack() {
        if (currentHistoryIndex > 0) {
            currentHistoryIndex--;
            loadPage(history.get(currentHistoryIndex));
        }
    }

    private void goForward() {
        if (currentHistoryIndex < history.size() - 1) {
            currentHistoryIndex++;
            loadPage(history.get(currentHistoryIndex));
        }
    }

    private void refresh() {
        if (currentHistoryIndex >= 0 && currentHistoryIndex < history.size()) {
            loadPage(history.get(currentHistoryIndex));
            statusLabel.setText("Refreshed");
        }
    }

    private void goHome() {
        loadPage(homePage);
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);

        // Show glassy error dialog
        JOptionPane.showMessageDialog(this, message, "Browser Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        // Enable better font rendering on macOS
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        SwingUtilities.invokeLater(() -> new RandomAssBrowser().setVisible(true));
    }
}
