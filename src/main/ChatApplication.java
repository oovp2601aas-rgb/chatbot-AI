package main;

import controller.ChatController;
import java.awt.*;
import javax.swing.*;
import ui.BuyerPanel;
import ui.SellerPanel;

/**
 * ChatApplication - Entry point
 *
 * PERUBAHAN BESAR:
 * - Setiap panel (1 buyer + 3 seller) tampil sebagai JFrame TERPISAH
 * - Total 4 jendela, berjejer otomatis di layar
 * - Semua terhubung melalui 1 ChatController yang sama
 */
public class ChatApplication {

    private ChatController controller;
    private BuyerFrame     buyerFrame;
    private SellerFrame[]  sellerFrames = new SellerFrame[3];

    public ChatApplication() {
        // 1 controller untuk semua
        controller = new ChatController();

        // Buat buyer frame
        buyerFrame = new BuyerFrame(controller);

        // Buat 3 seller frame
        for (int i = 0; i < 3; i++) {
            sellerFrames[i] = new SellerFrame(controller, i);
        }
    }

    /** Tampilkan semua jendela — posisi bebas, sedikit offset agar tidak tumpuk persis */
    public void showAll() {
        int frameW = 520;
        int frameH = 700;

        // Buyer di posisi default tengah-kiri, tiap jendela offset 30px
        buyerFrame.setSize(frameW, frameH);
        buyerFrame.setLocationRelativeTo(null); // tengah layar
        buyerFrame.setVisible(true);

        // Seller 1, 2, 3 offset ke kanan-bawah dari buyer
        Point base = buyerFrame.getLocation();
        for (int i = 0; i < 3; i++) {
            int offset = (i + 1) * 30;
            sellerFrames[i].setSize(frameW, frameH);
            sellerFrames[i].setLocation(base.x + offset, base.y + offset);
            sellerFrames[i].setVisible(true);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  BuyerFrame
    // ══════════════════════════════════════════════════════════
    static class BuyerFrame extends JFrame {
        BuyerFrame(ChatController controller) {
            setTitle("\uD83D\uDECD Buyer");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            BuyerPanel buyerPanel = new BuyerPanel();
            controller.setBuyerPanel(buyerPanel);
            add(buyerPanel);

            // Menu bar
            JMenuBar mb = new JMenuBar();
            JMenu fm = new JMenu("File");
            JMenuItem clearItem = new JMenuItem("Clear All Chats");
            clearItem.addActionListener(e -> controller.clearAllChats());
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.addActionListener(e -> System.exit(0));
            fm.add(clearItem); fm.addSeparator(); fm.add(exitItem);
            mb.add(fm);
            setJMenuBar(mb);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  SellerFrame
    // ══════════════════════════════════════════════════════════
    static class SellerFrame extends JFrame {
        SellerFrame(ChatController controller, int sellerIndex) {
            setTitle("\uD83D\uDC68\u200D\uD83C\uDF73 Seller " + (sellerIndex + 1));
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // jangan exit semua kalau seller ditutup

            SellerPanel sellerPanel = new SellerPanel(sellerIndex);
            controller.addSellerPanel(sellerPanel);
            add(sellerPanel);

            // Menu bar per seller
            JMenuBar mb = new JMenuBar();
            JMenu fm = new JMenu("File");
            JMenuItem clearItem = new JMenuItem("Clear My Requests");
            clearItem.addActionListener(e -> sellerPanel.clearAllRequests());
            fm.add(clearItem);
            mb.add(fm);
            setJMenuBar(mb);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  Main
    // ══════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ChatApplication app = new ChatApplication();
            app.showAll();

            System.out.println("==============================================");
            System.out.println("Food Chat System - Multi Window");
            System.out.println("  Buyer Frame    : 1");
            System.out.println("  Seller Frames  : 3");
            System.out.println("  Controller     : 1 shared ChatController");
            System.out.println("==============================================");
        });
    }
}