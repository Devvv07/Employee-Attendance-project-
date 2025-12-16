import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class AttendanceFrame extends JFrame {
    
    
    private int employeeId;
    private String employeeName;
    private String employeeEmail;
    private String department;
    private String role;
    
    
    private JLabel welcomeLabel;
    private JLabel statusLabel;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JButton viewProfileButton;
    private JButton refreshButton;
    private JButton logoutButton;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    
    
    public AttendanceFrame(int empId, String name, String email, String dept, String empRole) {
        this.employeeId = empId;
        this.employeeName = name;
        this.employeeEmail = email;
        this.department = dept;
        this.role = empRole;
        
        
        setTitle("Employee Attendance Dashboard");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        JPanel topPanel = createTopPanel();
        
        
        JPanel centerPanel = createCenterPanel();
        
        
        JPanel bottomPanel = createBottomPanel();
        
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        
        loadAttendanceRecords();
        
        
        updateStatusLabel();
        
        setVisible(true);
    }
    
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(70, 130, 180));
        
        welcomeLabel = new JLabel("Welcome, " + employeeName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel(role + " - " + department);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setForeground(Color.WHITE);
        
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(roleLabel, BorderLayout.CENTER);
        
        
        statusLabel = new JLabel("Status: Loading...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.YELLOW);
        
        panel.add(welcomePanel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        
        
        JLabel tableTitle = new JLabel("Your Attendance Records");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        
        String[] columnNames = {"Date", "Check-In Time", "Check-Out Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        attendanceTable = new JTable(tableModel);
        attendanceTable.setFont(new Font("Arial", Font.PLAIN, 13));
        attendanceTable.setRowHeight(25);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        attendanceTable.getTableHeader().setBackground(new Color(176, 196, 222));
        attendanceTable.setSelectionBackground(new Color(135, 206, 250));
        
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        
        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 248, 255));
        
        
        checkInButton = createStyledButton("Check In", new Color(34, 139, 34));
        checkInButton.addActionListener(e -> performCheckIn());
        
        
        checkOutButton = createStyledButton("Check Out", new Color(220, 20, 60));
        checkOutButton.addActionListener(e -> performCheckOut());
        
        
        viewProfileButton = createStyledButton("View Profile", new Color(70, 130, 180));
        viewProfileButton.addActionListener(e -> showProfile());
        
        
        refreshButton = createStyledButton("Refresh", new Color(255, 165, 0));
        refreshButton.addActionListener(e -> {
            loadAttendanceRecords();
            updateStatusLabel();
            JOptionPane.showMessageDialog(this, "Records refreshed!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
        });
        
        
        logoutButton = createStyledButton("Logout", new Color(128, 128, 128));
        logoutButton.addActionListener(e -> performLogout());
        
        panel.add(checkInButton);
        panel.add(checkOutButton);
        panel.add(viewProfileButton);
        panel.add(refreshButton);
        panel.add(logoutButton);
        
        return panel;
    }
    
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.BLACK); 
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(130, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }
    
    
    private void loadAttendanceRecords() {
        
        tableModel.setRowCount(0);
        
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            
            String query = "SELECT date, check_in_time, check_out_time FROM attendance " +
                          "WHERE employee_id = ? ORDER BY date DESC LIMIT 30";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, employeeId);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String date = rs.getString("date");
                String checkIn = rs.getString("check_in_time");
                String checkOut = rs.getString("check_out_time");
                
                
                String status;
                if (checkOut == null || checkOut.isEmpty()) {
                    status = "Checked In";
                } else {
                    status = "Completed";
                }
                
                
                tableModel.addRow(new Object[]{date, checkIn, checkOut, status});
            }
            
            rs.close();
            pst.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading attendance records!",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
    
    
    private void updateStatusLabel() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            
            
            String query = "SELECT check_in_time, check_out_time FROM attendance " +
                          "WHERE employee_id = ? AND date = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, employeeId);
            pst.setString(2, today);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String checkOut = rs.getString("check_out_time");
                if (checkOut == null || checkOut.isEmpty()) {
                    statusLabel.setText("Status: Checked In");
                    statusLabel.setForeground(Color.GREEN);
                    checkInButton.setEnabled(false);
                    checkOutButton.setEnabled(true);
                } else {
                    statusLabel.setText("Status: Checked Out (Completed)");
                    statusLabel.setForeground(Color.ORANGE);
                    checkInButton.setEnabled(false);
                    checkOutButton.setEnabled(false);
                }
            } else {
                statusLabel.setText("Status: Not Checked In");
                statusLabel.setForeground(Color.RED);
                checkInButton.setEnabled(true);
                checkOutButton.setEnabled(false);
            }
            
            rs.close();
            pst.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
   
    private void performCheckIn() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(new Date());
            String time = stf.format(new Date());
            
            
            String query = "INSERT INTO attendance (employee_id, date, check_in_time) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, employeeId);
            pst.setString(2, date);
            pst.setString(3, time);
            
            int result = pst.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                    "Check-in successful at " + time,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                
                loadAttendanceRecords();
                updateStatusLabel();
            }
            
            pst.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error during check-in!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
    
    
    private void performCheckOut() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(new Date());
            String time = stf.format(new Date());
            
            
            String query = "UPDATE attendance SET check_out_time = ? " +
                          "WHERE employee_id = ? AND date = ? AND check_out_time IS NULL";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, time);
            pst.setInt(2, employeeId);
            pst.setString(3, date);
            
            int result = pst.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                    "Check-out successful at " + time,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                
                loadAttendanceRecords();
                updateStatusLabel();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No check-in record found for today!",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            }
            
            pst.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error during check-out!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
    
    
    private void showProfile() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            
            String query = "SELECT COUNT(*) as total_days FROM attendance " +
                          "WHERE employee_id = ? AND check_out_time IS NOT NULL";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, employeeId);
            
            ResultSet rs = pst.executeQuery();
            int totalDays = 0;
            if (rs.next()) {
                totalDays = rs.getInt("total_days");
            }
            
            
            String profileMessage = String.format(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "        EMPLOYEE PROFILE\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "Name:           %s\n\n" +
                "Email:          %s\n\n" +
                "Department:     %s\n\n" +
                "Role:           %s\n\n" +
                "Total Working Days:  %d\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━",
                employeeName, employeeEmail, department, role, totalDays
            );
            
            
            JTextArea textArea = new JTextArea(profileMessage);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            textArea.setEditable(false);
            textArea.setBackground(new Color(240, 248, 255));
            
            JOptionPane.showMessageDialog(this,
                textArea,
                "Employee Profile",
                JOptionPane.INFORMATION_MESSAGE);
            
            rs.close();
            pst.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading profile!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
    
    
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new EmployeeLogin();
        }
    }
}