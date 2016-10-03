package org.myire.scent;


public class Statements
{
    public int methodWithAllSortsOfStatements(java.util.concurrent.locks.Lock pLock)
    {
        assert pLock != null;                                   // 1 statement

        int x = 0;                                              // 1 statement
        for (int i=0; i<10; i++)                                // 1 statement
        {
            if (pLock.hashCode() == i)                          // 1 statement
                break;                                          // 1 statement

            x++;                                                // 1 statement
            if (x == i*2)                                       // 1 statement
                continue;                                       // 1 statement

            System.out.println("Full iteration");               // 1 statement
        }

        for (Object aKey : System.getProperties().keySet())     // 1 statement
            System.out.println(aKey);                           // 1 statement

        System.out.println(x);                                  // 1 statement

        synchronized (pLock)                                    // 1 statement
        {
            do                                                  // 1 statement
            {
                System.out.println(x--);                        // 1 statement
            }
            while (x > 0);

            while (x < 10)                                      // 1 statement
                System.out.println(x++);                        // 1 statement
        }

        pLock.lock();                                           // 1 statement
        try                                                     // 1 statement
        {
            System.out.println(pLock);                          // 1 statement
        }
        catch (Throwable t)
        {
            t.printStackTrace();                                // 1 statement
        }
        finally
        {
            pLock.unlock();                                     // 1 statement
        }

        if (pLock.equals(this))                                 // 1 statement
            throw new IllegalStateException();                  // 1 statement

        switch (x)                                              // 1 statement
        {
            case 0:                                             // 1 statement
                return 17;                                      // 1 statement
            case 1:                                             // 1 statement
                System.out.println("Unlikely");                 // 1 statement
                break;                                          // 1 statement
            default:                                            // 1 statement
                System.out.println("Standard");                 // 1 statement
                break;                                          // 1 statement
        }

        try (                                                   // 1 statement
             FileInputStream is = new FileInputStream("in");    // 1 statement
             FileOutputStream os = new FileOutputStream("out")) // 1 statement
        {
            os.write(is.read());                                // 1 statement
        }

        return 17;                                              // 1 statement
    }

                                                                // Total 38 statements
}
