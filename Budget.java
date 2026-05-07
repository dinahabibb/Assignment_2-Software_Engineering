public class Budget {
    
    private int id;
    private String category;
    private double limitAmount;
    private String startDate;
    private String endDate;
    private int alertThreshold;
    private double currentSpending;
    private String status;
    

    public Budget(int id, String category, double limitAmount,String startDate, String endDate, int alertThreshold,double currentSpending,String status) {
        this.id = id;
        this.category = category;
        this.limitAmount = limitAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alertThreshold = alertThreshold;
        this.currentSpending = currentSpending;
        this.status =status;
    }

    public int getId() {
        return id;
    }
    
    public String getCategory() {
        return category;
    }
    
    public double getLimitAmount() {
       return limitAmount;
    }
    
    public double getCurrentSpending() {
        return currentSpending ;
    }
    
    public String getStatus() {
        return status ;
    }
    
    public int getAlertThreshold() {
        return alertThreshold;
    }
    
    public String getStartDate(){
        return startDate ;
    }
    public String getEndDate(){
        return endDate ;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }
    
    public boolean isExceeded() {
        return currentSpending > limitAmount;
    }
    
    public boolean isNearLimit() {
        if (limitAmount == 0) {
            return false;
        }
        double percentage = (currentSpending / limitAmount) * 100;
        return percentage >= alertThreshold;
    }
    
    public double getRemainingAmount() {
        return limitAmount - currentSpending;
    }
    
    public void updateSpending(double amount) {
        this.currentSpending += amount;
        updateStatus();
    }
    
    private void updateStatus() {
        if (isExceeded()) {
            this.status = "Exceeded";
        } else if (isNearLimit()) {
            this.status = "Near Limit";
        } else {
            this.status = "On Track";
        }
    }
}