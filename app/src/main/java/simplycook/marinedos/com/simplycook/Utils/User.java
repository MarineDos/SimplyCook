package simplycook.marinedos.com.simplycook.Utils;

import android.graphics.Bitmap;

/** @brief	Class that manage the information of an user. */
public class User {
    /** @brief	The user's first name. */
    public String firstName;
    /** @brief	The user's last name. */
    public String lastName;
    /** @brief	The connexion mode (facebook...). */
    public String connexionMode;
    /** @brief	Identifier for the firebase. */
    public String firebaseId;
    /** @brief	The image ressource. */
    public int imageRessource;
    /** @brief	The image bitmap. */
    public Bitmap imageBitmap;
}
