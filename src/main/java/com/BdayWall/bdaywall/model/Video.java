package com.BdayWall.bdaywall.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

/**
 * This is the model class which  is mapped to the database using the bday collection.This class contains the members
 * and their getter ,setter and constructors.
 *
 * @author Kopal Choure.
 */
@Document(collection = "Bdaywall")
public class Video
{
    private String firstname;
    private String lastname;
    private String teamname;
    private String gender;
    @Id
    private String associd;
    private MultipartFile file;
    private Integer day;
    private String months;
    private Integer year;

    /**
     * Default constructor
     */
    public Video ()
    {
    }

    /**
     * Parameterized constructor
     *
     * @param firstname The first name of the user.
     * @param lastname  Last name of the user.
     * @param teamname  The team user belongs to.
     * @param associd   User's associate id.
     * @param day       Day the user was born.
     * @param months    User's bday month.
     * @param year      User's birth year.
     */
    public Video (String firstname, String lastname, String teamname, String associd, Integer day, String months,
            Integer year)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.teamname = teamname;
        this.associd = associd;
        this.day = day;
        this.months = months;
        this.year = year;
    }

    /**
     * @return firstname in string format.
     */
    public String getFirstname ()
    {
        return firstname;
    }

    /**
     * @param firstname To set the first name of the user.
     */
    public void setFirstname (String firstname)
    {
        this.firstname = firstname;
    }

    /**
     * @return lastname in string format.
     */
    public String getLastname ()
    {
        return lastname;
    }

    /**
     * @param lastname To set the last name of the user.
     */
    public void setLastname (String lastname)
    {
        this.lastname = lastname;
    }

    /**
     * @return teamname in string format.
     */
    public String getTeamname ()
    {
        return teamname;
    }

    /**
     * @param teamname To set the team name of the user.
     */
    public void setTeamname (String teamname)
    {
        this.teamname = teamname;
    }

    /**
     * @return associd in string format.
     */
    public String getAssocid ()
    {
        return associd;
    }

    /**
     * @param associd To set the associate id of the user.
     */
    public void setAssocid (String associd)
    {
        this.associd = associd;
    }

    /**
     * @return emailid in String format.
     */

    public MultipartFile getFile ()
    {
        return file;
    }

    public void setFile (MultipartFile file)
    {
        this.file = file;
    }

    /**
     * @return day in int format.
     */
    public Integer getDay ()
    {
        return day;
    }

    /**
     * @param day To set the day of bday.
     */
    public void setDay (Integer day)
    {
        this.day = day;
    }

    /**
     * @return months in string format.
     */
    public String getMonths ()
    {
        return months;
    }

    /**
     * @param months To set the bday month.
     */
    public void setMonths (String months)
    {
        this.months = months;
    }

    /**
     * @return year in int format.
     */
    public Integer getYear ()
    {
        return year;
    }

    /**
     * @param year To set the year of birth.
     */
    public void setYear (Integer year)
    {
        this.year = year;
    }

    public String getGender ()
    {
        return gender;
    }

    public void setGender (String gender)
    {
        this.gender = gender;
    }

    /**
     * @return string values.
     */
    @Override
    public String toString ()
    {
        return "Bday{" + "firstname='" + firstname + '\'' + ", lastname='" + lastname + '\'' + ", teamname='" + teamname
                + '\'' + ", associd='" + associd + '\'' + ", day=" + day + ", months='" + months + '\'' + ", year="
                + year + '}';
    }
}
