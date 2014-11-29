package simplycook.marinedos.com.simplycook.Utils;

public class Taste{
    private String m_name;
    private String m_comment;
    private int m_like;

    public Taste(String name, int like, String comment){
        m_name = name;
        m_comment = comment;
        m_like = like;
    }
    public String getName(){
        return m_name;
    }
    public String getComment(){
        return m_comment;
    }
    public int getLike(){
        return m_like;
    }
}
