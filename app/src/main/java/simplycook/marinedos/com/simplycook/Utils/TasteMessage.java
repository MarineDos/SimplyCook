package simplycook.marinedos.com.simplycook.Utils;

/** @brief	Class that manage the taste message. */
public class TasteMessage {
    /** @brief	The person's first name. */
    public String firstName;
    /** @brief	The person's last name. */
    public String lastName;
    /** @brief	Name of the ingredient. */
    public String foodName;
    /** @brief	The like. */
    public int like;
    /** @brief	The comment. */
    public String comment;

    /**
    *@brief		Constructor.
    *
    *@param		_foodName 	Name of the ingredient.
    *@param		_like	  	The like.
    *@param		_comment  	The comment.
    *@param		_firstName	The person's first name.
    *@param		_lastName 	The person's last name.
     */
    public TasteMessage( String _foodName, int _like, String _comment, String _firstName,  String _lastName){
        foodName = _foodName;
        like = _like;
        comment = _comment;
        firstName = _firstName;
        lastName = _lastName;
    }
}
