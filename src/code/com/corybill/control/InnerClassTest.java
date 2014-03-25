package code.com.corybill.control;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 12/9/13
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class InnerClassTest {
    private String name;
    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String username;


    public class InnerClass {
        public void whatCanIDo() {

        }
    }
}
