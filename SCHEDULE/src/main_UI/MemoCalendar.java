package main_UI;
// originally uploaded to http://blog.naver.com/azure0777



// saveBut delBut 이 두 버튼의 동작을 서버에 패킷 전달로 바꾸고 일정 저장 시 입력할 문양과 색깔을 선택할 수 있도록 변경.


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.*;

class CalendarDataManager{ 
	static final int CAL_WIDTH = 7;
	final static int CAL_HEIGHT = 6;
	int calDates[][] = new int[CAL_HEIGHT][CAL_WIDTH];
	int calYear;
	int calMonth;
	int calDayOfMon;
	final int calLastDateOfMonth[]={31,28,31,30,31,30,31,31,30,31,30,31};
	int calLastDate;
	Calendar today = Calendar.getInstance();
	Calendar cal;
	
	
	public CalendarDataManager(){ 
		setToday(); 
	}
	public void setToday(){
		calYear = today.get(Calendar.YEAR); 
		calMonth = today.get(Calendar.MONTH);
		calDayOfMon = today.get(Calendar.DAY_OF_MONTH);
		makeCalData(today);
	}
	private void makeCalData(Calendar cal){
		int calStartingPos = (cal.get(Calendar.DAY_OF_WEEK)+7-(cal.get(Calendar.DAY_OF_MONTH))%7)%7;
		if(calMonth == 1) calLastDate = calLastDateOfMonth[calMonth] + leapCheck(calYear);
		else calLastDate = calLastDateOfMonth[calMonth];
		for(int i = 0 ; i<CAL_HEIGHT ; i++){
			for(int j = 0 ; j<CAL_WIDTH ; j++){
				calDates[i][j] = 0;
			}
		}
		
		for(int i = 0, num = 1, k = 0 ; i<CAL_HEIGHT ; i++){
			if(i == 0) k = calStartingPos;
			else k = 0;
			for(int j = k ; j<CAL_WIDTH ; j++){
				if(num <= calLastDate) calDates[i][j]=num++;
			}
		}
	}
	private int leapCheck(int year){ 
		if(year%4 == 0 && year%100 != 0 || year%400 == 0) return 1;
		else return 0;
	}
	public void moveMonth(int mon){ 
		calMonth += mon;
		if(calMonth>11) while(calMonth>11){
			calYear++;
			calMonth -= 12;
		} else if (calMonth<0) while(calMonth<0){
			calYear--;
			calMonth += 12;
		}
		cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);
		makeCalData(cal);
	}
}

public class MemoCalendar extends CalendarDataManager{ 
	
