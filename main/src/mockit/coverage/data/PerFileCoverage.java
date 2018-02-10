/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.coverage.data;

import java.io.*;

public interface PerFileCoverage extends Serializable
{
   int getTotalItems();
   int getCoveredItems();
   int getCoveragePercentage();
}
