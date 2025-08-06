public class Example {
    private String name;
    private int age;
    private String email; // Added new field
    
    public Example(String name, int age, String email) { // Modified constructor
        this.name = name;
        this.age = age;
        this.email = email; // Added initialization
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    // Added new getter method
    public String getEmail() {
        return email;
    }
    
    // Added new setter method
    public void setEmail(String email) {
        this.email = email;
    }
}
