package none.rg.basicfs;

import none.rg.basicfs.storage.PhysicalStorage;
import org.junit.Assert;
import org.junit.Test;

public class BasicFsTest {

    @Test(expected = NullPointerException.class)
    public void sillyTest() {
        Assert.assertNotNull(new BasicFs((PhysicalStorage) null));
    }
}
