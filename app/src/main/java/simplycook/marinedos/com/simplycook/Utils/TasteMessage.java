package simplycook.marinedos.com.simplycook.Utils;

/**
 * Created by Marine on 04/01/2015.
 */
public class TasteMessage {
    public String firstName;
    public String lastName;
    public String foodName;
    public int like;
    public String comment;

    public TasteMessage( String _foodName, int _like, String _comment, String _firstName,  String _lastName){
        foodName = _foodName;
        like = _like;
        comment = _comment;
        firstName = _firstName;
        lastName = _lastName;
    }
}
