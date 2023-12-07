package main_UI;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server implements Runnable {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Schedule;encrypt=true;trustServerCertificate=true";
    private static final String DB_USER = "leewonho";
    private static final String DB_PASSWORD = "qpalzm10!";
    private static Connection connection;

    private Socket clientSocket;

    public Server(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Server started. Waiting for connections...");

            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String message = input.readLine(); // Read the message from the client
            if (message.startsWith("LOGIN:")) {
                // Process the received message
                String[] LOGIN_data = message.split(";");
                String userID = LOGIN_data[0].substring(6);
                String password = LOGIN_data[1].substring(3);
           
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE ID=? AND PW=?");
                preparedStatement.setString(1, userID);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
               
                PreparedStatement getName = connection.prepareStatement("SELECT NAME FROM users WHERE ID=?");
                getName.setString(1, userID); 
                ResultSet nameResult = getName.executeQuery();
                
                if (nameResult.next()) {
                    String userName = nameResult.getString("NAME");
                    UserSession.setLoggedInUserName(userName); // 사용자의 이름을 설정합니다.
                    System.out.println(userName);
                } else {
                    UserSession.setLoggedInUserName(""); // 해당하는 사용자가 없을 경우 이름을 빈 문자열로 설정합니다.
                }

                String response;
                
                if (resultSet.next()) {
                    response = "LOGIN_SUCCESS";
                } else {
                    response = "LOGIN_FAIL";
                }

                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(response.getBytes());
                
            } else if (message.startsWith("REGISTER:")) {
                String[] REGISTER_data = message.split(";");
                String UserID = REGISTER_data[0].substring(9);
                String password = REGISTER_data[1].substring(3);
                String UserName = REGISTER_data[2].substring(5);

                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (ID, PW, NAME) VALUES (?, ?, ?)");
                preparedStatement.setString(1, UserID);
                preparedStatement.setString(2, password);
                preparedStatement.setString(2, UserName);
                int rowsInserted = preparedStatement.executeUpdate();
                String response;

                if (rowsInserted > 0) {
                    response = "REGISTER_SUCCESS";
                } else {
                    response = "REGISTER_FAIL";
                }

                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(response.getBytes());
            }
            else if (message.startsWith("ID:")) {
                String[] SAVE_data = message.split(";");
                String userID = SAVE_data[0].substring(3);
                String selectedDate = SAVE_data[1].substring(13);
                String memo = SAVE_data[2].substring(5);
                
                System.out.println(Arrays.toString(SAVE_data));
                
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO detail (ID, Memo, Select_Date) VALUES (?, ?, ?)");
                preparedStatement.setString(1, userID);
                preparedStatement.setString(2, memo);
                preparedStatement.setString(3, selectedDate);
                
                int rowsInserted = preparedStatement.executeUpdate();
                String response;

                if (rowsInserted > 0) {
                    response = "SAVE_SUCCESS";
                } else {
                    response = "SAVE_FAIL";
                }

                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(response.getBytes());
            }
            
            /*
            else if(message.startsWith("Load_User_Memo:"))
            {
                String[] user_memo_data = message.split(";");
                String user_ID = user_memo_data[0].substring(15);

                List<UserSchedule> userSchedules = new ArrayList<>(); // 사용자 일정을 담을 리스트

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT Memo, Select_Date FROM detail WHERE ID = ?");
                    preparedStatement.setString(1, user_ID);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        UserSchedule userSchedule = new UserSchedule();
                        userSchedule.setUserID(user_ID);
                        userSchedule.setMemo(resultSet.getString("Memo"));
                        userSchedule.setSelectedDate(resultSet.getString("Select_Date"));

                        userSchedules.add(userSchedule); // 조회한 일정을 리스트에 추가
                    }
                    

                    resultSet.close();
                    preparedStatement.close();
            	
            }     catch (SQLException e) {
                e.printStackTrace();
            }
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(userSchedules); // 리스트 직렬화

                    byte[] serializedData = baos.toByteArray(); // 직렬화된 데이터를 바이트 배열로 변환

                    // 클라이언트로 데이터 전송
                    OutputStream outputStream = clientSocket.getOutputStream();
                    outputStream.write(serializedData);
                    outputStream.flush(); // 버퍼 비우기

                    oos.close();
                    baos.close();

                }catch (IOException e) {
                    e.printStackTrace();
                }

            }
            */
            
            } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
       
    	try {
    	ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Server started. Waiting for connections...");

        while (true) {
            // 클라이언트의 연결을 수락하고 클라이언트 소켓을 얻습니다.
            Socket clientSocket = serverSocket.accept();

            // 서버 객체를 생성하고 스레드에게 전달합니다.
            Server server = new Server(clientSocket);

            // 새로운 스레드를 생성하고 시작합니다.
            Thread thread = new Thread(server);
            thread.start();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    }
    

}
