package model;

/**
 * Represents a transaction category in the system.
 * Categories can be default or user-created (custom).
 */
public class Category {

    /** Unique identifier for the category */
    private int ID;

    /** Name of the category */
    private String name;

    /** Indicates whether this category was created by the user */
    private boolean isCustom;

    /**
     * Creates a new Category.
     *
     * @param ID unique identifier
     * @param name category name
     * @param isCustom whether the category is user-defined
     */
    public Category(int ID, String name, boolean isCustom) {
        this.ID = ID;
        this.name = name;
        this.isCustom = isCustom;
    }

    /** @return category name */
    public String getName() {
        return name;
    }

    /** @return category ID */
    public int getID() {
        return ID;
    }

    /** @return true if category is custom-created */
    public boolean isCustom() {
        return isCustom;
    }

    /**
     * Updates the category name.
     *
     * @param name new name of the category
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the category name using a new value.
     * (Alternative method to setName)
     *
     * @param newName updated category name
     */
    public void updateName(String newName) {
        this.name = newName;
    }

    /**
     * Deletes the category (simulation only).
     * In real apps this would remove it from storage.
     */
    public void delete() {
        System.out.println(name + " deleted");
    }
}
