public class Previews
{
    // Pattern matching for instance of is a language feature preview in language levels 14 and 15
    public void strlen(Object pObject)
    {
        if (pObject instanceof String s)
            return s.length();
        else
            return -1;
    }
}
