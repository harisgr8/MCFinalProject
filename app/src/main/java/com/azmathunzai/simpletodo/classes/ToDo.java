package com.azmathunzai.simpletodo.classes;

/**
 * Created by virgo on 9/24/16.
 */
public class ToDo {
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "toDo";
    public static final String KEY_NOTE = "addedOn";
    public static final String KEY_ADD_IMAGE = "image_path";
    public static final String KEY_DATE = "date";
    public static final String KEY_BG_COLOR = "bg_color";
    public static final String KEY_IS_CHECK_BOX = "is_note_checkbox";
    public static final String KEY_STATUS = "status";


    //public variables
    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_COMPLETED = 1;

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_CHECKLIST = 1;


    //private variables
    private int ID;
    private String title;
    private String textNote;
    private String image;
    private String date;
    private String bgColor;
    private int isCheckList;
    private int status;

    public ToDo() {
        this(0, "", "","", "","", 0);
    }

    public ToDo(int ID, String title, String textNote, String image,String date, String bgColor, int status) {
        this(ID, title, textNote, image, date, bgColor, status, ToDo.TYPE_TEXT);
    }

    public ToDo(int ID, String title, String textNote, String image,String date,String bgColor, int status, int isCheckList) {
        this.ID = ID;
        this.title = title;
        this.textNote = textNote;
        this.image = image;
        this.date = date;
        this.bgColor = bgColor;
        this.status = status;
        this.isCheckList = isCheckList;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextNote() {
        return textNote;
    }

    public void setTextNote(String textNote) {
        this.textNote = textNote;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsCheckList() {
        return isCheckList;
    }

    public void setIsCheckList(int isCheckList) {
        this.isCheckList = isCheckList;
    }
}
