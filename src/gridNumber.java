public class gridNumber {
    private String number;
    private boolean isLocked;

    public gridNumber(){
        number   = "";
        isLocked = false;
    }

    /**
     *  gridNumber konstruktors.
     * @param num - Cipars, kurš tiks uzstādits kvadrata.
     */
    public gridNumber(gridNumber num){
        number   = num.getNumber();
        isLocked = num.isLocked;
    }

    public String getNumber(){
        return number;
    }

    public void setNumber(String pNumber) {
        if (!isLocked) number = pNumber;
    }

    public void setLock(boolean pLock){
        isLocked = pLock;
    }

    public boolean isLocked(){
        return isLocked;
    }

}
