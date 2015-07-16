/*
 * Copyright (c) 2006-2015 Rogério Liesenfeld
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package integrationTests;

import org.junit.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

@RunWith(CustomRunner.class)
public final class CustomClassLoadingTest extends CoverageTest
{
   Object tested;

   @After
   public void resetFileData()
   {
      fileData = null;
   }

   @Test
   public void runCodeInClassReloadedOnCustomClassLoader()
   {
      ClassLoader thisCL = getClass().getClassLoader();
      IfElseStatements ifElse = new IfElseStatements();
      ClassLoader customCL = ifElse.methodToBeCalledFromCustomRunnerTest("TEST");

      assertNotSame(ClassLoader.getSystemClassLoader(), thisCL);
      assertSame(thisCL, customCL);

      tested = ifElse;
      assertLines(189, 193, 2);
      assertLine(189, 1, 1, 1);
      assertLine(191, 1, 1, 1);
      assertLine(195, 1, 1, 1);

      findMethodData(189);
      assertPaths(2, 1, 1);
      assertPath(4, 0);
      assertPath(5, 1);

      assertInstanceFieldUncovered("instanceField");
   }

   @Test
   public void exerciseClassThatIsOnlyUsedHere()
   {
      ClassLoadedByCustomLoaderOnly obj = new ClassLoadedByCustomLoaderOnly("test");
      String value = obj.getValue();

      assertEquals("test", value);

      tested = obj;
      assertLines(9, 9, 1);
      assertLine(9, 1, 1, 1);

      findMethodData(9);
      assertPaths(1, 1, 1);
      assertPath(2, 1);
   }
}
