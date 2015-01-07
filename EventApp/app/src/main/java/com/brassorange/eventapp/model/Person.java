package com.brassorange.eventapp.model;

import java.io.Serializable;

public class Person implements Serializable {
	public String uid;
	public String firstName;
	public String middleNames;
	public String lastName;
	public String title;
	public String biography;
	public String imageName;
	public String email;

    public String getFullName() {
        String fullName = "";
        if (hasLastName())
            fullName = this.lastName;
        if (hasLastName() && hasMiddleNames())
            fullName = this.middleNames + " " + this.lastName;
        if (hasLastName() && hasFirstName())
            fullName = this.firstName + " " + fullName;
        return fullName;
    }

    public boolean hasFirstName() {
        return (this.firstName != null && this.firstName != "");
    }

    public boolean hasMiddleNames() {
        return (this.middleNames != null && this.middleNames != "");
    }

    public boolean hasLastName() {
        return (this.lastName != null && this.lastName != "");
    }
}
