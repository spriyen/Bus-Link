public class Ride {

    private String sourceLocation;
    private String destinationLocation;
    private String selectedDate;
    private String selectedTime;

    public Ride() {
        // Default constructor required for Firebase
    }

    public Ride(String sourceLocation, String destinationLocation, String selectedDate, String selectedTime) {
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }
}