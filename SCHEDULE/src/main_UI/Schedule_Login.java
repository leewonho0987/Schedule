package main_UI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;


public class Schedule_Login extends JPanel {
	
    private static JFrame mainFrame;
    private static JFrame registerFrame; // registerFrame을 필드로 선언

    public static void main(String[] args) {
    	
    	createMainFrame();

    }
    
    private static void createMainFrame() {
        mainFrame = new JFrame("로그인");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(300, 150);

        JPanel panel = new JPanel();
        mainFrame.add(panel);
        placeLoginComponents(panel);

        mainFrame.setLocationRelativeTo(null);
        
        mainFrame.setVisible(true);
    }

    private static void placeLoginComponents(final JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("아이디");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("비밀번호");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(10, 80, 100, 25);
        panel.add(loginButton);

        JButton registerButton = new JButton("회원가입");
        registerButton.setBounds(160, 80, 100, 25);
        panel.add(registerButton);
    
        // 로그인 버튼 클릭 시의 동작
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 
                String userID = userText.getText();
                char[] password = passwordText.getPassword();
                String passwordStr = new String(password);

                try {
                    Socket socket = new Socket("localhost", 9999); // 서버의 IP 주소와 포트로 소켓 생성
                    
                    String message = "LOGIN:" + userID + ";PW:" + passwordStr + "\n";

                    
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(message.getBytes());
                
                    
                    InputStream inputStream = socket.getInputStream();
                    byte[] responseBytes = new byte[1024];
                    int bytesRead = inputStream.read(responseBytes);
                    String response = new String(responseBytes, 0, bytesRead);
      
                    
                    if(response.equals("LOGIN_SUCCESS"))
                    {
                        JOptionPane.showMessageDialog(panel, "로그인 성공!");
                        UserSession.setLoggedInUserId(userID);
                        MemoCalendar memoCalendar = new MemoCalendar(); // 달력 UI 인스턴스 생성
                        memoCalendar.mainFrame.setVisible(true); // 달력 UI 표시
                        
                        mainFrame.dispose(); // 로그인 창 닫기
                    }
                    
                    else if(response.equals("LOGIN_FAIL"))
                    {
                        JOptionPane.showMessageDialog(panel, "로그인 실패.");
                    }
                    
                    outputStream.close();
                    inputStream.close();
                    socket.close();
                    
            }         catch (IOException ex) {
                ex.printStackTrace();
            }
            }
        });
        
        
        // 회원가입 버튼 클릭 시의 동작
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterWindow();
            }
        });
    }

    private static void openRegisterWindow() {
        registerFrame = new JFrame("회원가입");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setSize(400, 200);
        registerFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        registerFrame.add(panel);
        placeRegisterComponents(panel);

        registerFrame.setVisible(true);
    }

    private static void placeRegisterComponents(final JPanel panel) {
    	
    	panel.setLayout(null);
 
        JLabel userLabel = new JLabel("아이디");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);
        
        JButton checkDuplicateButton = new JButton("중복 확인");
        checkDuplicateButton.setBounds(270, 10, 100, 20); 
        panel.add(checkDuplicateButton);

        JLabel passwordLabel = new JLabel("비밀번호");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);
        
        JLabel nameLabel = new JLabel("이름");
        nameLabel.setBounds(10, 70, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(100, 70, 160, 25);
        panel.add(nameText);

        JButton registerButton = new JButton("회원가입");
        registerButton.setBounds(80, 110, 100, 20); 
        panel.add(registerButton);
        
        JButton cancelButton = new JButton("취소");
        cancelButton.setBounds(200, 110, 80, 20);
        panel.add(cancelButton);

        // 회원가입 버튼 클릭 시의 동작
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = userText.getText();
                char[] password = passwordText.getPassword();
                String passwordStr = new String(password);
                String username = nameText.getText();
                
                try (Socket socket = new Socket("localhost", 9999);
                     OutputStream outputStream = socket.getOutputStream();
                     InputStream inputStream = socket.getInputStream()) {
                    
                    String message = "REGISTER:" + userID+ ";PW:" + passwordStr + ";NAME:" + username+ "\n"; // 회원가입 요청 메시지
                    outputStream.write(message.getBytes());

                    // 응답 받기 (서버에서 회원가입 결과)
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String response = reader.readLine();

                    if (response.equals("REGISTER_SUCCESS")) {
                    	JOptionPane.showMessageDialog(panel, "회원가입 성공!");
                    	registerFrame.dispose();
                    } else if (response.equals("REGISTER_FAIL")) {
                    	JOptionPane.showMessageDialog(panel, "회원가입 실패...");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent e) {
        		
        		int result = JOptionPane.showConfirmDialog(panel, "정말로 취소하시겠습니까?", "형식 입력 취소", JOptionPane.YES_NO_OPTION);

        		if (result == JOptionPane.YES_OPTION) {
        			registerFrame.dispose();
        		}

        	}
        });
    }
}
   