package com.kerols2020.cloudgallery;

public class carDetails
{
 private String Description,Link;

    public carDetails(String description, String link) {
        Description = description;
        Link = link;
    }
    public carDetails(){}

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
