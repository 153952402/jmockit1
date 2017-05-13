/*
 * Copyright (c) 2006 Rogério Liesenfeld
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit;

import org.junit.*;
import static org.junit.Assert.*;

public final class TestedParametersTest
{
   static class TestedClass
   {
      final int i;
      final Collaborator collaborator;
      Dependency dependency;

      TestedClass() { i = -1; collaborator = null; }
      TestedClass(int i, Collaborator collaborator) { this.i = i; this.collaborator = collaborator; }
   }

   static class Dependency {}
   static final class Collaborator {}

   @Test
   public void createTestedObjectForTestMethodParameter(@Tested Dependency dep)
   {
      assertNotNull(dep);
   }

   @Tested TestedClass tested1;
   @Tested(fullyInitialized = true) TestedClass tested2;

   @Test
   public void injectTestedObjectFromTestMethodParameterIntoFullyInitializedTestedObject(@Tested Dependency dep)
   {
      assertNull(tested1.dependency);
      assertEquals(-1, tested2.i);
      assertNull(tested2.collaborator);
      assertSame(dep, tested2.dependency);
   }

   @Test
   public void injectTestedParametersIntoFullyInitializedTestedFieldUsingConstructor(
      @Tested("123") int i, @Tested Collaborator collaborator)
   {
      assertEquals(123, i);
      assertNotNull(collaborator);

      // Regular @Tested objects only consume @Injectable's, not other @Tested values.
      assertEquals(-1, tested1.i);
      assertNull(tested1.collaborator);
      assertNull(tested1.dependency);

      // Fully initialized @Tested objects, OTOH, do consume both.
      assertEquals(123, tested2.i);
      assertSame(collaborator, tested2.collaborator);
      assertNotNull(tested2.dependency);
   }
}
