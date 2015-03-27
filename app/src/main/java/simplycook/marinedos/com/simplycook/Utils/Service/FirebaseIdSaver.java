package simplycook.marinedos.com.simplycook.Utils.Service;


/** @brief	Class that manage the firebase identifier. */
public class FirebaseIdSaver {
    /** @brief	Identifier for the firebase. */
    public static String firebaseId;

    /**
    *@brief		Saves a firebase identifier.
    *
    *@param		_firebaseId	Identifier for the firebase.
     */
    public void saveFirebaseId(String _firebaseId){
        firebaseId = _firebaseId;
    }
}
