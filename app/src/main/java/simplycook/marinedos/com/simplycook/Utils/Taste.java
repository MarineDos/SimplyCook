package simplycook.marinedos.com.simplycook.Utils;

/** @brief	Class that manage the information of taste. */
public class Taste{
    /** @brief	The name of the ingredient. */
    private String m_name;
    /** @brief	The comment. */
    private String m_comment;
    /** @brief	The like (love, like, hate ...). */
    private int m_like;

    /**
    *@brief		Constructor.
    *
    *@param		name   	The name of the ingredient.
    *@param		like   	The like.
    *@param		comment	The comment.
     */
    public Taste(String name, int like, String comment){
        m_name = name;
        m_comment = comment;
        m_like = like;
    }
    /**
    *@brief		Gets the name of the ingredient.
    *
    *@return	The name of the ingredient.
     */
    public String getName(){
        return m_name;
    }
    /**
    *@brief		Gets the comment.
    *
    *@return	The comment.
     */
    public String getComment(){
        return m_comment;
    }
    /**
    *@brief		Gets the like.
    *
    *@return	The like.
     */
    public int getLike(){
        return m_like;
    }
}
