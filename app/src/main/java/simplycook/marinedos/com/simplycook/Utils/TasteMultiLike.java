package simplycook.marinedos.com.simplycook.Utils;


public class TasteMultiLike{
    private String m_name;
    private int[] m_like;

    public TasteMultiLike(String name){
        m_name = name;
        m_like = new int[4];
        m_like[0] = m_like[1] = m_like[2] = m_like[3] = -2;
    }
    public String getName(){
        return m_name;
    }
    public int[] getLikes(){
        return m_like;
    }
}
