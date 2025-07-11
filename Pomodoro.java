// RAW Pomodoro is a pomodoro app by Roland Waddilove (github.com/rwaddilove/).
// I'm learning Java and this is just to practise what I have learnt so far.
// It's posted here for others learning Java. There's still a lot I don't know,
// so it's not perfect and there may be better ways to do this. Public Domain.

// int[] pom = {} work,break... pairs. Set values, number of items to suit.
// Change it to {2,1,2,1} to quickly test RAW Pomodoro work/break sessions.
// background.jpg should be in the same folder as Pomodoro.class.

import javax.swing.*;
import java.awt.*;

class Notification {
    public static void Show(String message) {
        // Create a JWindow for the notification
        JWindow notificationWindow = new JWindow();
        notificationWindow.setSize(300, 100);
        notificationWindow.getContentPane().setBackground(new Color(255, 255, 192));
        notificationWindow.setLayout(new BorderLayout());
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        notificationWindow.add(messageLabel, BorderLayout.CENTER);
        notificationWindow.setAlwaysOnTop(true);        // Set the window to always be on top
        // Position the notification at the top-right corner of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - notificationWindow.getWidth() - 10;
        int y = 10; // 10 pixels from the top
        notificationWindow.setLocation(x, y);
        notificationWindow.setVisible(true);        // Make the notification visible
        // close after 5 seconds
        new Timer(5000, e -> notificationWindow.dispose()).start();
    }
}

class FormMain {
    boolean clockRunning = false;
    int[] pom = {25,5,25,5,25,15,25,5,25,5,25,15,25,5,25,5,25,15}; // work/break sessions
    int pomIndex = 0;
    int[] clock = {pom[pomIndex] * 60};     // int clock[0] gets around scope problem
    int workCount = 0;      // count how many work sessions completed

    Timer timer;
    JLabel labelClock;
    JLabel labelStatus;
    JButton buttonStartStop;
    JButton buttonNext;
    JPanel panelBottom;

    public FormMain() {
        // Create the JFrame window -----------------------------------
        JFrame frame = new JFrame("RAW Pomodoro");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 280);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        // create top panel ---------------------------------------------------
        JPanel panelTop = new JPanel();
        panelTop.setBackground(new Color(158, 207, 224));
        // Start-Stop button
        buttonStartStop = new JButton("Start");
        buttonStartStop.addActionListener(e -> ClockStartStop());
        panelTop.add(buttonStartStop);
        // Next button
        buttonNext = new JButton("Skip");
        buttonNext.addActionListener(e -> NextButton());
        panelTop.add(buttonNext);
        // Reset button
        JButton buttonReset = new JButton("Reset");
        buttonReset.setToolTipText("Reset work/breaks");
        buttonReset.addActionListener(e -> ResetButton());
        panelTop.add(buttonReset);

        // create center panel ---------------------------------------------------
        JPanel panelCenter = new JPanel() {
            String path = Pomodoro.class.getClassLoader().getResource("background.jpg").getPath();
            private Image background = new ImageIcon(path).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };

        panelCenter.setLayout(new BorderLayout());
        // text field for new note title
        labelClock = new JLabel(String.format("%02d : %02d", clock[0] / 60, clock[0] % 60), SwingConstants.CENTER);
        labelClock.setFont(new Font("Arial", Font.PLAIN, 48));
        panelCenter.add(labelClock);

        // create bottom panel ---------------------------------------------------
        panelBottom = new JPanel();
        panelBottom.setBackground(new Color(158, 207, 224));
        labelStatus = new JLabel("Start " + pom[0] + " min work. 0 sessions done.");
        panelBottom.add(labelStatus);

        // add panels to frame --------------------------------------------
        frame.add(panelTop, BorderLayout.NORTH);
        frame.add(panelCenter, BorderLayout.CENTER);
        frame.add(panelBottom, BorderLayout.SOUTH);
        frame.setVisible(true);

        // set up a timer to fire every second
        timer = new Timer(1000, e -> {
            if (clockRunning) {
                labelClock.setText(String.format("%02d : %02d", clock[0] / 60, clock[0] % 60));
                if (clock[0] == 0) {
                    if (pomIndex % 2 == 0) ++workCount;     // work session completed
                    Notification.Show("RAW Pomodoro session ended!");
                    ClockStartStop(); }
                else
                    --clock[0];
            }
        });
        timer.start();
    }

    public void ClockStartStop() {
        clockRunning = !clockRunning;

        // clock is running
        if (clockRunning) {
            buttonStartStop.setText("Pause");
            if (pomIndex % 2 == 0)          // work sessions are even numbers
                labelStatus.setText("Now: " + pom[pomIndex] + " min work!");
            else
                labelStatus.setText("Now: " + pom[pomIndex] + " min break!");
            return;
        }

        // clock paused by user?
        if (clock[0] > 0) {
            buttonStartStop.setText("Resume");
            return;
        }

        // clock = 0, session ended, clock not running
        pomIndex = (pomIndex + 1) % pom.length;     // next work or break session
        clock[0] = pom[pomIndex] * 60;              // set clock to next session in seconds
        labelClock.setText(String.format("%02d : %02d", clock[0] / 60, clock[0] % 60));
        if (pomIndex % 2 == 0)                      // work sessions are even numbers
            labelStatus.setText("Start " + pom[pomIndex] + " min work. " + workCount + " sessions done.");
        else
            labelStatus.setText("Start " + pom[pomIndex] + " min break!");
        buttonStartStop.setText("Start");
        buttonNext.setText("Skip");
    }

        public void NextButton() {
        clockRunning = true;
        clock[0] = 0;
        ClockStartStop();
    }

    public void ResetButton() {
        clockRunning = false;
        pomIndex = 0;
        clock[0] = pom[pomIndex] * 60;
        labelStatus.setText("Start " + pom[0] + " min work. 0 sessions done.");
        labelClock.setText(String.format("%02d : %02d", clock[0] / 60, clock[0] % 60));
    }
}


public class Pomodoro {
    public static void main(String[] args) {
        new FormMain();
    }
}
