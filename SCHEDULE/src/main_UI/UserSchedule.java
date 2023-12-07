package main_UI;

public class UserSchedule {
	   private String userID;
	    private String memo;
	    private String selectedDate;

	    // 생성자, 게터, 세터 등이 필요할 수 있습니다.

	    // Getter와 Setter 메서드들
	    public String getUserID() {
	        return userID;
	    }

	    public void setUserID(String userID) {
	        this.userID = userID;
	    }

	    public String getMemo() {
	        return memo;
	    }

	    public void setMemo(String memo) {
	        this.memo = memo;
	    }

	    public String getSelectedDate() {
	        return selectedDate;
	    }

	    public void setSelectedDate(String selectedDate) {
	        this.selectedDate = selectedDate;
	    }
	}