	JFrame mainFrame;
		ImageIcon icon = new ImageIcon ( Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
	
	JPanel calOpPanel;
		JButton todayBut;
		JLabel todayLab;
		JButton lYearBut;
		JButton lMonBut;
		JLabel curMMYYYYLab;
		JButton nMonBut;
		JButton nYearBut;
		ListenForCalOpButtons lForCalOpButtons = new ListenForCalOpButtons();
	
	JPanel calPanel;
		JButton weekDaysName[];
		JButton dateButs[][] = new JButton[6][7];
		listenForDateButs lForDateButs = new listenForDateButs(); 
	
	JPanel infoPanel;
		JLabel infoClock;
	
	JPanel memoPanel;
		JLabel selectedDate;
		JTextArea memoArea;
		JScrollPane memoAreaSP;
		JPanel memoSubPanel;
		JButton saveBut; 
		JButton delBut; 
		JButton clearBut;
		
		
	JPanel frameBottomPanel;
		JLabel bottomInfo = new JLabel("Welcome to Memo Calendar!");
	
	final String WEEK_DAY_NAME[] = { "日", "月", "火", "水", "木", "金", "土" };
	//final String title = "Calendar";
	final String SaveButMsg1 = "정상적으로 저장 되었습니다.";
	final String SaveButMsg2 = "기록하신 메모가 없습니다...";
	final String SaveButMsg3 = "<html><font color=red>ERROR : 저장 도중 오류 발생!</html>";
	final String DelButMsg1 = "삭제했습니다.";
	final String DelButMsg2 = "이미 삭제된 데이터 입니다.";
	final String DelButMsg3 = "<html><font color=red>ERROR : 삭제 도중 오류 발생!</html>";
	final String ClrButMsg1 = "Clear!";
     
	public static void main(String[] args){
			
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new MemoCalendar();
			}
		});
	}
	public MemoCalendar(){ 
		
		String loggedInUserId = UserSession.getLoggedInUserId();
		final String title = (loggedInUserId + " 님의 캘린더");
		mainFrame = new JFrame(title);

		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(700,400);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setResizable(false);
		mainFrame.setIconImage(icon.getImage());
		try{
			UIManager.setLookAndFeel ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mainFrame) ;
		}catch(Exception e){
			bottomInfo.setText("ERROR : LookAndFeel setting failed");
		}
		
		calOpPanel = new JPanel();
			todayBut = new JButton("Today");
			todayBut.setToolTipText("Today");
			todayBut.addActionListener(lForCalOpButtons);
			todayLab = new JLabel(today.get(Calendar.MONTH)+1+"/"+today.get(Calendar.DAY_OF_MONTH)+"/"+today.get(Calendar.YEAR));
			lYearBut = new JButton("<<");
			lYearBut.setToolTipText("Previous Year");
			lYearBut.addActionListener(lForCalOpButtons);
			lMonBut = new JButton("<");
			lMonBut.setToolTipText("Previous Month");
			lMonBut.addActionListener(lForCalOpButtons);
			curMMYYYYLab = new JLabel("<html><table width=100><tr><th><font size=5>"+((calMonth+1)<10?"&nbsp;":"")+(calMonth+1)+" / "+calYear+"</th></tr></table></html>");
			nMonBut = new JButton(">");
			nMonBut.setToolTipText("Next Month");
			nMonBut.addActionListener(lForCalOpButtons);
			nYearBut = new JButton(">>");
			nYearBut.setToolTipText("Next Year");
			nYearBut.addActionListener(lForCalOpButtons);
			calOpPanel.setLayout(new GridBagLayout());
			GridBagConstraints calOpGC = new GridBagConstraints();
			calOpGC.gridx = 1;
			calOpGC.gridy = 1;
			calOpGC.gridwidth = 2;
			calOpGC.gridheight = 1;
			calOpGC.weightx = 1;
			calOpGC.weighty = 1;
			calOpGC.insets = new Insets(5,5,0,0);
			calOpGC.anchor = GridBagConstraints.WEST;
			calOpGC.fill = GridBagConstraints.NONE;
			calOpPanel.add(todayBut,calOpGC);
			calOpGC.gridwidth = 3;
			calOpGC.gridx = 2;
			calOpGC.gridy = 1;
			calOpPanel.add(todayLab,calOpGC);
			calOpGC.anchor = GridBagConstraints.CENTER;
			calOpGC.gridwidth = 1;
			calOpGC.gridx = 1;
			calOpGC.gridy = 2;
			calOpPanel.add(lYearBut,calOpGC);
			calOpGC.gridwidth = 1;
			calOpGC.gridx = 2;
			calOpGC.gridy = 2;
			calOpPanel.add(lMonBut,calOpGC);
			calOpGC.gridwidth = 2;
			calOpGC.gridx = 3;
			calOpGC.gridy = 2;
			calOpPanel.add(curMMYYYYLab,calOpGC);
			calOpGC.gridwidth = 1;
			calOpGC.gridx = 5;
			calOpGC.gridy = 2;
			calOpPanel.add(nMonBut,calOpGC);
			calOpGC.gridwidth = 1;
			calOpGC.gridx = 6;
			calOpGC.gridy = 2;
			calOpPanel.add(nYearBut,calOpGC);

			GridBagConstraints userLabelConstraints = new GridBagConstraints();
			userLabelConstraints.gridx = 1; // Set the X position within the grid
			userLabelConstraints.gridy = 3; // Set the Y position within the grid
			userLabelConstraints.gridwidth = 2; // Set the width in grid units
			userLabelConstraints.insets = new Insets(5, 5, 5, 5); // Set insets (optional)
			userLabelConstraints.anchor = GridBagConstraints.CENTER; // Set the anchor (optional)
					
			JButton exitButton = new JButton("종료");
			exitButton.setForeground(Color.WHITE);
			exitButton.setBackground(new Color(200, 50, 50));
			exitButton.setBorderPainted(false);
			exitButton.setFocusPainted(false);
			exitButton.setContentAreaFilled(true);
			exitButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
			        int option = JOptionPane.showConfirmDialog(mainFrame, "프로그램을 종료하시겠습니까?", "종료 확인", JOptionPane.YES_NO_OPTION);
			        if (option == JOptionPane.YES_OPTION) {
			            System.exit(0);
			        }
			    }
			});

			GridBagConstraints exit = new GridBagConstraints();
			exit.insets = new Insets(5, 0, 0, 0);
			exit.gridwidth = 1;
			exit.gridx = 3; // x축 위치 설정
			exit.gridy = 1; // y축 위치 설정
			exit.anchor = GridBagConstraints.EAST; // 위치 설정
			

			calOpPanel.add(exitButton, exit); // 버튼을 calOpPanel에 추가
			
			JButton ChatButton = new JButton("채팅");
			ChatButton.setForeground(Color.WHITE);
			ChatButton.setBackground(new Color(50, 100, 200));
			ChatButton.setBorderPainted(false);
			ChatButton.setFocusPainted(false);
			ChatButton.setContentAreaFilled(true);
			ChatButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) 
			    {
			    	
			    }
			    });
			    
			GridBagConstraints chat = new GridBagConstraints();
			chat.insets = new Insets(0, 0, 0, 5);
			chat.gridwidth = 1;
			chat.gridx = 6; // x축 위치 설정
			chat.gridy = 3; // y축 위치 설정
			chat.anchor = GridBagConstraints.EAST; // 위치 설정
			

			calOpPanel.add(ChatButton, chat); // 버튼을 calOpPanel에 추가

		calPanel=new JPanel();
			weekDaysName = new JButton[7];
			for(int i=0 ; i<CAL_WIDTH ; i++){
				weekDaysName[i]=new JButton(WEEK_DAY_NAME[i]);
				weekDaysName[i].setBorderPainted(false);
				weekDaysName[i].setContentAreaFilled(false);
				weekDaysName[i].setForeground(Color.WHITE);
				if(i == 0) weekDaysName[i].setBackground(new Color(200, 50, 50));
				else if (i == 6) weekDaysName[i].setBackground(new Color(50, 100, 200));
				else weekDaysName[i].setBackground(new Color(150, 150, 150));
				weekDaysName[i].setOpaque(true);
				weekDaysName[i].setFocusPainted(false);
				calPanel.add(weekDaysName[i]);
			}
			for(int i=0 ; i<CAL_HEIGHT ; i++){
				for(int j=0 ; j<CAL_WIDTH ; j++){
					dateButs[i][j]=new JButton();
					dateButs[i][j].setBorderPainted(false);
					dateButs[i][j].setContentAreaFilled(false);
					dateButs[i][j].setBackground(Color.WHITE);
					dateButs[i][j].setOpaque(true);
					dateButs[i][j].addActionListener(lForDateButs);
					calPanel.add(dateButs[i][j]);
				}
			}
			calPanel.setLayout(new GridLayout(0,7,2,2));
			calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			showCal(); 
						
		infoPanel = new JPanel();
			infoPanel.setLayout(new BorderLayout());
			infoClock = new JLabel("", SwingConstants.RIGHT);
			infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			infoPanel.add(infoClock, BorderLayout.NORTH);
			selectedDate = new JLabel("<Html><font size=3>"+(today.get(Calendar.MONTH)+1)+"/"+today.get(Calendar.DAY_OF_MONTH)+"/"+today.get(Calendar.YEAR)+"&nbsp;(Today)</html>", SwingConstants.LEFT);
			selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
						
		memoPanel=new JPanel();
			memoPanel.setBorder(BorderFactory.createTitledBorder("Memo"));
			memoArea = new JTextArea();
			memoArea.setLineWrap(true);
			memoArea.setWrapStyleWord(true);
			memoAreaSP = new JScrollPane(memoArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			readMemo();
			
			memoSubPanel=new JPanel();
			saveBut = new JButton("Save"); 
			
			/*
			    public List<UserSchedule> getAllSchedulesForUser(String userID) {
			        List<UserSchedule> userSchedules = new ArrayList<>();
			    	
			        try {
			            Socket socket = new Socket("localhost", 9999);
			            String packet = "Load_User_Memo:" + userID + "/n";
			            
			            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			            Object receivedObject = ois.readObject();

			            if (receivedObject instanceof List<?>) {
			                @SuppressWarnings("unchecked")
			                List<UserSchedule> receivedSchedules = (List<UserSchedule>) receivedObject;
			                userSchedules.addAll(receivedSchedules);
			            }
			            
			            ois.close();
			            socket.close();
			        } catch (IOException | ClassNotFoundException e) {
			            e.printStackTrace();
			        }

			        return userSchedules;
			    }
			    
			    private void updateMemoAndUI(String userID, JLabel selectedDateLabel) {
			        String selectedDate = selectedDateLabel.getText();
			        List<UserSchedule> userSchedules = getAllSchedulesForUser(userID);

			        // 이후 처리 로직
			    }
			}
			    
			    if (memoFromDB != null && !memoFromDB.isEmpty()) {
			        memoArea.setText(memoFromDB);
			        selectedDateLabel.setText("<html><font size=3>" + selectedDate + "&nbsp;(Saved)</html>");
			    } else {
			        memoArea.setText("");
			        selectedDateLabel.setText("<html><font size=3>" + selectedDate + "&nbsp;(Empty)</html>");
			    }
			    memoPanel.add(selectedDateLabel, BorderLayout.NORTH);
		*/	
			
			
			saveBut.addActionListener(new ActionListener(){
			    public void actionPerformed(ActionEvent arg0) {
			        String userID = UserSession.getLoggedInUserId(); // 로그인된 사용자의 ID 가져오기
			        String memo = memoArea.getText(); // 메모 내용 가져오기
			        String selectedDate = calYear + "-" + ((calMonth + 1) < 10 ? "0" : "") + (calMonth + 1) + "-" + (calDayOfMon < 10 ? "0" : "") + calDayOfMon; // 선택된 날짜 형식으로 변환
			        
			        try {
			            Socket socket = new Socket("localhost", 9999); // 서버의 IP 주소와 포트로 소켓 생성
			            
			            String packet = "ID:" + userID + ";SelectedDate:" + selectedDate + ";Memo:" + memo + "\n"; // 패킷 생성
			            
			            
			            System.out.println(packet);
			            System.out.println(userID);
			            System.out.println(memo);
			            System.out.println(selectedDate);
			            
			            OutputStream outputStream = socket.getOutputStream();
			            outputStream.write(packet.getBytes()); // 패킷 서버로 전송
			            
	                    InputStream inputStream = socket.getInputStream();
	                    byte[] responseBytes = new byte[1024];
	                    int bytesRead = inputStream.read(responseBytes);
	                    String response = new String(responseBytes, 0, bytesRead);
	      
	                    
	                    if(response.equals("SAVE_SUCCESS"))
	                    {
	                        JOptionPane.showMessageDialog(null, "정상적으로 저장되었습니다.");
	                        //updateMemoAndUI(userID, selectedDate);

	                    }
	                    
	                    else if(response.equals("SAVE_FAIL"))
	                    {
	                        JOptionPane.showMessageDialog(null, "저장에 실패했습니다...");
	                    }
			            
			            outputStream.close();
			            socket.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
			    }
			});

			delBut = new JButton("Delete");
			delBut.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					memoArea.setText("");
					File f =new File("MemoData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt");
					if(f.exists()){
						f.delete();
						showCal();
						bottomInfo.setText(DelButMsg1);
					}
					else 
						bottomInfo.setText(DelButMsg2);					
				}					
			});
			clearBut = new JButton("Clear");
			clearBut.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
			        int option = JOptionPane.showConfirmDialog(mainFrame, "내용을 지우시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
			        if (option == JOptionPane.YES_OPTION) {
			            memoArea.setText(null);
			            bottomInfo.setText(ClrButMsg1);
			        }
			    }
			});

			memoSubPanel.add(saveBut);
			memoSubPanel.add(delBut);
			memoSubPanel.add(clearBut);
			memoPanel.setLayout(new BorderLayout());
			memoPanel.add(selectedDate, BorderLayout.NORTH);
			memoPanel.add(memoAreaSP,BorderLayout.CENTER);
			memoPanel.add(memoSubPanel,BorderLayout.SOUTH);

		
		JPanel frameSubPanelWest = new JPanel();
		Dimension calOpPanelSize = calOpPanel.getPreferredSize();
		calOpPanelSize.height = 90;
		calOpPanel.setPreferredSize(calOpPanelSize);
		frameSubPanelWest.setLayout(new BorderLayout());
		frameSubPanelWest.add(calOpPanel,BorderLayout.NORTH);
		frameSubPanelWest.add(calPanel,BorderLayout.CENTER);

		
		JPanel frameSubPanelEast = new JPanel();
		Dimension infoPanelSize=infoPanel.getPreferredSize();
		infoPanelSize.height = 65;
		infoPanel.setPreferredSize(infoPanelSize);
		frameSubPanelEast.setLayout(new BorderLayout());
		frameSubPanelEast.add(infoPanel,BorderLayout.NORTH);
		frameSubPanelEast.add(memoPanel,BorderLayout.CENTER);

		Dimension frameSubPanelWestSize = frameSubPanelWest.getPreferredSize();
		frameSubPanelWestSize.width = 410;
		frameSubPanelWest.setPreferredSize(frameSubPanelWestSize);
		
		
		frameBottomPanel = new JPanel();
		frameBottomPanel.add(bottomInfo);
		
		
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(frameSubPanelWest, BorderLayout.WEST);
		mainFrame.add(frameSubPanelEast, BorderLayout.CENTER);
		mainFrame.add(frameBottomPanel, BorderLayout.SOUTH);
		mainFrame.setVisible(true);

		focusToday(); 
		
		
		ThreadConrol threadCnl = new ThreadConrol();
		threadCnl.start();	
	}
	
	private void focusToday() {
	    Calendar firstDayOfMonth = new GregorianCalendar(calYear, calMonth, 1);
	    int startingPos = (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) + 7 - 2) % 7; 
	    
	    int todayWeek = today.get(Calendar.WEEK_OF_MONTH) - firstDayOfMonth.get(Calendar.WEEK_OF_MONTH); 
	    int todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK) - 1; 
	    
	    if (todayWeek == 0) {
	        dateButs[0][startingPos + todayDayOfWeek].requestFocusInWindow();
	    } else if (todayWeek > 0 && todayWeek < CAL_HEIGHT) {
	        dateButs[todayWeek][todayDayOfWeek].requestFocusInWindow();
	    }
	}
	
	private void readMemo(){
		try{
			File f = new File("MemoData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt");
			if(f.exists()){
				BufferedReader in = new BufferedReader(new FileReader("MemoData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt"));
				String memoAreaText= new String();
				while(true){
					String tempStr = in.readLine();
					if(tempStr == null) break;
					memoAreaText = memoAreaText + tempStr + System.getProperty("line.separator");
				}
				memoArea.setText(memoAreaText);
				in.close();	
			}
			else memoArea.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void showCal(){
		for(int i=0;i<CAL_HEIGHT;i++){
			for(int j=0;j<CAL_WIDTH;j++){
				String fontColor="black";
				if(j==0) fontColor="red";
				else if(j==6) fontColor="blue";
				
				File f =new File("MemoData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)+(calDates[i][j]<10?"0":"")+calDates[i][j]+".txt");
				if(f.exists()){
					dateButs[i][j].setText("<html><b><font color="+fontColor+">"+calDates[i][j]+"</font></b></html>");
				}
				else dateButs[i][j].setText("<html><font color="+fontColor+">"+calDates[i][j]+"</font></html>");

				JLabel todayMark = new JLabel("<html><font color=green>*</html>");
				//JLabel todayMark = new JLabel("<html><font color=green>✔</html>");
				dateButs[i][j].removeAll();
				if(calMonth == today.get(Calendar.MONTH) &&
						calYear == today.get(Calendar.YEAR) &&
						calDates[i][j] == today.get(Calendar.DAY_OF_MONTH)){
					dateButs[i][j].add(todayMark);
					dateButs[i][j].setToolTipText("Today");
				}
				
				if(calDates[i][j] == 0) dateButs[i][j].setVisible(false);
				else dateButs[i][j].setVisible(true);
			}
		}
	}
	private class ListenForCalOpButtons implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == todayBut){
				setToday();
				lForDateButs.actionPerformed(e);
				focusToday();
			}
			else if(e.getSource() == lYearBut) moveMonth(-12);
			else if(e.getSource() == lMonBut) moveMonth(-1);
			else if(e.getSource() == nMonBut) moveMonth(1);
			else if(e.getSource() == nYearBut) moveMonth(12);
			
			curMMYYYYLab.setText("<html><table width=100><tr><th><font size=5>"+((calMonth+1)<10?"&nbsp;":"")+(calMonth+1)+" / "+calYear+"</th></tr></table></html>");
			showCal();
		}
	}
	private class listenForDateButs implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			int k=0,l=0;
			for(int i=0 ; i<CAL_HEIGHT ; i++){
				for(int j=0 ; j<CAL_WIDTH ; j++){
					if(e.getSource() == dateButs[i][j]){ 
						k=i;
						l=j;
					}
				}
			}
	
			if(!(k ==0 && l == 0)) calDayOfMon = calDates[k][l]; 

			cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);
			
			String dDayString = new String();
			int dDay=((int)((cal.getTimeInMillis() - today.getTimeInMillis())/1000/60/60/24));
			if(dDay == 0 && (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) 
					&& (cal.get(Calendar.MONTH) == today.get(Calendar.MONTH))
					&& (cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))) dDayString = "Today"; 
			else if(dDay >=0) dDayString = "D-"+(dDay+1);
			else if(dDay < 0) dDayString = "D+"+(dDay)*(-1);
			
			selectedDate.setText("<Html><font size=3>"+(calMonth+1)+"/"+calDayOfMon+"/"+calYear+"&nbsp;("+dDayString+")</html>");
			
			readMemo();
		}
	}
	
	private class ThreadConrol extends Thread{
		public void run(){
			boolean msgCntFlag = false;
			int num = 0;
			String curStr = new String();
			while(true){
				try{
					today = Calendar.getInstance();
					String amPm = (today.get(Calendar.AM_PM)==0?"AM":"PM");
					String hour;
							if(today.get(Calendar.HOUR) == 0) hour = "12"; 
							else if(today.get(Calendar.HOUR) == 12) hour = " 0";
							else hour = (today.get(Calendar.HOUR)<10?" ":"")+today.get(Calendar.HOUR);
					String min = (today.get(Calendar.MINUTE)<10?"0":"")+today.get(Calendar.MINUTE);
					String sec = (today.get(Calendar.SECOND)<10?"0":"")+today.get(Calendar.SECOND);
					infoClock.setText(amPm+" "+hour+":"+min+":"+sec);

					sleep(1000);
					String infoStr = bottomInfo.getText();
					
					if(infoStr != " " && (msgCntFlag == false || curStr != infoStr)){
						num = 5;
						msgCntFlag = true;
						curStr = infoStr;
					}
					else if(infoStr != " " && msgCntFlag == true){
						if(num > 0) num--;
						else{
							msgCntFlag = false;
							bottomInfo.setText(" ");
						}
					}		
				}
				catch(InterruptedException e){
					System.out.println("Thread:Error");
				}
			}
		}
	}
}