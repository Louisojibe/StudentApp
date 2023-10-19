package ng.ojibe.storeme;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Student implements Serializable {
    private String regNo;
    private String name;
    private String dept;
    private int age;
    private int level;

    public Student(String regNo, String name, String dept, int age, int level) {
        this.regNo = regNo;
        this.name = name;
        this.dept = dept;
        this.age = age;
        this.level = level;
    }

    public Student() {

    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
